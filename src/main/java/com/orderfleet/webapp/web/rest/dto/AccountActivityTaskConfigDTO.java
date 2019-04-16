package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.AccountActivityTaskConfig;

public class AccountActivityTaskConfigDTO {

	private String accountTypePid;
	private String accountTypeName;
	private String activityPids;
	private String activityPid;
	private String activityName;
	private boolean assignNotification;
	
	public AccountActivityTaskConfigDTO() {
		
	}
	
	public AccountActivityTaskConfigDTO(AccountActivityTaskConfig activityAccountConfig) {
		this.accountTypePid = activityAccountConfig.getAccountType().getPid();
		this.accountTypeName = activityAccountConfig.getAccountType().getName();
		this.activityPid = activityAccountConfig.getActivity().getPid();
		this.activityName = activityAccountConfig.getActivity().getName();
		this.assignNotification = activityAccountConfig.getAssignNotification();
	}
	

	public String getAccountTypePid() {
		return accountTypePid;
	}

	public void setAccountTypePid(String accountTypePid) {
		this.accountTypePid = accountTypePid;
	}

	public String getAccountTypeName() {
		return accountTypeName;
	}

	public void setAccountTypeName(String accountTypeName) {
		this.accountTypeName = accountTypeName;
	}

	public String getActivityPids() {
		return activityPids;
	}

	public void setActivityPids(String activityPids) {
		this.activityPids = activityPids;
	}

	public String getActivityPid() {
		return activityPid;
	}

	public void setActivityPid(String activityPid) {
		this.activityPid = activityPid;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public boolean isAssignNotification() {
		return assignNotification;
	}

	public void setAssignNotification(boolean assignNotification) {
		this.assignNotification = assignNotification;
	}
	
	
	
	
	
	
}
