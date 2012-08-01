package com.beachdog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.sipdev.commons.mutables.MutableInt;
import org.sipdev.framework.Framework;
import org.sipdev.framework.Log;

import com.beachdog.primusCore.Constants;
import com.beachdog.primusCore.PactolusPOSConstants;
import com.beachdog.primusCore.Utilities;
import com.beachdog.primusCore.model.Lot;
import com.beachdog.primusCore.model.PreActivatedSubscribers;
import com.beachdog.primusCore.model.Subscriber;

public class App {

	protected File outFile = null ;
	protected BufferedWriter writer = null ;
	protected Framework framework = null ;
	protected Log logger = null ;
	protected SessionFactory sessionFactory = null ;
	protected Transaction transaction = null ;
	protected Session session = null ;
	protected Timestamp startDate = null ;
	protected Timestamp endDate = null ;
	
	protected Map<String,Lot> lotMap = new HashMap<String,Lot>() ;
	
	public static void main(String[] args) {
		
		App a = new App() ;
		a.run( args ) ;
	}
	
	public void run( String[] args ) {
		
		try {
			if( !parseCommandLine( args ) ) {
				usage() ;
				return ;
			}

			framework = Framework.getInstance() ;
			logger = framework.getLogger() ;
			logger.debug("starting pos reporting") ;
			
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
			
			logger.debug("Start date: " + sdf.format(startDate) ) ;
			logger.debug("End date: " + sdf.format(endDate) ) ;
			logger.debug("Output file: " + outFile.getAbsolutePath()  ) ;
			System.out.println("Start date: " + sdf.format(startDate) ) ;
			System.out.println("End date: " + sdf.format(endDate) ) ;
			System.out.println("Output file: " + outFile.getAbsolutePath()  ) ;
				
			sessionFactory = (SessionFactory) framework.getResource("sessionFactory") ;
			
			session = sessionFactory.getCurrentSession() ;
			transaction = session.beginTransaction() ;
			
			Query query = session.getNamedQuery("posQuery2") ;
			List<Object> results = query
					.setParameter("start", startDate)
					.setParameter("end", endDate)
					.list(); 
			
			int nRecords = 0 ;
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yy") ;
			DecimalFormat df = new DecimalFormat("#####.00") ;
			for( Iterator<Object> it = results.iterator(); it.hasNext(); ) {
				
				Object[] row = (Object[]) it.next() ;
				BigDecimal id = null; //pos_event_id
				String userName = null;   
				BigDecimal offeringId = null; 
				BigDecimal amount  = null; 
				String serialNumber = null ;
				Date timeStamp  = null ;
				String transactionCode = null ;
				String custTransactionCode = null ;
				String interfaceCode  = null;
				String responseCode = null ;
				String custData1 = null;
				String custData2  = null ;
				BigDecimal subscriberId = null  ;
				String activityId = null ;
				BigDecimal currencyId = null ;
				try {
					id = (BigDecimal) row[0] ; //pos_event_id
					userName = null != row[1] ? (String) row[1] : null ;   
					offeringId = (BigDecimal) row[2] ; 
					amount = row[3] != null ? (BigDecimal) row[3] : null ; 
					serialNumber = (String) row[4] ;
					timeStamp = (Date) row[5] ;
					transactionCode = null != row[6] ? (String) row[6] : null ;
					custTransactionCode = null != row[7] ? (String) row[7] : null ;
					interfaceCode = null != row[8] ? (String) row[8] : null ;
					responseCode = null != row[9] ? (String) row[9] : null ;
					custData1 = null != row[10] ? (String) row[10] : null ;
					custData2 = null != row[11] ? (String) row[11] : null ;
					subscriberId = null != row[12] ? (BigDecimal) row[12] : null ;
					activityId = null != row[13] ? (String) row[13] : null ;
					currencyId = null != row[14] ? (BigDecimal) row[14] : null ;
				} catch( ClassCastException e ) {
					e.printStackTrace(); 
					System.out.println("Exception reading data, row retrieved was as follows:") ;
					for( int i = 0; i < row.length; i++ ) {
						System.out.println("col #" + (i+1)  + ": " + (null == row[i] ? "null" : row[i].toString() ) ) ;
					}
					continue ;
					
				}
				String paymentMethod = "" ;
				
				boolean bProcessRecord = true ;
				Integer txnCode = Integer.parseInt(transactionCode) ;
				switch( txnCode ) {
				case PactolusPOSConstants.TRANS_CODE_ACTIVATE:
					break ;
				
				case PactolusPOSConstants.TRANS_CODE_DEACTIVATE:
					amount = getSubscriberBalance(subscriberId, serialNumber) ;
					if( null == amount ) {
						logger.warn("Unable to find subscriber or preactivated subscriber associated with pos_event_id " + id + "; serial number was " + serialNumber) ;
						bProcessRecord = false ;
					}
					else amount = amount.negate() ;
					break ;
					
				case PactolusPOSConstants.TRANS_CODE_RECHARGE:
					if( "U".equalsIgnoreCase(custData2) ) paymentMethod = Constants.UKASH_PAYMENT ;
					else if( "P".equalsIgnoreCase("P") ) paymentMethod = Constants.PAYMENTECH_PAYMENT ;
					break ;

				case PactolusPOSConstants.TRANS_CODE_STATUS_REQ:
					logger.info("Not including evt_point_of_sale record because it is a status request: " + id ) ;
					bProcessRecord = false ;
					break ;
				
				default:
					logger.error("Invalid transaction code: " + transactionCode + " for pos_event_id " + id ) ;
					bProcessRecord = false ;
					break ;
				}

				if( bProcessRecord ) {
					nRecords++ ;
						
					if( null != id ) writer.write(id.toString()) ;
					writer.write("|") ;
					if( null != userName && !("null".equalsIgnoreCase(userName))) writer.write(userName) ;
					writer.write("|") ;
					if( null != offeringId ) writer.write(offeringId.toString()) ;
					writer.write("|") ;
					if( null != serialNumber) writer.write(serialNumber) ;
					writer.write("|") ;
					if( null != timeStamp ) writer.write(sdf2.format(timeStamp).toUpperCase()) ;
					writer.write("|") ;
					writer.write("1") ;
					writer.write("|") ;
					if( null != amount ) writer.write(df.format(amount)) ;
					writer.write("|") ;
					writer.write(paymentMethod) ;
					writer.write("|") ;
					
					char c = 0x0A ;
					writer.write( 0x0a ) ;
				}
			}
			
			System.out.println( " " + nRecords + " were written to the output file") ;
			logger.debug(" " + nRecords + " were written to the output file") ;
						
		} catch( Exception e ) {
			e.printStackTrace() ;
			if( null != transaction ) transaction.rollback() ;
		}
		finally {
			if( null != writer )
				try {
					writer.close() ;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	protected Lot getLotFromControlNumber( String lotControlNumber ) {
		
		if( lotMap.containsKey(lotControlNumber))
			return lotMap.get(lotControlNumber) ;
		
		Lot lot = (Lot) session.createCriteria(Lot.class)
				.add(Restrictions.eq("lotControlNumber", lotControlNumber))
				.uniqueResult() ;
		if( null != lot ) {
			lotMap.put(lotControlNumber, lot) ;
			return lot ;
		}
		return null ;
	}
	
	protected BigDecimal getSubscriberBalance( BigDecimal subscriberId, String serialNumber ) throws Exception {
		assert( null != subscriberId || null != serialNumber ) ;
		
		if( null != subscriberId ) {
			Subscriber sub = (Subscriber) session.get(Subscriber.class, subscriberId) ;
			if( null == sub ) {
				
				logger.warn("Unable to find subscriber with id: " + subscriberId) ;
				return null ;
			}
			
			return sub.getCurrPrepaidBalance() ;
		}
		
		/* look up by serial number */
		StringBuffer lotControlNumber = new StringBuffer() ;
		StringBuffer lotSequenceNumber = new StringBuffer() ;
		Utilities.parseSubSerialNumber(serialNumber, lotControlNumber, lotSequenceNumber) ;
		
		/* Note: the card may still be in the subscriber table, even though there is no subscriber_id.  Consider the following sequence:
		 * (1) Card is activated
		 * (2) Card is deactivated
		 * (3) Card is activated again
		 * (4) Card is used
		 * 
		 * At the end of that sequence, we have a deactivation record in evt_point_of_sale with null subscriber id (resulting from step 2), 
		 * no associated record in pre_authorized_subscribers table (since the card was used), and a record in the subscriber table.
		 * 
		 * Thus, we search for serial number first in subscribers table, then in pre_activated_subscribers table
		 */
		Lot lot = getLotFromControlNumber(lotControlNumber.toString()) ;
		
		if( null == lot ) {
			logger.error("Unknown lot control number: " + lotControlNumber.toString() ) ;
			return null ;
		}
		Subscriber sub = (Subscriber) session.createCriteria(Subscriber.class)
				.add(Restrictions.eq("lotId", lot.getLotId()))
				.add(Restrictions.eq("lotSeqNumber", new BigDecimal( lotSequenceNumber.toString())))
				.uniqueResult(); 
		if( null != sub ) {
			return lot.getInitialBalance() ;
		}
				
		PreActivatedSubscribers pre = (PreActivatedSubscribers) session.createCriteria(PreActivatedSubscribers.class)
				.add(Restrictions.eq("lotControlNumber", lotControlNumber.toString()))
				.add(Restrictions.eq("lotSeqNumber", new BigDecimal( lotSequenceNumber.toString())))
				.uniqueResult(); 
		
		if( null != pre ) return pre.getLot().getInitialBalance() ;
		
		logger.warn("Unable to find preactivated sub with lot control number: " + lotControlNumber + " and sequence number " + lotSequenceNumber + "; serial number was " + serialNumber) ;
		return null ;
		
		
	}
	
	protected boolean parseCommandLine( String[] args ) {
		boolean processingStart = false ;
		boolean processingEnd = false ;
		boolean processingDirectory = false ;
		
      	java.util.Date now = new java.util.Date() ;
      	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
      	
       	for( String s : args ) {
			if( processingStart ) {
				if( null == (startDate = parseDate( s ) ) ) {
					System.out.println("Invalid date: " + s ) ;
					return false ;
				}
				processingStart = false ;
			}
			else if( processingEnd ) {
				if( null == (endDate = parseDate( s ) ) ) {
					System.out.println("Invalid date: " + s ) ;
					return false ;	
				}
				processingEnd = false ;
			}
			else if( processingDirectory ) {
      			File dir = new File(s) ;
      			if( !dir.exists() || !dir.isDirectory() ) {
      				logger.error("'" + s + "' either does not exist, or is not a directory") ;
      				System.out.println("'" + s + "' either does not exist, or is not a directory") ;
      				return false ;
      			}
       			SimpleDateFormat sdf2 = new SimpleDateFormat("HHmm") ;
      			
  				outFile = new File( dir, new String("dailyPos-" + sdf.format(now) + "-" + sdf2.format(now) + ".txt") ) ;
   				try {
					writer = new BufferedWriter(new FileWriter( outFile ) );
				} catch (IOException e) {
					logger.error("Error opening output file: " + e.getLocalizedMessage()) ;
					System.out.println("Error opening output file: " + e.getLocalizedMessage()) ;
					e.printStackTrace();
					return false ;
				}
     			
     			processingDirectory = false ;
				
			}
			else if( s.equalsIgnoreCase("--start")) {
				processingStart = true ;
			}
			else if( s.equalsIgnoreCase("--end")) {
				processingEnd = true ;
			}
			else if( s.equalsIgnoreCase("--dir")) {
				processingDirectory = true ;
			}
		}
       	
       	if( null == outFile ) {
       		System.out.println("--dir parameter is required") ;
       		return false ;
       	}
       	if( null == startDate ) {
       		System.out.println("--start is a required parameter") ;
       		return false ;
       	}
       	if( null == startDate ) {
       		System.out.println("--start is a required parameter") ;
       		return false ;
       	}
		
		return true ;
	}
	public Timestamp parseDate( String s ) {
		Timestamp dtReturn = null ;
      	java.util.Date now = new java.util.Date() ;
      	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
	
		if( 0 == s.indexOf('-') || "0".equalsIgnoreCase(s)) {
			/* subtract number of days from today */
			Integer n = Integer.valueOf(s) ;
			if( null == n || n > 0 ) {
				logger.error("Invalid 'start' param value: " + s ) ;
				return null ;
			}
			dtReturn = new Timestamp( extendDate( now, 0, n ).getTime() ) ;
		}
		else {
			java.util.Date dt = null ;
			try {
				dt = sdf.parse(s) ;
			} catch (ParseException e) {
				logger.error("Invalid 'start' param value: " + s ) ;
				return null ;
			}
			dtReturn = new Timestamp( dt.getTime() ) ;
		}

		GregorianCalendar cal = new GregorianCalendar() ;
		cal.setTime( dtReturn ) ;
		cal.set(Calendar.HOUR_OF_DAY, 0) ;
		cal.set(Calendar.MINUTE, 0) ;
		cal.set(Calendar.SECOND, 0) ;
		cal.set(Calendar.MILLISECOND, 0) ;
		

		return new Timestamp( cal.getTime().getTime() ) ;
	}
	public java.util.Date extendDate( java.util.Date dtStart, int nMonthsToExtend, int nDaysToExtend ) {
		java.util.Date dtExtend = null ;
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd") ;
		
		/* special logic: drop people from the list if their resupply is in the next month */
		GregorianCalendar cal = new GregorianCalendar() ;
		cal.setTime( dtStart ) ;
		cal.add(Calendar.MONTH, nMonthsToExtend) ;
		cal.add(Calendar.DAY_OF_MONTH, nDaysToExtend ) ;
		
		dtExtend = cal.getTime();
		
		return dtExtend ;
	}

	protected void usage() {
		System.out.println("Usage: `basename $0` --start startValue --end endValue --dir path ") ;
		System.out.println("       where:") ;
		System.out.println("       startValue - a start date in yyyy-mm-dd format, or else a negative integer representing a nuber of days in the past") ;
		System.out.println("       endValue   - an end date in yyyy-mm-dd format, or else a non-positive integer representing a number of days in the past") ;
		System.out.println("       path   - a directory on the filesystem where the report should be written do") ;
		System.out.println() ;
		System.out.println("       note: the date range selected for the report will be from the start date to the end date, inclusive of the start date but NOT inclusive of the end date") ;
		System.out.println() ;
		System.out.println("   examples:") ;
		System.out.println() ;
		System.out.println(" --start -21 --end 0                        #runs report starting 21 days ago through yesterday") ;
		System.out.println(" --start -1 --end 0                         #runs report for yesterday") ;
		System.out.println(" --start 2012-01-01 --end 2012-02-01        #runs report for month of January 2012") ;
	}
}
