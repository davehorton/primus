package com.primus.pl.xml;

public class CreditCardPaymentResponse {

    protected Integer code;
    protected String message;
    private CreditCardProcessorResponse details ;
    
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public CreditCardProcessorResponse getDetails() {
		return details;
	}
	public void setDetails(CreditCardProcessorResponse details) {
		this.details = details;
	}

}
