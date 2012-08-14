package com.primus.pl.service;

import org.apache.log4j.Logger;
import org.sipdev.framework.Framework;

import com.pactolus.java.*;

import com.beachdog.primusCore.Config;
import com.beachdog.primusCore.Dao;
import com.beachdog.primusCore.Utilities;

import com.primus.pl.xml.*;


public class ProvisioningService {

	private final int SUCCESS = 0 ;
	
	protected static Logger logger =Logger.getLogger(ProvisioningService.class) ;
	

	public ProvisioningService() {
	}


	public ActivationResponse activateAccount( ActivationRequest req ) {
		
		Long lotId = req.getLotId() ;
		Double initialBalance = req.getInitialBalance() ;
		String phone = req.getSubscriberPhone() ;
		
		logger.info("START***********ProvisioningService: activateAccount") ;
		logger.info("Lot id: " + lotId) ;
		logger.info("Initial balance: " + initialBalance ) ;
		logger.info("Subscriber phone: " + phone) ;
		
		StringBuffer msg = new StringBuffer() ;
		StringBuffer subscriberId = new StringBuffer() ;
		StringBuffer pin = new StringBuffer() ;
		

		ActivationResponse resp = new ActivationResponse() ;
		resp.setMessage("Account was successfully provisioned") ;
		resp.setCode(SUCCESS) ;

		int rc = Dao.activateSinglePinFromLot(lotId, phone, initialBalance, msg, subscriberId, pin) ;
		if( SUCCESS != rc ) {
			logger.info("activateAccount failed: " + msg.toString() + " (" + rc + ")" ) ;
			resp.setCode( rc ) ;
			resp.setMessage(msg.toString()) ;
			return resp ;
		}
				
		if( subscriberId.length() > 0 ) {
			resp.setSubscriberId( Long.valueOf( subscriberId.toString() ) ) ;
		}
		if( pin.length() > 0 ) {
			resp.setSubscriberPin( pin.toString() ) ;
		}
		
		logger.info("activateAccount succeeded:") ;
		logger.info("subscriberId: " + subscriberId.toString()) ;
		logger.info("pin: " + pin.toString() ) ;
		
		return resp;
	}

	public VoucherPaymentResponse processVoucherPaymentRequest( VoucherPaymentRequest req ) {
		VoucherPaymentResponse resp = new VoucherPaymentResponse() ;
		resp.setCode(SUCCESS) ;
		resp.setMessage("Successfully processed voucher request") ;
		
		MutableInteger errorCode = new MutableInteger() ;
		StringBuffer errorDescription = new StringBuffer() ;
		StringBuffer transactionCode = new StringBuffer() ;
		StringBuffer transactionDescription = new StringBuffer() ;
		StringBuffer ukashTransactionId = new StringBuffer() ;
		MutableFloat settlementAmount = new MutableFloat(0.0f) ;
		StringBuffer msg = new StringBuffer() ;

		logger.info("START***********ProvisioningService: processVoucherPaymentRequest") ;
		logger.info("SP name: " + req.getSubscriber().getServiceProviderName() ) ;
		logger.info("sub phone: " + req.getSubscriber().getPhone()) ;
		logger.info("userId: " + req.getRequestor().getUserId()) ;
		logger.info("clientId: " + req.getRequestor().getClientId()) ;
		logger.info("voucher number: " + req.getVoucher().getVoucherNumber()) ;
		logger.info("voucher value: " + req.getVoucher().getVoucherValue()) ;
		logger.info("transaction id: " + req.getRequestor().getTransactionId() ) ;

		int rc = Dao.processRechargeTransactionUkash(req.getSubscriber().getPhone(), 
				req.getSubscriber().getServiceProviderName(), 
				req.getRequestor().getUserId(), 
				req.getRequestor().getClientId(), 
				req.getVoucher().getVoucherNumber(), 
				req.getVoucher().getVoucherValue(),
				req.getRequestor().getTransactionId(), 
				transactionCode, transactionDescription, 
				settlementAmount, errorCode, errorDescription, 
				ukashTransactionId, msg) ;
		
		VoucherProcessorResponse v = new VoucherProcessorResponse() ;
		v.setErrorCode(String.valueOf(errorCode.intValue()) ) ;
		v.setErrorDescription( errorDescription.toString() ) ;
		v.setTransactionCode( transactionCode.toString() ) ;
		v.setTransactionDescription( transactionDescription.toString() ) ;
		Float f = settlementAmount.floatValue() ;
		v.setSettleAmount( f.doubleValue() ) ;
		v.setVendorTransactionId( ukashTransactionId.toString() ) ;
		
		resp.setDetails(v) ;

		resp.setCode(rc) ;
		resp.setMessage(msg.toString()) ;

		logger.info("END***********ProvisioningService: processVoucherPaymentRequest") ;
		logger.info("code: " + resp.getCode() ) ;
		logger.info("msg: " + resp.getMessage() ) ;
		logger.info("errorCode: " + v.getErrorCode()) ;
		logger.info("errorDescription: " + v.getErrorDescription()) ;
		logger.info("transactionCode: " + v.getTransactionCode()) ;
		logger.info("transactionDescription: " + v.getTransactionDescription()) ;
		logger.info("vendorTransactionId: " + v.getVendorTransactionId()) ;
		logger.info("settleAmount: " + v.getSettleAmount()) ;
		

		return resp ;
	}

