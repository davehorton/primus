package com.primus.batch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
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

import com.beachdog.primusCore.model.AccountActivity;
import com.beachdog.primusCore.model.Rate;
import com.beachdog.primusCore.model.ServiceProvider;
import com.beachdog.primusCore.model.SubAuthAni;
import com.beachdog.primusCore.model.Subscriber;
import com.beachdog.primusCore.model.SuspendTracking;

import com.vocaldata.AdminSOAP.*;

public class App {

	final protected static BigDecimal MAINT_FEE_EVENT = BigDecimal.valueOf(10) ;
	final protected static BigDecimal MAINT_FEE2_EVENT = BigDecimal.valueOf(30) ;
	final protected static BigDecimal MAINT_FEE3_EVENT = BigDecimal.valueOf(31) ;

	protected String hostName = "localhost" ;
	protected String m6User ;
	protected String m6Password ;
	protected String m6Address ;
	protected Integer purgeDays ;
	
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

			getHostAndIP() ;
			
			sessionFactory = (SessionFactory)framework.getResource("sessionFactory");    	
    		session = sessionFactory.openSession() ;
    		
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
        				.add(Restrictions.le("currPrepaidBalance", BigDecimal.valueOf(0.0)))
        				.add(Restrictions.eq("serviceProviderId", sp.getServiceProviderId())) ;
        		
        		
        		criteria.add(Subqueries.exists(authAniCriteria.setProjection(Projections.property("subscriberId")))) ;
        		
