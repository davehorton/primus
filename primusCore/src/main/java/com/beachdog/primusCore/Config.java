package com.beachdog.primusCore;

public class Config {

	private String webServiceEndoint ;
	
	private String wsdlLocation ;
	private String M6Address ;
	private String M6User ;
	private String M6Password ;

	public String getWebServiceEndoint() {
		return webServiceEndoint;
	}

	public void setWebServiceEndoint(String webServiceEndoint) {
		this.webServiceEndoint = webServiceEndoint;
	}

	public String getWsdlLocation() {
		return wsdlLocation;
	}

	public void setWsdlLocation(String wsdlLocation) {
		this.wsdlLocation = wsdlLocation;
	}

	public String getM6Address() {
		return M6Address;
	}

	public void setM6Address(String m6Address) {
		M6Address = m6Address;
	}

	public String getM6User() {
		return M6User;
	}

	public void setM6User(String m6User) {
		M6User = m6User;
	}

	public String getM6Password() {
		return M6Password;
	}

	public void setM6Password(String m6Password) {
		M6Password = m6Password;
	}
	
	
}
