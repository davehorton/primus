package com.primus.batch;

import java.io.BufferedWriter;
import java.io.File;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
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
	protected Integer numDays ;
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
		
		List<String> listUpdates = new ArrayList<String>(); 

		try {
			framework =Framework.getInstance() ;
			logger = framework.getLogger();
	
	  		logger.info("--------------   setFirstUseFlag   --  Starting  ------------------");

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
			java.util.Date dtNow = new java.sql.Date( System.currentTimeMillis() );
    		
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
    		int nUpdates = 0 ;
    		for( ServiceProvider sp : serviceProviders ) {
    			
    			logger.info("Processing service provider " + sp.getName() ) ;
    			

        		Criteria criteria = session.createCriteria(Subscriber.class, "sub")
        				.add(Restrictions.eq("disabledFlag", "F")) 
        				.add(Restrictions.isNull("firstCallDate"))
        				.add(Restrictions.eq("serviceProviderId", sp.getServiceProviderId())) ;
        		      		
        		
        		List<Subscriber> list = criteria.list() ;
        		Iterator<Subscriber> itSubscriber = list.iterator() ;
        		while( itSubscriber.hasNext() ) {
        			Subscriber sub = itSubscriber.next() ;
        			
        			if( sub.getSubAuthAnis().isEmpty() ) {
        				continue ;
        			}
        			        			
        			
        			String cli = null ;
        			Iterator<SubAuthAni> itAuthAni = sub.getSubAuthAnis().iterator() ;
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
        				continue ;
        			}

        			logger.info("   Processing subscriber with phone number " + cli + " and subscriber_id " + sub.getSubscriberId() + " that has has not set first call date") ;
        			
        			/* Check if this phone number exists on the M6, and if so when it was activated */
        			java.util.Date dtActivated = Dao.getPcsActivationDateForNumber(cli) ;
        			if( null == dtActivated ) {
        				logger.info("   Phone " + cli + " was not found in PCS") ;
        				continue ;
        			}
        			
        			long diffInDays = (dtNow.getTime() - dtActivated.getTime())  / (1000 * 60 * 60 * 24) ;
        			
        			if( (int) diffInDays > numDays ) {
            			logger.info("   Phone " + cli + " was activated on PCS on " + sdf.format(dtActivated) + ", which was " + diffInDays + " ago; first call date will be set on Pactolus to today" ) ;
        				logger.info("   First call date will be set on the Pactolus system since the phone has been active more than " + numDays + " days") ;
        				Transaction transaction = null ;
        				try {
        					transaction = session.beginTransaction() ;
        					sub.setFirstCallDate(dtNow) ;
        					transaction.commit() ;
        					listUpdates.add(cli) ;
        					nUpdates++ ;
        				} catch( HibernateException e ) {
        					logger.error("Error setting first call date for phone " + cli, e) ;
        				}
        			}
        			else {
            			logger.info("   Phone " + cli + " was activated on PCS on " + sdf.format(dtActivated) + ", which was " + diffInDays + " ago; first call date will not be set on Pactolus" ) ;        				
        			}    			
        		}
    		}

    		logger.info("Completed: " + nUpdates + " accounts had their first_call_date attribute set") ;
    		if( nUpdates > 0 ) {
    			logger.info("The phone numbers updated were:") ;
    			for( String phone : listUpdates ) {
    				logger.info( phone ) ;
    			}
    		}
			
		} catch( Exception e ) {
			Framework.getInstance().getLogger().error("Caught exception in App.run", e);
			e.printStackTrace() ;
		}
		finally {
			Framework.getInstance().getLogger().info("--------------   setFirstUseFlag   --  Ending  ------------------");
			if( null != session ) session.close() ;
		}
		
	}

	protected void usage() {
		System.out.println("java -cp:<classpath> -serviceProviders \"sp 1, sp 2\" -days numDays") ;
	}
	
	protected boolean parseCommandLine( String[] args ) {
		boolean processingServiceProviderList = false ;
		boolean processingDays = false ;
		boolean processingSleep = false ;
		
		
       	for( String s : args ) {
       		s = s.trim() ;
			if( processingServiceProviderList ) {
				
				if( 0 == s.indexOf("-") ) {
					processingServiceProviderList = false ;
					if( s.equalsIgnoreCase("-days") ) {
						processingDays = true ;
					}
				}
				else {
					spNames.add( s ) ;
				}
			}
			else if( processingDays ) {
				this.numDays = Integer.valueOf(s) ;
				processingDays = false ;
			}
			else if( processingSleep ) {
				this.sleep = Integer.valueOf(s) ;
				processingSleep = false; 
			}
			else if( s.equalsIgnoreCase("-serviceProviders") ) {
				processingServiceProviderList = true ;
			}
			else if( s.equalsIgnoreCase("-days")) {
				processingDays = true ;
			}
			else if( s.equalsIgnoreCase("-sleep")) {
				processingSleep = true ;
			}
		}
       	
       	if( spNames.isEmpty() ) {
       		System.err.println("Missing parameter: -serviceProviders") ;
       		return false ;
       	}
      
       	if( null == this.numDays ) {
       		System.err.println("Missing parameter: -days") ;
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
