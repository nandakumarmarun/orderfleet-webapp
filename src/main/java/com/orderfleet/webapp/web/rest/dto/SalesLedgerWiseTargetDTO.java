package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.ProductGroupLocationTarget;
import com.orderfleet.webapp.domain.SalesLedgerWiseTarget;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.UserWiseSalesTarget;

public class SalesLedgerWiseTargetDTO {

	private String pid;
	private LocalDate fromDate;
	private LocalDate toDate;
	private double volume;
	private double amount;
	private String targetUnit;
	private String salesLedgerPid;
	private String salesLedgerName;
	private double achievedAmount;
	private double achievedVolume;
	private LocalDateTime lastModifiedDate;
	private String day;
	// private List<SalesTargetAchievedDTO> salesTargetAchievedDTOs;
	private double totalTarget;

	public SalesLedgerWiseTargetDTO() {
		super();
	}

	public SalesLedgerWiseTargetDTO(String pid, LocalDate fromDate, LocalDate toDate, double volume, double amount) {
		super();
		this.pid = pid;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.volume = volume;
		this.amount = amount;

	}

	public SalesLedgerWiseTargetDTO(SalesLedgerWiseTarget salesLedgerWiseTarget) {
		super();
		this.pid = salesLedgerWiseTarget.getPid();
		this.fromDate = salesLedgerWiseTarget.getFromDate();
		this.toDate = salesLedgerWiseTarget.getToDate();
		this.amount = salesLedgerWiseTarget.getAmount();
		this.salesLedgerPid = salesLedgerWiseTarget.getSalesLedger().getPid();
		this.salesLedgerName = salesLedgerWiseTarget.getSalesLedger().getName();
		this.day = salesLedgerWiseTarget.getFromDate().getDayOfWeek().toString();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getTargetUnit() {
		return targetUnit;
	}

	public void setTargetUnit(String targetUnit) {
		this.targetUnit = targetUnit;
	}

	public double getAchievedAmount() {
		return achievedAmount;
	}

	public void setAchievedAmount(double achievedAmount) {
		this.achievedAmount = achievedAmount;
	}

	public double getAchievedVolume() {
		return achievedVolume;
	}

	public void setAchievedVolume(double achievedVolume) {
		this.achievedVolume = achievedVolume;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public double getTotalTarget() {
		return totalTarget;
	}

	public void setTotalTarget(double totalTarget) {
		this.totalTarget = totalTarget;
	}

	public String getSalesLedgerPid() {
		return salesLedgerPid;
	}

	public void setSalesLedgerPid(String salesLedgerPid) {
		this.salesLedgerPid = salesLedgerPid;
	}

	public String getSalesLedgerName() {
		return salesLedgerName;
	}

	public void setSalesLedgerName(String salesLedgerName) {
		this.salesLedgerName = salesLedgerName;
	}

	@Override
	public String toString() {
		return "UserWiseSalesTargetDTO [pid=" + pid + ", fromDate=" + fromDate + ", toDate=" + toDate + ", volume="
				+ volume + ", amount=" + amount + ", targetUnit=" + targetUnit + ", achievedAmount=" + achievedAmount
				+ ", achievedVolume=" + achievedVolume + ", lastModifiedDate=" + lastModifiedDate + ", day=" + day
				+ ", totalTarget=" + totalTarget + "]";
	}

}
