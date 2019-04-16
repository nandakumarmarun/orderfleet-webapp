package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class BuyToAccountsDTO {

	List<AccountProfileDTO>buyAccountProfiles;
	
	List<AccountProfileDTO>toAccountProfiles;

	public List<AccountProfileDTO> getBuyAccountProfiles() {
		return buyAccountProfiles;
	}

	public void setBuyAccountProfiles(List<AccountProfileDTO> buyAccountProfiles) {
		this.buyAccountProfiles = buyAccountProfiles;
	}

	public List<AccountProfileDTO> getToAccountProfiles() {
		return toAccountProfiles;
	}

	public void setToAccountProfiles(List<AccountProfileDTO> toAccountProfiles) {
		this.toAccountProfiles = toAccountProfiles;
	}

	@Override
	public String toString() {
		return "BuyToAccountsDTO [buyAccountProfiles=" + buyAccountProfiles + ", toAccountProfiles=" + toAccountProfiles
				+ "]";
	}
	
	
}
