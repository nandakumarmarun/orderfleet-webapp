package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

/**
 * A DTO for the ProductGroupEcomProduct entity.
 *
 * @author Sarath
 * @since Nov 21, 2017
 *
 */
public class ProductGroupEcomProductDTO {

	private ProductGroupDTO productGroupDTO;

	private List<EcomProductProfileDTO> ecomProductProfileDTOs;

	public ProductGroupEcomProductDTO() {
		super();
	}

	public ProductGroupEcomProductDTO(ProductGroupDTO productGroupDTO, List<EcomProductProfileDTO> productProfiles) {
		super();
		this.productGroupDTO = productGroupDTO;
		this.ecomProductProfileDTOs = productProfiles;
	}

	public ProductGroupDTO getProductGroupDTO() {
		return productGroupDTO;
	}

	public void setProductGroupDTO(ProductGroupDTO productGroupDTO) {
		this.productGroupDTO = productGroupDTO;
	}

	public List<EcomProductProfileDTO> getEcomProductProfileDTOs() {
		return ecomProductProfileDTOs;
	}

	public void setEcomProductProfileDTOs(List<EcomProductProfileDTO> ecomProductProfileDTOs) {
		this.ecomProductProfileDTOs = ecomProductProfileDTOs;
	}

	@Override
	public String toString() {
		return "ProductGroupEcomProductDTO [productGroupDTO=" + productGroupDTO + ", ecomProductProfileDTOs="
				+ ecomProductProfileDTOs + "]";
	}

}
