package com.orderfleet.webapp.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A DTO for the LocationHierarchy entity.
 * 
 * @author Shaheer
 * @since May 27, 2016
 */
public class LocationHierarchyDTO {
	
	@JsonProperty("id")
    private Long locationId;

    private String locationName;

    private Long parentId;

    private String parentName;

	public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }


    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long locationId) {
        this.parentId = locationId;
    }


    public String getParentName() {
        return parentName;
    }

    public void setParentName(String locationName) {
        this.parentName = locationName;
    }


    @Override
    public String toString() {
        return "LocationHierarchyDTO{" +
            "locationId=" + locationId +
            ", parentId=" + parentId +
            ", locationName='" + locationName + "'" +
            ", parentName='" + parentName + "'" +
            '}';
    }
}
