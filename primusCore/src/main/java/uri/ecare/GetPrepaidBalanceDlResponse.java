
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
 *         &lt;element name="ECare__PrepaidBalanceResponse">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="balance" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *                   &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="statuscode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "eCarePrepaidBalanceResponse"
})
@XmlRootElement(name = "get_prepaid_balance_dlResponse")
public class GetPrepaidBalanceDlResponse {

    @XmlElement(name = "ECare__PrepaidBalanceResponse", required = true)
    protected GetPrepaidBalanceDlResponse.ECarePrepaidBalanceResponse eCarePrepaidBalanceResponse;

    /**
     * Gets the value of the eCarePrepaidBalanceResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetPrepaidBalanceDlResponse.ECarePrepaidBalanceResponse }
     *     
     */
    public GetPrepaidBalanceDlResponse.ECarePrepaidBalanceResponse getECarePrepaidBalanceResponse() {
        return eCarePrepaidBalanceResponse;
    }

    /**
     * Sets the value of the eCarePrepaidBalanceResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetPrepaidBalanceDlResponse.ECarePrepaidBalanceResponse }
     *     
     */
    public void setECarePrepaidBalanceResponse(GetPrepaidBalanceDlResponse.ECarePrepaidBalanceResponse value) {
        this.eCarePrepaidBalanceResponse = value;
    }


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
     *         &lt;element name="balance" type="{http://www.w3.org/2001/XMLSchema}float"/>
     *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="statuscode" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "balance",
        "status",
        "statuscode",
        "message"
    })
    public static class ECarePrepaidBalanceResponse {

        protected float balance;
        @XmlElement(required = true)
        protected String status;
        protected int statuscode;
        @XmlElement(required = true)
        protected String message;

        /**
         * Gets the value of the balance property.
         * 
         */
        public float getBalance() {
            return balance;
        }

        /**
         * Sets the value of the balance property.
         * 
         */
        public void setBalance(float value) {
            this.balance = value;
        }

        /**
         * Gets the value of the status property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStatus() {
            return status;
        }

        /**
         * Sets the value of the status property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStatus(String value) {
            this.status = value;
        }

        /**
         * Gets the value of the statuscode property.
         * 
         */
        public int getStatuscode() {
            return statuscode;
        }

        /**
         * Sets the value of the statuscode property.
         * 
         */
        public void setStatuscode(int value) {
            this.statuscode = value;
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

    }

}
