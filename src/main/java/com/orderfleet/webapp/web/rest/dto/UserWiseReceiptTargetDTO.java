package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.ProductGroupLocationTarget;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.UserWiseReceiptTarget;

public class UserWiseReceiptTargetDTO {

	private String pid;
	private LocalDate fromDate;
	private LocalDate toDate;
	private double volume;
	private double amount;
	private String targetUnit;
	private String userPid;
	private String userName;
	private double achievedAmount;
	private double achievedVolume;
	private LocalDateTime lastModifiedDate;
	private String day;

	private double totalTarget;

	public UserWiseReceiptTargetDTO() {
		super();
	}

	public UserWiseReceiptTargetDTO(String pid, LocalDate fromDate, LocalDate toDate, double volume, double amount,
			String userPid, String userName) {
		super();
		this.pid = pid;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.volume = volume;
		this.amount = amount;
		this.userPid = userPid;
		this.userName = userName;

	}

	public UserWiseReceiptTargetDTO(UserWiseReceiptTarget userWiseReceiptTarget) {
		super();
		this.pid = userWiseReceiptTarget.getPid();
		this.fromDate = userWiseReceiptTarget.getFromDate();
		this.toDate = userWiseReceiptTarget.getToDate();
		this.volume = userWiseReceiptTarget.getVolume();
		this.amount = userWiseReceiptTarget.getAmount();
		this.userPid = userWiseReceiptTarget.getEmployeeProfile().getPid();
		this.userName = userWiseReceiptTarget.getEmployeeProfile().getName();
		this.day = userWiseReceiptTarget.getFromDate().getDayOfWeek().toString();
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
		return "UserWiseReceiptTargetDTO [pid=" + pid + ", fromDate=" + fromDate + ", toDate=" + toDate + ", volume="
				+ volume + ", amount=" + amount + ", targetUnit=" + targetUnit + ", userPid=" + userPid + ", userName="
				+ userName + ", achievedAmount=" + achievedAmount + ", achievedVolume=" + achievedVolume
				+ ", lastModifiedDate=" + lastModifiedDate + ", day=" + day + ", totalTarget=" + totalTarget + "]";
	}

}
