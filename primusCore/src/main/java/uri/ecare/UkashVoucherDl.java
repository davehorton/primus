
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
 *         &lt;element name="ukashvoucher" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ukashvouchervalue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="currency" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="transactionid" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "ukashvoucher",
    "ukashvouchervalue",
    "currency",
    "transactionid"
})
@XmlRootElement(name = "ukash_voucher_dl")
public class UkashVoucherDl {

    @XmlElement(required = true)
    protected String ukashvoucher;
    @XmlElement(required = true)
    protected String ukashvouchervalue;
    @XmlElement(required = true)
    protected String currency;
    @XmlElement(required = true)
    protected String transactionid;

    /**
     * Gets the value of the ukashvoucher property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUkashvoucher() {
        return ukashvoucher;
    }

    /**
     * Sets the value of the ukashvoucher property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUkashvoucher(String value) {
        this.ukashvoucher = value;
    }

    /**
     * Gets the value of the ukashvouchervalue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUkashvouchervalue() {
        return ukashvouchervalue;
    }

    /**
     * Sets the value of the ukashvouchervalue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUkashvouchervalue(String value) {
        this.ukashvouchervalue = value;
    }

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrency(String value) {
        this.currency = value;
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

}
