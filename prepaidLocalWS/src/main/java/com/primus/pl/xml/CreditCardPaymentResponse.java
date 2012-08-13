//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.13 at 03:24:53 PM EDT 
//


package com.primus.pl.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreditCardPaymentResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreditCardPaymentResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="code">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;enumeration value="0"/>
 *               &lt;enumeration value="-1"/>
 *               &lt;enumeration value="-2"/>
 *               &lt;enumeration value="-3"/>
 *               &lt;enumeration value="-4"/>
 *               &lt;enumeration value="-5"/>
 *               &lt;enumeration value="-6"/>
 *               &lt;enumeration value="-7"/>
 *               &lt;enumeration value="-8"/>
 *               &lt;enumeration value="-9"/>
 *               &lt;enumeration value="-97"/>
 *               &lt;enumeration value="-98"/>
 *               &lt;enumeration value="-99"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="details" type="{http://primus.com/prepaidLocal/schemas}CreditCardProcessorResponse" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditCardPaymentResponse", propOrder = {

})
public class CreditCardPaymentResponse {

    @XmlElement(namespace = "http://primus.com/prepaidLocal/schemas")
    protected int code;
    @XmlElement(namespace = "http://primus.com/prepaidLocal/schemas", required = true)
    protected String message;
    @XmlElement(namespace = "http://primus.com/prepaidLocal/schemas")
    protected CreditCardProcessorResponse details;

    /**
     * Gets the value of the code property.
     * 
     */
    public int getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     */
    public void setCode(int value) {
        this.code = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the details property.
     * 
     * @return
     *     possible object is
     *     {@link CreditCardProcessorResponse }
     *     
     */
    public CreditCardProcessorResponse getDetails() {
        return details;
    }

    /**
     * Sets the value of the details property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditCardProcessorResponse }
     *     
     */
    public void setDetails(CreditCardProcessorResponse value) {
        this.details = value;
    }

}
