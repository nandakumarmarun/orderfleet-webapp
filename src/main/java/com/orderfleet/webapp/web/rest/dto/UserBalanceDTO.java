package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.UserBalance;

public class UserBalanceDTO {

	private Long id;
	
	private String  userPid;
	
	private String  userName;
	
	private String dateString;
	
	private LocalDateTime dateTime;
	
	private double amount;
	
	private String remarks;

	public UserBalanceDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public UserBalanceDTO(Long id, String userPid, String userName, String dateString, LocalDateTime dateTime,
			double amount, String remarks) {
		super();
		this.id = id;
		this.userPid = userPid;
		this.userName = userName;
		this.dateString = dateString;
		this.dateTime = dateTime;
		this.amount = amount;
		this.remarks = remarks;
	}
	
	public UserBalanceDTO(UserBalance userBalance) {
		super();
		this.id = userBalance.getId();
		this.userPid = userBalance.getUser().getPid();
		this.userName = userBalance.getUser().getFirstName();
		this.dateTime = userBalance.getDate();
		this.amount = userBalance.getAmount();
		this.remarks = userBalance.getRemarks();
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		return "UserBalanceDTO [id=" + id + ", userPid=" + userPid + ", userName=" + userName + ", dateString="
				+ dateString + ", dateTime=" + dateTime + ", amount=" + amount + ", remarks=" + remarks + "]";
	}
	
	
}
