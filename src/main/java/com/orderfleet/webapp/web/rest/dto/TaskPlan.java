package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class TaskPlan {

	private String accountType;

	private String accountProfile;

	private String activity;

	private LocalDateTime date;

	private String taskPid;

	private String userPid;
	
	private String pid;
	
	private boolean activated;

	public TaskPlan() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TaskPlan(String accountType, String accountProfile, String activity, LocalDateTime date, String taskPid,
			String userPid,String pid) {
		super();
		this.accountType = accountType;
		this.accountProfile = accountProfile;
		this.activity = activity;
		this.date = date;
		this.taskPid = taskPid;
		this.userPid = userPid;
		this.pid = pid;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountProfile() {
		return accountProfile;
	}

	public void setAccountProfile(String accountProfile) {
		this.accountProfile = accountProfile;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getTaskPid() {
		return taskPid;
	}

	public void setTaskPid(String taskPid) {
		this.taskPid = taskPid;
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	
	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	@Override
	public String toString() {
		return "TaskPlan [accountType=" + accountType + ", accountProfile=" + accountProfile + ", activity=" + activity
				+ ", date=" + date + "]";
	}

}
