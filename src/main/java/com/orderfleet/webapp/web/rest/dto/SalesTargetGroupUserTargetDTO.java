package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;

public class SalesTargetGroupUserTargetDTO {

	private String pid;
	private LocalDate fromDate;
	private LocalDate toDate;
	private double volume;
	private double amount;
	private String salesTargetGroupPid;
	private String salesTargetGroupName;
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

	public SalesTargetGroupUserTargetDTO() {
		super();
	}

	public SalesTargetGroupUserTargetDTO(String pid, LocalDate fromDate, LocalDate toDate, double volume, double amount,
			String salesTargetGroupPid, String salesTargetGroupName, String userPid, String userName) {
		super();
		this.pid = pid;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.volume = volume;
		this.amount = amount;
		this.salesTargetGroupPid = salesTargetGroupPid;
		this.salesTargetGroupName = salesTargetGroupName;
		this.userPid = userPid;
		this.userName = userName;
	}

	public SalesTargetGroupUserTargetDTO(SalesTargetGroupUserTarget salesTargetGroupUserTarget) {
		super();
		this.pid = salesTargetGroupUserTarget.getPid();
		this.fromDate = salesTargetGroupUserTarget.getFromDate();
		this.toDate = salesTargetGroupUserTarget.getToDate();
		this.volume = salesTargetGroupUserTarget.getVolume();
		this.amount = salesTargetGroupUserTarget.getAmount();
		this.salesTargetGroupPid = salesTargetGroupUserTarget.getSalesTargetGroup().getPid();
		this.salesTargetGroupName = salesTargetGroupUserTarget.getSalesTargetGroup().getName();
		this.userPid = salesTargetGroupUserTarget.getUser().getPid();
		this.userName = salesTargetGroupUserTarget.getUser().getFirstName();
		this.day=salesTargetGroupUserTarget.getFromDate().getDayOfWeek().toString();
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

	public String getSalesTargetGroupPid() {
		return salesTargetGroupPid;
	}

	public void setSalesTargetGroupPid(String salesTargetGroupPid) {
		this.salesTargetGroupPid = salesTargetGroupPid;
	}

	public String getSalesTargetGroupName() {
		return salesTargetGroupName;
	}

	public void setSalesTargetGroupName(String salesTargetGroupName) {
		this.salesTargetGroupName = salesTargetGroupName;
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
				+ ", volume=" + volume + ", amount=" + amount + ", salesTargetGroupPid=" + salesTargetGroupPid
				+ ", salesTargetGroupName=" + salesTargetGroupName + ", userPid=" + userPid + ", userName=" + userName
				+ "]";
	}

}
