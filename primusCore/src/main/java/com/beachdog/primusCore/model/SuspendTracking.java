package com.beachdog.primusCore.model;

// Generated Jul 22, 2012 11:07:32 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.Date;

/**
 * SuspendTracking generated by hbm2java
 */
public class SuspendTracking implements java.io.Serializable {

	private Long id;
	private BigDecimal subscriberId;
	private String phoneNumber;
	private String aaActivityId;
	private Date suspendedOn;
	private Date unsuspendedOn;

	public SuspendTracking() {
	}

	public SuspendTracking(Long id, BigDecimal subscriberId,
			String phoneNumber, String aaActivityId, Date suspendedOn) {
		this.id = id;
		this.subscriberId = subscriberId;
		this.phoneNumber = phoneNumber;
		this.aaActivityId = aaActivityId;
		this.suspendedOn = suspendedOn;
	}

	public SuspendTracking(Long id, BigDecimal subscriberId,
			String phoneNumber, String aaActivityId, Date suspendedOn,
			Date unsuspendedOn) {
		this.id = id;
		this.subscriberId = subscriberId;
		this.phoneNumber = phoneNumber;
		this.aaActivityId = aaActivityId;
		this.suspendedOn = suspendedOn;
		this.unsuspendedOn = unsuspendedOn;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getSubscriberId() {
		return this.subscriberId;
	}

	public void setSubscriberId(BigDecimal subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAaActivityId() {
		return this.aaActivityId;
	}

	public void setAaActivityId(String aaActivityId) {
		this.aaActivityId = aaActivityId;
	}

	public Date getSuspendedOn() {
		return this.suspendedOn;
	}

	public void setSuspendedOn(Date suspendedOn) {
		this.suspendedOn = suspendedOn;
	}

	public Date getUnsuspendedOn() {
		return this.unsuspendedOn;
	}

	public void setUnsuspendedOn(Date unsuspendedOn) {
		this.unsuspendedOn = unsuspendedOn;
	}

}
