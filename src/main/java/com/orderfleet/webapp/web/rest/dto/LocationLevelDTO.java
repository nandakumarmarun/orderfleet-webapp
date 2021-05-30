package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class LocationLevelDTO {

	private long level;

	private List<LocationDTO> locationDtos;

	private List<AccountProfileDTO> accountProfileDTOs;

	public long getLevel() {
		return level;
	}

	public void setLevel(long level) {
		this.level = level;
	}

	public List<LocationDTO> getLocationDtos() {
		return locationDtos;
	}

	public void setLocationDtos(List<LocationDTO> locationDtos) {
		this.locationDtos = locationDtos;
	}

	public List<AccountProfileDTO> getAccountProfileDTOs() {
		return accountProfileDTOs;
	}

	public void setAccountProfileDTOs(List<AccountProfileDTO> accountProfileDTOs) {
		this.accountProfileDTOs = accountProfileDTOs;
	}

}
