package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.ProductGroupLocationTarget;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;

public class ProductGroupLocationTargetDTO {

	private String pid;
	private LocalDate fromDate;
	private LocalDate toDate;
	private double volume;
	private double amount;
	private String productGroupPid;
	private String productGroupName;
	private String targetUnit;
	private String locationPid;
	private String locationName;
	private double achievedAmount;
	private double achievedVolume;
	private LocalDateTime lastModifiedDate;
	private String day;
	// private List<SalesTargetAchievedDTO> salesTargetAchievedDTOs;
	private double totalTarget;

	public ProductGroupLocationTargetDTO() {
		super();
	}

	public ProductGroupLocationTargetDTO(String pid, LocalDate fromDate, LocalDate toDate, double volume, double amount,
			String productGroupPid, String productGroupName, String locationPid, String locationName) {
		super();
		this.pid = pid;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.volume = volume;
		this.amount = amount;
		this.productGroupPid = productGroupPid;
		this.productGroupName = productGroupName;
		this.locationPid = locationPid;
		this.locationName = locationName;
	}

	public ProductGroupLocationTargetDTO(ProductGroupLocationTarget productGroupUserTarget) {
		super();
		this.pid = productGroupUserTarget.getPid();
		this.fromDate = productGroupUserTarget.getFromDate();
		this.toDate = productGroupUserTarget.getToDate();
		this.volume = productGroupUserTarget.getVolume();
		this.amount = productGroupUserTarget.getAmount();
		this.productGroupPid = productGroupUserTarget.getProductGroup().getPid();
		this.productGroupName = productGroupUserTarget.getProductGroup().getName();
		this.locationPid = productGroupUserTarget.getLocation().getPid();
		this.locationName = productGroupUserTarget.getLocation().getName();
		this.day = productGroupUserTarget.getFromDate().getDayOfWeek().toString();
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

	public String getProductGroupPid() {
		return productGroupPid;
	}

	public void setProductGroupPid(String productGroupPid) {
		this.productGroupPid = productGroupPid;
	}

	public String getProductGroupName() {
		return productGroupName;
	}

	public void setProductGroupName(String productGroupName) {
		this.productGroupName = productGroupName;
	}

	public String getTargetUnit() {
		return targetUnit;
	}

	public void setTargetUnit(String targetUnit) {
		this.targetUnit = targetUnit;
	}

	public String getLocationPid() {
		return locationPid;
	}

	public void setLocationPid(String locationPid) {
		this.locationPid = locationPid;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
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

	@Override
	public String toString() {
		return "SalesTargetGroupUserTargetDTO [pid=" + pid + ", fromDate=" + fromDate + ", toDate=" + toDate
				+ ", volume=" + volume + ", amount=" + amount + ", productGroupPid=" + productGroupPid
				+ ", productGroupName=" + productGroupName + ", locationPid=" + locationPid + ", locationName="
				+ locationName + "]";
	}

}
