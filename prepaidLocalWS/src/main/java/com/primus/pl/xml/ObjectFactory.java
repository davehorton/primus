//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.08 at 11:56:24 AM EDT 
//


package com.primus.pl.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.primus.pl.xml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CreditCardPaymentResponse_QNAME = new QName("http://primus.com/prepaidLocal/schemas", "creditCardPaymentResponse");
    private final static QName _M6ModifyResponse_QNAME = new QName("http://primus.com/prepaidLocal/schemas", "m6ModifyResponse");
    private final static QName _VoucherPaymentResponse_QNAME = new QName("http://primus.com/prepaidLocal/schemas", "voucherPaymentResponse");
    private final static QName _SubscriberQueryRequest_QNAME = new QName("http://primus.com/prepaidLocal/schemas", "subscriberQueryRequest");
    private final static QName _CreditCardPaymentRequest_QNAME = new QName("http://primus.com/prepaidLocal/schemas", "creditCardPaymentRequest");
    private final static QName _SubscriberQueryResponse_QNAME = new QName("http://primus.com/prepaidLocal/schemas", "subscriberQueryResponse");
    private final static QName _ActivationRequest_QNAME = new QName("http://primus.com/prepaidLocal/schemas", "activationRequest");
    private final static QName _ActivationResponse_QNAME = new QName("http://primus.com/prepaidLocal/schemas", "activationResponse");
    private final static QName _SubscriberUpdateRequest_QNAME = new QName("http://primus.com/prepaidLocal/schemas", "subscriberUpdateRequest");
    private final static QName _VoucherPaymentRequest_QNAME = new QName("http://primus.com/prepaidLocal/schemas", "voucherPaymentRequest");
    private final static QName _SubscriberUpdateResponse_QNAME = new QName("http://primus.com/prepaidLocal/schemas", "subscriberUpdateResponse");
    private final static QName _M6ModifyRequest_QNAME = new QName("http://primus.com/prepaidLocal/schemas", "m6ModifyRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.primus.pl.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link VoucherProcessorResponse }
     * 
     */
    public VoucherProcessorResponse createVoucherProcessorResponse() {
        return new VoucherProcessorResponse();
    }

    /**
     * Create an instance of {@link SubscriberQueryResponse }
     * 
     */
    public SubscriberQueryResponse createSubscriberQueryResponse() {
        return new SubscriberQueryResponse();
    }

    /**
     * Create an instance of {@link Subscriber }
     * 
     */
    public Subscriber createSubscriber() {
        return new Subscriber();
    }

    /**
     * Create an instance of {@link VoucherPaymentRequest }
     * 
     */
    public VoucherPaymentRequest createVoucherPaymentRequest() {
        return new VoucherPaymentRequest();
    }

    /**
     * Create an instance of {@link M6ModifyResponse }
     * 
     */
    public M6ModifyResponse createM6ModifyResponse() {
        return new M6ModifyResponse();
    }

    /**
     * Create an instance of {@link Voucher }
     * 
     */
    public Voucher createVoucher() {
        return new Voucher();
    }

    /**
     * Create an instance of {@link SubscriberUpdateRequest }
     * 
     */
    public CreditCard createCreditCard() {
        return new CreditCard();
    }

    /**
     * Create an instance of {@link CreditCardProcessorResponse }
     * 
     */
    public SubscriberUpdateRequest createSubscriberUpdateRequest() {
        return new SubscriberUpdateRequest();
    }

    /**
     * Create an instance of {@link VoucherPaymentResponse }
     * 
     */
    public VoucherPaymentResponse createVoucherPaymentResponse() {
        return new VoucherPaymentResponse();
    }

    /**
     * Create an instance of {@link ActivationResponse }
     * 
     */
    public ActivationResponse createActivationResponse() {
        return new ActivationResponse();
    }

    /**
     * Create an instance of {@link ActivationRequest }
     * 
     */
    public ActivationRequest createActivationRequest() {
        return new ActivationRequest();
    }

    /**
     * Create an instance of {@link CreditCardProcessorResponse }
     * 
     */
    public CreditCardProcessorResponse createCreditCardProcessorResponse() {
        return new CreditCardProcessorResponse();
    }

    /**
     * Create an instance of {@link SubscriberUpdateResponse }
     * 
     */
    public SubscriberUpdateResponse createSubscriberUpdateResponse() {
        return new SubscriberUpdateResponse();
    }

    /**
     * Create an instance of {@link Requestor }
     * 
     */
    public Requestor createRequestor() {
        return new Requestor();
    }

    /**
     * Create an instance of {@link CreditCardPaymentRequest }
     * 
     */
    public CreditCardPaymentRequest createCreditCardPaymentRequest() {
        return new CreditCardPaymentRequest();
    }


    /**
     * Create an instance of {@link CreditCardPaymentResponse }
     * 
     */
    public CreditCardPaymentResponse createCreditCardPaymentResponse() {
        return new CreditCardPaymentResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreditCardPaymentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://primus.com/prepaidLocal/schemas", name = "creditCardPaymentResponse")
    public JAXBElement<CreditCardPaymentResponse> createCreditCardPaymentResponse(CreditCardPaymentResponse value) {
        return new JAXBElement<CreditCardPaymentResponse>(_CreditCardPaymentResponse_QNAME, CreditCardPaymentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link M6ModifyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://primus.com/prepaidLocal/schemas", name = "m6ModifyResponse")
    public JAXBElement<M6ModifyResponse> createM6ModifyResponse(M6ModifyResponse value) {
        return new JAXBElement<M6ModifyResponse>(_M6ModifyResponse_QNAME, M6ModifyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VoucherPaymentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://primus.com/prepaidLocal/schemas", name = "voucherPaymentResponse")
    public JAXBElement<VoucherPaymentResponse> createVoucherPaymentResponse(VoucherPaymentResponse value) {
        return new JAXBElement<VoucherPaymentResponse>(_VoucherPaymentResponse_QNAME, VoucherPaymentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubscriberQueryRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://primus.com/prepaidLocal/schemas", name = "subscriberQueryRequest")
    public JAXBElement<SubscriberQueryRequest> createSubscriberQueryRequest(SubscriberQueryRequest value) {
        return new JAXBElement<SubscriberQueryRequest>(_SubscriberQueryRequest_QNAME, SubscriberQueryRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreditCardPaymentRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://primus.com/prepaidLocal/schemas", name = "creditCardPaymentRequest")
    public JAXBElement<CreditCardPaymentRequest> createCreditCardPaymentRequest(CreditCardPaymentRequest value) {
        return new JAXBElement<CreditCardPaymentRequest>(_CreditCardPaymentRequest_QNAME, CreditCardPaymentRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubscriberQueryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://primus.com/prepaidLocal/schemas", name = "subscriberQueryResponse")
    public JAXBElement<SubscriberQueryResponse> createSubscriberQueryResponse(SubscriberQueryResponse value) {
        return new JAXBElement<SubscriberQueryResponse>(_SubscriberQueryResponse_QNAME, SubscriberQueryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ActivationRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://primus.com/prepaidLocal/schemas", name = "activationRequest")
    public JAXBElement<ActivationRequest> createActivationRequest(ActivationRequest value) {
        return new JAXBElement<ActivationRequest>(_ActivationRequest_QNAME, ActivationRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ActivationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://primus.com/prepaidLocal/schemas", name = "activationResponse")
    public JAXBElement<ActivationResponse> createActivationResponse(ActivationResponse value) {
        return new JAXBElement<ActivationResponse>(_ActivationResponse_QNAME, ActivationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubscriberUpdateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://primus.com/prepaidLocal/schemas", name = "subscriberUpdateRequest")
    public JAXBElement<SubscriberUpdateRequest> createSubscriberUpdateRequest(SubscriberUpdateRequest value) {
        return new JAXBElement<SubscriberUpdateRequest>(_SubscriberUpdateRequest_QNAME, SubscriberUpdateRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VoucherPaymentRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://primus.com/prepaidLocal/schemas", name = "voucherPaymentRequest")
    public JAXBElement<VoucherPaymentRequest> createVoucherPaymentRequest(VoucherPaymentRequest value) {
        return new JAXBElement<VoucherPaymentRequest>(_VoucherPaymentRequest_QNAME, VoucherPaymentRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubscriberUpdateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://primus.com/prepaidLocal/schemas", name = "subscriberUpdateResponse")
    public JAXBElement<SubscriberUpdateResponse> createSubscriberUpdateResponse(SubscriberUpdateResponse value) {
        return new JAXBElement<SubscriberUpdateResponse>(_SubscriberUpdateResponse_QNAME, SubscriberUpdateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link M6ModifyRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://primus.com/prepaidLocal/schemas", name = "m6ModifyRequest")
    public JAXBElement<M6ModifyRequest> createM6ModifyRequest(M6ModifyRequest value) {
        return new JAXBElement<M6ModifyRequest>(_M6ModifyRequest_QNAME, M6ModifyRequest.class, null, value);
    }

}
