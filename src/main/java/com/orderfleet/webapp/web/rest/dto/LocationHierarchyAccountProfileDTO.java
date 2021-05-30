package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.enums.PaymentMode;

public class LocationHierarchyAccountProfileDTO {

	private String accountProfileName;

	private String locations;

	public String getAccountProfileName() {
		return accountProfileName;
	}

	public void setAccountProfileName(String accountProfileName) {
		this.accountProfileName = accountProfileName;
	}

	public String getLocations() {
		return locations;
	}

	public void setLocations(String locations) {
		this.locations = locations;
	}

}
