package com.primus.pl.xml;

public class CreditCardProcessorResponse {

	private String authorizationCode ;
	private String resultCode ;
	private String resultDescription ;
	private String avsResultCode ;
	private String avsResultDescription ;
	private String inquiryId ;
	
	public String getAuthorizationCode() {
		return authorizationCode;
	}
	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getResultDescription() {
		return resultDescription;
	}
	public void setResultDescription(String resultDescription) {
		this.resultDescription = resultDescription;
	}
	public String getAvsResultCode() {
		return avsResultCode;
	}
	public void setAvsResultCode(String avsResultCode) {
		this.avsResultCode = avsResultCode;
	}
	public String getAvsResultDescription() {
		return avsResultDescription;
	}
	public void setAvsResultDescription(String avsResultDescription) {
		this.avsResultDescription = avsResultDescription;
	}
	public String getInquiryId() {
		return inquiryId;
	}
	public void setInquiryId(String inquiryId) {
		this.inquiryId = inquiryId;
	}
}
