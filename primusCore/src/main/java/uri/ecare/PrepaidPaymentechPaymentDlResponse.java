
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
 *         &lt;element name="ECare__PrepaidPaymentechResponse">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="transactionid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="authorizationcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="resultcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="resultdescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="avsresultcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="avsresultdescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="inquiryid" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "eCarePrepaidPaymentechResponse"
})
@XmlRootElement(name = "prepaid_paymentech_payment_dlResponse")
public class PrepaidPaymentechPaymentDlResponse {

    @XmlElement(name = "ECare__PrepaidPaymentechResponse", required = true)
    protected PrepaidPaymentechPaymentDlResponse.ECarePrepaidPaymentechResponse eCarePrepaidPaymentechResponse;

    /**
     * Gets the value of the eCarePrepaidPaymentechResponse property.
     * 
     * @return
     *     possible object is
     *     {@link PrepaidPaymentechPaymentDlResponse.ECarePrepaidPaymentechResponse }
     *     
     */
    public PrepaidPaymentechPaymentDlResponse.ECarePrepaidPaymentechResponse getECarePrepaidPaymentechResponse() {
        return eCarePrepaidPaymentechResponse;
    }

    /**
     * Sets the value of the eCarePrepaidPaymentechResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrepaidPaymentechPaymentDlResponse.ECarePrepaidPaymentechResponse }
     *     
     */
    public void setECarePrepaidPaymentechResponse(PrepaidPaymentechPaymentDlResponse.ECarePrepaidPaymentechResponse value) {
        this.eCarePrepaidPaymentechResponse = value;
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
     *         &lt;element name="authorizationcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="resultcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="resultdescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="avsresultcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="avsresultdescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="inquiryid" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    public static class ECarePrepaidPaymentechResponse {

        @XmlElement(required = true)
        protected String transactionid;
        @XmlElement(required = true)
        protected String authorizationcode;
        @XmlElement(required = true)
        protected String resultcode;
        @XmlElement(required = true)
        protected String resultdescription;
        @XmlElement(required = true)
        protected String avsresultcode;
        @XmlElement(required = true)
        protected String avsresultdescription;
        @XmlElement(required = true)
        protected String inquiryid;

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
         * Gets the value of the authorizationcode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAuthorizationcode() {
            return authorizationcode;
        }

        /**
         * Sets the value of the authorizationcode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAuthorizationcode(String value) {
            this.authorizationcode = value;
        }

        /**
         * Gets the value of the resultcode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getResultcode() {
            return resultcode;
        }

        /**
         * Sets the value of the resultcode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setResultcode(String value) {
            this.resultcode = value;
        }

        /**
         * Gets the value of the resultdescription property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getResultdescription() {
            return resultdescription;
        }

        /**
         * Sets the value of the resultdescription property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setResultdescription(String value) {
            this.resultdescription = value;
        }

        /**
         * Gets the value of the avsresultcode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAvsresultcode() {
            return avsresultcode;
        }

        /**
         * Sets the value of the avsresultcode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAvsresultcode(String value) {
            this.avsresultcode = value;
        }

        /**
         * Gets the value of the avsresultdescription property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAvsresultdescription() {
            return avsresultdescription;
        }

        /**
         * Sets the value of the avsresultdescription property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAvsresultdescription(String value) {
            this.avsresultdescription = value;
        }

        /**
         * Gets the value of the inquiryid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInquiryid() {
            return inquiryid;
        }

        /**
         * Sets the value of the inquiryid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInquiryid(String value) {
            this.inquiryid = value;
        }

    }

}
