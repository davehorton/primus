package com.primus.pl.service;


import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.sipdev.commons.mutables.MutableFloat;
import org.sipdev.commons.mutables.MutableInt;

import com.beachdog.primusCore.Dao;

import com.primus.pl.ws.*;
import com.primus.pl.xml.*;


public class ProvisioningService {

	private final int SUCCESS = 0 ;
	
	private static final int INVALID_LOT_ID = -1;
	private static final int INVALID_NUMBER_PINS = -2;
	private static final int NO_AVAILABLE_GROUP = -3;
	private static final int DB_ERROR = -98;
	private static final int OTHER_ERROR = -99;

	protected static Logger logger =Logger.getLogger(ActivationRequestEndpoint.class) ;
	

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
			resp.setCode(rc) ;
			resp.setMessage(msg.toString()) ;
			return resp ;
		}
				
		if( subscriberId.length() > 0 ) {
			resp.setSubscriberId(Long.valueOf(subscriberId.toString())) ;
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
		resp.setCode(0) ;
		resp.setMessage("Successfully processed voucher request") ;
		
		MutableInt errorCode = new MutableInt() ;
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
		v.setSettleAmount( f.doubleValue()) ;
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
		res.setCode(0) ;
		res.setMessage("Successfully processed credit card request") ;
		
		StringBuffer authorizationCode = new StringBuffer() ;
		MutableInt resultCode = new MutableInt() ;
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
		v.setResultCode( Integer.toString(resultCode.getInt(), 10 ) ) ;
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

}
