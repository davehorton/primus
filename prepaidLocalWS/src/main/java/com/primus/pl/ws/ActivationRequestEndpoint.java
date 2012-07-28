package com.primus.pl.ws; 


import javax.annotation.Resource;

import com.primus.pl.service.ProvisioningService;
import com.primus.pl.xml.ActivationRequest;
import com.primus.pl.xml.ActivationResponse;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import org.apache.log4j.Logger;

@Endpoint
public class ActivationRequestEndpoint  {

	private static final String NAMESPACE_URI = "http://primus.com/prepaidLocal/schemas";
	
	protected static Logger logger =Logger.getLogger(ActivationRequestEndpoint.class) ;

	@Resource(name="provisioningService")
    private final ProvisioningService provisioningService = null ;

    public static final String REQUEST_LOCAL_NAME = "activationRequest";
        
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = REQUEST_LOCAL_NAME)   
    @ResponsePayload
    public ActivationResponse processActivationRequest(@RequestPayload ActivationRequest req) throws Exception {     
    	ActivationResponse res = null ;
    	try {
    		res = provisioningService.activateAccount(  req );
    	} catch( Exception e ) {
    		logger.error("Failed to process activation request:") ;
    		logger.error(e) ;
    		e.printStackTrace() ;
    		ActivationResponse resFail = new ActivationResponse() ;
    		resFail.setCode( -99)  ;
    		resFail.setMessage("activation request failed") ;
    		return resFail ;
    	}
		return res ;
    }
}