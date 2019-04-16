package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Sarath
 * @since Aug 12, 2016
 */
public class UserReceiptTargetDTO {

	private String pid;
	private LocalDate startDate;
	private LocalDate endDate;
	private Long targetAmount;
	private int targetPercentage;
	private String userPid;
	private String userName;
	private double achievedAmount;
	private LocalDateTime lastModifiedDate;

	public UserReceiptTargetDTO() {
		super();
	}

	public UserReceiptTargetDTO(String pid, LocalDate startDate, LocalDate endDate, Long targetAmount,
			int targetPercentage, String userPid, String userName) {
		super();
		this.pid = pid;
		this.startDate = startDate;
		this.endDate = endDate;
		this.targetAmount = targetAmount;
		this.targetPercentage = targetPercentage;
		this.userPid = userPid;
		this.userName = userName;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public Long getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(Long targetAmount) {
		this.targetAmount = targetAmount;
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

	public int getTargetPercentage() {
		return targetPercentage;
	}

	public void setTargetPercentage(int targetPercentage) {
		this.targetPercentage = targetPercentage;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public String toString() {
		return "UserReceiptTargetDTO [pid=" + pid + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", targetAmount=" + targetAmount + ", userPid=" + userPid + ", userName=" + userName + "]";
	}

}
