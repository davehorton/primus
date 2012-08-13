package com.primus.pl.ws; 



import javax.annotation.Resource;

import com.primus.pl.service.ProvisioningService;
import com.primus.pl.xml.M6ModifyRequest;
import com.primus.pl.xml.M6ModifyResponse;
import com.primus.pl.xml.SubscriberQueryRequest;
import com.primus.pl.xml.SubscriberQueryResponse;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import org.apache.log4j.Logger;

@Endpoint
public class M6ModifyRequestEndpoint  {

	private static final String NAMESPACE_URI = "http://primus.com/prepaidLocal/schemas";
	
	protected static Logger logger = Logger.getLogger(M6ModifyRequestEndpoint.class) ;

	@Resource(name="provisioningService")
    private final ProvisioningService provisioningService = null ;

    public static final String REQUEST_LOCAL_NAME = "m6ModifyRequest";
        
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = REQUEST_LOCAL_NAME)   
    @ResponsePayload
    public M6ModifyResponse processM6ModifyRequest(@RequestPayload M6ModifyRequest req) throws Exception {     
    	M6ModifyResponse res = null ;
    	try {
    		res = provisioningService.modifyM6Subscriber(  req );
    	} catch( Exception e ) {
    		logger.error("Failed to process activation request:") ;
    		logger.error(e) ;
    		e.printStackTrace() ;
    		M6ModifyResponse resFail = new M6ModifyResponse() ;
    		resFail.setCode( -1 )  ;
    		return resFail ;
    	}
		return res ;
    }
}