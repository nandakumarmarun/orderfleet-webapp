package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class SaleTargetGroupProductProfileDTO {

	List<ProductProfileDTO> allProductProfile;

	List<ProductProfileDTO> assignedProductProfile;

	public List<ProductProfileDTO> getAllProductProfile() {
		return allProductProfile;
	}

	public void setAllProductProfile(List<ProductProfileDTO> allProductProfile) {
		this.allProductProfile = allProductProfile;
	}

	public List<ProductProfileDTO> getAssignedProductProfile() {
		return assignedProductProfile;
	}

	public void setAssignedProductProfile(List<ProductProfileDTO> assignedProductProfile) {
		this.assignedProductProfile = assignedProductProfile;
	}

	@Override
	public String toString() {
		return "SaleTargetGroupProductProfileDTO [allProductProfile=" + allProductProfile + ", assignedProductProfile="
				+ assignedProductProfile + "]";
	}

}
