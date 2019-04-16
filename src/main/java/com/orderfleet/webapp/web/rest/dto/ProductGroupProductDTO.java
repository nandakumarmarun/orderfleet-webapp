package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * A DTO for the ProductGroupProduct entity.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
public class ProductGroupProductDTO {

	private ProductGroupDTO productGroupDTO;

	private List<ProductProfileDTO> productProfiles;

	private LocalDateTime lastModifiedDate;

	@NotNull
	private boolean activated = true;

	public ProductGroupProductDTO() {
	}

	public ProductGroupProductDTO(ProductGroupDTO productGroupDTO, List<ProductProfileDTO> productProfiles,
			LocalDateTime lastModifiedDate, boolean activated) {
		super();
		this.productGroupDTO = productGroupDTO;
		this.productProfiles = productProfiles;
		this.lastModifiedDate = lastModifiedDate;
		this.activated = activated;
	}

	public ProductGroupDTO getProductGroupDTO() {
		return productGroupDTO;
	}

	public void setProductGroupDTO(ProductGroupDTO productGroupDTO) {
		this.productGroupDTO = productGroupDTO;
	}

	public List<ProductProfileDTO> getProductProfiles() {
		return productProfiles;
	}

	public void setProductProfiles(List<ProductProfileDTO> productProfiles) {
		this.productProfiles = productProfiles;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

}
