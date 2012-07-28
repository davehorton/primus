
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
 *         &lt;element name="ECare__PrepaidUkashResponse">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="transactionid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="transactioncode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="transactiondescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="settleamount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ukashtransactionid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="errorcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="errordescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/all>
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
    "eCarePrepaidUkashResponse"
})
@XmlRootElement(name = "prepaid_ukash_payment_dlResponse")
public class PrepaidUkashPaymentDlResponse {

    @XmlElement(name = "ECare__PrepaidUkashResponse", required = true)
    protected PrepaidUkashPaymentDlResponse.ECarePrepaidUkashResponse eCarePrepaidUkashResponse;

    /**
     * Gets the value of the eCarePrepaidUkashResponse property.
     * 
     * @return
     *     possible object is
     *     {@link PrepaidUkashPaymentDlResponse.ECarePrepaidUkashResponse }
     *     
     */
    public PrepaidUkashPaymentDlResponse.ECarePrepaidUkashResponse getECarePrepaidUkashResponse() {
        return eCarePrepaidUkashResponse;
    }

    /**
     * Sets the value of the eCarePrepaidUkashResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrepaidUkashPaymentDlResponse.ECarePrepaidUkashResponse }
     *     
     */
    public void setECarePrepaidUkashResponse(PrepaidUkashPaymentDlResponse.ECarePrepaidUkashResponse value) {
        this.eCarePrepaidUkashResponse = value;
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
     *       &lt;all>
     *         &lt;element name="transactionid" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="transactioncode" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="transactiondescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="settleamount" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ukashtransactionid" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="errorcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="errordescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/all>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class ECarePrepaidUkashResponse {

        @XmlElement(required = true)
        protected String transactionid;
        @XmlElement(required = true)
        protected String transactioncode;
        @XmlElement(required = true)
        protected String transactiondescription;
        @XmlElement(required = true)
        protected String settleamount;
        @XmlElement(required = true)
        protected String ukashtransactionid;
        @XmlElement(required = true)
        protected String errorcode;
        @XmlElement(required = true)
        protected String errordescription;

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
         * Gets the value of the transactioncode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTransactioncode() {
            return transactioncode;
        }

        /**
         * Sets the value of the transactioncode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTransactioncode(String value) {
            this.transactioncode = value;
        }

        /**
         * Gets the value of the transactiondescription property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTransactiondescription() {
            return transactiondescription;
        }

        /**
         * Sets the value of the transactiondescription property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTransactiondescription(String value) {
            this.transactiondescription = value;
        }

        /**
         * Gets the value of the settleamount property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSettleamount() {
            return settleamount;
        }

        /**
         * Sets the value of the settleamount property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSettleamount(String value) {
            this.settleamount = value;
        }

        /**
         * Gets the value of the ukashtransactionid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUkashtransactionid() {
            return ukashtransactionid;
        }

        /**
         * Sets the value of the ukashtransactionid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUkashtransactionid(String value) {
            this.ukashtransactionid = value;
        }

        /**
         * Gets the value of the errorcode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getErrorcode() {
            return errorcode;
        }

        /**
         * Sets the value of the errorcode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setErrorcode(String value) {
            this.errorcode = value;
        }

        /**
         * Gets the value of the errordescription property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getErrordescription() {
            return errordescription;
        }

        /**
         * Sets the value of the errordescription property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setErrordescription(String value) {
            this.errordescription = value;
        }

    }

}
