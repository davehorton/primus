package com.beachdog.primusCore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPBody;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.log4j.Logger;

 
public class SoapTraceHandler implements SOAPHandler<SOAPMessageContext>{
 
	private static Logger logger = Logger.getLogger(SoapTraceHandler.class);

	
   @Override
   public boolean handleMessage(SOAPMessageContext context) {
  
	Boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
 
    SOAPMessage soapMsg = context.getMessage();
    

    log( soapMsg, isRequest ) ;
    
 	return true;
   }
   
   private void log( SOAPMessage soapMessage, boolean isRequest ) {
       try {
    	   ByteArrayOutputStream bout = new ByteArrayOutputStream();  
    	   soapMessage.writeTo(bout);  
    	   logger.info( isRequest ? "SOAP Request:" : "SOAP Response") ;
    	   logger.info(bout.toString("UTF-8")) ;
        }catch(IOException e){
    		System.err.println(e);
        } catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
   }
 
   private String getType( SOAPElement el ) {
	   
	   String type = null ;
		Iterator<QName> it = el.getAllAttributesAsQNames() ;
		while( it.hasNext() ) {
			QName qname = it.next() ;
			if( qname.getNamespaceURI().endsWith("XMLSchema-instance" ) && qname.getLocalPart().equals("type") ) {
				type = el.getAttributeNS(qname.getNamespaceURI(), qname.getLocalPart() ) ;
				int nPos = type.indexOf(":") ;
				if( -1 != nPos ) type = type.substring( nPos + 1 ) ;
				return type ;
			}
		}
		return type ;
	   
   }
   
   /* remove all SOAPStruct elements from the incoming message */
   private List<SOAPElement> cleanStructs( SOAPElement node ) throws SOAPException {
	   	   
	   List<SOAPElement> elList = new ArrayList<SOAPElement>() ;
	   
	   /* remove structs from nested child elements */
	   Iterator<Node> itAll = node.getChildElements() ;	   
	   List<Node> childList = new ArrayList<Node>() ;
	   while( itAll.hasNext() ) childList.add( itAll.next() ) ;
	   
	   for( Node n : childList ) {
		   if( n instanceof SOAPElement ) {
			   List<SOAPElement> newNodes = cleanStructs( (SOAPElement) n  ) ;
			   node.removeChild( n ) ;
			   for( SOAPElement newNode : newNodes ) {
				   node.addChildElement( newNode ) ;
			   }
		   }
	   }
	   
	   /* if I'm a struct then remove myself and clone my children */
	   String type = getType(node) ;
	   if( null != type && getType(node).equals("SOAPStruct") ) {
		   Iterator<SOAPElement> it = node.getChildElements() ;
		   while( it.hasNext() ) {
			   elList.add( (SOAPElement ) it.next().cloneNode(true) ) ;
		   }
	   }
	   else {
		   /* just clone myself */
		   elList.add( (SOAPElement) node.cloneNode(true) ) ;
	   }
	   
	   return elList ;
   }

   @Override
	public boolean handleFault(SOAPMessageContext context) {
        try {
        	 SOAPMessage soapMsg = ((SOAPMessageContext) context).getMessage(); 
        	 log( soapMsg, false) ;
         	soapMsg.writeTo(System.out);
     	}catch(SOAPException e){
     			System.err.println(e);
         }catch(IOException e){
     		System.err.println(e);
         }
		return true;
	}
 
	@Override
	public void close(MessageContext context) {
		//System.out.println("Client : close()......");
	}
 
	@Override
	public Set<QName> getHeaders() {
		//System.out.println("Client : getHeaders()......");
		return null;
	}
 
 
}
