package com.primus.pl.ws; 


import java.math.BigInteger;

import javax.annotation.Resource;

import com.primus.pl.service.ProvisioningService;
import com.primus.pl.xml.ActivationRequest;
import com.primus.pl.xml.ActivationResponse;
import com.primus.pl.xml.CreditCardPaymentRequest;
import com.primus.pl.xml.CreditCardPaymentResponse;
import com.primus.pl.xml.VoucherPaymentRequest;
import com.primus.pl.xml.VoucherPaymentResponse;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import org.apache.log4j.Logger;

@Endpoint
public class CreditCardPaymentRequestEndpoint  {

	private static final String NAMESPACE_URI = "http://primus.com/prepaidLocal/schemas";
	
	protected static Logger logger =Logger.getLogger(CreditCardPaymentRequestEndpoint.class) ;

	@Resource(name="provisioningService")
    private final ProvisioningService provisioningService = null ;

    public static final String REQUEST_LOCAL_NAME = "creditCardPaymentRequest";
        
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = REQUEST_LOCAL_NAME)   
    @ResponsePayload
    public CreditCardPaymentResponse processActivationRequest(@RequestPayload CreditCardPaymentRequest req) throws Exception {     
    	CreditCardPaymentResponse res = null ;
    	try {
    		res = provisioningService.processCreditCardPaymentRequest(req) ;
    	} catch( Exception e ) {
    		logger.error("Failed to process credit card payment request:") ;
    		logger.error(e) ;
    		e.printStackTrace() ;
    		CreditCardPaymentResponse resFail = new CreditCardPaymentResponse() ;
    		resFail.setCode( -99 )  ;
    		resFail.setMessage("credit card payment request failed") ;
    		return resFail ;
    	}
		return res ;
    }
}