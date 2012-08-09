package com.primus.batch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.sipdev.framework.Framework;
import org.sipdev.framework.Log;

import com.beachdog.primusCore.Config;
import com.beachdog.primusCore.Dao;
import com.beachdog.primusCore.M6ModifyUserCommand;
import com.beachdog.primusCore.PactolusConstants;
import com.beachdog.primusCore.Utilities;
import com.beachdog.primusCore.model.AccountActivity;
import com.beachdog.primusCore.model.Rate;
import com.beachdog.primusCore.model.ServiceProvider;
import com.beachdog.primusCore.model.SubAuthAni;
import com.beachdog.primusCore.model.Subscriber;
import com.beachdog.primusCore.model.SuspendTracking;

import com.vocaldata.AdminSOAP.*;

public class App {

	protected String hostName = "localhost" ;
	protected Integer purgeDays ;
	protected Integer sleep ;
	
	protected File outFile = null ;
	protected BufferedWriter writer = null ;
	protected Boolean verbose = false ;
	protected List<String> spNames = new ArrayList<String>() ;
	protected List<ServiceProvider> serviceProviders = new ArrayList<ServiceProvider>() ;
	Log logger = null;
	Framework framework = null;
	
	public static void main(String[] args) {

		App a = new App() ;
		a.run( args ) ;
	}
	
