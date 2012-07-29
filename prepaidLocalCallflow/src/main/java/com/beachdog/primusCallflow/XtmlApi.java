package com.beachdog.primusCallflow;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import java.text.SimpleDateFormat ;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.ws.soap.SOAPFaultException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.pactolus.java.*;

import org.sipdev.framework.Framework;
import org.sipdev.framework.Log;

import uri.ecare.GetPrepaidBalanceDlResponse.ECarePrepaidBalanceResponse;
import uri.ecare.ObjectFactory;
import uri.ecare.PrepaidPaymentechPaymentDlResponse.ECarePrepaidPaymentechResponse;
import uri.ecare.PrepaidUkashPaymentDlResponse.ECarePrepaidUkashResponse;


import com.beachdog.primusCore.Constants;
import com.beachdog.primusCore.PactolusPOSConstants ;
import com.beachdog.primusCore.Config;
import com.beachdog.primusCore.Dao;
import com.beachdog.primusCore.PrimusWS;
import com.beachdog.primusCore.model.AccessNumber;
import com.beachdog.primusCore.model.AccountActivity;
import com.beachdog.primusCore.model.EvtPointOfSale;
import com.beachdog.primusCore.model.CurrencyRef;
import com.beachdog.primusCore.model.LanguageRef;
import com.beachdog.primusCore.model.LockedPins;
import com.beachdog.primusCore.model.ProductOffering;
import com.beachdog.primusCore.model.ServiceProvider;
import com.beachdog.primusCore.model.ServiceProviderSettings;
import com.beachdog.primusCore.model.ServiceProviderSettingsId;
import com.beachdog.primusCore.model.Subscriber;

public class XtmlApi {

	static final int SUCCESS = 0 ;
	static final int UNKNOWN_SUB = -1 ;
	static final int AMBIGIOUS_SUB = -2 ;
	static final int MISSING_BUSINESS_UNIT_NAME = -3 ;
	static final int PIN_IN_USE = -4 ;
	static final int UNKNOWN_ACCESS_NUMBER = -5 ;
	static final int UNKNOWN_LANGUAGE = -6 ;
	static final int UNKNOWN_CURRENCY = -7 ;
	static final int CC_DECLINED = -8 ;

	static final int WS_DUPLICATE_TRANSACTION_ID = -96 ;
	static final int WS_REJECTED = -97 ;
	static final int WS_ERROR = -98 ;
	static final int DB_ERROR = -99 ;

	static final int STATUS_OK = 0 ;
	static final int STATUS_DISABLED_SUB = -1 ;
	static final int STATUS_DISABLED_SP = -2 ;
	
	static private int getWsFailureReason( SOAPFaultException sfe ) {
		int rc = WS_ERROR ;
		
		String msg = sfe.getLocalizedMessage() ;
		if("ensure transaction id is unique".equalsIgnoreCase(msg) ) rc = WS_DUPLICATE_TRANSACTION_ID ;
		
		return rc ;
	}
	
