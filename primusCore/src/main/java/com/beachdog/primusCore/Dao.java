package com.beachdog.primusCore;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.acl.Owner;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.sipdev.framework.Framework;

import com.pactolus.java.*;

import uri.ecare.PrepaidPaymentechPaymentDlResponse.ECarePrepaidPaymentechResponse;
import uri.ecare.PrepaidUkashPaymentDlResponse.ECarePrepaidUkashResponse;

import com.beachdog.primusCore.model.*;
import com.vocaldata.AdminSOAP.DBSOAPException;
import com.vocaldata.AdminSOAP.UserKeys;

public class Dao {
	public static int POS_RECHARGE_EVENT = 15 ;
	public static int POS_REFUND_EVENT = 16 ;
	public static int EXTERNAL_PAYMENT_EVENT = 19;
	public static int EXTERNAL_BALANCE_ADJ_EVENT = 20;
    public static int BALANCE_XFER_EVENT = 5;
    public static int BAL_ADJUSTMENT_EVENT = 7;
	public static int CREDIT_CARD_REFUND_EVENT = 30;
	public static int CREDIT_CARD_CHARGE_EVENT = 29 ;
	
	/* event type ids from available_event */
	public static int POS_RECHARGE_EVENT_TYPE_ID = 15 ;
	
	/* audit event types */
    public static int SUBSCRIBER_RECHARGE_ONE_TIME_AUDIT = 12;
    public static int PRPD_SUBSCRIBER_TO_BALANCE_XFER_AUDIT = 38;
    
    /* predefined user ids */
    private static long PROVISIONING_API_USER_ID = 3;

    /* error codes for activation operation */
	public static final int LOT_NOTFOUND = -1 ;
	public static final int LOT_INVALID_STATE = -2 ;
	public static final int LOT_EXHAUSTED = -3 ;
	public static final int LOT_EXPIRED = -4 ;
	public static final int PHONE_NBR_IN_USE = -5 ;
	
	/* error codes for credit card transaction */
	public static final int BAD_CC_TYPE = -1 ;
	public static final int BAD_CC_NUMBER = -2 ;
	public static final int UNKNOWN_SERVICE_PROVIDER = -3 ;
	public static final int UNKNOWN_PHONE_NBR = -4 ;
	public static final int SUB_DISABLED = -5 ;	
	public static final int SP_DISABLED = -6 ;	
	public static final int BAD_CC_EXPIRY = -7 ;
	public static final int CC_DECLINED = -8 ;
	public static final int SP_BUSINESS_UNIT_NOTFOUND = -9 ;
	
	
	/* general error codes */
	public static final int SUCCESS = 0;
	public static final int WS_FAILURE = -97;
	public static final int OTHER_ERROR = -98 ;
	public static final int DB_ERROR = -99 ;
	
	public static Boolean bypassRechargeTransaction = false ;
	
	protected static String sqlCreateActivationGroup = "INSERT INTO  evt_prepaid_activation " + 
			"(activation_id,total_pins,status,lot_id,start_lot_seq,end_lot_seq,initial_balance,offering_id, time_stamp, description) " + 
			"select ?, 1, 1, ?, min( pe.lot_seq_number ), min( pe.lot_seq_number ),20.0, ?, ?, ? " +
			"from pre_activated_subscribers pe " +
			"where pe.lot_id = ? " +
			"and not exists ( " + 
			"select * from evt_prepaid_activation epa " + 
			"where epa.lot_id = ? " + 
			"and  pe.lot_seq_number >= epa.start_lot_seq  and pe.lot_seq_number <= epa.end_lot_seq) "  ;
	
	protected static PreparedStatement stmtCreateActivationGroup = null ;

	protected static Logger logger = Logger.getLogger(Dao.class) ;
	


    static public Timestamp getCurrentTimestamp() {
		TimeZone tz = TimeZone.getTimeZone("GMT");
		java.util.Date date = new java.util.Date(System.currentTimeMillis());
		SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(tz);
		String formattedDate = (sdf.format(date));
		Timestamp tsNow = Timestamp.valueOf(formattedDate);
		return tsNow ;
    }
	
	static public LockedPins createLockedPin( Subscriber sub, String xtmlSessionId ) throws UnknownHostException {

		return new LockedPins(sub, getCurrentTimestamp(), xtmlSessionId, InetAddress.getLocalHost().toString()) ;
		
	}
		
	static public AccountActivity createAccountActivity( Session session, Subscriber sub, Float fAmount, int eventTypeId ) {
		String activityId = Dao.getStringOID(session, "account_activity") ;
		BigDecimal amount = new BigDecimal( fAmount ) ;
		String eventId = Dao.getStringOID(session, "event") ;
		
		AccountActivity aa = new AccountActivity(activityId, sub, amount, sub.getCurrencyId(), eventId, 'N') ;
		aa.setEventTypeId( new BigDecimal( eventTypeId ) ) ;
		aa.setServiceProvider( (ServiceProvider) session.get(ServiceProvider.class, sub.getServiceProviderId()) ) ;
		aa.setTimeDateStamp( Dao.getCurrentTimestamp() ) ;
		aa.setPostActivityBalance(sub.getCurrPrepaidBalance()) ;
		return aa ;
	}
	
	static public EvtPointOfSale createEvtPointOfSale( Session session, Subscriber sub, AccountActivity aa, CurrencyRef cr, String userName, Float fAmount, 
			int pcsTransactionCode, String custData1, String custData2 ) {
		
		String posEventId = Dao.getStringOID(session, "evt_point_of_sale") ;
		
		String serialNumber = sub.getLotControlNumber() + sub.getLotSeqNumber().toPlainString() ;
		String interfaceCode = "" ;
		BigDecimal amount = new BigDecimal( fAmount.doubleValue() ) ;
		EvtPointOfSale epos = new EvtPointOfSale(new BigDecimal(posEventId), serialNumber, String.valueOf(pcsTransactionCode), String.valueOf(pcsTransactionCode), 
				interfaceCode, getCurrentTimestamp(), PactolusPOSConstants.RESP_CODE_SUCCESS) ;
		epos.setActivityId( aa.getActivityId() ) ;
		epos.setCustData1(custData1) ;
		epos.setCustData2(custData2) ;
		epos.setAmount(amount) ;
		epos.setCurrencyRef(cr) ;
		epos.setSubscriberId(sub.getSubscriberId()) ;
		epos.setUserName(userName) ;
		epos.setProductOffering(  (ProductOffering) session.load(ProductOffering.class, sub.getPrimaryOfferingId()) ) ;
		epos.setInterfaceCode("IVR") ;

		return epos ;
		
	}
	
