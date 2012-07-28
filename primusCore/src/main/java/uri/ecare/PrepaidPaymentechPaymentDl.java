
package uri.ecare;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="user_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="business_unit" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="client_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="phone_no" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{uri:ECare}paymentech_cc_dl"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "userId",
    "businessUnit",
    "clientId",
    "phoneNo",
    "paymentechCcDl"
})
@XmlRootElement(name = "prepaid_paymentech_payment_dl")
public class PrepaidPaymentechPaymentDl {

    @XmlElement(name = "user_id", required = true)
    protected String userId;
    @XmlElement(name = "business_unit", required = true)
    protected String businessUnit;
    @XmlElement(name = "client_id", required = true)
    protected String clientId;
    @XmlElement(name = "phone_no", required = true)
    protected String phoneNo;
    @XmlElement(name = "paymentech_cc_dl", namespace = "uri:ECare", required = true)
    protected PaymentechCcDl paymentechCcDl;

    /**
     * Gets the value of the userId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the value of the userId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserId(String value) {
        this.userId = value;
    }

    /**
     * Gets the value of the businessUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessUnit() {
        return businessUnit;
    }

    /**
     * Sets the value of the businessUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessUnit(String value) {
        this.businessUnit = value;
    }

    /**
     * Gets the value of the clientId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the value of the clientId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientId(String value) {
        this.clientId = value;
    }

    /**
     * Gets the value of the phoneNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhoneNo() {
        return phoneNo;
    }

    /**
     * Sets the value of the phoneNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhoneNo(String value) {
        this.phoneNo = value;
    }

    /**
     * Gets the value of the paymentechCcDl property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentechCcDl }
     *     
     */
    public PaymentechCcDl getPaymentechCcDl() {
        return paymentechCcDl;
    }

    /**
     * Sets the value of the paymentechCcDl property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentechCcDl }
     *     
     */
    public void setPaymentechCcDl(PaymentechCcDl value) {
        this.paymentechCcDl = value;
    }

}
