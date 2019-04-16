package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

/**
 * A DTO for the LiveTracking.
 * 
 * @author Muhammed Riyas T
 * @since Sep 16, 2016
 */
public class LiveTrackingDTO {

	private String userPid;

	private String userName;

	private List<ExecutiveTaskExecutionDTO> trackingPoints;
	
	private String employeeName;

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<ExecutiveTaskExecutionDTO> getTrackingPoints() {
		return trackingPoints;
	}

	public void setTrackingPoints(List<ExecutiveTaskExecutionDTO> trackingPoints) {
		this.trackingPoints = trackingPoints;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

}