	static public PsAudit createAuditEvent( Session session, Subscriber sub, int auditEventId, 
			String data1, String data2, String data3, String data4, String data5,
			String errorCode, String errorDescription ) {
		
		Long auditId = Dao.getOID(session, "audit_event") ;
		PsAudit audit = new PsAudit(new BigDecimal(auditId), new BigDecimal(auditEventId), new BigDecimal(PROVISIONING_API_USER_ID), 'N', 
				sub.getSubscriberId(), sub.getServiceProviderId(), 
				errorCode, errorDescription, data1, data2, data3, data4, data5) ;
		
		
		return audit ;
	}
	
	static public String getStringOID( Session session, String sequence ) {
		String key = null ;
		String sql = "select to_char(" + sequence + "_seq.nextval) nextval from dual" ;
		
		key = (String) session.createSQLQuery( sql )
			.uniqueResult() ;
		
		return key ;
	}
	static public Long getOID( Session session, String sequence ) {
		Long key = null ;
		String sql = "select " + sequence + "_seq.nextval from dual" ;
		
		key = ((BigDecimal) session.createSQLQuery( sql )
			.uniqueResult()).longValue();
		
		return key ;
	}

	static public int activateSinglePinFromLot( Long lotId, String phone, Double initialBalance, 
			StringBuffer msg, StringBuffer subscriberId, StringBuffer pin ) {
		int rc = PactolusConstants.SUCCESS ;
		
		
		Session session = null ;
		Transaction transaction = null ;
		Lot lot = null ;
		Connection conn = null ;
		
		try {
			Framework framework = Framework.getInstance("beans.xml") ;
			SessionFactory sf = (SessionFactory)framework.getResource("sessionFactory");    	
			session = sf.openSession() ;
			transaction = session.beginTransaction() ;
			
			/* retrieve the lot */
			lot = (Lot) session.get(Lot.class, BigDecimal.valueOf(lotId)); 
			if( null == lot ) {
				logger.error("activateSinglePin: lot not found with id: " + lotId) ;
				msg.append("No lot with id " + lotId + " was found in the database.") ;
				return LOT_NOTFOUND ;
			}
			
			/* verify the lot is in a proper state to vend us a pin for use */
			Integer lotStatus = lot.getLotStatus().intValue() ;
			if( PactolusConstants.LOT_PROCESSED != lotStatus ) {
				logger.error("lot status is not valid for pin activation: " + lotStatus ) ;
				msg.append("Lot status value of " + lotStatus + " is not valid for pin activation"	) ;
				return LOT_INVALID_STATE ;
			}
			
			/* make sure the phone number isn't already in the database for someone else */
			Query q = session.createSQLQuery("SELECT count(*) FROM sub_auth_ani WHERE phone_number = ? AND service_provider_id = ?") ;
			q.setString(0, phone ) ;
			q.setBigDecimal(1, lot.getServiceProvider().getServiceProviderId() ) ;
			BigDecimal count = (BigDecimal) q.uniqueResult() ;
			if( count.intValue() > 0 ) {
				logger.error("phone number is already in use: " + phone ) ;
				msg.append("Phone number " + phone + " is already in use"	) ;
				return PHONE_NBR_IN_USE ;
				
			}
			//TODO: check that we haven't passed the lot unused expiration date
			
			/* ok, here we go.  First step: create an activation group consisting 
			 * consisting of a single pin that is the lowest available serial number
			 */
			Long offeringId = lot.getProductOffering().getOfferingId().longValue() ;
			Long lActivationId = createActivationGroupForSinglePin(session, lotId, offeringId, initialBalance ) ;
			if( lActivationId <= 0L ) {
				if( LOT_EXHAUSTED == lActivationId ) {
					msg.append("No available pins found in lot: " + lotId ) ;
					return LOT_EXHAUSTED ;
				}
				else if( DB_ERROR == lActivationId ) {
					msg.append("A database access error has occured") ;
					return DB_ERROR  ;
				}
				else {
					//WTF??
					msg.append("A system error has occurred") ;
					return OTHER_ERROR ;
				}
			}
			EvtPrepaidActivation activation = (EvtPrepaidActivation) session.get(EvtPrepaidActivation.class, String.valueOf(lActivationId)) ;
			if( null == activation.getStartLotSeq() ) {
				/* there were no more pins available */
				session.delete(activation) ;
				transaction.commit() ;
				msg.append("No available pins found in lot: " + lotId ) ;
				return LOT_EXHAUSTED ;				
			}
			
			
			/* ok, now we need to:
			 * (1) write an audit record
			 * (2) write a evt_prepaid_activation_log record, which references the audit record
			 * (3) update the status of the pre_activated_subscriber record to indicate that it has been activated
			 */
			
			PsAudit audit = new PsAudit() ;
			Long lAuditId = Dao.getOID(session, "audit_event") ;
			audit.setAuditId(BigDecimal.valueOf(lAuditId)) ;
			audit.setAuditEventId(BigDecimal.valueOf(PactolusConstants.SVC_PRVDR_LOT_GROUP_ACTIVATE_AUDIT)) ;
			audit.setServiceProviderId(lot.getProductOffering().getServiceProvider().getServiceProviderId()) ;
			audit.setAcdUserFlag('F') ;
			audit.setTimestamp( Utilities.getCurrentTimestamp() ) ;
			audit.setDataCol1( lot.getLotControlNumber() ) ;
			audit.setDataCol2( Long.toString(lActivationId, 10) ) ;
			audit.setDataCol3( "1" ) ;
			audit.setUserId( BigDecimal.valueOf(PactolusConstants.PROVISIONING_API_USER_ID ) );
						
			session.save( audit ) ;
			
			EvtPrepaidActivationLog log = new EvtPrepaidActivationLog() ;
			
			EvtPrepaidActivationLogId id = new EvtPrepaidActivationLogId() ;
			id.setActivationId(String.valueOf(lActivationId)) ;
			id.setAuditId(BigDecimal.valueOf(lAuditId)) ;
			log.setId(id) ;

			log.setActionCode(BigDecimal.valueOf(PactolusConstants.GROUP_ACTIVATED)) ;
			log.setDescription("Acivated single pin") ;
			log.setLotId(BigDecimal.valueOf(lotId)) ;
			log.setOfferingId(BigDecimal.valueOf(offeringId)) ;
			log.setTimeStamp(new Date( System.currentTimeMillis())) ;
			log.setTotalPins(BigDecimal.valueOf(1)) ;
			log.setTotalActivePins(1L) ;	
			log.setStartLotSeq(activation.getStartLotSeq().longValue()); 
			log.setEndLotSeq(activation.getEndLotSeq().longValue()) ;
			
			session.save( log ) ;
			
			PreActivatedSubscribers pre = (PreActivatedSubscribers) session.createCriteria(PreActivatedSubscribers.class)
				.add(Restrictions.eq("lotControlNumber", lot.getLotControlNumber()))
				.add(Restrictions.eq("lotSeqNumber", activation.getStartLotSeq()))
				.add(Restrictions.or(
						Restrictions.eq("activationFlag", 'F'), 
						Restrictions.isNull("activationFlag")))
				.createCriteria("lot")
					.add(Restrictions.idEq(BigDecimal.valueOf(lotId)))
				.uniqueResult() ;
			
			if( null == pre ) {
				logger.error("failed to update preactivated_subscribers for activation id " + lActivationId) ;
				logger.error("lotControlNumber: " + lot.getLotControlNumber() ) ;
				logger.error("lotSeqNumber: " + activation.getStartLotSeq().longValue() ) ;
				logger.error("lotId: " + lotId ) ;
				msg.append("A system error has occurred") ;
				return OTHER_ERROR ;								
			}

			pre.setActivationFlag('T') ;
			pre.setActivationDate( Utilities.getCurrentTimestamp() ) ;
			pre.setEvtPrepaidActivation(activation) ;
			
			session.save( pre ) ;			
			
			/* identify the prepaid calling service within the product offering */
			String svcQuerySql = "select svc.service_id " +
					"from offering_service_xref osx, service svc " +
					"WHERE osx.service_id = svc.service_id " +
					"AND osx.offering_id = ? " +
					"AND svc.service_element_id = ? " ;
			
			Query svcQuery = session.createSQLQuery(svcQuerySql) ;
			svcQuery.setBigDecimal(0, lot.getProductOffering().getOfferingId() ) ;
			svcQuery.setInteger(1, PactolusConstants.PREPAID_CALLING_CARD_SERVICE) ;
			BigDecimal svcId = (BigDecimal) svcQuery.uniqueResult() ;
						 
			 
			 
			/* now create an entry in the subscriber table */
			Subscriber sub = new Subscriber() ;
			sub.setSubscriberId( pre.getSubscriberId() ) ;
			sub.setCurrPrepaidBalance( BigDecimal.valueOf(initialBalance)) ;
			sub.setInitialBalance(BigDecimal.valueOf(initialBalance)) ;
			sub.setFirstCallDate(null) ;
			sub.setFirstUseDate(null) ;
			sub.setLotId(BigDecimal.valueOf(lotId)) ;
			sub.setPin( pre.getPin() ) ;
			sub.setPinPassword( pre.getPinPassword() ) ;
			sub.setServiceProviderId(lot.getServiceProvider().getServiceProviderId()) ;
			sub.setCurrencyId( lot.getProductOffering().getCurrencyId() ) ;
			sub.setCallingSvcId(svcId) ;
			sub.setActivationDate( pre.getActivationDate() ) ;
			sub.setExpirationDate(null) ; //would be better to actually check lot/PO settings, but primus is always going with no expiration
			sub.setDisabledFlag('F') ;
			sub.setBillingType( BigDecimal.valueOf(1) ) ;	//1=prepaid,2=postpaid,3=broadband
			sub.setLotControlNumber( pre.getLotControlNumber() );
			sub.setLotSeqNumber(pre.getLotSeqNumber()) ;
			if( null != lot.getExpirationType() )
				sub.setExpirationType( lot.getExpirationType() ) ;
			else 
				sub.setExpirationType( lot.getProductOffering().getExpirationType() ) ;
			sub.setLanguageId( lot.getProductOffering().getLanguageId() ) ;
			sub.setConfOperatorAssistType(BigDecimal.valueOf(0)); 
			sub.setBilledSequence(BigDecimal.valueOf(0)) ;
			sub.setDirectCallFlag('F') ;
			sub.setBucketRefillWarningFlag('F') ;
			sub.setBucketExhaustWarningFlag('F') ;
			sub.setFirstUseFeeState(BigDecimal.valueOf(0)) ;
						
			session.save( sub ) ;
			
			/* add the authorized ani */
			Long subAuthAniId = Dao.getOID(session, "sub_auth_ani") ;
			
			SubAuthAni ani = new SubAuthAni() ;
			ani.setAuthAniId(BigDecimal.valueOf(subAuthAniId)) ;
			ani.setPhoneNumber(phone) ;
			ani.setSubscriber(sub) ;
			ani.setServiceProvider(lot.getServiceProvider()) ;
			ani.setStatus( "0" ) ;
			
			session.save( ani ) ;
			session.delete( pre ) ;
			
			transaction.commit() ;

			/* last thing,tie siubscriber to offering */
			String subOfferingXrefSql = "INSERT INTO sub_offering_xref (subscriber_id, offering_id, primary_flag) VALUES (?, ?, 'T')";
			conn = framework.getConnection("psprd1") ;
			CallableStatement stmt = conn.prepareCall(subOfferingXrefSql) ;
			BigDecimal subId = sub.getSubscriberId() ;
			BigDecimal poId = lot.getProductOffering().getOfferingId() ;
			stmt.setBigDecimal(1, subId ) ;
			stmt.setBigDecimal(2, poId) ;
			int nRows = stmt.executeUpdate() ;
			if( 1 != nRows ) {
				logger.error("Failed to insert a row into sub_offering_xref") ;
			}
			conn.commit() ;
			
			pin.append( sub.getPin() ) ;
			subscriberId.append( sub.getSubscriberId().toPlainString() ) ;
			
			msg.append("Account was successfully provisioned") ;
			
			
		} catch( Exception e ) {
			e.printStackTrace() ;
			if( null != transaction ) transaction.rollback() ;
			if( null != conn ) try { conn.rollback() ; } catch( SQLException e1 ) {}
		}
		finally {
			if( null != session ) session.close() ;
			if( null != conn ) try {  conn.close() ; } catch( SQLException e2 ) {}
		}
	
		
		return rc ;
	}
	
