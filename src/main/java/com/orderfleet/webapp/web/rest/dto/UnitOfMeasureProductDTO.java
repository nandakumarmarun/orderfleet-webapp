package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * A DTO for the UnitOfMeasureProduct entity.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
public class UnitOfMeasureProductDTO {

	private UnitOfMeasureDTO unitOfMeasureDTO;

	private List<ProductProfileDTO> productProfiles;

	private LocalDateTime lastModifiedDate;

	@NotNull
	private boolean activated = true;

	public UnitOfMeasureProductDTO() {
	}

	public UnitOfMeasureProductDTO(UnitOfMeasureDTO unitOfMeasureDTO, List<ProductProfileDTO> productProfiles,
			LocalDateTime lastModifiedDate, boolean activated) {
		super();
		this.unitOfMeasureDTO = unitOfMeasureDTO;
		this.productProfiles = productProfiles;
		this.lastModifiedDate = lastModifiedDate;
		this.activated = activated;
	}

	public UnitOfMeasureDTO getUnitOfMeasureDTO() {
		return unitOfMeasureDTO;
	}

	public void setUnitOfMeasureDTO(UnitOfMeasureDTO unitOfMeasureDTO) {
		this.unitOfMeasureDTO = unitOfMeasureDTO;
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
