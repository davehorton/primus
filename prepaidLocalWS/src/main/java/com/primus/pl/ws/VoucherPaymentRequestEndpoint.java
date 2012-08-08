package com.primus.pl.ws; 


import java.math.BigInteger;

import javax.annotation.Resource;

import com.primus.pl.service.ProvisioningService;
import com.primus.pl.xml.ActivationRequest;
import com.primus.pl.xml.ActivationResponse;
import com.primus.pl.xml.VoucherPaymentRequest;
import com.primus.pl.xml.VoucherPaymentResponse;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import org.apache.log4j.Logger;

@Endpoint
public class VoucherPaymentRequestEndpoint  {

	private static final String NAMESPACE_URI = "http://primus.com/prepaidLocal/schemas";
	
	protected static Logger logger =Logger.getLogger(VoucherPaymentRequestEndpoint.class) ;

	@Resource(name="provisioningService")
    private final ProvisioningService provisioningService = null ;

    public static final String REQUEST_LOCAL_NAME = "voucherPaymentRequest";
        
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = REQUEST_LOCAL_NAME)   
    @ResponsePayload
    public VoucherPaymentResponse processActivationRequest(@RequestPayload VoucherPaymentRequest req) throws Exception {     
    	VoucherPaymentResponse res = null ;
    	try {
    		res = provisioningService.processVoucherPaymentRequest(req) ;
    	} catch( Exception e ) {
    		logger.error("Failed to process voucher request:") ;
    		logger.error(e) ;
    		e.printStackTrace() ;
    		VoucherPaymentResponse resFail = new VoucherPaymentResponse() ;
    		resFail.setCode( -99 )  ;
    		resFail.setMessage("voucher payment request failed") ;
    		return resFail ;
    	}
		return res ;
    }
}