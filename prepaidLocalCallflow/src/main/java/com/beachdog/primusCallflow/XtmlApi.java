package com.beachdog.primusCallflow;

import java.util.List;
import java.util.Properties;

import javax.xml.ws.soap.SOAPFaultException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.pactolus.java.*;

import org.sipdev.commons.mutables.MutableFloat;
import org.sipdev.commons.mutables.MutableInt;
import org.sipdev.framework.Framework;
import org.sipdev.framework.Log;

import uri.ecare.GetPrepaidBalanceDlResponse.ECarePrepaidBalanceResponse;


import com.beachdog.primusCore.Constants;
import com.beachdog.primusCore.Config;
import com.beachdog.primusCore.Dao;
import com.beachdog.primusCore.PrimusWS;
import com.beachdog.primusCore.model.AccessNumber;
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
			StringBuffer strBusinessUnit, 
			StringBuffer strServiceProviderName)  {
		
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
		strServiceProviderName.setLength(0) ;
		
		
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
			strServiceProviderName.append(sp.getName()) ;
			
			
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
	
	static public int ProcessRechargeUkash( 
			String xtmlSessionId,
			long subscriberId,
			String strSubscriberPhone,
			String strServiceProviderName,
			String strUserId,
			String strClientId,
			String strBusinessUnit,
			String strUkashVoucher,
			String strUkashVoucherValue,
			String strTransactionId,
			StringBuffer strTransactionCode,
			StringBuffer strTransactionDesc,
			MutableFloat fSettlementAmount,
			MutableInt errorCode,
			StringBuffer strErrorDesc,
			StringBuffer strUkashTransactionId)   {
		
		Framework framework = null;
		Log logger = null;
		int rc = DB_ERROR;
				
		try {
			
			framework = Framework.getInstance(xtmlSessionId) ;
			logger = framework.getLogger();

    		logger.info("--------------   XTMLApi.ProcessRechargeUkash   --  Starting  ------------------");
			logger.info("subscriberId:          	 " + subscriberId) ;
			logger.info("strUserId: 			     " + strUserId) ;
			logger.info("strClientId: 			     " + strClientId) ;
			logger.info("strBusinessUnit: 			 " + strBusinessUnit) ;
			logger.info("strUkashVoucher: 			 " + strUkashVoucher) ;
			logger.info("strSubscriberPhone: 		 " + strSubscriberPhone) ;
			logger.info("strUkashVoucherValue: 		 " + strUkashVoucherValue) ;

			StringBuffer msg = new StringBuffer() ;
			
			rc = Dao.processRechargeTransactionUkash(strSubscriberPhone, 
					strServiceProviderName, 
					strUserId, 
					strClientId, 
					strUkashVoucher, 
					Double.valueOf( strUkashVoucherValue ),
					strTransactionId, 
					strTransactionCode, strTransactionDesc, 
					fSettlementAmount, errorCode, strErrorDesc, 
					strUkashTransactionId, msg) ;

			logger.info("Error code: " + errorCode.getInteger()) ;
			logger.info("Error description: " + strErrorDesc.toString()) ;
			logger.info("Settlement amount: " + fSettlementAmount.getFloat()) ;
			logger.info("Transaction code: " + strTransactionCode.toString()) ;
			logger.info("Ukash transaction id: " + strUkashTransactionId.toString()) ;
	   		logger.info("--------------   XtmlApi.ProcessRechargeUkash   --  Ending  ------------------");				
			
	
		} catch (Exception ex) {
			Framework.getInstance().getLogger().error("Caught exception in XtmlApi.ProcessRechargeUkash", ex);
		} finally {
		}			
		
		return rc ;
	}

	
	public int ProcessRechargePaymentTech( 
			String xtmlSessionId,
			Long subscriberId,
			String strSubscriberPhone,
			String strServiceProviderName,
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
			MutableInt resultCode,
			StringBuffer strResultDescription,
			StringBuffer strAvsCode,
			StringBuffer strAvsDescription,
			StringBuffer strInquiryId) {


		Framework framework = null;
		Log logger = null;
		int rc = DB_ERROR;
				
		try {
			
			framework = Framework.getInstance(xtmlSessionId) ;
			logger = framework.getLogger();
	
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

			StringBuffer msg = new StringBuffer() ;
			
			rc = Dao.processRechargeTransactionCC( strSubscriberPhone, 
					strServiceProviderName, 
					strUserId, strClientId, 
					strCardType, strCardNumber, strExpiryDate, Double.valueOf( strAmount ), 
					strNameOnCard, 
					strAddress1, strAddress2, strCity, 
					strProvince, strPostalCode, strTransactionId, 
					strAuthorizationCode, resultCode, strResultDescription, strAvsCode, strAvsDescription, strInquiryId, msg) ;
			
			
			logger.info("END***********ProvisioningService: ProcessRechargePaymentTech") ;
			logger.info("code: " + rc ) ;
			logger.info("msg: " + msg.toString() ) ;
			logger.info("authorizationCode: " + strAuthorizationCode.toString() ) ;
			logger.info("resultCode: " + resultCode.getInt() ) ;
			logger.info("resultDescription: " + strResultDescription.toString() ) ;
			logger.info("inquiryId: " + strInquiryId.toString() ) ;
			logger.info("avsResultCode: " + strAvsCode.toString() ) ;
			logger.info("avsResultDescription: " + strAvsDescription.toString() ) ;
		
		} catch (Exception ex) {
			Framework.getInstance().getLogger().error("Caught exception in XtmlApi.ProcessRechargePaymentTech", ex);
		} finally {
		}			


		return rc ;
	}

	/*
	private static void Test() throws MalformedURLException {
		
		String xtmlSessionId = "test@test" ;
		String strSubscriberPhone = "6477251347" ;
		MutableFloat balance = new MutableFloat(0) ;
		StringBuffer status = new StringBuffer() ;
		StringBuffer message = new StringBuffer() ;
		MutableInteger statusCode = new MutableInteger() ;
		
		
		Framework framework = Framework.getInstance("") ;
		Config cfg = (Config) framework.getResource("wsConfig") ;
		PrimusWS pws = new PrimusWS( cfg.getWebServiceEndoint(), cfg.getWsdlLocation() ) ;
		
		String strDnis = "2005001004" ;
		MutableLong mlSubscriberId = new MutableLong() ;
		MutableInteger miStatus = new MutableInteger() ;
		StringBuffer strLanguage = new StringBuffer() ;
		MutableFloat mfBalance = new MutableFloat() ;
		MutableBoolean mbPlayAsUnits = new MutableBoolean() ;
		MutableFloat mfMaxRechargeAmount = new MutableFloat() ;
		MutableFloat mfMinRechargeAmount = new MutableFloat() ;
		StringBuffer strBusinessUnit = new StringBuffer() ;
		StringBuffer strServiceProviderName = new StringBuffer() ;

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
				strBusinessUnit, 
				strServiceProviderName) ;
		
		
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
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		System.out.println("ukash result: " + ukashResult.getErrorcode() ) ;
				
        //Thread.currentThread().setContextClassLoader(existing);

	}
	*/
	public static void main(String[] args) {
		
		/*
		XtmlApi.Init() ;
		
		try {
			XtmlApi.Test() ;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ;
		*/
	}
	
}
