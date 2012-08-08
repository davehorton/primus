package com.beachdog.primusCore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.sipdev.framework.Framework;

import com.pactolus.java.*;

import com.beachdog.primusCore.model.AccountActivity;
import com.beachdog.primusCore.model.Rate;
import com.beachdog.primusCore.model.ServiceProvider;
import com.beachdog.primusCore.model.Subscriber;
import com.beachdog.primusCore.model.SuspendTracking;

public class App {
	
	static protected Integer sleep = null ;
	static protected String emailAddress = null ;

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args)  {

		Framework framework = null ;
		Session session = null ;

		if( !parseCommandLine( args ) ) {
			return ;
		}
		if( null != sleep ) {
			Thread.currentThread();
			try {
				Thread.sleep( sleep * 1000 ) ;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			framework = Framework.getInstance("beans.xml") ;
			Config cfg = (Config) framework.getResource("wsConfig") ;

			if( null != emailAddress ) {
				System.out.println("Sending test email to: " + emailAddress ) ;
				boolean bOK = Utilities.sendMail(emailAddress, cfg.getEmailServer(), "test", "test", null) ;
				if( bOK ) {
					System.out.println("Success") ;
				}
				else {
					System.out.println("Success") ;					
				}
				return ;
			}
			Boolean bSuspended = false ;
			DecimalFormat fmt = new DecimalFormat("#####.00") ;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;

			SessionFactory sf = (SessionFactory)framework.getResource("sessionFactory");    	
			session = sf.openSession() ;
			
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Command-line utility for testing ukash or credit card payment") ;
			System.out.print("Enter the subscriber phone number: ") ;
		    String phone = bufferRead.readLine();

			/* look up subscriber by phone number */
		    Subscriber sub = null ;
			Iterator<Subscriber> itSub = session.createCriteria(Subscriber.class) 
					.add( Restrictions.ne("disabledFlag", 'T'))
			        .createCriteria("subAuthAnis") 
			        	.add(Restrictions.eq("phoneNumber", phone)) 
			        	.add(Restrictions.eq("status", "0")) 
			        .list()
			        .iterator() ;
			
			
			while( itSub.hasNext() ) {
				Subscriber s = itSub.next() ;
				ServiceProvider sp = (ServiceProvider) session.load(ServiceProvider.class, s.getServiceProviderId() ) ;
				if( 'T' == sp.getDisabledFlag() || 'T' == sp.getDeletedFlag() ) {
					continue ;
				}
				
				if( null == sub ) {
					sub = s ;
				}
				else {
					System.out.println("Subscriber phone number " + phone + " is associated to multiple subscribers") ;
					return  ;					
				}
			}
			if( null == sub ) {
				System.out.println("Subscriber phone number not found: " + phone) ;
				return ;				
			}

			ServiceProvider sp = (ServiceProvider) session.load(ServiceProvider.class, sub.getServiceProviderId() ) ;
			
			/* check whether the subscriber needs to be unsuspended on the M6 */
			SuspendTracking st = (SuspendTracking) session.createCriteria(SuspendTracking.class)
					.add(Restrictions.eq("subscriberId", sub.getSubscriberId() ) )
					.add(Restrictions.isNull("unsuspendedOn") )
					.uniqueResult() ;
			if( null == st ) {
				System.out.println("Found subscriber. (Note: this subscriber is NOT currently suspended on the M6).") ;
			}
			else {
				bSuspended = true ;
				
				Calendar now = Calendar.getInstance();
				Calendar then = Calendar.getInstance() ;
				then.setTime( st.getSuspendedOn() ) ;
				Long daysInArrears = (now.getTimeInMillis() - then.getTimeInMillis())/(1000*60*60*24);
				System.out.println("Found subscriber.  Subscriber was suspended on: " + sdf.format( st.getSuspendedOn() ) + " which was " + daysInArrears + " days ago." ) ;
			}
			System.out.println("Subscriber's current prepaid balance is now: $" + fmt.format(sub.getCurrPrepaidBalance().doubleValue() ) ) ;
			
			System.out.println() ;
			System.out.print("Enter 1 to recharge by ukash voucher; enter 2 to recharge by a credit card: ") ;
			String type = bufferRead.readLine() ;
			if( "1".equalsIgnoreCase(type ) ) {
				System.out.print("Enter the ukash voucher number: ") ;
			    String voucher = bufferRead.readLine();
				System.out.print("Enter the amount of the voucher: ") ;
			    String payment = bufferRead.readLine();
			    Float fPayment = Float.valueOf( payment ) ;
			    
				StringBuffer transactionCode = new StringBuffer() ;
				StringBuffer transactionDesc = new StringBuffer() ;
				MutableFloat settlementAmount = new MutableFloat(0f) ;
				MutableInteger errorCode = new MutableInteger(0) ;
				StringBuffer errorDescription  = new StringBuffer() ;
				StringBuffer ukashTransactionId = new StringBuffer() ; 
				StringBuffer msg = new StringBuffer() ;
								
				System.out.print("Enter a userid to use for the ukash transaction (can be anything): ") ;
			    String userId = bufferRead.readLine();
				System.out.print("Enter a clientid to use for the ukash transaction (can be anything): ") ;
			    String clientId = bufferRead.readLine();
				System.out.print("Enter a transaction id to use for the ukash transaction (must be unique against all previous transactions): ") ;
			    String transactionId = bufferRead.readLine();
				System.out.print("Enter 1 to bypass sending the ukash transaction and assume success, any other key to send the ukash transaction: ") ;
			    String bypass = bufferRead.readLine();

			    try {
			    	if( "1".equalsIgnoreCase(bypass) ) Dao.bypassRechargeTransaction = true ;
			    	int rc = Dao.processRechargeTransactionUkash(phone, sp.getName(), userId, clientId, voucher, fPayment.doubleValue(), 
			    			transactionId, transactionCode, transactionDesc, 
			    			settlementAmount, errorCode, errorDescription, ukashTransactionId, msg) ;
			    	if( Dao.WS_FAILURE == rc ) {
			    		System.out.println("Web service failure: " + msg.toString() ) ;
			    		return ;
			    	}
			    	else if( Dao.DB_ERROR == rc ) {
			    		System.out.println("Database failure: " + msg.toString() ) ;
			    		return ;			    		
			    	}
			    	else if( Dao.OTHER_ERROR == rc ) {
			    		System.out.println("Exception: " + msg.toString() ) ;
			    		return ;			    		
			    	}
			    } catch( Exception e ) {
			    	e.printStackTrace() ;
			    	System.out.println("error message: " + msg.toString() ) ;
			    	return  ;
			    }
			    
			    System.out.println("Transaction completed, results from ukash as follows:") ;
			    System.out.println("transaction code: " + transactionCode.toString()) ;
			    System.out.println("transaction description: " + transactionDesc.toString()) ;
			    System.out.println("settlement amount: " + settlementAmount.floatValue()) ;
			    System.out.println("error code: " + errorCode.getInteger()) ;
			    System.out.println("errorDescription: " + errorDescription.toString()) ;
			    System.out.println("ukash transaction id: " + ukashTransactionId.toString() ) ;
				    				    
				System.out.println("Account has been updated") ;
			
			}
			else if( "2".equalsIgnoreCase(type) ) {
				System.out.print("Enter a userid to use for the ukash transaction (can be anything): ") ;
			    String userId = bufferRead.readLine();
				System.out.print("Enter a clientid to use for the ukash transaction (can be anything): ") ;
			    String clientId = bufferRead.readLine();
			    System.out.print("Enter the card type: ") ;
			    String cardType = bufferRead.readLine();
			    System.out.print("Enter the card number: ") ;
			    String cardNumber = bufferRead.readLine();
			    System.out.print("Enter the expiryDate: ") ;
			    String expiryDate = bufferRead.readLine();
			    System.out.print("Enter the amount: ") ;
			    String payment = bufferRead.readLine();
			    Float fPayment = Float.valueOf( payment ) ;
			    System.out.print("Enter the cardholder name: ") ;
			    String cardHolderName = bufferRead.readLine();
			    System.out.print("Enter address1: ") ;
			    String address1 = bufferRead.readLine();
			    System.out.print("Enter address2: ") ;
			    String address2 = bufferRead.readLine();
			    System.out.print("Enter the city: ") ;
			    String city = bufferRead.readLine();
			    System.out.print("Enter the province: ") ;
			    String province = bufferRead.readLine();
			    System.out.print("Enter the postal code: ") ;
			    String postalCode = bufferRead.readLine();
			    System.out.print("Enter the transaction id (must be unique): ") ;
			    String transactionId = bufferRead.readLine();
				System.out.print("Enter 1 to bypass sending the credit card transaction and assume success, any other key to send the transaction: ") ;
			    String bypass = bufferRead.readLine();

				StringBuffer avsCode = new StringBuffer() ;
				StringBuffer avsDescription = new StringBuffer() ;
				StringBuffer inquiryId = new StringBuffer() ;
				StringBuffer authorizationCode = new StringBuffer() ;
				StringBuffer transactionDesc = new StringBuffer() ;
				MutableInteger resultCode = new MutableInteger(0) ;
				StringBuffer resultDescription  = new StringBuffer() ;
				StringBuffer ukashTransactionId = new StringBuffer() ; 
				StringBuffer msg = new StringBuffer() ;

				try {
			    	if( "1".equalsIgnoreCase(bypass) ) Dao.bypassRechargeTransaction = true ;
					int rc = Dao.processRechargeTransactionCC(phone, sp.getName(), userId, clientId, cardType, cardNumber, expiryDate, fPayment.doubleValue(), 
							cardHolderName, address1, address2, city, province, postalCode, 
							transactionId, authorizationCode, resultCode, resultDescription, avsCode, avsDescription, inquiryId, msg) ;
			    	if( Dao.WS_FAILURE == rc ) {
			    		System.out.println("Web service failure: " + msg.toString() ) ;
			    		return ;
			    	}
			    	else if( Dao.DB_ERROR == rc ) {
			    		System.out.println("Database failure: " + msg.toString() ) ;
			    		return ;			    		
			    	}
			    	else if( Dao.OTHER_ERROR == rc ) {
			    		System.out.println("Exception: " + msg.toString() ) ;
			    		return ;			    		
			    	}
			    } catch( Exception e ) {
			    	e.printStackTrace() ;
			    	System.out.println("error message: " + msg.toString() ) ;
			    	return  ;
			    }
			    System.out.println("Transaction completed, results as follows:") ;
			    System.out.println("authorization code: " + authorizationCode.toString()) ;
			    System.out.println("inquiry id: " + inquiryId.toString()) ;
			    System.out.println("result code: " + resultCode.getInteger()) ;
			    System.out.println("result description: " + resultDescription.toString()) ;
			    System.out.println("avs code: " + avsCode.toString()) ;
			    System.out.println("avs description: " + avsDescription.toString()) ;
				    				    
				System.out.println("Account has been updated") ;

			}
			else {
				System.out.println("invalid choice") ;
				return ;
			}
			
		    if( bSuspended ) {
		    	/* check whether the subscriber is now unsuspended */
				st = (SuspendTracking) session.createCriteria(SuspendTracking.class)
						.add(Restrictions.eq("subscriberId", sub.getSubscriberId() ) )
						.add(Restrictions.isNull("unsuspendedOn") )
						.uniqueResult() ;

				System.out.println();
		    	if( null == st ) {
		    		System.out.println("Subscriber has been unsuspended on the M6") ;
		    	}
		    	else {
		    		System.out.println("Subscriber is still suspended on the M6") ;				    		
		    	}		    	
		    }

		    
		} catch (SOAPFaultException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}		
	}
	static protected boolean parseCommandLine( String[] args ) {
		boolean processingSleep = false ;
		boolean processingMailTest = false ;
		
		
       	for( String s : args ) {
       		s = s.trim() ;
       		if( processingSleep ) {
				sleep = Integer.valueOf(s) ;
				processingSleep = false; 
			}
       		else if( processingMailTest ) {
       			emailAddress = s ;
       			processingMailTest = false ;
       		}
			else if( s.equalsIgnoreCase("-sleep")) {
				processingSleep = true ;
			}
			else if( s.equalsIgnoreCase("-emailTest") ) {
				processingMailTest = true ;
			}
		}
		return true ;
	}

}