        		List<Subscriber> list = criteria.list() ;
        		Iterator<Subscriber> itSubscriber = list.iterator() ;
        		while( itSubscriber.hasNext() ) {
        			Subscriber sub = itSubscriber.next() ;
        			
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
        			logger.info("   Processing subscriber with phone number " + cli + " and subscriber_id " + sub.getSubscriberId() ) ;
        			
        			
        			/* to be suspended, a subscriber must also 
        			 * 		(1) have a product offering that has a maintenance fee, and
        			 * 		(2) the last time that maintenance fee ran it must have taken less than the configured amount
        			 */
        			
        			Criterion cr1 = Restrictions.eq("eventTypeId", MAINT_FEE_EVENT) ;
        			Criterion cr2 = Restrictions.or( Restrictions.eq("eventTypeId", MAINT_FEE2_EVENT), Restrictions.eq("eventTypeId", MAINT_FEE3_EVENT) ) ;
        			
        			List<Rate> maintenanceFeeRates = session.createCriteria(Rate.class)
        					.createAlias("ratedEvents", "re")
        					.add(Restrictions.or( cr1, cr2 ) )
        					.add(Restrictions.eq("re.productOfferingId", sub.getPrimaryOfferingId()))
        					.list() ;
        			if( null == maintenanceFeeRates || maintenanceFeeRates.isEmpty() ) {
            			logger.info("   Not suspending -- product offering has no maintenance fees; subscriber id: " + sub.getSubscriberId().longValue() ) ;
        				continue ;
        			}
        							
        			/* find the most recent account activity record for one of the product offering's maintenance fees */		      			
        			boolean bFoundAA = false ;
        			Iterator<AccountActivity> itAA = sub.getAccountActivities().iterator() ;
        			AccountActivity aa = null ;
        			Rate rateApplied = null ;
        			while( itAA.hasNext() && !bFoundAA ) {
        				aa = itAA.next() ;
        				if( null == aa.getRateId1() )
        					continue ;
        				
        				for( Rate rate : maintenanceFeeRates ) {
        					if( null != rate.getRateId() && rate.getRateId().compareTo( aa.getRateId1() ) == 0 ) {
        						bFoundAA = true ;
        						rateApplied = rate ;
        					}
            				if( bFoundAA ) break ;
        				}
        			}
        			
        			if( !bFoundAA ) {
        				
        				/* no account activity records exist; this can happen in two cases:
        				 * (1) The batch maintenance job has never been run for this account (in which case, we do not suspend the account)
        				 * (2) The batch maintenance job ran, but there was zero balance so it did not deduct anything (in which case we DO want to suspend the account)
        				 * 
        				 * Case #2 can be detected by the fact that the next_maint_fee_date field will be non-null, whereas in case #2 it 
        				 */
 
        				
        				if( null != sub.getNextMaintFeeDate() ) {
        					logger.info("   No account activity records have been written for this account, but next_maint_fee_date is non-null, " +
        							"indicating batch maintenance ran previously and could not collect from this zero balance account") ;
        				}
        				else {
        					logger.info("   Not suspending -- there are no account activity records for maintenance fees and next_maint_fee_date is null for subscriber id: " + sub.getSubscriberId().longValue() ) ;
        					continue ;
        				}
        			}
        			else {
        			
	        			/* now we have the most recent maintenance fee account activity record.  Check whether we got the full amount */
	        			logger.info("Last maintenance fee collected was: " + fmt.format( aa.getTotalAmount().abs().doubleValue() ) + ", activity id was " + aa.getActivityId() ) ;
	        		    logger.info("Maintenance fee rate is:            " + fmt.format( rateApplied.getDefaultAmount().abs().doubleValue() ) + " rate id was " + rateApplied.getRateId().longValue() ) ;
	
	        			if( rateApplied.getDefaultAmount().abs().compareTo( aa.getTotalAmount().abs() ) <= 0 ) {
	        				/* we got the total fee on our last attempt....probably we'll expire them tomorrow after the next one fails (if they don't recharge) */
	            			logger.info("   Not suspending -- the most recent maintenance fee got the full amount; subscriber id: " + sub.getSubscriberId().longValue() ) ;
	        				continue ;
	        			}
        			}
        			logger.info("   Attempting to suspend subscriber with subscriber id: " + sub.getSubscriberId().longValue() + "...." ) ;
        			
        			try {
	        			VDAdmin vdadmin = new VDAdmin(m6Address) ;
	        			String sessionOid = vdadmin.authenticate(m6User, m6Password, hostName);
	        			if( null == sessionOid ) {
	        				logger.error("Failure logging into M6 using username '" + m6User + "' and password '" + m6Password + "'; program will terminate") ;
	        				System.err.println("Failure logging into M6 using username '" + m6User + "' and password '" + m6Password + "'; program will terminate");
	        				throw new Exception("Failure logging into M6 using username '" + m6User + "' and password '" + m6Password + "'") ;
	        			}
	        			
	        			try {
		        			String customerOid = vdadmin.getUserByExtension(sessionOid, "", cli) ;
		        			if( null == customerOid ) {
		               			logger.info("   Not suspending -- subscriber lookup failed on M6 by phone number: " + cli ) ;	        				
		        			}
		        			
		        	        Hashtable values = new Hashtable() ;
		        	        values.put( UserKeys.SUSPEND_SERVICE, true);
		        	        vdadmin.modify(sessionOid, WizardTypeCode.USER, customerOid, values);
		        	        
		        	        Transaction transaction = null ;
		        	        try {
			        	        transaction = session.beginTransaction() ;
			        	        
			        			SuspendTracking st = new SuspendTracking() ;
			        	        st.setSubscriberId(sub.getSubscriberId()) ;
			        	        st.setPhoneNumber(cli) ;
			        	        st.setSuspendedOn( new Date( System.currentTimeMillis() ) ) ;
			        	        st.setAaActivityId( aa.getActivityId() ) ;
			        	        
			        	        session.save(st) ;
			        	        transaction.commit() ;
		        	        } catch( HibernateException he ) {
		        	        	logger.error("Error saving record to suspend_tracking for subscriber with subscriber id: " + sub.getSubscriberId() +
		        	        			" and phone number " + cli, he ) ;
		        	        	if( null != transaction ) transaction.rollback() ;
		        	        }
	        			} catch( DBSOAPException dbe ) {
	        				logger.error("Error attempting to suspend subscriber with phone number " + cli + ", continuing", dbe) ;
	        				dbe.printStackTrace() ;
	        			}

	        			logger.info("   Successfully suspendend subscriber with subscriber id: " + sub.getSubscriberId().longValue() ) ;
	        			
        			
        			} catch (DBSOAPException dbe ) {
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
		System.out.println("java -cp:<classpath> -serviceProviders \"sp 1, sp 2\" -M6User username -M6Password password -M6Address ip-address") ;
	}
	
	protected boolean parseCommandLine( String[] args ) {
		boolean processingServiceProviderList = false ;
		boolean processingLogfile = false ;
		boolean processingM6User = false ;
		boolean processingM6Pass = false ;
		boolean processingM6Address = false ;
		boolean processingPurge = false ;
		
		
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
			if( processingM6User ) {
				this.m6User = s ;
				processingM6User = false ;
			}
			else if( processingM6Pass ) {
				this.m6Password = s ;
				processingM6Pass = false ;
			}
			else if( processingM6Address ) {
				this.m6Address = s ;
				processingM6Address = false ;
			}
			else if( processingPurge ) {
				this.purgeDays = Integer.valueOf(s) ;
			}
			else if( s.equalsIgnoreCase("-serviceProviders") ) {
				processingServiceProviderList = true ;
			}
			else if( s.equalsIgnoreCase("-logFile")) {
				processingLogfile = true ;
			}
			else if( s.equalsIgnoreCase("-M6User")) {
				processingM6User = true ;
			}
			else if( s.equalsIgnoreCase("-M6Password")) {
				processingM6Pass = true ;
			}
			else if( s.equalsIgnoreCase("-M6Address")) {
				processingM6Address = true ;
			}
			else if( s.equalsIgnoreCase("-purge")) {
				processingPurge = true ;
			}
		}
       	
       	if( spNames.isEmpty() ) {
       		System.err.println("Missing parameter: -serviceProviders") ;
       		return false ;
       	}
       	if( null == m6User || null == m6Password ) {
       		System.err.println("M6 user id and password are required parameters: -M6User username -M6Password password") ;       		
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