	public static int processRechargeTransactionCC( 
		String phone,
		String spName,
		String userId,
		String clientId,
		String cardType,
		String cardNumber,
		String expiryDate,
		Double amount,
		String cardHolderName,
		String address1, 
		String address2,
		String city,
		String province, 
		String postalCode,
		String transactionId,
		StringBuffer authorizationCode,
		MutableInteger resultCode,
		StringBuffer resultDescription,
		StringBuffer avsCode,
		StringBuffer avsDescription,
		StringBuffer inquiryId, 
		StringBuffer msg)  {

		int rc = DB_ERROR ;
		int ccType ;
		
		resultCode.setInteger(0) ;
		resultDescription.setLength(0) ;
		avsCode.setLength(0) ;
		avsDescription.setLength(0) ;
		inquiryId.setLength(0) ;
		msg.setLength(0) ;
	
		
		/* do some basic validation checking on the credit card number */
		if( cardType.equalsIgnoreCase("VI") ) {
			ccType = CreditCardNumberValidator.VISA ;
		}
		else if( cardType.equalsIgnoreCase("MC") ) {
			ccType = CreditCardNumberValidator.MASTERCARD ;
		}
		else if( cardType.equalsIgnoreCase("AM") ) {
			ccType = CreditCardNumberValidator.AMEX ;
		}
		else {
			logger.error("Invalid credit card type: " + cardType ) ;
			msg.append("Invalid credit card type '" + cardType + "'; valid types are VI, MC, and AM") ;
			return BAD_CC_TYPE ;
		}
		
		if( !CreditCardNumberValidator.validate(cardNumber, ccType ) ) {
			logger.error("Credit card number " + cardNumber + " is not valid for specified card type: '" + cardType + "'") ;
			msg.append("Invalid credit card number") ;
			return BAD_CC_NUMBER ;
		}
		
		Session session = null ;
		Transaction transaction = null ;
		try {
			Framework framework = Framework.getInstance("beans.xml") ;
			SessionFactory sf = (SessionFactory)framework.getResource("sessionFactory");    	
			session = sf.openSession() ;
			transaction = session.beginTransaction() ;
			
			/* make sure the subscriber and service provider exists, and is not */
			/* make sure the subscriber and service provider exists, and is not disabled */
			ServiceProvider sp = (ServiceProvider) session.createCriteria(ServiceProvider.class)
					.add(Restrictions.eq("name", spName))
					.add(Restrictions.eq("deletedFlag", 'F'))
					.uniqueResult() ;
			
			if( null == sp ) {
				logger.error("Unable to find service provider with name: " + spName ) ;
				msg.append("Invalid service provider name " + spName) ;
				return UNKNOWN_SERVICE_PROVIDER ;			
			}

			if( 'T' == sp.getDisabledFlag() ) {
				logger.error("Service provider " + sp.getName() + " is disabled") ;
				msg.append("Service provider " + sp.getName() + " is disabled") ;
				return SP_DISABLED ;							
			}
					
			/* lookup the business unit from service provider settings */
			String businessUnit = null ;
			ServiceProviderSettingsId spsid = new ServiceProviderSettingsId( Constants.BUSINESS_UNIT, sp.getServiceProviderId() ) ;
			ServiceProviderSettings sps = (ServiceProviderSettings) session.createCriteria(ServiceProviderSettings.class)
					.add(Restrictions.eq("id", spsid ))
					.uniqueResult() ;
			if( null == sps ) {
				logger.error("No business unit identifier (service_provider_settings.name == business_unit_id) was found for service provider " + 
						sp.getName() ) ;
				msg.append("No business unit identifier could be found for service provider " + spName) ;
				return SP_BUSINESS_UNIT_NOTFOUND ;
			}
			businessUnit = sps.getSpValue() ;

			Subscriber sub = (Subscriber) session.createCriteria(Subscriber.class)
					.add(Restrictions.eq("serviceProviderId",sp.getServiceProviderId()))
					.createCriteria("subAuthAnis")
						.add(Restrictions.eq("phoneNumber", phone))
						.add(Restrictions.eq("status", "0") )
					.uniqueResult() ;
			
			if( null == sub ) {
				logger.error("Unable to find sub_auth_ani with active phone number " + phone) ;
				msg.append("Phone number is not associated with any subscriber: " + phone) ;
				return UNKNOWN_PHONE_NBR ;
			}
			
			if( 'T' == sub.getDisabledFlag() ) {
				logger.error("Unable to find sub_auth_ani with active phone number " + phone) ;
				msg.append("Subscriber associated with phone " + phone + " is disabled") ;
				return SUB_DISABLED ;				
			}
						
			/* now call the web service */
			Config cfg = (Config) framework.getResource("wsConfig") ;
			PrimusWS pws = new PrimusWS( cfg.getWebServiceEndoint(), cfg.getWsdlLocation() ) ;
			ECarePrepaidPaymentechResponse res  ;
			if( bypassRechargeTransaction ) {
				res = new ECarePrepaidPaymentechResponse() ;
				res.setResultcode("0") ;
				res.setAuthorizationcode("") ;
				res.setAvsresultcode("Accepted (no transaction sent)") ;
				res.setAvsresultdescription("") ;
				res.setInquiryid("") ;
				
			}
			else {
				Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader() ) ;
				res = pws.prepaidPaymentTechPayment( userId, clientId, businessUnit, 
					phone, cardNumber, expiryDate, cardType, amount.floatValue(), cardHolderName,
					address1, address2, city, province,  postalCode, "purchase", transactionId ) ;
			}
			
			authorizationCode.append( res.getAuthorizationcode() ) ;
			if( null != res.getResultcode() ) resultCode.setInteger( Integer.valueOf( res.getResultcode() ) ) ;
			resultDescription.append(res.getResultdescription() ) ;
			avsCode.append( res.getAvsresultcode() ) ;
			avsDescription.append( res.getAvsresultdescription() ) ;
			inquiryId.append( res.getInquiryid() ) ;
				
			if( SUCCESS == resultCode.getInteger() ) {
				CurrencyRef currency = (CurrencyRef) session.load(CurrencyRef.class, sub.getCurrencyId() ) ;
				Dao.updateSubscriberWithSuccessfulRecharge(session, sub, amount.floatValue(),
						phone, cfg, 
						res.getAuthorizationcode(), businessUnit, "P") ;
				logger.info("credit card recharge approved") ;
				msg.append("Credit card recharge approved") ;
				rc = SUCCESS ;
			}
			else {
				logger.info("credit card recharge declined") ;
				msg.append("Credit card recharge declined") ;
				rc = CC_DECLINED ;
			}
			
			transaction.commit() ;
			 
		} catch( HibernateException he ) {
			logger.error("Hibernate exception: ", he) ;
			if( null != transaction ) transaction.rollback() ;	
			msg.append("Database exception: " + he.getLocalizedMessage() ) ;
			return DB_ERROR ;
		} catch( SOAPFaultException sfe ) {
			logger.error("SOAP Fault calling prepaidPaymentTechPayment: ", sfe ) ;
			if( null != transaction ) transaction.rollback() ;
			msg.append("Fault: " + sfe.getLocalizedMessage() ) ;
			return WS_FAILURE ;
		} catch( Exception e ) {
			if( null != transaction ) transaction.rollback() ;
			logger.error("Exception: ", e) ;
			if( null != transaction ) transaction.rollback() ;	
			msg.append("Exception: " + e.getLocalizedMessage() ) ;
			return OTHER_ERROR ;
			
		} finally {
			if( null != session ) session.close() ;
		}
		
