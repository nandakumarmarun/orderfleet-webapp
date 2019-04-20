package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.AccountGroupAccountProfile;

public class AccountGroupAccountProfileDTO {

	private String accountProfilePid;
	private String accountProfileName;
	private String accountGroupPid;
	private String accountGroupName;
	private LocalDateTime lastModifiedDate;

	public AccountGroupAccountProfileDTO() {
		super();
	}
	
	public AccountGroupAccountProfileDTO(String accountProfilePid, String accountProfileName, String accountGroupPid,
			String accountGroupName, LocalDateTime lastModifiedDate) {
		super();
		this.accountProfilePid = accountProfilePid;
		this.accountProfileName = accountProfileName;
		this.accountGroupPid = accountGroupPid;
		this.accountGroupName = accountGroupName;
		this.lastModifiedDate = lastModifiedDate;
	}



	public AccountGroupAccountProfileDTO(AccountGroupAccountProfile accountGroupAccountProfile) {
		this.accountProfilePid = accountGroupAccountProfile.getAccountProfile().getPid();
		this.accountProfileName = accountGroupAccountProfile.getAccountProfile().getName();
		this.accountGroupPid = accountGroupAccountProfile.getAccountGroup().getPid();
		this.accountGroupName = accountGroupAccountProfile.getAccountGroup().getName();
		this.lastModifiedDate = accountGroupAccountProfile.getLastModifiedDate();
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

	public String getAccountGroupPid() {
		return accountGroupPid;
	}

	public void setAccountGroupPid(String accountGroupPid) {
		this.accountGroupPid = accountGroupPid;
	}

	public String getAccountGroupName() {
		return accountGroupName;
	}

	public void setAccountGroupName(String accountGroupName) {
		this.accountGroupName = accountGroupName;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
