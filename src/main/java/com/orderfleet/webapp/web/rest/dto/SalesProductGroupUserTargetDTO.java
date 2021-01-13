package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.SalesProductGroupUserTarget;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;

public class SalesProductGroupUserTargetDTO {

	private String pid;
	private LocalDate fromDate;
	private LocalDate toDate;
	private double volume;
	private double amount;
	private String productGroupPid;
	private String productGroupName;
	private String targetUnit;
	private String userPid;
	private String userName;
	private double achievedAmount;
	private double achievedVolume;
	private String accountProfilePid;
	private String accountProfileName;
	private boolean accountWiseTarget;
	private LocalDateTime lastModifiedDate;
	private String day;
	private List<SalesTargetAchievedDTO> salesTargetAchievedDTOs;
	private double totalTarget;

	public SalesProductGroupUserTargetDTO() {
		super();
	}

	public SalesProductGroupUserTargetDTO(String pid, LocalDate fromDate, LocalDate toDate, double volume,
			double amount, String productGroupPid, String productGroupName, String userPid, String userName) {
		super();
		this.pid = pid;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.volume = volume;
		this.amount = amount;
		this.productGroupPid = productGroupPid;
		this.productGroupName = productGroupName;
		this.userPid = userPid;
		this.userName = userName;
	}

	public SalesProductGroupUserTargetDTO(SalesProductGroupUserTarget salesProductGroupUserTarget) {
		super();
		this.pid = salesProductGroupUserTarget.getPid();
		this.fromDate = salesProductGroupUserTarget.getFromDate();
		this.toDate = salesProductGroupUserTarget.getToDate();
		this.volume = salesProductGroupUserTarget.getVolume();
		this.amount = salesProductGroupUserTarget.getAmount();
		this.productGroupPid = salesProductGroupUserTarget.getProductGroup().getPid();
		this.productGroupName = salesProductGroupUserTarget.getProductGroup().getName();
		this.userPid = salesProductGroupUserTarget.getUser().getPid();
		this.userName = salesProductGroupUserTarget.getUser().getFirstName();
		this.day = salesProductGroupUserTarget.getFromDate().getDayOfWeek().toString();
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

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public String getAccountProfilePid() {
		return accountProfilePid;
	}

	public void setAccountProfilePid(String accountProfilePid) {
		this.accountProfilePid = accountProfilePid;
	}

	public String getAccountProfileName() {
		return accountProfileName;
	}

	public void setAccountProfileName(String accountProfileName) {
		this.accountProfileName = accountProfileName;
	}

	public boolean getAccountWiseTarget() {
		return accountWiseTarget;
	}

	public void setAccountWiseTarget(boolean accountWiseTarget) {
		this.accountWiseTarget = accountWiseTarget;
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

	public List<SalesTargetAchievedDTO> getSalesTargetAchievedDTOs() {
		return salesTargetAchievedDTOs;
	}

	public void setSalesTargetAchievedDTOs(List<SalesTargetAchievedDTO> salesTargetAchievedDTOs) {
		this.salesTargetAchievedDTOs = salesTargetAchievedDTOs;
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
				+ ", productGroupName=" + productGroupName + ", userPid=" + userPid + ", userName=" + userName + "]";
	}

}
