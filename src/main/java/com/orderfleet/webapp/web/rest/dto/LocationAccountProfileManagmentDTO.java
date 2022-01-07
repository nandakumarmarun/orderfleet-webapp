package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationAccountProfileManagmentDTO {

	private String pid;

	private String name;

	private String locationPid;
	private String locationName;

	public LocationAccountProfileManagmentDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LocationAccountProfileManagmentDTO(String pid, String name, String locationPid, String locationName) {
		super();
		this.pid = pid;
		this.name = name;
		this.locationPid = locationPid;
		this.locationName = locationName;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

}