	public CreditCardPaymentResponse processCreditCardPaymentRequest( CreditCardPaymentRequest req ) {

		CreditCardPaymentResponse res = new CreditCardPaymentResponse() ;
		res.setCode(SUCCESS) ;
		res.setMessage("Successfully processed credit card request") ;
		
		StringBuffer authorizationCode = new StringBuffer() ;
		MutableInteger resultCode = new MutableInteger() ;
		StringBuffer resultDescription = new StringBuffer() ;
		StringBuffer avsCode = new StringBuffer() ;
		StringBuffer avsDescription = new StringBuffer() ;
		StringBuffer inquiryId = new StringBuffer() ;
		StringBuffer msg = new StringBuffer() ;
		
		int nDigits = req.getCard().getCardNumber().length() ;
		String last4 = req.getCard().getCardNumber().substring(nDigits - 4) ;

		logger.info("START***********ProvisioningService: processCreditCardPaymentRequest") ;
		logger.info("SP name: " + req.getSubscriber().getServiceProviderName() ) ;
		logger.info("sub phone: " + req.getSubscriber().getPhone()) ;
		logger.info("userId: " + req.getRequestor().getUserId()) ;
		logger.info("clientId: " + req.getRequestor().getClientId()) ;
		logger.info("transaction id: " + req.getRequestor().getTransactionId() ) ;
		logger.info("card type: " + req.getCard().getCardType()) ;
		logger.info("last 4 digits: " + last4) ;
		logger.info("expiry: " + req.getCard().getExpiryDate() ) ;
		logger.info("amount: " + req.getCard().getAmount()) ;
		logger.info("name: " + req.getCard().getNameOnCard()) ;
		logger.info("address 1: " + req.getCard().getAddressLine1()) ;
		logger.info("address 2: " + req.getCard().getAddressLine2()) ;
		logger.info("city: " + req.getCard().getCity()) ;
		logger.info("province: " + req.getCard().getProvince()) ;
		logger.info("postalCode: " + req.getCard().getPostalCode()) ;
		

		int rc = Dao.processRechargeTransactionCC(req.getSubscriber().getPhone(), 
				req.getSubscriber().getServiceProviderName(), 
				req.getRequestor().getUserId(), req.getRequestor().getClientId(), 
				req.getCard().getCardType(), req.getCard().getCardNumber(), req.getCard().getExpiryDate(), req.getCard().getAmount(), 
				req.getCard().getNameOnCard(), 
				req.getCard().getAddressLine1(), req.getCard().getAddressLine2(), req.getCard().getCity(), 
				req.getCard().getProvince(), req.getCard().getPostalCode(), req.getRequestor().getTransactionId(), 
				authorizationCode, resultCode, resultDescription, avsCode, avsDescription, inquiryId, msg) ;
		
		res.setCode(rc) ;
		if( SUCCESS != rc ) res.setMessage(msg.toString()); 
		
		CreditCardProcessorResponse v = new CreditCardProcessorResponse() ;
		v.setAuthorizationCode( authorizationCode.toString() ) ;
		v.setAvsResultCode( avsCode.toString() ) ;
		v.setAvsResultDescription( avsDescription.toString() ) ;
		v.setResultCode( Integer.toString(resultCode.getInteger(), 10 ) ) ;
		v.setResultDescription( resultDescription.toString() ) ;
		v.setInquiryId( inquiryId.toString() ) ;

		res.setDetails(v) ;
		
		logger.info("END***********ProvisioningService: processCreditCardPaymentRequest") ;
		logger.info("code: " + res.getCode() ) ;
		logger.info("msg: " + res.getMessage() ) ;
		logger.info("authorizationCode: " + v.getAuthorizationCode() ) ;
		logger.info("resultCode: " + v.getResultCode() ) ;
		logger.info("resultDescription: " + v.getResultDescription()) ;
		logger.info("inquiryId: " + v.getInquiryId()) ;
		logger.info("avsResultCode: " + v.getAvsResultCode()) ;
		logger.info("avsResultDescription: " + v.getAvsResultDescription() ) ;
		

		return res ;
	}

