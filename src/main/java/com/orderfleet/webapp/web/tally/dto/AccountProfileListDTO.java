package com.orderfleet.webapp.web.tally.dto;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;

public class AccountProfileListDTO {

	private List<AccountProfileDTO> accountProfiles;
	
	

	public AccountProfileListDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<AccountProfileDTO> getAccountProfiles() {
		return accountProfiles;
	}

	public void setAccountProfiles(List<AccountProfileDTO> accountProfiles) {
		this.accountProfiles = accountProfiles;
	}

	@Override
	public String toString() {
		return "AccountProfileListDTO [accountProfiles=" + accountProfiles + "]";
	}
	
	
	
	
}
