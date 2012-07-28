package com.primus.pl.xml;

public class VoucherProcessorResponse {

	private String transactionCode ;
	private String transactionDescription ;
	private String errorCode ;
	private String errorDescription ;
	private String vendorTransactionId ;
	private Double settleAmount ;
	
	public String getTransactionCode() {
		return transactionCode;
	}
	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}
	public String getTransactionDescription() {
		return transactionDescription;
	}
	public void setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	public String getVendorTransactionId() {
		return vendorTransactionId;
	}
	public void setVendorTransactionId(String vendorTransactionId) {
		this.vendorTransactionId = vendorTransactionId;
	}
	public Double getSettleAmount() {
		return settleAmount;
	}
	public void setSettleAmount(Double settleAmount) {
		this.settleAmount = settleAmount;
	}
}
