package com.beachdog.primusCore.model;

// Generated Mar 1, 2012 2:19:03 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;

/**
 * SubAuthAni generated by hbm2java
 */
public class SubAuthAni implements java.io.Serializable {

	private BigDecimal authAniId;
	private Subscriber subscriber;
	private ServiceProvider serviceProvider;
	private String status;
	private String phoneNumber;
	private BigDecimal pinId;
	//private BigDecimal subscriberId ;

	public SubAuthAni() {
	}

	public SubAuthAni(BigDecimal authAniId, Subscriber subscriber,
			ServiceProvider serviceProvider, String status, String phoneNumber) {
		this.authAniId = authAniId;
		this.subscriber = subscriber;
		this.serviceProvider = serviceProvider;
		this.status = status;
		this.phoneNumber = phoneNumber;
	}

	public SubAuthAni(BigDecimal authAniId, Subscriber subscriber,
			ServiceProvider serviceProvider, String status, String phoneNumber,
			BigDecimal pinId) {
		this.authAniId = authAniId;
		this.subscriber = subscriber;
		this.serviceProvider = serviceProvider;
		this.status = status;
		this.phoneNumber = phoneNumber;
		this.pinId = pinId;
	}

	public BigDecimal getAuthAniId() {
		return this.authAniId;
	}

	public void setAuthAniId(BigDecimal authAniId) {
		this.authAniId = authAniId;
	}

	public Subscriber getSubscriber() {
		return this.subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public ServiceProvider getServiceProvider() {
		return this.serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public BigDecimal getPinId() {
		return this.pinId;
	}

	public void setPinId(BigDecimal pinId) {
		this.pinId = pinId;
	}
/*
	public BigDecimal getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(BigDecimal subscriberId) {
		this.subscriberId = subscriberId;
	}
*/
}