	static private void updateSubscriberWithSuccessfulRecharge( Session session, Log logger, Subscriber sub, CurrencyRef cr,
			String strTransactionId, String authorizationCode, String strBusinessUnit, Float amount, String strTransactionType ) throws HibernateException {
		
		/* success - update the pactolus database */
		logger.info("updateSubscriberWithSuccessfulRecharge, amount: " + amount) ;
		
		/* step 1: update the subscriber balance */
		DecimalFormat fmt = new DecimalFormat("#####.00") ;

		BigDecimal balance = sub.getCurrPrepaidBalance()  ;
		logger.info("Subscriber balance before voucher transfer is $" + fmt.format(balance)) ;
		
		BigDecimal newBalance  = balance.add( new BigDecimal( amount.doubleValue() ) ) ;
		sub.setCurrPrepaidBalance( newBalance ) ;
		session.save(sub) ;
		
		logger.info("Subscriber balance after voucher transfer is $" + fmt.format(newBalance)) ;

		/* step 2: write an account activity record */
		AccountActivity aa = Dao.createAccountActivity(session, sub, amount, Dao.POS_RECHARGE_EVENT_TYPE_ID) ;
		
		session.save( aa ) ;
		
		/* step 3: write an evt_point_of_sale record, link it to the account activity record, and store the transaction ids, identifying it as a Ukash transaction */
		EvtPointOfSale epos = Dao.createEvtPointOfSale(session, sub, aa, cr, strBusinessUnit, amount, PactolusPOSConstants.TRANS_CODE_RECHARGE, 
				authorizationCode, strTransactionType) ;
		
		session.save( epos ) ;

	}
	static public int Init() {
		
		Framework framework = null;
		Log logger = null;
		Session session = null ;
		SessionFactory sessionFactory = null ;
		int rc = DB_ERROR ;

		try {
			
			Properties prop = System.getProperties();
			System.out.println("Classpath is: " + prop.getProperty("java.class.path")) ;
			
			framework =Framework.getInstance() ;
			logger = framework.getLogger();
	
	  		logger.info("--------------   XTMLApi.Init   --  Starting  ------------------");

			sessionFactory = (SessionFactory)framework.getResource("sessionFactory");    	
    		session = sessionFactory.openSession() ;


    		logger.info("--------------   XTMLApi.Init   --  Ending  ------------------");
		} catch (Exception ex) {
			Framework.getInstance().getLogger().error("Caught exception in XtmlApi.Init", ex);
		} finally {
			if( null != session ) session.close() ;
		}			

		return rc ;
	}
	
