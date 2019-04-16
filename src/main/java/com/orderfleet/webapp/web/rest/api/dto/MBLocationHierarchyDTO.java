package com.orderfleet.webapp.web.rest.api.dto;

import com.orderfleet.webapp.domain.LocationHierarchy;

/**
 * A DTO for the LocationHierarchy entity.
 * 
 * @author Shaheer
 * @since May 27, 2016
 */
public class MBLocationHierarchyDTO {

	private String locationPid;

	private String locationName;

	private String parentPid;

	private String parentName;

	public MBLocationHierarchyDTO() {
		super();
	}

	public MBLocationHierarchyDTO(LocationHierarchy locationHierarchy) {
		super();
		this.locationPid = locationHierarchy.getLocation().getPid();
		this.locationName = locationHierarchy.getLocation().getName();
		if (locationHierarchy.getParent() == null) {
			this.parentPid = "-1";
			this.parentName = null;
		} else {
			this.parentPid = locationHierarchy.getParent().getPid();
			this.parentName = locationHierarchy.getParent().getName();
		}
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String locationName) {
		this.parentName = locationName;
	}

	public String getLocationPid() {
		return locationPid;
	}

	public void setLocationPid(String locationPid) {
		this.locationPid = locationPid;
	}

	public String getParentPid() {
		return parentPid;
	}

	public void setParentPid(String parentPid) {
		this.parentPid = parentPid;
	}

	@Override
	public String toString() {
		return "MBLocationHierarchyDTO [locationPid=" + locationPid + ", locationName=" + locationName + ", parentPid="
				+ parentPid + ", parentName=" + parentName + "]";
	}

}
