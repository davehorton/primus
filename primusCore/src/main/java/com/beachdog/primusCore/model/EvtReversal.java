package com.beachdog.primusCore.model;

// Generated Mar 1, 2012 2:19:03 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.Date;

/**
 * EvtReversal generated by hbm2java
 */
public class EvtReversal implements java.io.Serializable {

	private String eventId;
	private Date timeStamp;
	private String reversedEventId;
	private String reversingEventId;
	private String sessionId;
	private BigDecimal auditId;
	private BigDecimal sessionType;
	private BigDecimal initiatedBy;
	private Character removeFlag;

	public EvtReversal() {
	}

	public EvtReversal(String eventId, Date timeStamp, String reversedEventId) {
		this.eventId = eventId;
		this.timeStamp = timeStamp;
		this.reversedEventId = reversedEventId;
	}

	public EvtReversal(String eventId, Date timeStamp, String reversedEventId,
			String reversingEventId, String sessionId, BigDecimal auditId,
			BigDecimal sessionType, BigDecimal initiatedBy, Character removeFlag) {
		this.eventId = eventId;
		this.timeStamp = timeStamp;
		this.reversedEventId = reversedEventId;
		this.reversingEventId = reversingEventId;
		this.sessionId = sessionId;
		this.auditId = auditId;
		this.sessionType = sessionType;
		this.initiatedBy = initiatedBy;
		this.removeFlag = removeFlag;
	}

	public String getEventId() {
		return this.eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public Date getTimeStamp() {
		return this.timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getReversedEventId() {
		return this.reversedEventId;
	}

	public void setReversedEventId(String reversedEventId) {
		this.reversedEventId = reversedEventId;
	}

	public String getReversingEventId() {
		return this.reversingEventId;
	}

	public void setReversingEventId(String reversingEventId) {
		this.reversingEventId = reversingEventId;
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public BigDecimal getAuditId() {
		return this.auditId;
	}

	public void setAuditId(BigDecimal auditId) {
		this.auditId = auditId;
	}

	public BigDecimal getSessionType() {
		return this.sessionType;
	}

	public void setSessionType(BigDecimal sessionType) {
		this.sessionType = sessionType;
	}

	public BigDecimal getInitiatedBy() {
		return this.initiatedBy;
	}

	public void setInitiatedBy(BigDecimal initiatedBy) {
		this.initiatedBy = initiatedBy;
	}

	public Character getRemoveFlag() {
		return this.removeFlag;
	}

	public void setRemoveFlag(Character removeFlag) {
		this.removeFlag = removeFlag;
	}

}
