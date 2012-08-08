package com.primus.pl.ws; 



import javax.annotation.Resource;

import com.primus.pl.service.ProvisioningService;
import com.primus.pl.xml.SubscriberQueryRequest;
import com.primus.pl.xml.SubscriberQueryResponse;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import org.apache.log4j.Logger;

@Endpoint
public class SubscriberQueryRequestEndpoint  {

	private static final String NAMESPACE_URI = "http://primus.com/prepaidLocal/schemas";
	
	protected static Logger logger = Logger.getLogger(SubscriberQueryRequestEndpoint.class) ;

	@Resource(name="provisioningService")
    private final ProvisioningService provisioningService = null ;

    public static final String REQUEST_LOCAL_NAME = "subscriberQueryRequest";
        
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = REQUEST_LOCAL_NAME)   
    @ResponsePayload
    public SubscriberQueryResponse processSubscriberQueryRequest(@RequestPayload SubscriberQueryRequest req) throws Exception {     
    	SubscriberQueryResponse res = null ;
    	try {
    		res = provisioningService.querySubscriberAttribute(  req );
    	} catch( Exception e ) {
    		logger.error("Failed to process activation request:") ;
    		logger.error(e) ;
    		e.printStackTrace() ;
    		SubscriberQueryResponse resFail = new SubscriberQueryResponse() ;
    		resFail.setCode( -99 )  ;
    		return resFail ;
    	}
		return res ;
    }
}