		return rc ;
	}
	
	public static int processRechargeTransactionUkash( 
			String phone,
			String spName,
			String userId,
			String clientId,
			String voucherNumber,
			Double amount,
			String transactionId,
			StringBuffer transactionCode,
			StringBuffer transactionDesc,
			MutableFloat settlementAmount,
			MutableInteger errorCode,
			StringBuffer errorDescription,
			StringBuffer ukashTransactionId, 
			StringBuffer msg)  {

			int rc = DB_ERROR ;
			
			errorCode.setInteger(0) ;
			errorDescription.setLength(0) ;
			transactionCode.setLength(0) ;
			transactionDesc.setLength(0) ;
			ukashTransactionId.setLength(0) ;
			msg.setLength(0) ;
			settlementAmount.setFloat(0.0f) ;
		
						
			Session session = null ;
			Transaction transaction = null ;
			try {
				Framework framework = Framework.getInstance("beans.xml") ;
				SessionFactory sf = (SessionFactory)framework.getResource("sessionFactory");    	
				session = sf.openSession() ;
				transaction = session.beginTransaction() ;
				
				/* make sure the subscriber and service provider exists, and is not disabled */
				ServiceProvider sp = (ServiceProvider) session.createCriteria(ServiceProvider.class)
						.add(Restrictions.eq("name", spName))
						.add(Restrictions.ne("deletedFlag", 'T'))
						.uniqueResult() ;
				
				if( null == sp ) {
					logger.error("Unable to find service provider with name " + spName) ;
					msg.append("Invalid service provider name") ;
					return UNKNOWN_SERVICE_PROVIDER ;			
				}

				if( 'T' == sp.getDisabledFlag() ) {
					logger.error("Service provider " + sp.getName() + " is disabled") ;
					msg.append("Service provider " + sp.getName() + " is disabled") ;
					return SP_DISABLED ;							
				}
						
				/* lookup the business unit from service provider settings */
				String businessUnit = null ;
				ServiceProviderSettingsId spsid = new ServiceProviderSettingsId( Constants.BUSINESS_UNIT, sp.getServiceProviderId() ) ;
				ServiceProviderSettings sps = (ServiceProviderSettings) session.createCriteria(ServiceProviderSettings.class)
						.add(Restrictions.eq("id", spsid ))
						.uniqueResult() ;
				if( null == sps ) {
					logger.error("No business unit identifier (service_provider_settings.name == business_unit_id) was found for service provider " + 
							sp.getName() ) ;
					msg.append("No business unit identifier could be found for service provider " + spName) ;
					return SP_BUSINESS_UNIT_NOTFOUND ;
				}
				businessUnit = sps.getSpValue() ;

				Subscriber sub = (Subscriber) session.createCriteria(Subscriber.class)
						.add(Restrictions.eq("serviceProviderId",sp.getServiceProviderId()))
						.createCriteria("subAuthAnis")
							.add(Restrictions.eq("phoneNumber", phone))
							.add(Restrictions.eq("status", "0") )
						.uniqueResult() ;
				
				if( null == sub ) {
					logger.error("Unable to find sub_auth_ani with active phone number " + phone) ;
					msg.append("Phone number is not associated with any subscriber: " + phone) ;
					return UNKNOWN_PHONE_NBR ;
				}
				
				if( 'T' == sub.getDisabledFlag() ) {
					logger.error("Unable to find sub_auth_ani with active phone number " + phone) ;
					msg.append("Subscriber associated with phone " + phone + " is disabled") ;
					return SUB_DISABLED ;				
				}
				CurrencyRef currency = (CurrencyRef) session.load(CurrencyRef.class, sub.getCurrencyId() ) ;
							
				/* now call the web service */
				Config cfg = (Config) framework.getResource("wsConfig") ;
				PrimusWS pws = new PrimusWS( cfg.getWebServiceEndoint(), cfg.getWsdlLocation() ) ;
				ECarePrepaidUkashResponse res  ;
				if( bypassRechargeTransaction ) {
					res = new ECarePrepaidUkashResponse() ;
					res.setTransactioncode("0") ;
					res.setErrorcode("0") ;
				}
				else {
					Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader() ) ;
					res = pws.prepaidUkashPayment(userId, clientId, businessUnit, phone, voucherNumber, 
							amount.floatValue(), currency.getIsoCurrencyCode(), transactionId) ;
				}
				
				Integer ec = null ;
				Integer txnCode = null ;
				try {
					if( null != res && null != res.getErrorcode() && res.getErrorcode().length() > 0 ) {
						ec = Integer.valueOf( res.getErrorcode() ) ;
					}
					txnCode = Integer.valueOf( res.getTransactioncode() ) ;
				} catch( NumberFormatException nfe ) {
					logger.error("ProcessRechargeUkash: prepaid_ukash_payment web service returned a non-integer error code: " + res.getErrorcode() ) ;
					msg.append("ProcessRechargeUkash: prepaid_ukash_payment web service returned a non-integer error code: " + res.getErrorcode() ) ;
					return WS_FAILURE; 
				}
				
				if( null != ec ) errorCode.setInteger( ec ) ;
				if( null != res.getErrordescription() ) errorDescription.append( res.getErrordescription() ) ;
				if( null != res.getSettleamount() && res.getSettleamount().length() > 0 ) settlementAmount.setFloat( Float.valueOf(res.getSettleamount() ) ) ;
				if( null != res.getTransactioncode() ) transactionCode.append( res.getTransactioncode() ) ;
				if( null != res.getTransactiondescription() ) transactionDesc.append( res.getTransactiondescription() ) ;
				if( null != res.getUkashtransactionid() ) ukashTransactionId.append( res.getUkashtransactionid() ) ;

				if( 0 == txnCode ) {
					Dao.updateSubscriberWithSuccessfulRecharge(session, sub, 
							Float.valueOf( res.getSettleamount() ), phone, cfg, 
							res.getUkashtransactionid(), businessUnit, "U"  ) ;					
					rc = SUCCESS ;
				}
				else {
					logger.error("ProcessRechargeUkash: prepaid_ukash_payment web service returned a non-success result: " + txnCode ) ;
					msg.append("Ukash transaction was declined") ;
					return WS_FAILURE; 
				}
				
				transaction.commit() ;
				 
			} catch( HibernateException he ) {
				logger.error("Hibernate exception: ", he) ;
				if( null != transaction ) transaction.rollback() ;	
				msg.append("Database exception: " + he.getLocalizedMessage() ) ;
				return DB_ERROR ;
			} catch( SOAPFaultException sfe ) {
				logger.error("SOAP Fault calling prepaidPaymentTechPayment: ", sfe ) ;
				if( null != transaction ) transaction.rollback() ;
				msg.append("Fault: " + sfe.getLocalizedMessage() ) ;
				return WS_FAILURE ;
			} catch( Exception e ) {
				if( null != transaction ) transaction.rollback() ;
				logger.error("Exception: ", e) ;
				msg.append("Exception: " + e.getLocalizedMessage() ) ;
				return OTHER_ERROR ;
				
			} finally {
				if( null != session ) session.close() ;
			}
			
			return rc ;
		}

	synchronized protected static Long createActivationGroupForSinglePin( Session session, Long lotId, Long offeringId, Double amount ) {
		Long lActivationId = null ;
	
		Framework framework = null ;
		Connection conn = null ;
		try {
			lActivationId = Dao.getOID(session, "evt_prepaid_activation") ;
			
			framework = Framework.getInstance() ;
			conn = framework.getConnection("psprd1") ;
			if( null == conn ) {
				return (long) DB_ERROR ;
			}
			
			stmtCreateActivationGroup = conn.prepareStatement(sqlCreateActivationGroup) ;
			
			stmtCreateActivationGroup.clearParameters() ;
			stmtCreateActivationGroup.setLong(1, lActivationId) ;
			stmtCreateActivationGroup.setLong( 2, lotId ) ;
			stmtCreateActivationGroup.setLong( 3, offeringId ) ;
			stmtCreateActivationGroup.setDate( 4, new Date( System.currentTimeMillis() ) ) ;
			stmtCreateActivationGroup.setString( 5, "API activation" ) ;
			stmtCreateActivationGroup.setLong( 6, lotId ) ;
			stmtCreateActivationGroup.setLong( 7, lotId ) ;
			int nRows = stmtCreateActivationGroup.executeUpdate() ;
			if( 1 != nRows ) {
				return (long) LOT_EXHAUSTED ;
			}
			
			conn.commit() ;
			
		} catch( SQLException e ) {
			lActivationId = (long) DB_ERROR ;
			if( null != conn ) try { conn.rollback() ; } catch( SQLException se ) { logger.error("Failed to rollback connection",se) ; }
			logger.error("SQLException adding activation group", e) ;
		} catch( Exception e ) {
			lActivationId = (long) DB_ERROR ;
			if( null != conn ) try { conn.rollback() ; } catch( SQLException se ) { logger.error("Failed to rollback connection",se) ; }
			logger.error("Exception adding activation group", e) ;
		}
		finally {
			if( null != conn ) try { conn.close() ; } catch( SQLException se ) { logger.error("Failed to close connection",se) ; }
		}
		
		return lActivationId ;
	}
	static public void updateSubscriberWithSuccessfulRecharge( Session session, Subscriber sub, Float amount, 
			String phoneNumber, Config cfg, 
			String authorizationCode, String businessUnit, String rechargeType ) throws HibernateException {
		
		/* success - update the pactolus database */
		logger.info("updateSubscriberWithSuccessfulRecharge, amount: " + amount) ;
		
		BigDecimal newBalance ;
		BigDecimal owedMaintFees = BigDecimal.ZERO ;
		DecimalFormat fmt = new DecimalFormat("#####.00") ;
		BigDecimal balance = sub.getCurrPrepaidBalance()  ;
		logger.info("Subscriber balance before voucher transfer is $" + fmt.format(balance)) ;
				
		
		/* check whether the subscriber needs to be unsuspended on the M6 */
		SuspendTracking st = (SuspendTracking) session.createCriteria(SuspendTracking.class)
				.add(Restrictions.eq("subscriberId", sub.getSubscriberId() ) )
				.add(Restrictions.isNull("unsuspendedOn") )
				.uniqueResult() ;
		if( null != st ) {
			
			/* unsuspend the account first.  If we can't unsuspend the account, continue since we have already processed the payment with ukash */		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
			logger.info("Subscriber has been suspended on the M6, attempting to unsuspend....") ;

			try {
				M6ModifyUserCommand cmd;
				cmd = new M6ModifyUserCommand(cfg.getM6Address(), cfg.getM6User(), cfg.getM6Password(), Utilities.getHost(), phoneNumber);
				cmd.setValue(UserKeys.SUSPEND_SERVICE, false ) ;
				cmd.execute() ;
			} catch( DBSOAPException e ) {
				logger.info("Error trying to unsuspend the account with phone number " + phoneNumber + " on the M6, continuing anyways..", e) ;
				if( cfg.getEmailServer() != null && cfg.getEmailRecipients() != null ) {
					Utilities.sendMail(cfg.getEmailRecipients(), cfg.getEmailServer(), "M6 unsuspend failure", 
							"failure attempting to unsuspend account with phone number " + phoneNumber + " on the M6 after successfully processing a payment", null) ;
				}
			}
			
			st.setUnsuspendedOn( new Date( System.currentTimeMillis() ) ) ;
			session.update(st) ;
			
			/* calculate the amount of payment in arrears to take:
			 * calculated as max ($15, (number of days * maintenance fee amount) / fee frequency + unpaid amount from first failed main fee 
			 */
			logger.info("Subscriber has been successfully unsuspended on the M6, calculate the amount of payment in arrears to deduct from payment") ;
			Calendar now = Calendar.getInstance();
			Calendar then = Calendar.getInstance() ;
			then.setTime( st.getSuspendedOn() ) ;
			Long daysInArrears = (now.getTimeInMillis() - then.getTimeInMillis())/(1000*60*60*24);
			logger.info("Subscriber was suspended on: " + sdf.format( st.getSuspendedOn() ) + " which was " + daysInArrears + " days ago." ) ;
			
			/* get the maintenance fee rate for this subscriber */
			Rate rate = Dao.getSubscriberMaintenanceFeeRate(session, sub.getPrimaryOfferingId()) ;
			if( null != rate && ( null == rate.getMaintFeeFrequency() || 0 == rate.getMaintFeeFrequency().longValue())) {
				logger.error("Subscriber has a maintenance fee rate, but frequency has not been set so payment in arrears will not be taken; rate id is: " + rate.getRateId().longValue()) ;
			}

  			if( null != rate && null != rate.getMaintFeeFrequency() && 0 != rate.getMaintFeeFrequency().longValue() ) {
  				logger.info("Maintenance fee for this subscriber has rate_id: " + rate.getRateId() + " amount " + fmt.format(rate.getDefaultAmount().doubleValue()) + 
  						" frequency: " + rate.getMaintFeeFrequency() ) ;
				if( daysInArrears > 0 ) {
					owedMaintFees = rate.getDefaultAmount().multiply( BigDecimal.valueOf(daysInArrears) ).divide( rate.getMaintFeeFrequency() ) ;
					logger.info("Charging $" + fmt.format( owedMaintFees.doubleValue() ) + " for " + daysInArrears + " days of missed maintenance fees") ;
					logger.info("Note: this maintenance fee has a frequency (interval) of " + rate.getMaintFeeFrequency().longValue() + 
							" days and a fee of $" + fmt.format(rate.getDefaultAmount() ) ) ;
				}
				if( daysInArrears >= 0 ) {
					AccountActivity aa = null ;
					if( null != st.getAaActivityId() ) aa = (AccountActivity) session.load(AccountActivity.class, st.getAaActivityId() ) ;
					BigDecimal partialMissedFee = rate.getDefaultAmount().add( null == aa ? BigDecimal.ZERO : aa.getTotalAmount() ) ;
					logger.info("Charging $" + fmt.format( partialMissedFee.doubleValue() )  + " for the partial maintenance fee collected that resulted in suspension") ;
					owedMaintFees = owedMaintFees.add( partialMissedFee ) ;
				}
				logger.info("Total payment in arrears to be collected is calculated as " + fmt.format( owedMaintFees.doubleValue())) ;
				if( owedMaintFees.compareTo(BigDecimal.valueOf(15)) > 0 ) {
					logger.info("The payment in arrears exceeds $15, so only $15 will be collected") ;
					owedMaintFees = BigDecimal.valueOf(15) ;
				}
 			}	
		}
		
		
		/* update the subscriber balance with the full amount of the payment */	
		newBalance  = balance.add( new BigDecimal( amount.doubleValue() ) ) ;
		sub.setCurrPrepaidBalance( newBalance ) ;
		session.update(sub) ;
		
		logger.info("Subscriber balance after voucher transfer is $" + fmt.format(newBalance)) ;

		/* write an account activity record for the payment */
		AccountActivity aa = Dao.createAccountActivity(session, sub, amount, Dao.POS_RECHARGE_EVENT_TYPE_ID) ;
		session.save( aa ) ;
		
		CurrencyRef cr = (CurrencyRef) session.load(CurrencyRef.class, sub.getCurrencyId() ) ;
		EvtPointOfSale epos = Dao.createEvtPointOfSale(session, sub, aa, cr, businessUnit, amount, PactolusPOSConstants.TRANS_CODE_RECHARGE, 
				authorizationCode, rechargeType) ;	
		session.save( epos ) ;

		
		if( owedMaintFees.compareTo(BigDecimal.ZERO) > 0 ) {
			
			/* if we've taken a payment in arrears, now adjust the subscriber account for that and write a second account activity record */
			
			if( newBalance.compareTo(owedMaintFees) < 0 ) {
				logger.info("The payment in arrears is greater then the account balance after processing the payment; taking only $" + fmt.format(newBalance.doubleValue()) +
						"for the past due maintenance fees, instead of the amount actually owed which is $" + fmt.format(owedMaintFees.doubleValue())) ;
				owedMaintFees = newBalance ;
			}
			newBalance = newBalance.subtract(owedMaintFees) ;
			sub.setCurrPrepaidBalance(newBalance) ;
			logger.info("Subscriber balance after payment in arrears is $" + fmt.format(newBalance)) ;

			session.update( sub ) ;

			/* write an account activity record for the payment in arrears */
			AccountActivity aa2 = Dao.createAccountActivity(session, sub, -(owedMaintFees.floatValue()), PactolusConstants.MAINT_FEE_EVENT) ;
			session.save( aa2 ) ;
		}
	}
	
	public static int querySubscriberAttribute( String spName, String phone, String attribute, StringBuffer value ) {
		
		int rc = SUCCESS ;
		Session session = null ;
		Transaction transaction = null ;
		value.setLength(0) ;
		try {
			Framework framework = Framework.getInstance("beans.xml") ;
			SessionFactory sf = (SessionFactory)framework.getResource("sessionFactory");    	
			session = sf.openSession() ;
			transaction = session.beginTransaction() ;
			
			/* make sure the subscriber and service provider exists, and is not disabled */
			ServiceProvider sp = (ServiceProvider) session.createCriteria(ServiceProvider.class)
					.add(Restrictions.eq("name", spName))
					.add(Restrictions.eq("deletedFlag", 'F'))
					.uniqueResult() ;
			
			if( null == sp ) {
				logger.error("Unable to find service provider with name: " + spName ) ;
				return UNKNOWN_SERVICE_PROVIDER ;			
			}

			if( 'T' == sp.getDisabledFlag() ) {
				logger.error("Service provider " + sp.getName() + " is disabled") ;
				return SP_DISABLED ;							
			}

			Subscriber sub = (Subscriber) session.createCriteria(Subscriber.class)
					.add(Restrictions.eq("serviceProviderId",sp.getServiceProviderId()))
					.createCriteria("subAuthAnis")
						.add(Restrictions.eq("phoneNumber", phone))
						.add(Restrictions.eq("status", "0") )
					.uniqueResult() ;
			
			if( null == sub ) {
				logger.error("Unable to find sub_auth_ani with active phone number " + phone) ;
				return UNKNOWN_PHONE_NBR ;
			}
			
			if( 'T' == sub.getDisabledFlag() ) {
				logger.error("Subscriber with phone number " + phone + " is disabled") ;
				return SUB_DISABLED ;				
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
			DecimalFormat fmt = new DecimalFormat("###0.00") ;
			if( "firstCallDate".equalsIgnoreCase(attribute) ) {
				if( null != sub.getFirstCallDate() ) value.append( sdf.format( sub.getFirstCallDate() ) ) ;
			}
			else if( "firstUseDate".equalsIgnoreCase(attribute) ) {
				if( null != sub.getFirstUseDate() ) value.append( sdf.format( sub.getFirstUseDate() ) ) ;
			}
			else if( "currPrepaidBalance".equalsIgnoreCase(attribute) ) {
				if( null != sub.getCurrPrepaidBalance() ) value.append( fmt.format( sub.getCurrPrepaidBalance().doubleValue() ) ) ;
			}
			
			transaction.commit() ;
			
			return rc ;
			
		} catch( HibernateException he ) {
			logger.error("Hibernate exception: ", he) ;
			if( null != transaction ) transaction.rollback() ;	
			return DB_ERROR ;
		} catch( SOAPFaultException sfe ) {
			logger.error("SOAP Fault calling prepaidPaymentTechPayment: ", sfe ) ;
			if( null != transaction ) transaction.rollback() ;
			return WS_FAILURE ;
		} catch( Exception e ) {
			if( null != transaction ) transaction.rollback() ;
			logger.error("Exception: ", e) ;
			if( null != transaction ) transaction.rollback() ;	
			return OTHER_ERROR ;
			
		} finally {
			if( null != session ) session.close() ;
		}
	}

	public static int updateSubscriberAttribute( String spName, String phone, String attribute, String value ) {
		
		int rc = SUCCESS ;
		Session session = null ;
		Transaction transaction = null ;
		try {
			Framework framework = Framework.getInstance("beans.xml") ;
			SessionFactory sf = (SessionFactory)framework.getResource("sessionFactory");    	
			session = sf.openSession() ;
			transaction = session.beginTransaction() ;
			
			/* make sure the subscriber and service provider exists, and is not disabled */
			ServiceProvider sp = (ServiceProvider) session.createCriteria(ServiceProvider.class)
					.add(Restrictions.eq("name", spName))
					.add(Restrictions.eq("deletedFlag", 'F'))
					.uniqueResult() ;
			
			if( null == sp ) {
				logger.error("Unable to find service provider with name: " + spName ) ;
				return UNKNOWN_SERVICE_PROVIDER ;			
			}

			if( 'T' == sp.getDisabledFlag() ) {
				logger.error("Service provider " + sp.getName() + " is disabled") ;
				return SP_DISABLED ;							
			}

			Subscriber sub = (Subscriber) session.createCriteria(Subscriber.class)
					.add(Restrictions.eq("serviceProviderId",sp.getServiceProviderId()))
					.createCriteria("subAuthAnis")
						.add(Restrictions.eq("phoneNumber", phone))
						.add(Restrictions.eq("status", "0") )
					.uniqueResult() ;
			
			if( null == sub ) {
				logger.error("Unable to find sub_auth_ani with active phone number " + phone) ;
				return UNKNOWN_PHONE_NBR ;
			}
			
			if( 'T' == sub.getDisabledFlag() ) {
				logger.error("Subscriber with phone number " + phone + " is disabled") ;
				return SUB_DISABLED ;				
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
			DecimalFormat fmt = new DecimalFormat("###0.00") ;
			if( "firstCallDate".equalsIgnoreCase(attribute) ) {
				sub.setFirstCallDate( value.length() > 0 ? sdf.parse(value) : null ) ;
			}
			else if( "firstUseDate".equalsIgnoreCase(attribute) ) {
				sub.setFirstUseDate( value.length() > 0 ? sdf.parse(value) : null ) ;
			}
			
			PsAudit audit = new PsAudit() ;
			Long lAuditId = Dao.getOID(session, "audit_event") ;
			audit.setAuditId(BigDecimal.valueOf(lAuditId)) ;
			audit.setAuditEventId(BigDecimal.valueOf(PactolusConstants.PRPD_SUBSCRIBER_MODIFY_AUDIT)) ;
			audit.setSubscriberId(sub.getSubscriberId()) ;
			audit.setServiceProviderId( sp.getServiceProviderId() ) ;
			audit.setAcdUserFlag('F') ;
			audit.setTimestamp( Utilities.getCurrentTimestamp() ) ;
			audit.setDataCol1( attribute ) ;
			audit.setDataCol2( value ) ;
			audit.setUserId( BigDecimal.valueOf(PactolusConstants.PROVISIONING_API_USER_ID ) );
						
			session.save( audit ) ;

			transaction.commit() ;
			
			return rc ;
			
		} catch( HibernateException he ) {
			logger.error("Hibernate exception: ", he) ;
			if( null != transaction ) transaction.rollback() ;	
			return DB_ERROR ;
		} catch( SOAPFaultException sfe ) {
			logger.error("SOAP Fault calling prepaidPaymentTechPayment: ", sfe ) ;
			if( null != transaction ) transaction.rollback() ;
			return WS_FAILURE ;
		} catch( Exception e ) {
			if( null != transaction ) transaction.rollback() ;
			logger.error("Exception: ", e) ;
			if( null != transaction ) transaction.rollback() ;	
			return OTHER_ERROR ;
			
		} finally {
			if( null != session ) session.close() ;
		}
	}
	
	public static Rate getSubscriberMaintenanceFeeRate( Session session, BigDecimal offeringId ) {
			
		Timestamp dtNow = new java.sql.Timestamp((new java.util.Date()).getTime());
		Rate rate = (Rate) session.createCriteria(Rate.class)
			.add( Restrictions.eq("eventTypeId", BigDecimal.valueOf( (long) PactolusConstants.MAINT_FEE_EVENT) ) )
			.add(Restrictions.le("effectiveDate", dtNow))
			.add(Restrictions.ge("endDate", dtNow))
			.createAlias("ratedEvents", "re")
				.add(Restrictions.eq("re.productOfferingId", offeringId) ) 
			.uniqueResult() ;

		return rate ;
	}

}
