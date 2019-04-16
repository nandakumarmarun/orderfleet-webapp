package com.orderfleet.webapp.web.rest.integration.dto;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;

public class TPProductProfileCustomDTO {

	private List<ProductProfileDTO> productProfileDTOs;
	
	private List<String> attributes;

	public List<ProductProfileDTO> getProductProfileDTOs() {
		return productProfileDTOs;
	}

	public void setProductProfileDTOs(List<ProductProfileDTO> productProfileDTOs) {
		this.productProfileDTOs = productProfileDTOs;
	}

	public List<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}
	
}
