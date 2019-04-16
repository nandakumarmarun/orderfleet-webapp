package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.PriceTrendConfiguration;

/**
 * A DTO for the PriceTrendConfiguration entity.
 * 
 * @author Muhammed Riyas T
 * @since Sep 03, 2016
 */
public class PriceTrendConfigurationDTO {

	private String name;

	@Size(max = 55)
	private String value;

	private LocalDateTime lastModifiedDate;

	public PriceTrendConfigurationDTO() {
		super();
	}

	public PriceTrendConfigurationDTO(PriceTrendConfiguration priceTrendConfiguration) {
		super();
		this.name = priceTrendConfiguration.getName();
		this.value = priceTrendConfiguration.getValue();
		this.lastModifiedDate = priceTrendConfiguration.getLastModifiedDate();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
