package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class SaleTargetGroupLocationDTO {

	List<LocationDTO> allLocations;

	List<LocationDTO> assignedLocations;

	public List<LocationDTO> getAllLocations() {
		return allLocations;
	}

	public void setAllLocations(List<LocationDTO> allLocations) {
		this.allLocations = allLocations;
	}

	public List<LocationDTO> getAssignedLocations() {
		return assignedLocations;
	}

	public void setAssignedLocations(List<LocationDTO> assignedLocations) {
		this.assignedLocations = assignedLocations;
	}

}
