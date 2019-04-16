package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class ProductProfileGroupDTO {

	private ProductProfileDTO productProfileDTO;

	private List<ProductGroupDTO> productGroupDTOs;

	public ProductProfileDTO getProductProfileDTO() {
		return productProfileDTO;
	}

	public void setProductProfileDTO(ProductProfileDTO productProfileDTO) {
		this.productProfileDTO = productProfileDTO;
	}

	public List<ProductGroupDTO> getProductGroupDTOs() {
		return productGroupDTOs;
	}

	public void setProductGroupDTOs(List<ProductGroupDTO> productGroupDTOs) {
		this.productGroupDTOs = productGroupDTOs;
	}

}
