package com.orderfleet.webapp.web.rest.dto;

/**
 * A DTO for the EcomProductGroupProduct entity.
 * 
 * @author Sarath
 * @since Sep 23, 2016
 */
public class EcomProductProfileProductDTO {

	private EcomProductProfileDTO ecomEcomProductGroupDTO;

	private ProductProfileDTO productProfileDTO;

	public EcomProductProfileProductDTO() {
	}

	public EcomProductProfileProductDTO(EcomProductProfileDTO ecomEcomProductGroupDTO, ProductProfileDTO productProfileDTO) {
		super();
		this.ecomEcomProductGroupDTO = ecomEcomProductGroupDTO;
		this.productProfileDTO = productProfileDTO;
	}

	public EcomProductProfileDTO getEcomProductGroupDTO() {
		return ecomEcomProductGroupDTO;
	}

	public void setEcomProductGroupDTO(EcomProductProfileDTO ecomEcomProductGroupDTO) {
		this.ecomEcomProductGroupDTO = ecomEcomProductGroupDTO;
	}

	public ProductProfileDTO getProductProfileDTO() {
		return productProfileDTO;
	}

	public void setProductProfileDTO(ProductProfileDTO productProfileDTO) {
		this.productProfileDTO = productProfileDTO;
	}

	@Override
	public String toString() {
		return "EcomProductGroupProductDTO [ecomEcomProductGroupDTO=" + ecomEcomProductGroupDTO
				+ ", productProfileDTO=" + productProfileDTO + "]";
	}

}