	public SubscriberQueryResponse querySubscriberAttribute( SubscriberQueryRequest req ) {

		SubscriberQueryResponse res = new SubscriberQueryResponse() ;
		res.setCode(SUCCESS) ;
		
		logger.info("START***********ProvisioningService: querySubscriberAttribute") ;
		logger.info("SP name: " + req.getSubscriber().getServiceProviderName() ) ;
		logger.info("sub phone: " + req.getSubscriber().getPhone()) ;
		logger.info("attribute: " + req.getAttribute() ) ;

		StringBuffer value = new StringBuffer() ;
		int rc = Dao.querySubscriberAttribute(req.getSubscriber().getServiceProviderName(), req.getSubscriber().getPhone(), req.getAttribute(), value) ;
		
		res.setCode(rc) ;
		res.setValue( value.toString() ) ;
		
		
		logger.info("END***********ProvisioningService: querySubscriberAttribute") ;
		logger.info("code: " + res.getCode() ) ;
		logger.info("value: " + res.getValue() ) ;
		

		return res ;
	}
	public SubscriberUpdateResponse updateSubscriberAttribute( SubscriberUpdateRequest req ) {

		SubscriberUpdateResponse res = new SubscriberUpdateResponse() ;
		res.setCode(SUCCESS) ;
		
		logger.info("START***********ProvisioningService: updateSubscriberAttribute") ;
		logger.info("SP name: " + req.getSubscriber().getServiceProviderName() ) ;
		logger.info("sub phone: " + req.getSubscriber().getPhone()) ;
		logger.info("attribute: " + req.getAttribute() ) ;

		int rc = Dao.updateSubscriberAttribute(req.getSubscriber().getServiceProviderName(), req.getSubscriber().getPhone(), req.getAttribute(), req.getValue() ) ;
		
		res.setCode(rc) ;
		
		
		logger.info("END***********ProvisioningService: updateSubscriberAttribute") ;
		logger.info("code: " + res.getCode() ) ;
		

		return res ;
	}
	public M6ModifyResponse modifyM6Subscriber(  M6ModifyRequest req ) {

		M6ModifyResponse res = new M6ModifyResponse() ;
		res.setCode(SUCCESS) ;
		
		logger.info("START***********ProvisioningService: querySubscriberAttribute") ;
		logger.info("phone: " + req.getPhone() ) ;
		logger.info("suspend: " + req.isSuspend() ) ;

		Framework framework = Framework.getInstance("beans.xml") ;
		Config cfg = (Config) framework.getResource("wsConfig") ;
		Utilities.M6Credential c = Utilities.getM6Credential(cfg, req.getPhone() ) ;

		StringBuffer msg = new StringBuffer() ;
		
		boolean rc = Dao.modifyM6Subscriber(req.getPhone() , req.isSuspend(), c.address, c.username, c.password, msg ) ;
		
		res.setCode(rc ? 0 : -1) ;
		res.setMessage( msg.toString() ) ;
		
		
		logger.info("END***********ProvisioningService: querySubscriberAttribute") ;
		logger.info("code: " + res.getCode() ) ;
		logger.info("msg: " + res.getMessage() ) ;
		

		return res ;
	}

}
