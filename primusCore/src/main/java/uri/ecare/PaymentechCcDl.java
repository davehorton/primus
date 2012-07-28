
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
 *         &lt;element name="cardtype" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cardnumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="expirydate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nameoncard" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="reason" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="transactionid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="addressline1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="addressline2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="province" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="postalcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "cardtype",
    "cardnumber",
    "expirydate",
    "nameoncard",
    "amount",
    "reason",
    "transactionid",
    "addressline1",
    "addressline2",
    "city",
    "province",
    "postalcode"
})
@XmlRootElement(name = "paymentech_cc_dl")
public class PaymentechCcDl {

    @XmlElement(required = true)
    protected String cardtype;
    @XmlElement(required = true)
    protected String cardnumber;
    @XmlElement(required = true)
    protected String expirydate;
    @XmlElement(required = true)
    protected String nameoncard;
    @XmlElement(required = true)
    protected String amount;
    @XmlElement(required = true)
    protected String reason;
    @XmlElement(required = true)
    protected String transactionid;
    @XmlElement(required = true)
    protected String addressline1;
    @XmlElement(required = true)
    protected String addressline2;
    @XmlElement(required = true)
    protected String city;
    @XmlElement(required = true)
    protected String province;
    @XmlElement(required = true)
    protected String postalcode;

    /**
     * Gets the value of the cardtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardtype() {
        return cardtype;
    }

    /**
     * Sets the value of the cardtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardtype(String value) {
        this.cardtype = value;
    }

    /**
     * Gets the value of the cardnumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardnumber() {
        return cardnumber;
    }

    /**
     * Sets the value of the cardnumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardnumber(String value) {
        this.cardnumber = value;
    }

    /**
     * Gets the value of the expirydate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpirydate() {
        return expirydate;
    }

    /**
     * Sets the value of the expirydate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpirydate(String value) {
        this.expirydate = value;
    }

    /**
     * Gets the value of the nameoncard property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameoncard() {
        return nameoncard;
    }

    /**
     * Sets the value of the nameoncard property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameoncard(String value) {
        this.nameoncard = value;
    }

    /**
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmount(String value) {
        this.amount = value;
    }

    /**
     * Gets the value of the reason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReason(String value) {
        this.reason = value;
    }

    /**
     * Gets the value of the transactionid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionid() {
        return transactionid;
    }

    /**
     * Sets the value of the transactionid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionid(String value) {
        this.transactionid = value;
    }

    /**
     * Gets the value of the addressline1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressline1() {
        return addressline1;
    }

    /**
     * Sets the value of the addressline1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressline1(String value) {
        this.addressline1 = value;
    }

    /**
     * Gets the value of the addressline2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressline2() {
        return addressline2;
    }

    /**
     * Sets the value of the addressline2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressline2(String value) {
        this.addressline2 = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the province property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProvince() {
        return province;
    }

    /**
     * Sets the value of the province property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProvince(String value) {
        this.province = value;
    }

    /**
     * Gets the value of the postalcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostalcode() {
        return postalcode;
    }

    /**
     * Sets the value of the postalcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostalcode(String value) {
        this.postalcode = value;
    }

}
