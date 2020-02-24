package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

public class AssociatedAccountTypesAndAccountsDTO {

	@NotNull
	String accountProfilePid;
	@NotNull
	List<String> associatedAccountProfilePids;
	@NotNull
	List<String> associatedAccountTypePids;

	List<AccountProfileDTO> accountProfileDtos;

	public String getAccountProfilePid() {
		return accountProfilePid;
	}

	public void setAccountProfilePid(String accountProfilePid) {
		this.accountProfilePid = accountProfilePid;
	}

	public List<String> getAssociatedAccountProfilePids() {
		return associatedAccountProfilePids;
	}

	public void setAssociatedAccountProfilePids(List<String> associatedAccountProfilePids) {
		this.associatedAccountProfilePids = associatedAccountProfilePids;
	}

	public List<String> getAssociatedAccountTypePids() {
		return associatedAccountTypePids;
	}

	public void setAssociatedAccountTypePids(List<String> associatedAccountTypePids) {
		this.associatedAccountTypePids = associatedAccountTypePids;
	}

	public List<AccountProfileDTO> getAccountProfileDtos() {
		return accountProfileDtos;
	}

	public void setAccountProfileDtos(List<AccountProfileDTO> accountProfileDtos) {
		this.accountProfileDtos = accountProfileDtos;
	}
	
	

}
