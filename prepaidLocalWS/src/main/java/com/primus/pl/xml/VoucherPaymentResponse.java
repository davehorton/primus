package com.primus.pl.xml;

public class VoucherPaymentResponse {

    protected Integer code;
    protected String message;
    private VoucherProcessorResponse details ;
    
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
	public VoucherProcessorResponse getDetails() {
		return details;
	}
	public void setDetails(VoucherProcessorResponse details) {
		this.details = details;
	}
    
    
}
