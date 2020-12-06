package com.orderfleet.webapp.web.vendor.garuda.dto;

import java.util.List;

public class PriceLevelGarudaDTO {
	private String name;
	
	private List<PriceLevelListGarudaDTO> priceLevelListGarudaDTO;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PriceLevelListGarudaDTO> getPriceLevelListGarudaDTO() {
		return priceLevelListGarudaDTO;
	}

	public void setPriceLevelListGarudaDTO(List<PriceLevelListGarudaDTO> priceLevelListGarudaDTO) {
		this.priceLevelListGarudaDTO = priceLevelListGarudaDTO;
	}
}
