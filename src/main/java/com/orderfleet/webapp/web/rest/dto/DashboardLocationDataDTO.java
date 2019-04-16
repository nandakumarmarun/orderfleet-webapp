package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A DTO for the DashboardTerritoryData.
 * 
 */
public class DashboardLocationDataDTO<T> {
	
	private Long locationId;

	private String locationPid;

	private String locationName;

	private List<T> accountSummaryData;

	private long attendenceTotalUsers;
	
	private long attendencePresentUsers;
	
	//used in territory dash board, when web socket push data
	private List<String> accountProfilePids;
	
	private LocalDateTime lastTime;
	
	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
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

	public List<T> getAccountSummaryData() {
		return accountSummaryData;
	}

	public void setAccountSummaryData(List<T> accountSummaryData) {
		this.accountSummaryData = accountSummaryData;
	}

	public long getAttendenceTotalUsers() {
		return attendenceTotalUsers;
	}

	public void setAttendenceTotalUsers(long attendenceTotalUsers) {
		this.attendenceTotalUsers = attendenceTotalUsers;
	}

	public long getAttendencePresentUsers() {
		return attendencePresentUsers;
	}

	public void setAttendencePresentUsers(long attendencePresentUsers) {
		this.attendencePresentUsers = attendencePresentUsers;
	}

	public List<String> getAccountProfilePids() {
		return accountProfilePids;
	}

	public void setAccountProfilePids(List<String> accountProfilePids) {
		this.accountProfilePids = accountProfilePids;
	}

	public LocalDateTime getLastTime() {
		return lastTime;
	}

	public void setLastTime(LocalDateTime lastTime) {
		this.lastTime = lastTime;
	}
	
}
