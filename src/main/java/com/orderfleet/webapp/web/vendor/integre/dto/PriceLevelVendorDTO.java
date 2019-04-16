package com.orderfleet.webapp.web.vendor.integre.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class PriceLevelVendorDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String code;

	private String description;

	private List<PriceLevelListVendorDTO> levelListDTOs;

	private boolean activated;
	
	private int sortOrder;

	private LocalDateTime lastModifiedDate;
	
	
	
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<PriceLevelListVendorDTO> getLevelListDTOs() {
		return levelListDTOs;
	}

	public void setLevelListDTOs(List<PriceLevelListVendorDTO> levelListDTOs) {
		this.levelListDTOs = levelListDTOs;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		PriceLevelVendorDTO priceLevelVendorDTO = (PriceLevelVendorDTO) o;

		if (!Objects.equals(pid, priceLevelVendorDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}
}
