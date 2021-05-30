package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

import com.orderfleet.webapp.domain.enums.PaymentMode;

public class MultipleLocationReportDTO {

	private String accountProfileName;

	private List<String> locations;

	public String getAccountProfileName() {
		return accountProfileName;
	}

	public void setAccountProfileName(String accountProfileName) {
		this.accountProfileName = accountProfileName;
	}

	public List<String> getLocations() {
		return locations;
	}

	public void setLocations(List<String> locations) {
		this.locations = locations;
	}

}