	public void run( String[] args ) {
		Session session = null ;
		SessionFactory sessionFactory = null ;
		
		DecimalFormat fmt = new DecimalFormat("###.00") ;

		try {
			framework =Framework.getInstance() ;
			logger = framework.getLogger();
	
	  		logger.info("--------------   SuspendZeroBalanceAccounts   --  Starting  ------------------");

			if( !parseCommandLine( args ) ) {
				usage() ;
				return ;
			}

			if( null != sleep ) {
				Thread.currentThread();
				Thread.sleep( sleep * 1000 ) ;
			}
			getHostAndIP() ;
			
			sessionFactory = (SessionFactory)framework.getResource("sessionFactory");    	
    		session = sessionFactory.openSession() ;
    		
    		Config cfg = (Config) framework.getResource("wsConfig") ;
    		
			Calendar now = Calendar.getInstance();
			Timestamp dtNow = new java.sql.Timestamp((new java.util.Date()).getTime());

 		
    		if( null != purgeDays ) {
    			
    			Date dt =  new Date( System.currentTimeMillis() )  ;
    			Calendar c = Calendar.getInstance();
    		    c.setTime( dt );
    		    c.add(Calendar.DATE, -purgeDays );
    		    dt.setTime( c.getTime().getTime() ) ;

    		    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" ) ;
    			logger.info("Purging old unsuspend records that occurred on or before " + sdf.format(dt) ) ;

    			int nrows = session.createSQLQuery("DELETE FROM SUSPEND_TRACKING WHERE UNSUSPENDED_ON <= ?")
    				.setDate(0, dt) 
    				.executeUpdate() ;

    			logger.info("Purged " + nrows + " rows from suspend_tracking table") ;
    		}
    		
    		logger.info("Processing " + spNames.size() + " Service Providers") ;
    		
    		/* lookup service provider by name, make sure all are resolvable */
    		Iterator<String> itName = spNames.iterator() ;
    		while( itName.hasNext() ) {
    			String spName = itName.next() ;
    			
    			 ServiceProvider sp = (ServiceProvider) session.createCriteria(ServiceProvider.class)
    					 .add(Restrictions.eq("name", spName))
    					 .add(Restrictions.eq("disabledFlag", "F"))
    					 .add(Restrictions.eq("deletedFlag", "F"))
    					 .uniqueResult() ;
    			 if( null == sp ) {
    				 logger.error("invalid/unknown service provider name: '" + spName + "'; terminating") ;
    				 System.err.println("invalid/unknown service provider name: '" + spName + "'; terminating");
    				 return  ;
    			 }
    			 
    			 serviceProviders.add( sp ) ;
    		}
    		
    		/* now iterator through the service providers, processing each subscriber that is active and has an authorized ANI and a zero balance */
    		for( ServiceProvider sp : serviceProviders ) {
    			
    			logger.info("Processing service provider " + sp.getName() ) ;
    			
        		DetachedCriteria authAniCriteria = DetachedCriteria.forClass(SubAuthAni.class, "authAni")
        				.add(Property.forName("authAni.subscriberId").eqProperty( "sub.subscriberId") ) ;

        		Criteria criteria = session.createCriteria(Subscriber.class, "sub")
        				.add(Restrictions.eq("disabledFlag", "F")) 
        				.add(Restrictions.isNotNull("nextMaintFeeDate"))
        				.add(Restrictions.le("currPrepaidBalance", BigDecimal.valueOf(0.0)))
        				.add(Restrictions.eq("serviceProviderId", sp.getServiceProviderId())) ;
        		      		
        		criteria.add(Subqueries.exists(authAniCriteria.setProjection(Projections.property("subscriberId")))) ;
        		
        		List<Subscriber> list = criteria.list() ;
        		Iterator<Subscriber> itSubscriber = list.iterator() ;
        		while( itSubscriber.hasNext() ) {
        			Subscriber sub = itSubscriber.next() ;
        			
        			java.util.Date dtLastUnsuspend = null ;
        			
        			/* make sure they are not already suspended */
        			SuspendTracking exst = (SuspendTracking) session.createCriteria(SuspendTracking.class)
        					.add(Restrictions.eq("subscriberId", sub.getSubscriberId() ) )
        					.uniqueResult() ;
        			if( null != exst ) {
        				
        				if( null == exst.getUnsuspendedOn() ) {
        					/* already suspended */
        					logger.info("Ignoring subscriber with id " + sub.getSubscriberId() + " because they are already suspended") ;
        					continue ;
        				}
        				dtLastUnsuspend = exst.getUnsuspendedOn() ;
        				
        				/* they are in suspend tracking, but were unsuspended at some point in the past, and now we are re-suspending them (possibly).
        				 * clear the old record
        				 */
        				Transaction transaction = null ;
        				try {
	        				transaction = session.beginTransaction() ;
	        				session.delete(exst) ;
	        				transaction.commit() ;
	        				logger.info("Subscriber with id " + sub.getSubscriberId() + " was previously suspended/unsuspended and now has a zero balance; clearing old record from suspend_tracking table") ;
        				} catch( HibernateException e) {
        					logger.error("Error deleting record for subscriber id " + sub.getSubscriberId() + " from suspend tracking", e) ;
        					transaction.rollback() ;
        				}	
        			}
        			
        			
        			
        			String cli = null ;
        			List<SubAuthAni> authAnis = session.createCriteria(SubAuthAni.class)
        					.add(Restrictions.eq("subscriberId", sub.getSubscriberId()))
        					.list() ;
        			if( authAnis.isEmpty() ) {
        				throw new Exception("Auth anis list unexpectedly empty") ;
        			}
        			
        			Iterator<SubAuthAni> itAuthAni = authAnis.iterator() ;
        			while( null == cli && itAuthAni.hasNext() ) {
        				SubAuthAni ani = itAuthAni.next() ;
        				try {
	        				if( 0 == Integer.valueOf( ani.getStatus() ) ) {
	        					cli = ani.getPhoneNumber() ;
	        				}
        				} catch( NumberFormatException nfe ) {}
        			}
        			
        			if( null == cli ) {
        				/* subscriber does not have an active auth ani */
            			logger.info("Not suspending subscriber -- does not have an active auth ani; subscriber id: " + sub.getSubscriberId().longValue() ) ;
        				continue ;
        			}
        			logger.info("   Processing subscriber with phone number " + cli + " and subscriber_id " + sub.getSubscriberId() + " that has zero balance") ;
        			
        			
        			/* to be suspended, a subscriber must also 
        			 * 		(1) have a product offering that has a maintenance fee, and
        			 * 		(2) the last time that maintenance fee ran it must have taken less than the configured amount
        			 */
        			
        			Rate maintFeeRate = Dao.getSubscriberMaintenanceFeeRate(session, sub.getPrimaryOfferingId()) ;

        		
        			if( null == maintFeeRate  ) {
            			logger.info("   Not suspending -- product offering has no maintenance fee rate; subscriber id: " + sub.getSubscriberId().longValue() ) ;
        				continue ;
        			}
        			else if( null == maintFeeRate.getMaintFeeFrequency() || 0 == maintFeeRate.getMaintFeeFrequency().longValue() ){
            			logger.error("   Not suspending -- product offering has a maintenance fee rate but no frequency has been set; subscriber id: " + sub.getSubscriberId().longValue() ) ;
        				continue ;        				
        			}
        							
        			/* find the most recent account activity record for this maintenance fees */		      			
        			Iterator<AccountActivity> itAA = sub.getAccountActivities().iterator() ;
        			AccountActivity aa = null ;
        			//Rate rateApplied = null ;
        			while( itAA.hasNext()  ) {
        				AccountActivity aaTemp = itAA.next() ;
        				if( null != dtLastUnsuspend && aaTemp.getTimeDateStamp().before( dtLastUnsuspend ) ) {
             				logger.info("     account activity with activity_id " + aaTemp.getActivityId() + " is old -- before their last unsuspension, so ignore it") ;
        					break ;
        				}
        				if( null != aaTemp.getEventTypeId() && 
        						aaTemp.getEventTypeId().longValue() == (long) PactolusConstants.MAINT_FEE_EVENT && 
        						null != aaTemp.getRateId1() && 
        						0 == maintFeeRate.getRateId().compareTo( aaTemp.getRateId1() ) ) {
        					
        					aa = aaTemp ; 
             				logger.info("     account activity with activity_id " + aaTemp.getActivityId() + " is the most recent maintenance fee") ;
           					break ;
        				}
        				logger.info("     account activity with activity_id " + aaTemp.getActivityId() + " is not a maintenance fee") ;
         			}
        			
        			if( null == aa ) {
        				
        				/* if there are no Account Activity records, then this subscriber should be suspended.
        				 * 
        				 * The reason is that since we know next_maint_fee is not null, the batch maintenance job must have run.
        				 * The fact that there isn't an AA record can only mean that there was no money to take.
        				 */
        				logger.info("  Suspending this subscriber because the first maintenance fee for this subscriber failed to take any money") ;
        				
         			}
        			else {
        				
        				/* now we have the most recent maintenance fee account activity record.  Check to see when it occurred --
        				 * if there was a maintenance fee run in the meantime, that means we did not write an AA record because the balance
        				 * was zero, and in that case we DO want to suspend them.
        				 */
        				if( aa.getTotalAmount().abs().compareTo( maintFeeRate.getDefaultAmount()) < 0 ) {
            				logger.info("  Suspending this subscriber because the last maintenance fee was only able to take a partial amount; activity id was " + aa.getActivityId() +
            						" and amount taken was $" + fmt.format(aa.getTotalAmount().abs().doubleValue())) ;        					
        				}
        				else {
        				
        					/* ok, we have a situation where the last AA record got the full amount.
        					 * However, it may be that there was a more recent attempt that got nothing...
        					 */
	        				Long nDays = maintFeeRate.getMaintFeeFrequency().longValue() ;
	         				Calendar then = Calendar.getInstance() ;
	        				then.setTime( aa.getTimeDateStamp() ) ;
	        				Long daysInArrears = (now.getTimeInMillis() - then.getTimeInMillis())/(1000*60*60*24);
	
		        			logger.info("   Last maintenance fee was collected " + daysInArrears + " days ago and was for an amount of $" + fmt.format( aa.getTotalAmount().abs().doubleValue() ) + ", the activity id was " + aa.getActivityId() ) ;
		        		    logger.info("   The maintenance fee rate is: $" + fmt.format( maintFeeRate.getDefaultAmount().abs().doubleValue() ) + 
		        		    		", the rate id is " + maintFeeRate.getRateId().longValue() + " and the fee is collected every " + nDays + " days" ) ;
	
	        				if( daysInArrears >= nDays ) {
	        					logger.info("   Suspending the account because the most recent maintenance fee failed to collect anything from this account") ;
	        				}
	        				else {	     			
		            			logger.info("   Not suspending -- the most recent maintenance fee got the full amount and it appears no ; subscriber id: " + sub.getSubscriberId().longValue() ) ;
		        				continue ;
	        				}
        				}
        			}

        			Utilities.M6Credential c = Utilities.getM6Credential(cfg, cli) ;
        			logger.info("Attempting to unsuspend phone number " + cli + " on M6: " + c.address + " user/pass: " + c.username + "/" + c.password ) ;
       			
					Transaction transaction = null ;
        			try {
        				
    					M6ModifyUserCommand cmd;
    					cmd = new M6ModifyUserCommand(c.address, c.username, c.password, Utilities.getLocalHost(), cli);
    					cmd.setValue(UserKeys.SUSPEND_SERVICE, true ) ;
    					cmd.execute() ;
    					logger.info("Successfully suspended phone number " + cli ) ;   				
        						        	        
	        	        transaction = session.beginTransaction() ;
	        	        
	        			SuspendTracking st = new SuspendTracking() ;
	        	        st.setSubscriberId(sub.getSubscriberId()) ;
	        	        st.setPhoneNumber(cli) ;
	        	        st.setSuspendedOn( new Date( System.currentTimeMillis() ) ) ;
	        	        if( null != aa ) st.setAaActivityId( aa.getActivityId() ) ;
	        	        
	        	        session.save(st) ;
	        	        transaction.commit() ;

	        			logger.info("   Successfully suspendend subscriber with subscriber id: " + sub.getSubscriberId().longValue() ) ;
	        			
        	        } catch( HibernateException he ) {
        	        	logger.error("Error saving record to suspend_tracking for subscriber with subscriber id: " + sub.getSubscriberId() +
        	        			" and phone number " + cli, he ) ;
        	        	if( null != transaction ) transaction.rollback() ;
        	        	throw he ;
	
        			} catch (DBSOAPException dbe ) {
    					if( cfg.getEmailServer() != null && cfg.getEmailRecipients() != null ) {
    						Utilities.sendMail(cfg.getEmailRecipients(), cfg.getEmailServer(), "M6 suspend failure", 
    								"failure attempting to suspend account with phone number " + cli + " on the M6", null) ;
    					}
    					System.err.println("Error attempting to suspend subscriber on M6, program will terminate") ;
        				dbe.printStackTrace() ;
        				logger.error("Error attempting to suspend subscriber on M6, program will terminate", dbe) ;
        				throw dbe ;
        			}			
        		}
    		}

			
		} catch( Exception e ) {
			Framework.getInstance().getLogger().error("Caught exception in XtmlApi.Init", e);
			e.printStackTrace() ;
		}
		finally {
			Framework.getInstance().getLogger().info("--------------   SuspendZeroBalanceAccounts   --  Ending  ------------------");
			if( null != session ) session.close() ;
		}
		
	}