	static public int InitializeRechargeSession( 
			String xtmlSessionId,
			String strDnis,
			String strSubscriberPhone,
			MutableLong mlSubscriberId,
			MutableInteger miStatus,
			StringBuffer strLanguage,
			MutableFloat mfBalance,
			MutableBoolean mbPlayAsUnits,
			MutableFloat mfMaxRechargeAmount,
			MutableFloat mfMinRechargeAmount,
			StringBuffer strBusinessUnit)  {
		
		Framework framework = null;
		Log logger = null;
		Session session = null ;
		SessionFactory sessionFactory = null ;
		Subscriber sub = null ;
		int rc = DB_ERROR;
		
		strLanguage.setLength(0) ;
		mlSubscriberId.setLong(0) ;
		miStatus.setInteger(STATUS_OK) ;
		mfBalance.setFloat(0) ;
		mfMinRechargeAmount.setFloat(0) ;
		mfMaxRechargeAmount.setFloat(0) ;
		strBusinessUnit.setLength(0) ;
		
		
		try {
			
			framework =Framework.getInstance(xtmlSessionId) ;
			logger = framework.getLogger();
	   		
			sessionFactory = (SessionFactory)framework.getResource("sessionFactory");    	
    		session = sessionFactory.openSession() ;

    		logger.info("--------------   XTMLApi.InitializeRechargeSession   --  Starting  ------------------");
			logger.info("dnis:          	      " + strDnis) ;
			logger.info("subscriber phone number: " + strSubscriberPhone) ;
			
			/* look up subscriber by phone number */
			Criteria c = session.createCriteria(Subscriber.class) ;
			Criteria subAuthAnisCriteria = c.createCriteria("subAuthAnis") ;
			subAuthAnisCriteria.add(Restrictions.eq("phoneNumber", strSubscriberPhone)) ;
			List<Subscriber> listSubs = c.list() ;
			if( null == listSubs || listSubs.isEmpty() ) {
				logger.warn("Subscriber phone number not found: " + strSubscriberPhone) ;
				return rc = UNKNOWN_SUB ;
			}
			else if( listSubs.size() > 1 ) {
				logger.error("Subscriber phone number " + strSubscriberPhone + " is associated to " + listSubs.size() + " subscribers") ;
				return rc = AMBIGIOUS_SUB ;
			}
			
			sub = listSubs.get(0)  ;
			ProductOffering po = (ProductOffering) session.get(ProductOffering.class, sub.getPrimaryOfferingId() ) ;
			ServiceProvider sp = (ServiceProvider) session.get( ServiceProvider.class, sub.getServiceProviderId() ) ;
			logger.debug("Service provider: " + sp.getName() ) ;
			
			
			mlSubscriberId.setLong( sub.getSubscriberId().longValue() ) ;
			mfBalance.setFloat( sub.getCurrPrepaidBalance().floatValue() ) ;
			
			mfMinRechargeAmount.setFloat( null != po.getMinRecharge() ? po.getMinRecharge().floatValue() : 0 ) ;
			mfMaxRechargeAmount.setFloat( null != po.getMaxRecharge() ?  po.getMaxRecharge().floatValue() : 0 ) ;
			
			/* look up access number by dnis */
			AccessNumber an = (AccessNumber) session.createCriteria(AccessNumber.class)
				.add(Restrictions.eq("accessNumber", strDnis))
				.uniqueResult() ;
			if( null == an ) {
				logger.error("Access number not found for dnis: " + strDnis) ;
				return rc = UNKNOWN_ACCESS_NUMBER ;
			}
			/* for this application we take language from the access number level */
			if( null != an.getLanguageRef() ) {
				String sLang = an.getLanguageRef().getLanguageCode() ;
				if( null != sLang ) {
					strLanguage.append( sLang ) ;
				}
			}
			if( 0 == strLanguage.length() ) {
				logger.error("Language is required but has not been set for the specified access number") ;
				return rc = UNKNOWN_LANGUAGE ;
			}
			
			if( sub.getDisabledFlag() == 'T') {
				miStatus.setInteger(STATUS_DISABLED_SUB) ;
			}
			else if( sp.getDisabledFlag() == 'T') {
				miStatus.setInteger(STATUS_DISABLED_SP) ;
			}
			
			/* lookup the business unit from service provider settings */
			ServiceProviderSettingsId spsid = new ServiceProviderSettingsId( Constants.BUSINESS_UNIT, sp.getServiceProviderId() ) ;
			ServiceProviderSettings sps = (ServiceProviderSettings) session.createCriteria(ServiceProviderSettings.class)
					.add(Restrictions.eq("id", spsid ))
					.uniqueResult() ;
			if( null == sps ) {
				logger.error("No business unit identifier (service_provider_settings.name == business_unit_id) was found for service provider " + 
						sp.getName() ) ;
				return MISSING_BUSINESS_UNIT_NAME ;
			}
			strBusinessUnit.append( sps.getSpValue() ) ;
			
			logger.info("subscriber id:       " + mlSubscriberId.getLong()) ;
			logger.info("language:            " + strLanguage.toString() ) ;
			logger.info("status:              " + miStatus.getInteger() ) ;
			logger.info("balance:             " + mfBalance.getFloat()) ;
			logger.info("play as units:       " + mbPlayAsUnits.getBoolean()) ;
			logger.info("max recharge amount: " + mfMaxRechargeAmount.getFloat()) ;
			logger.info("min recharge amount: " + mfMinRechargeAmount.getFloat()) ;
			logger.info("business unit:       " + strBusinessUnit.toString()) ;
			
			rc = SUCCESS ;

		} catch (Exception ex) {
			Framework.getInstance().getLogger().error("Caught exception in XtmlApi.InitializeRechargeSession", ex);
			if(SUCCESS == rc) {
				rc = DB_ERROR;
			}
		} finally {
			if( SUCCESS == rc) {
		   		logger.info("--------------   XtmlApi.InitializeRechargeSession   --  Ending  ------------------");				
			}
			if( null != session ) session.close() ;
		}			
		return rc ;
	}

