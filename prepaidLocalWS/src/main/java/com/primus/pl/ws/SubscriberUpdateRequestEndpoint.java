package com.primus.pl.ws; 


import java.math.BigInteger;

import javax.annotation.Resource;

import com.primus.pl.service.ProvisioningService;
import com.primus.pl.xml.SubscriberUpdateRequest;
import com.primus.pl.xml.SubscriberUpdateResponse;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import org.apache.log4j.Logger;

@Endpoint
public class SubscriberUpdateRequestEndpoint  {

	private static final String NAMESPACE_URI = "http://primus.com/prepaidLocal/schemas";
	
	protected static Logger logger = Logger.getLogger(SubscriberUpdateRequestEndpoint.class) ;

	@Resource(name="provisioningService")
    private final ProvisioningService provisioningService = null ;

    public static final String REQUEST_LOCAL_NAME = "subscriberUpdateRequest";
        
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = REQUEST_LOCAL_NAME)   
    @ResponsePayload
    public SubscriberUpdateResponse processSubscriberQueryRequest(@RequestPayload SubscriberUpdateRequest req) throws Exception {     
    	SubscriberUpdateResponse res = null ;
    	try {
    		res = provisioningService.updateSubscriberAttribute(  req );
    	} catch( Exception e ) {
    		logger.error("Failed to process activation request:") ;
    		logger.error(e) ;
    		e.printStackTrace() ;
    		SubscriberUpdateResponse resFail = new SubscriberUpdateResponse() ;
    		resFail.setCode( -99 )  ;
    		return resFail ;
    	}
		return res ;
    }
}