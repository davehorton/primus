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
			framework = Framework.getInstance("beans.xml") ;
			Config cfg = (Config) framework.getResource("wsConfig") ;
			SessionFactory sf = (SessionFactory)framework.getResource("sessionFactory");    	
			session = sf.openSession() ;

			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Command-line utility for testing ukash payment") ;
			System.out.print("Enter the subscriber phone number: ") ;
		    String phone = bufferRead.readLine();

			List<Subscriber> list = session.createCriteria(Subscriber.class)
					.createCriteria("subAuthAnis")
						.add(Restrictions.eq("phoneNumber", phone))
						.add(Restrictions.eq("status", "0") )
					.list() ;
			if( list.isEmpty() ) {
				System.out.println("No subscriber with that phone number was found") ;
				return  ;
			}
			else if( list.size() > 1 ) {
				System.out.println("There is more than one subscriber with that phone number") ;
				return ;
			}
			Subscriber sub = list.get(0) ;
			/* check whether the subscriber needs to be unsuspended on the M6 */
			SuspendTracking st = (SuspendTracking) session.createCriteria(SuspendTracking.class)
					.add(Restrictions.eq("subscriberId", sub.getSubscriberId() ) )
					.add(Restrictions.isNull("unsuspendedOn") )
					.uniqueResult() ;
			if( null == st ) {
				System.out.println("This subscriber is not suspended on the M6") ;
			}
			else {
				DecimalFormat fmt = new DecimalFormat("#####.00") ;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
				System.out.println("This subscriber is currently suspended on the M6 due to non-payment of maintenance fee on " + sdf.format(st.getSuspendedOn() ) ) ;
				
				Calendar now = Calendar.getInstance();
				Calendar then = Calendar.getInstance() ;
				then.setTime( st.getSuspendedOn() ) ;
				Long daysInArrears = (now.getTimeInMillis() - then.getTimeInMillis())/(1000*60*60*24);
				System.out.println("Subscriber was suspended on: " + sdf.format( st.getSuspendedOn() ) + " which was " + daysInArrears + " ago." ) ;
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

				
			}
			System.out.print("Enter the ukash voucher number, or none if you want to bypass sending the transaction to ukash: ") ;
		    String voucher = bufferRead.readLine();
			System.out.print("Enter the amount of the payment: ") ;
		    String payment = bufferRead.readLine();
		    Float fPayment = Float.valueOf( payment ) ;
			System.out.print("Enter the ip address of the M6: ") ;
		    String M6Address = bufferRead.readLine();
			System.out.print("Enter the username for the M6: ") ;
		    String M6User = bufferRead.readLine();
			System.out.print("Enter the password for the M6: ") ;
		    String M6Password = bufferRead.readLine();
		    
		    Transaction transaction = null ;
		    if( null != voucher && voucher.length() > 0 ) {
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
				System.out.print("Enter a transaction id to use for the ukash transaction (must be unique): ") ;
			    String transactionId = bufferRead.readLine();

			    try {
			    	transaction = session.beginTransaction() ;
			    	Dao.processRechargeTransactionUkash(phone, sp.getName(), userId, clientId, voucher, fPayment.doubleValue(), 
			    			transactionId, transactionCode, transactionDesc, 
			    			settlementAmount, errorCode, errorDescription, ukashTransactionId, msg) ;
			    	transaction.commit() ;
			    } catch( Exception e ) {
			    	e.printStackTrace() ;
			    	if( null != transaction ) transaction.rollback() ;
			    	return ;
			    }
			    
			    System.out.println("Transaction completed, results from ukash as follows:") ;
			    System.out.println("transaction code: " + transactionCode.toString()) ;
			    System.out.println("transaction description: " + transactionDesc.toString()) ;
			    System.out.println("settlement amount: " + settlementAmount.floatValue()) ;
			    System.out.println("error code: " + errorCode.getInt()) ;
			    System.out.println("errorDescription: " + errorDescription.toString()) ;
			    System.out.println("ukash transaction id: " + ukashTransactionId.toString() ) ;
			    
		    }
		    else {
		    	try {
			    	transaction = session.beginTransaction() ;
		    		Dao.updateSubscriberWithSuccessfulRecharge(session, sub, fPayment, M6Address, M6User, M6Password, phone ) ;	
			    	transaction.commit() ;
		    	} catch( Exception e ) {
		    		e.printStackTrace() ;
			    	if( null != transaction ) transaction.rollback() ;
		    		return ;
		    	}
		    }
			System.out.println("Account has been updated") ;
			
			
		    
			//ukashResult = pws.prepaidUkashPayment("userId", "clientId", businessUnit, "5083084809", voucher, 50.0f, "CAD", txnId) ;
		} catch (SOAPFaultException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("ukash result: " + ukashResult.getErrorcode() ) ;
		
	}

}