	static public int ProcessRechargeUkash( 
			String xtmlSessionId,
			long subscriberId,
			String strSubscriberPhone,
			String strUserId,
			String strClientId,
			String strBusinessUnit,
			String strUkashVoucher,
			String strUkashVoucherValue,
			String strTransactionId,
			StringBuffer strTransactionCode,
			StringBuffer strTransactionDesc,
			MutableFloat fSettlementAmount,
			MutableInteger errorCode,
			StringBuffer strErrorDesc,
			StringBuffer strUkashTransactionId)  {
		
		Framework framework = null;
		Log logger = null;
		Session session = null ;
		SessionFactory sessionFactory = null ;
		Transaction transaction = null ;
		int rc = DB_ERROR;
		
		strTransactionCode.setLength(0) ;
		strTransactionDesc.setLength(0) ;
		fSettlementAmount.setFloat(0f) ;
		errorCode.setInteger(0) ;
		strErrorDesc.setLength(0) ;
		strUkashTransactionId.setLength(0) ;

		LockedPins lock = null ;
		
		try {
			
			framework = Framework.getInstance(xtmlSessionId) ;
			logger = framework.getLogger();
	   		
			sessionFactory = (SessionFactory)framework.getResource("sessionFactory");    	
    		session = sessionFactory.openSession() ;

    		logger.info("--------------   XTMLApi.ProcessRechargeUkash   --  Starting  ------------------");
			logger.info("subscriberId:          	 " + subscriberId) ;
			logger.info("strUserId: 			     " + strUserId) ;
			logger.info("strClientId: 			     " + strClientId) ;
			logger.info("strBusinessUnit: 			 " + strBusinessUnit) ;
			logger.info("strUkashVoucher: 			 " + strUkashVoucher) ;
			logger.info("strSubscriberPhone: 		 " + strSubscriberPhone) ;
			logger.info("strUkashVoucherValue: 		 " + strUkashVoucherValue) ;

			/* retrieve subscriber */
			Subscriber sub = (Subscriber) session.get(Subscriber.class, BigDecimal.valueOf(subscriberId)) ;
			if( null == sub ) {
				logger.error("Unknown Subscriber id: " + subscriberId) ;
				return rc = UNKNOWN_SUB ;
			}
			CurrencyRef currency = (CurrencyRef) session.get(CurrencyRef.class, sub.getCurrencyId() ) ;
			if( null == currency || null == currency.getIsoCurrencyCode() ) {
				logger.error("Unknown currency for subscriber id: " + subscriberId) ;
				return rc = UNKNOWN_CURRENCY ;				
			}
			String strCurrency = currency.getIsoCurrencyCode() ;
			transaction = session.beginTransaction() ;
			
			lock = Dao.createLockedPin(sub, xtmlSessionId) ;
			try {
				session.save(lock) ;
				transaction.commit() ;
			} catch( HibernateException hbe ) {
				logger.error("Unable to lock pin for subscriber id: " + subscriberId + "; recharge can not be processed") ;
				transaction.rollback() ;
				transaction = null ;
				return rc = PIN_IN_USE ;
			}
			logger.debug("prepaid_ukash_payment: subscriber was successfully retrieved and pin locked, ready to attempt ukash payment") ;
			transaction = session.beginTransaction() ;
			
			/* perform the transaction */
			ECarePrepaidUkashResponse res = null ;
			try {
				Config cfg = (Config) framework.getResource("wsConfig") ;
				PrimusWS pws = new PrimusWS( cfg.getWebServiceEndoint(), cfg.getWsdlLocation() ) ;
				Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader() ) ;
				res = pws.prepaidUkashPayment(strUserId, strClientId, strBusinessUnit, strSubscriberPhone, strUkashVoucher, 
						Float.valueOf(strUkashVoucherValue), strCurrency, strTransactionId) ;
			} catch( SOAPFaultException sfe ) {
				logger.error("SOAP Fault calling prepaid_ukash_payment: " + sfe.getLocalizedMessage() ) ;
				transaction.rollback() ;
				transaction = null ;
				return rc = getWsFailureReason( sfe ) ;
			} catch( Exception e ) {
				logger.error("Error calling primus web service prepaid_ukash_payment") ;
				logger.error(e) ;
				transaction.rollback() ;
				transaction = null ;
				return rc = WS_ERROR ;
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
				rc = WS_REJECTED; 
				throw nfe ;
			}
			if( 0 == txnCode ) {
				updateSubscriberWithSuccessfulRecharge(session, logger, sub, currency, strTransactionId, res.getUkashtransactionid(), strBusinessUnit, 
						Float.valueOf( res.getSettleamount() ), "U" ) ;
				
				rc = SUCCESS ;
			}
			else {
				logger.error("ProcessRechargeUkash: prepaid_ukash_payment web service returned a non-success result: " + txnCode ) ;
				rc = WS_REJECTED; 
			}
			
			if( null != ec ) errorCode.setInteger( ec ) ;
			if( null != res.getErrordescription() ) strErrorDesc.append( res.getErrordescription() ) ;
			if( null != res.getSettleamount() && res.getSettleamount().length() > 0 ) fSettlementAmount.setFloat( Float.valueOf( res.getSettleamount() ) ) ;
			if( null != res.getTransactioncode() ) strTransactionCode.append( res.getTransactioncode() ) ;
			if( null != res.getTransactiondescription() ) strTransactionDesc.append( res.getTransactiondescription() ) ;
			if( null != res.getUkashtransactionid() ) strUkashTransactionId.append( res.getUkashtransactionid() ) ;
			
			
		} catch (Exception ex) {
			Framework.getInstance().getLogger().error("Caught exception in XtmlApi.ProcessRechargeUkash", ex);
			if( null != transaction ) {
				transaction.rollback() ;
				transaction = null ;
			}
		} finally {
			if( null != transaction ) {
				try {
					transaction.commit() ;
				} catch( HibernateException sqe ){
					logger.error("Error committing transaction") ;
					logger.error( sqe ) ;
				}
			}
			if( null != lock ) {
				try {
					transaction = session.beginTransaction() ;
					int nRows = session.createSQLQuery("delete from locked_pins where subscriber_id = :sub_id")
						.setBigDecimal("sub_id", lock.getSubscriberId() )
						.executeUpdate() ;
					//session.delete(lock) ;
					transaction.commit() ;
				} catch( Exception ex2 ) {
					logger.error("Unable to unlock pin", ex2) ;
				}
			}
			
			if( SUCCESS == rc || WS_REJECTED == rc ) {
				logger.info("Error code: " + errorCode.getInteger()) ;
				logger.info("Error description: " + strErrorDesc.toString()) ;
				logger.info("Settlement amount: " + fSettlementAmount.getFloat()) ;
				logger.info("Transaction code: " + strTransactionCode.toString()) ;
				logger.info("Ukash transaction id: " + strUkashTransactionId.toString()) ;
		   		logger.info("--------------   XtmlApi.ProcessRechargeUkash   --  Ending  ------------------");				
			}
			if( null != session ) session.close() ;
		}			
		return rc ;
	}

	static public int ProcessRechargePaymentTech( 
			String xtmlSessionId,
			Long subscriberId,
			String strSubscriberPhone,
			String strUserId,
			String strClientId,
			String strBusinessUnit,
			String strCardNumber,
			String strExpiryDate,
			String strCardType,
			String strAmount,
			String strNameOnCard,
			String strAddress1, 
			String strAddress2,
			String strCity,
			String strProvince, 
			String strPostalCode,
			String strReason,
			String strTransactionId,
			StringBuffer strAuthorizationCode,
			MutableInteger resultCode,
			StringBuffer strResultDescription,
			StringBuffer strAvsCode,
			StringBuffer strAvsDescription,
			StringBuffer strInquiryId)  {
		
		Framework framework = null;
		Log logger = null;
		Session session = null ;
		SessionFactory sessionFactory = null ;
		Transaction transaction = null ;
		int rc = DB_ERROR;
		
		strAuthorizationCode.setLength(0) ;
		strResultDescription.setLength(0) ;
		resultCode.setInteger(0) ;
		strAvsCode.setLength(0) ;
		strAvsDescription.setLength(0) ;
		strInquiryId.setLength(0) ;
		
		LockedPins lock = null ;
		
		try {
			
			framework =Framework.getInstance(xtmlSessionId) ;
			logger = framework.getLogger();
	   		
			sessionFactory = (SessionFactory)framework.getResource("sessionFactory");    	
    		session = sessionFactory.openSession() ;

    		logger.info("--------------   XTMLApi.ProcessRechargePaymentTech   --  Starting  ------------------");
			logger.info("subscriberId:          	 " + subscriberId) ;
			logger.info("strSubscriberPhone: 			 " + strSubscriberPhone) ;
			logger.info("strUserId: 			 " + strUserId) ;
			logger.info("strClientId: 			 " + strClientId) ;
			logger.info("strBusinessUnit: 			 " + strBusinessUnit) ;
			logger.info("strCardNumber: 			 " + strCardNumber) ;
			logger.info("strExpiryDate: 			 " + strExpiryDate) ;
			logger.info("strCardType: 			 " + strCardType) ;
			logger.info("strAmount: 			 " + strAmount) ;
			logger.info("strNameOnCard: 			 " + strNameOnCard) ;
			logger.info("strAddress1: 			 " + strAddress1) ;
			logger.info("strAddress2: 			 " + strAddress2) ;
			logger.info("strCity: 			 " + strCity) ;
			logger.info("strProvince: 			 " + strProvince) ;
			logger.info("strPostalCode: 			 " + strPostalCode) ;
			logger.info("strReason: 			 " + strReason) ;
			logger.info("strTransaction: 			 " + strTransactionId) ;

			/* retrieve subscriber */
			Subscriber sub = (Subscriber) session.get(Subscriber.class, BigDecimal.valueOf(subscriberId)) ;
			if( null == sub ) {
				logger.error("Unknown Subscriber id: " + subscriberId) ;
				return rc = UNKNOWN_SUB ;
			}
			CurrencyRef currency = (CurrencyRef) session.get(CurrencyRef.class, sub.getCurrencyId() ) ;
			if( null == currency || null == currency.getIsoCurrencyCode() ) {
				logger.error("Unknown currency for subscriber id: " + subscriberId) ;
				return rc = UNKNOWN_CURRENCY ;				
			}
			
			transaction = session.beginTransaction() ;
			
			lock = Dao.createLockedPin(sub, xtmlSessionId) ;
			try {
				session.save(lock) ;
				transaction.commit() ;
			} catch( HibernateException hbe ) {
				logger.error("Unable to lock pin for subscriber id: " + subscriberId + "; recharge can not be processed") ;
				transaction.rollback() ;
				transaction = null ;
				return rc = PIN_IN_USE ;
			}
			transaction = session.beginTransaction() ;
			
			/* perform the transaction */
			ECarePrepaidPaymentechResponse res = null ;
			Float fAmount = Float.valueOf(strAmount) ;
			try {
				Config cfg = (Config) framework.getResource("wsConfig") ;
				PrimusWS pws = new PrimusWS( cfg.getWebServiceEndoint(), cfg.getWsdlLocation() ) ;
				Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader() ) ;
				res = pws.prepaidPaymentTechPayment(strUserId, strClientId, strBusinessUnit, strSubscriberPhone, strCardNumber, strExpiryDate, 
						strCardType, fAmount, strNameOnCard, strAddress1, strAddress2, strCity, strProvince, strPostalCode, strReason, strTransactionId) ;
			} catch( SOAPFaultException sfe ) {
				logger.error("SOAP Fault calling prepaid_paymentech_payment: " + sfe.getLocalizedMessage() ) ;
				transaction.rollback() ;
				transaction = null ;
				return rc = getWsFailureReason( sfe ) ;
			} catch( Exception e ) {
				logger.error("Error calling primus web service prepaid_paymentech_payment") ;
				logger.error(e) ;
				transaction.rollback() ;
				transaction = null ;
				return rc = WS_ERROR ;
			}

			strAuthorizationCode.append( res.getAuthorizationcode() ) ;
			if( null != res.getResultcode() ) resultCode.setInteger( Integer.valueOf( res.getResultcode() ) ) ;
			strResultDescription.append(res.getResultdescription() ) ;
			strAvsCode.append( res.getAvsresultcode() ) ;
			strAvsDescription.append( res.getAvsresultdescription() ) ;
			strInquiryId.append( res.getInquiryid() ) ;
			
			if( SUCCESS == resultCode.getInteger() ) {
				updateSubscriberWithSuccessfulRecharge(session, logger, sub, currency, strTransactionId, res.getAuthorizationcode(), strBusinessUnit, 
						fAmount, "P" ) ;
				rc = SUCCESS ;

			}
			else {
				rc = CC_DECLINED ;
			}
			
			logger.info("Result code: " + resultCode.getInteger()) ;
			logger.info("Result description: " + strResultDescription.toString()) ;
			logger.info("Authorization code: " + strAuthorizationCode.toString()) ;
			logger.info("AVS code:           " +  strAvsCode.toString()) ;
			logger.info("AVS description:    " +  strAvsDescription.toString()) ;
			logger.info("InquiryId:          " +  strInquiryId.toString()) ;
			
	   		logger.info("--------------   XtmlApi.ProcessRechargePaymentTech   --  Ending  ------------------");				
			
		} catch (Exception ex) {
			Framework.getInstance().getLogger().error("Caught exception in XtmlApi.ProcessRechargePaymentTech", ex);
			if( null != transaction ) {
				transaction.rollback() ;
				transaction = null ;
			}
		} finally {
			if( null != transaction ) {
				transaction.commit() ;
			}
			if( null != lock ) {
				try {
					transaction = session.beginTransaction() ;
					session.delete(lock) ;
					transaction.commit() ;
				} catch( Exception ex2 ) {
					logger.error("Unable to unlock pin", ex2) ;
				}
			}
			
			if( null != session ) session.close() ;
		}			
		return rc ;
	}

	static public int GetPrepaidBalance( 
			String xtmlSessionId,
			String strSubscriberPhone,
			MutableFloat balance, 
			StringBuffer status,
			MutableInteger statusCode,
			StringBuffer message)  {
		
		Framework framework = null;
		Log logger = null;
		int rc = DB_ERROR;
		
		status.setLength(0) ;
		message.setLength(0) ;
		statusCode.setInteger(0) ;
		
		try {
			
			framework = Framework.getInstance(xtmlSessionId) ;
			logger = framework.getLogger();
	   		
			ECarePrepaidBalanceResponse res = null ;
			try {
				Config cfg = (Config) framework.getResource("wsConfig") ;
				PrimusWS pws = new PrimusWS( cfg.getWebServiceEndoint(), cfg.getWsdlLocation() ) ;
				Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader() ) ;
				res = pws.getPrepaidBalance(strSubscriberPhone) ;
			} catch( SOAPFaultException sfe ) {
				logger.error("SOAP Fault calling GetPrepaidBalance: " + sfe.getLocalizedMessage() ) ;
				return rc = getWsFailureReason( sfe ) ;
			} catch( Exception e ) {
				logger.error("Error calling primus web service GetPrepaidBalance") ;
				logger.error(e) ;
				return rc = WS_ERROR ;
			}

			status.append( res.getStatus() ) ;
			message.append( res.getMessage() ) ;
			statusCode.setInteger( res.getStatuscode() ) ;
			balance.setFloat( res.getBalance() ) ;
			
			
			logger.info("Status code: " + statusCode.getInteger()) ;
			logger.info("Message: " + message.toString()) ;
			logger.info("Status: " + status.toString()) ;
			logger.info("Balance: " + balance.getFloat() ) ;
	   		logger.info("--------------   XtmlApi.GetPrepaidBalance   --  Ending  ------------------");				
			
		} catch (Exception ex) {
			Framework.getInstance().getLogger().error("Caught exception in XtmlApi.GetPrepaidBalance", ex);
		} finally {
		}			
		return rc ;
	}
	
	private static void Test() throws MalformedURLException {
		
		String xtmlSessionId = "test@test" ;
		String strSubscriberPhone = "6477251347" ;
		MutableFloat balance = new MutableFloat(0) ;
		StringBuffer status = new StringBuffer() ;
		StringBuffer message = new StringBuffer() ;
		MutableInteger statusCode = new MutableInteger() ;
		
		//GetPrepaidBalance(xtmlSessionId, strSubscriberPhone, balance, status, statusCode, message) ;
		
		Framework framework = Framework.getInstance("") ;
		Config cfg = (Config) framework.getResource("wsConfig") ;
		PrimusWS pws = new PrimusWS( cfg.getWebServiceEndoint(), cfg.getWsdlLocation() ) ;
		/*		
		try {
			//Class cls = Class.forName("com.sun.xml.bind.ContextFactory") ;
			Class cls = Class.forName("com.sun.xml.bind.v2.ContextFactory") ;
			ClassLoader loader = cls.getClassLoader() ;
			System.out.println("class loader: " + loader.toString() ) ;
			Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader() ) ;
			JAXBContext bindingContext = JAXBContext.newInstance(com.soaplite.namespaces.perl.ECare.class);
			
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		*/
		
		String strDnis = "2005001004" ;
		MutableLong mlSubscriberId = new MutableLong() ;
		MutableInteger miStatus = new MutableInteger() ;
		StringBuffer strLanguage = new StringBuffer() ;
		MutableFloat mfBalance = new MutableFloat() ;
		MutableBoolean mbPlayAsUnits = new MutableBoolean() ;
		MutableFloat mfMaxRechargeAmount = new MutableFloat() ;
		MutableFloat mfMinRechargeAmount = new MutableFloat() ;
		StringBuffer strBusinessUnit = new StringBuffer() ;

		int rc = InitializeRechargeSession(xtmlSessionId, 
				strDnis, 
				strSubscriberPhone, 
				mlSubscriberId, 
				miStatus, 
				strLanguage, 
				mfBalance, 
				mbPlayAsUnits, 
				mfMaxRechargeAmount,
				mfMinRechargeAmount, 
				strBusinessUnit) ;
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yy-mm-dd-HHmmss") ;
		String strTransactionId = "PCS-" + sdf.format( Dao.getCurrentTimestamp()) ;
		String voucherValue = "50.0" ;
		String voucher = "9999991533755379281" ;
		StringBuffer strTransactionCode = new StringBuffer() ;
		StringBuffer strTransactionDesc = new StringBuffer() ;
		MutableFloat fSettlementAmount = new MutableFloat() ;
		MutableInteger errorCode = new MutableInteger() ;
		StringBuffer strErrorDesc = new StringBuffer() ;
		StringBuffer strUkashTransactionId = new StringBuffer() ;
		
		
		rc = ProcessRechargeUkash(xtmlSessionId, mlSubscriberId.getLong(), strSubscriberPhone, 
				"PCS-AS", "", strBusinessUnit.toString(), voucher, voucherValue, strTransactionId, 
				strTransactionCode, strTransactionDesc, fSettlementAmount, errorCode, strErrorDesc, strUkashTransactionId) ;
		
		if( true ) return ;
		
		String strCardNumber = "5454545454545454" ;
		String strExpiryDate = "201312" ;
		String strCardType = "MC" ;
		String strNameOnCard = "David C Horton" ;
		String strReason = "purchase" ;
		String strAddress1 = "308 Maple St" ;
		String strAddress2 = "" ;
		String strCity = "Mansfield" ;
		String strProvince = "MA" ;
		String strPostalCode = "02048" ;
		StringBuffer strAuthorizationCode = new StringBuffer() ;
		MutableInteger resultCode = new MutableInteger() ;
		StringBuffer strResultDescription = new StringBuffer() ;
		StringBuffer strAvsCode = new StringBuffer() ;
		StringBuffer strAvsDescription = new StringBuffer() ;
		StringBuffer strInquiryId = new StringBuffer() ;
		
		
		rc = ProcessRechargePaymentTech(xtmlSessionId, mlSubscriberId.getLong(), strSubscriberPhone, 
				"PCS-AS", "", strBusinessUnit.toString(), strCardNumber, strExpiryDate, strCardType, voucherValue, 
				strNameOnCard, strAddress1, strAddress2, strCity, strProvince, strPostalCode, strReason, strTransactionId, 
				strAuthorizationCode, resultCode, strResultDescription, strAvsCode, strAvsDescription, strInquiryId ) ;

		
		ECarePrepaidUkashResponse ukashResult = null ;
		try {
			ukashResult = pws.prepaidUkashPayment("userId", "clientId", "win", "5083084809", "9999991530094712388", 50.0f, "CAD", "123456") ;
		} catch (SOAPFaultException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("ukash result: " + ukashResult.getErrorcode() ) ;
				
        //Thread.currentThread().setContextClassLoader(existing);

	}
	public static void main(String[] args) {
		
		
		//XtmlApi.Init() ;
		
		try {
			XtmlApi.Test() ;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ;
		
	}
	
}
