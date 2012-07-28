package com.beachdog.primusCore.model;

// Generated Mar 1, 2012 2:19:03 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.Date;

/**
 * EvtPrepaidActivationLog generated by hbm2java
 */
public class EvtPrepaidActivationLog implements java.io.Serializable {

	private EvtPrepaidActivationLogId id;
	private BigDecimal actionCode;
	private BigDecimal totalPins;
	private BigDecimal lotId;
	private Date timeStamp;
	private String description;
	private BigDecimal totalAmount;
	private BigDecimal offeringId;
	private Long startLotSeq;
	private Long endLotSeq;
	private Long totalActivePins;

	public EvtPrepaidActivationLog() {
	}

	public EvtPrepaidActivationLog(EvtPrepaidActivationLogId id,
			BigDecimal actionCode, BigDecimal totalPins) {
		this.id = id;
		this.actionCode = actionCode;
		this.totalPins = totalPins;
	}

	public EvtPrepaidActivationLog(EvtPrepaidActivationLogId id,
			BigDecimal actionCode, BigDecimal totalPins, BigDecimal lotId,
			Date timeStamp, String description, BigDecimal totalAmount,
			BigDecimal offeringId, Long startLotSeq, Long endLotSeq,
			Long totalActivePins) {
		this.id = id;
		this.actionCode = actionCode;
		this.totalPins = totalPins;
		this.lotId = lotId;
		this.timeStamp = timeStamp;
		this.description = description;
		this.totalAmount = totalAmount;
		this.offeringId = offeringId;
		this.startLotSeq = startLotSeq;
		this.endLotSeq = endLotSeq;
		this.totalActivePins = totalActivePins;
	}

	public EvtPrepaidActivationLogId getId() {
		return this.id;
	}

	public void setId(EvtPrepaidActivationLogId id) {
		this.id = id;
	}

	public BigDecimal getActionCode() {
		return this.actionCode;
	}

	public void setActionCode(BigDecimal actionCode) {
		this.actionCode = actionCode;
	}

	public BigDecimal getTotalPins() {
		return this.totalPins;
	}

	public void setTotalPins(BigDecimal totalPins) {
		this.totalPins = totalPins;
	}

	public BigDecimal getLotId() {
		return this.lotId;
	}

	public void setLotId(BigDecimal lotId) {
		this.lotId = lotId;
	}

	public Date getTimeStamp() {
		return this.timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getTotalAmount() {
		return this.totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getOfferingId() {
		return this.offeringId;
	}

	public void setOfferingId(BigDecimal offeringId) {
		this.offeringId = offeringId;
	}

	public Long getStartLotSeq() {
		return this.startLotSeq;
	}

	public void setStartLotSeq(Long startLotSeq) {
		this.startLotSeq = startLotSeq;
	}

	public Long getEndLotSeq() {
		return this.endLotSeq;
	}

	public void setEndLotSeq(Long endLotSeq) {
		this.endLotSeq = endLotSeq;
	}

	public Long getTotalActivePins() {
		return this.totalActivePins;
	}

	public void setTotalActivePins(Long totalActivePins) {
		this.totalActivePins = totalActivePins;
	}

}
