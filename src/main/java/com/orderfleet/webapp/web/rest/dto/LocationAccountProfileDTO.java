package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.LocationAccountProfile;

public class LocationAccountProfileDTO {

	private String accountProfilePid;
	private String accountProfileName;
	private String locationPid;
	private String locationName;
	private LocalDateTime lastModifiedDate;

	public LocationAccountProfileDTO() {
		super();
	}
	
	public LocationAccountProfileDTO(String accountProfilePid, String accountProfileName, String locationPid,
			String locationName, LocalDateTime lastModifiedDate) {
		super();
		this.accountProfilePid = accountProfilePid;
		this.accountProfileName = accountProfileName;
		this.locationPid = locationPid;
		this.locationName = locationName;
		this.lastModifiedDate = lastModifiedDate;
	}



	public LocationAccountProfileDTO(LocationAccountProfile locationAccountProfile) {
		this.accountProfilePid = locationAccountProfile.getAccountProfile().getPid();
		this.accountProfileName = locationAccountProfile.getAccountProfile().getName();
		this.locationPid = locationAccountProfile.getLocation().getPid();
		this.locationName = locationAccountProfile.getLocation().getName();
		this.lastModifiedDate = locationAccountProfile.getLastModifiedDate();
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

	public String getLocationPid() {
		return locationPid;
	}

	public void setLocationPid(String locationPid) {
		this.locationPid = locationPid;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
