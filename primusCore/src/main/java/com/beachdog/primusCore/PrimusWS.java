package com.beachdog.primusCore;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import uri.ecare.GetPrepaidBalanceDlResponse.ECarePrepaidBalanceResponse;
import uri.ecare.PaymentechCcDl;
import uri.ecare.PrepaidPaymentechPaymentDlResponse.ECarePrepaidPaymentechResponse;
import uri.ecare.PrepaidUkashPaymentDlResponse.ECarePrepaidUkashResponse;
import uri.ecare.UkashVoucherDl;

import com.soaplite.namespaces.perl.ECare;
import com.soaplite.namespaces.perl.ECarePortType;
import com.sun.xml.rpc.client.ClientTransportException;

@SuppressWarnings("restriction")
public class PrimusWS {

	private String endpoint ;
	private URL url ;
	private QName qname ;

	private static Logger logger = Logger.getLogger(PrimusWS.class);

		
	public PrimusWS( String ep, String wsdlLocation ) throws MalformedURLException {
		this.endpoint = ep ;
		this.url = new URL(wsdlLocation) ;
		this.qname = new QName("http://namespaces.soaplite.com/perl", "ECare") ;
	}
	
    public ECarePrepaidPaymentechResponse prepaidPaymentTechPayment( String userId, String clientId, String businessUnit, 
			String phoneNumber, String cardNumber, String expiryDate, String cardType, Float amount, String nameOnCard,
			String address1, String address2, String city, String province,  String postalCode, 
			String reason, String transactionId ) throws ParseException, SOAPFaultException, ClientTransportException {
		assert( null != this.endpoint ) ;
		
		ECarePortType ecp = new ECare( url, qname ).getECare() ;
        ((BindingProvider)ecp).getRequestContext().put( BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint )  ;
        
		DecimalFormat df = new DecimalFormat("####.00") ;
         
        PaymentechCcDl paymentechCcDl = new PaymentechCcDl() ;
        paymentechCcDl.setAddressline1( address1 ) ;
        paymentechCcDl.setAddressline2( address2 ) ;
        paymentechCcDl.setAmount(df.format(amount)) ;
        paymentechCcDl.setCardnumber(cardNumber) ;
        paymentechCcDl.setCardtype(cardType) ;
        paymentechCcDl.setCity(city) ;
        paymentechCcDl.setExpirydate(expiryDate) ;
        paymentechCcDl.setNameoncard(nameOnCard) ;
        paymentechCcDl.setPostalcode(postalCode) ;
        paymentechCcDl.setProvince(province) ;
        paymentechCcDl.setReason(reason) ;
        paymentechCcDl.setTransactionid(transactionId) ;
        
        return ecp.prepaidPaymentechPaymentDl(userId, businessUnit, clientId, phoneNumber, paymentechCcDl) ;
        
	}
	
	public ECarePrepaidUkashResponse prepaidUkashPayment( String userId, String clientId, String businessUnit, 
			String phoneNumber, String voucher, Float voucherValue, String currency, String transactionId ) 
			throws ParseException, SOAPFaultException, ClientTransportException {
		assert( null != this.endpoint ) ;
		
		ECarePortType ecp = new ECare( url, qname ).getECare() ;
        ((BindingProvider)ecp).getRequestContext().put( BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint )  ;

		DecimalFormat df = new DecimalFormat("####.00") ;
		
        UkashVoucherDl ukashVoucherDl = new UkashVoucherDl() ;
        ukashVoucherDl.setCurrency(currency) ;
        ukashVoucherDl.setTransactionid(transactionId) ;
        ukashVoucherDl.setUkashvoucher(voucher) ;
        ukashVoucherDl.setUkashvouchervalue(df.format(voucherValue.doubleValue())) ;
        
        logger.info("PrimusWS.prepaidUkashPayment: ") ;
        logger.info("    voucher:       " + voucher) ;
        logger.info("    value:         " + df.format(voucherValue.doubleValue())) ;
        logger.info("    transactionId: " + transactionId ) ;
        logger.info("    currency:      " + currency ) ;
        
        ECarePrepaidUkashResponse response = ecp.prepaidUkashPaymentDl(userId, businessUnit, clientId, 
        		phoneNumber, ukashVoucherDl) ;
        
        logger.info("*******response") ;
        logger.info("Error code:        " + response.getErrorcode() ) ;
        logger.info("Error desc:        " + response.getErrordescription() ) ;
        logger.info("Settlement:        " + response.getSettleamount() ) ;
        logger.info("transaction code:  " + response.getTransactioncode() ) ;
        logger.info("transaction desc:  " + response.getTransactiondescription() ) ;
        logger.info("ukash txn id:      " + response.getUkashtransactionid() ) ;
        
        return response ;
        
	}
	public ECarePrepaidBalanceResponse getPrepaidBalance( String phoneNumber ) throws SOAPFaultException, ClientTransportException
	{
		assert( null != this.endpoint ) ;
		
		ECarePortType ecp = new ECare( url, qname ).getECare() ;
        ((BindingProvider)ecp).getRequestContext().put( BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint )  ;
						
		return ecp.getPrepaidBalanceDl(phoneNumber) ;

 	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public QName getQname() {
		return qname;
	}

	public void setQname(QName qname) {
		this.qname = qname;
	}
	
	
	
	
}
