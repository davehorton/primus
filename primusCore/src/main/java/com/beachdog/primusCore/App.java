package com.beachdog.primusCore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.sipdev.commons.mutables.MutableFloat;
import org.sipdev.commons.mutables.MutableInt;
import org.sipdev.framework.Framework;

import com.beachdog.primusCore.model.AccountActivity;
import com.beachdog.primusCore.model.Rate;
import com.beachdog.primusCore.model.ServiceProvider;
import com.beachdog.primusCore.model.Subscriber;
import com.beachdog.primusCore.model.SuspendTracking;

public class App {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Framework framework = null ;
		Session session = null ;

		try {
			Boolean bSuspended = false ;
			DecimalFormat fmt = new DecimalFormat("#####.00") ;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;

			framework = Framework.getInstance("beans.xml") ;
			Config cfg = (Config) framework.getResource("wsConfig") ;
			SessionFactory sf = (SessionFactory)framework.getResource("sessionFactory");    	
			session = sf.openSession() ;
			
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Command-line utility for testing ukash or credit card payment") ;
			System.out.print("Enter the subscriber phone number: ") ;
		    String phone = bufferRead.readLine();

			List<Subscriber> list = session.createCriteria(Subscriber.class)
					.createCriteria("subAuthAnis")
						.add(Restrictions.eq("phoneNumber", phone))
						.add(Restrictions.eq("status", "0") )
					.list() ;
			if( list.isEmpty() ) {
				System.out.println("No subscriber with that phone number was found in the database") ;
				return  ;
			}
			else if( list.size() > 1 ) {
				System.out.println("There is more than one subscriber with that phone number in the database") ;
				return ;
			}
			Subscriber sub = list.get(0) ;
			
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
				/*
				AccountActivity aa = (AccountActivity) session.load(AccountActivity.class, st.getAaActivityId() ); 
				if( null != aa ) {
					BigDecimal owedMaintFees = BigDecimal.ZERO ;
					Rate rate = (Rate) session.load(Rate.class, aa.getRateId1() ) ;
					if( daysInArrears > 1 ) {
						owedMaintFees = rate.getDefaultAmount().multiply( BigDecimal.valueOf(daysInArrears-1) ).divide( rate.getMaintFeeFrequency() ) ;
						System.out.println("Charging $" + fmt.format( owedMaintFees.doubleValue() ) + " for " + (daysInArrears-1) + " days of missed maintenance fees") ;
						System.out.println("Note: this maintenance fee has a frequency (interval) of " + rate.getMaintFeeFrequency().longValue() + 
								" days and a fee of $" + fmt.format(rate.getDefaultAmount() ) ) ;
					}
					if( daysInArrears > 0 ) {
						System.out.println("activity id: " + aa.getActivityId() + ", rate id: " + rate.getRateId() ) ;
						double rateAmount = rate.getDefaultAmount().doubleValue() ; 
						double chargedAmount = aa.getTotalAmount().doubleValue() ;
						double rateAmountAcharged = aa.getRateAmt1().doubleValue() ;
						BigDecimal partialMissedFee = rate.getDefaultAmount().add( aa.getTotalAmount() ) ;
						System.out.println("Charging $" + fmt.format( partialMissedFee.doubleValue() )  + " for the partial maintenance fee collected that resulted in suspension") ;
						owedMaintFees.add( partialMissedFee ) ;
					}
					System.out.println("Total payment in arrears to be collected is calculated as " + fmt.format( owedMaintFees.doubleValue())) ;
					if( owedMaintFees.compareTo(BigDecimal.valueOf(15)) > 0 ) {
						System.out.println("The payment in arrears exceeds $15, so only $15 will be collected") ;
						owedMaintFees = BigDecimal.valueOf(15) ;
					}
	 			}	
	 			*/			
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
			    
			    Transaction transaction = null ;
				StringBuffer transactionCode = new StringBuffer() ;
				StringBuffer transactionDesc = new StringBuffer() ;
				MutableFloat settlementAmount = new MutableFloat(0f) ;
				MutableInt errorCode = new MutableInt(0) ;
				StringBuffer errorDescription  = new StringBuffer() ;
				StringBuffer ukashTransactionId = new StringBuffer() ; 
				StringBuffer msg = new StringBuffer() ;
				
				ServiceProvider sp = (ServiceProvider) session.load(ServiceProvider.class, sub.getServiceProviderId() ) ;
				
				System.out.print("Enter a userid to use for the ukash transaction (can be anything): ") ;
			    String userId = bufferRead.readLine();
				System.out.print("Enter a clientid to use for the ukash transaction (can be anything): ") ;
			    String clientId = bufferRead.readLine();
				System.out.print("Enter a transaction id to use for the ukash transaction (must be unique against all previous transactions): ") ;
			    String transactionId = bufferRead.readLine();

			    try {
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
			    	if( null != transaction ) transaction.rollback() ;
			    	return  ;
			    }
			    
			    System.out.println("Transaction completed, results from ukash as follows:") ;
			    System.out.println("transaction code: " + transactionCode.toString()) ;
			    System.out.println("transaction description: " + transactionDesc.toString()) ;
			    System.out.println("settlement amount: " + settlementAmount.floatValue()) ;
			    System.out.println("error code: " + errorCode.getInt()) ;
			    System.out.println("errorDescription: " + errorDescription.toString()) ;
			    System.out.println("ukash transaction id: " + ukashTransactionId.toString() ) ;
				    				    
				System.out.println("Account has been updated") ;
			
			}
			else if( "2".equalsIgnoreCase(type) ) {
				//TODO: credit card recharge
				System.out.println("sorry not implemented yet") ;
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

			Subscriber subNow = (Subscriber) session.createCriteria(Subscriber.class)
					.createCriteria("subAuthAnis")
						.add(Restrictions.eq("phoneNumber", phone))
						.add(Restrictions.eq("status", "0") )
					.uniqueResult() ;
			System.out.println("Subscriber's prepaid balance is now: $" + fmt.format(subNow.getCurrPrepaidBalance().doubleValue() ) ) ;

		    
			//ukashResult = pws.prepaidUkashPayment("userId", "clientId", businessUnit, "5083084809", voucher, 50.0f, "CAD", txnId) ;
		} catch (SOAPFaultException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("ukash result: " + ukashResult.getErrorcode() ) ;
		
	}

}
