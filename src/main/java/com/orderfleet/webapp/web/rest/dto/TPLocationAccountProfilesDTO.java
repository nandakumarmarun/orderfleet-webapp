package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class TPLocationAccountProfilesDTO {

	private String locationName;

	private List<Long> accountProfileIds;

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public List<Long> getAccountProfileIds() {
		return accountProfileIds;
	}

	public void setAccountProfileIds(List<Long> accountProfileIds) {
		this.accountProfileIds = accountProfileIds;
	}

}