	protected void usage() {
		System.out.println("java -cp:<classpath> -serviceProviders \"sp 1, sp 2\"") ;
	}
	
	protected boolean parseCommandLine( String[] args ) {
		boolean processingServiceProviderList = false ;
		boolean processingPurge = false ;
		boolean processingSleep = false ;
		
		
       	for( String s : args ) {
       		s = s.trim() ;
			if( processingServiceProviderList ) {
				
				if( 0 == s.indexOf("-") ) {
					processingServiceProviderList = false ;
				}
				else {
					spNames.add( s ) ;
				}
			}
			else if( processingPurge ) {
				this.purgeDays = Integer.valueOf(s) ;
				processingPurge = false ;
			}
			else if( processingSleep ) {
				this.sleep = Integer.valueOf(s) ;
				processingSleep = false; 
			}
			else if( s.equalsIgnoreCase("-serviceProviders") ) {
				processingServiceProviderList = true ;
			}
			else if( s.equalsIgnoreCase("-purge")) {
				processingPurge = true ;
			}
			else if( s.equalsIgnoreCase("-sleep")) {
				processingSleep = true ;
			}
		}
       	
       	if( spNames.isEmpty() ) {
       		System.err.println("Missing parameter: -serviceProviders") ;
       		return false ;
       	}
      
		
		return true ;
	}

	protected boolean getHostAndIP() {
		boolean ok = false ;
		try {
		    InetAddress addr = InetAddress.getLocalHost();

		    // Get IP Address
		    byte[] ipAddr = addr.getAddress();

		    // Get hostname
		    this.hostName = addr.getHostName();
		    ok = true ;
		} catch (UnknownHostException e) {
			e.printStackTrace() ;
		}
		return ok ;
	}
}
