
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
 *         &lt;element name="ECare__PrepaidServiceDetailsResponse">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="statuscode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="startdate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="servicestatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="clientnumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="servicenumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="servicetype" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="serviceidpin" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="enddate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="statusdate" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "eCarePrepaidServiceDetailsResponse"
})
@XmlRootElement(name = "get_prepaid_service_details_dlResponse")
public class GetPrepaidServiceDetailsDlResponse {

    @XmlElement(name = "ECare__PrepaidServiceDetailsResponse", required = true)
    protected GetPrepaidServiceDetailsDlResponse.ECarePrepaidServiceDetailsResponse eCarePrepaidServiceDetailsResponse;

    /**
     * Gets the value of the eCarePrepaidServiceDetailsResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetPrepaidServiceDetailsDlResponse.ECarePrepaidServiceDetailsResponse }
     *     
     */
    public GetPrepaidServiceDetailsDlResponse.ECarePrepaidServiceDetailsResponse getECarePrepaidServiceDetailsResponse() {
        return eCarePrepaidServiceDetailsResponse;
    }

    /**
     * Sets the value of the eCarePrepaidServiceDetailsResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetPrepaidServiceDetailsDlResponse.ECarePrepaidServiceDetailsResponse }
     *     
     */
    public void setECarePrepaidServiceDetailsResponse(GetPrepaidServiceDetailsDlResponse.ECarePrepaidServiceDetailsResponse value) {
        this.eCarePrepaidServiceDetailsResponse = value;
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
     *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="statuscode" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="startdate" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="servicestatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="clientnumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="servicenumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="servicetype" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="serviceidpin" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="enddate" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="statusdate" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    public static class ECarePrepaidServiceDetailsResponse {

        @XmlElement(required = true)
        protected String status;
        @XmlElement(required = true)
        protected String statuscode;
        @XmlElement(required = true)
        protected String message;
        @XmlElement(required = true)
        protected String startdate;
        @XmlElement(required = true)
        protected String servicestatus;
        @XmlElement(required = true)
        protected String clientnumber;
        @XmlElement(required = true)
        protected String servicenumber;
        @XmlElement(required = true)
        protected String servicetype;
        @XmlElement(required = true)
        protected String serviceidpin;
        @XmlElement(required = true)
        protected String enddate;
        @XmlElement(required = true)
        protected String statusdate;

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
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStatuscode() {
            return statuscode;
        }

        /**
         * Sets the value of the statuscode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStatuscode(String value) {
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

        /**
         * Gets the value of the startdate property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStartdate() {
            return startdate;
        }

        /**
         * Sets the value of the startdate property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStartdate(String value) {
            this.startdate = value;
        }

        /**
         * Gets the value of the servicestatus property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getServicestatus() {
            return servicestatus;
        }

        /**
         * Sets the value of the servicestatus property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setServicestatus(String value) {
            this.servicestatus = value;
        }

        /**
         * Gets the value of the clientnumber property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getClientnumber() {
            return clientnumber;
        }

        /**
         * Sets the value of the clientnumber property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setClientnumber(String value) {
            this.clientnumber = value;
        }

        /**
         * Gets the value of the servicenumber property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getServicenumber() {
            return servicenumber;
        }

        /**
         * Sets the value of the servicenumber property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setServicenumber(String value) {
            this.servicenumber = value;
        }

        /**
         * Gets the value of the servicetype property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getServicetype() {
            return servicetype;
        }

        /**
         * Sets the value of the servicetype property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setServicetype(String value) {
            this.servicetype = value;
        }

        /**
         * Gets the value of the serviceidpin property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getServiceidpin() {
            return serviceidpin;
        }

        /**
         * Sets the value of the serviceidpin property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setServiceidpin(String value) {
            this.serviceidpin = value;
        }

        /**
         * Gets the value of the enddate property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEnddate() {
            return enddate;
        }

        /**
         * Sets the value of the enddate property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEnddate(String value) {
            this.enddate = value;
        }

        /**
         * Gets the value of the statusdate property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStatusdate() {
            return statusdate;
        }

        /**
         * Sets the value of the statusdate property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStatusdate(String value) {
            this.statusdate = value;
        }

    }

}
