package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MobileGeoLocationView {

	private String pid;
	
	private String accountPid;
	
	private LocalDateTime sendDate;
	
	private String userName;
	
	private String employeeName;
	
	private String accountProfileName;

	private BigDecimal longitude;

	private BigDecimal latitude;

	private String location;
	private String description;
	
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getAccountPid() {
		return accountPid;
	}

	public void setAccountPid(String accountPid) {
		this.accountPid = accountPid;
	}

	public LocalDateTime getSendDate() {
		return sendDate;
	}

	public void setSendDate(LocalDateTime sendDate) {
		this.sendDate = sendDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getAccountProfileName() {
		return accountProfileName;
	}

	public void setAccountProfileName(String accountProfileName) {
		this.accountProfileName = accountProfileName;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
