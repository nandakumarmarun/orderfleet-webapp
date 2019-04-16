package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import com.orderfleet.webapp.domain.RootPlanHeader;

public class RootPlanHeaderDTO {

	private String pid;
	
	private String name;
	
	private LocalDate fromDate;
	
	private LocalDate toDate;
	
	private String fromDateString;
	
	private String toDateString;
	
	private String userPid;
	
	private String userName;
	
	private boolean activated;
	
	
	public RootPlanHeaderDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RootPlanHeaderDTO(String pid, String name, LocalDate fromDate, LocalDate toDate, String fromDateString,
			String toDateString, String userPid, String userName) {
		super();
		this.pid = pid;
		this.name = name;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.fromDateString = fromDateString;
		this.toDateString = toDateString;
		this.userPid = userPid;
		this.userName = userName;
	}

	public RootPlanHeaderDTO(RootPlanHeader rootPlanHeader) {
		super();
		this.pid = rootPlanHeader.getPid();
		this.name = rootPlanHeader.getName();
		this.fromDate = rootPlanHeader.getFromDate();
		this.toDate = rootPlanHeader.getToDate();
		this.userPid = rootPlanHeader.getUser().getPid();
		this.userName = rootPlanHeader.getUser().getFirstName();
		this.activated=rootPlanHeader.isActivated();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getFromDateString() {
		return fromDateString;
	}

	public void setFromDateString(String fromDateString) {
		this.fromDateString = fromDateString;
	}

	public String getToDateString() {
		return toDateString;
	}

	public void setToDateString(String toDateString) {
		this.toDateString = toDateString;
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

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	@Override
	public String toString() {
		return "RootPlanHeaderDTO [pid=" + pid + ", name=" + name + ", fromDate=" + fromDate + ", toDate=" + toDate
				+ ", fromDateString=" + fromDateString + ", toDateString=" + toDateString + "]";
	}

}
