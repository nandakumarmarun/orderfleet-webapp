package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A DTO for the PriceLevel entity.
 * 
 * @author Muhammed Riyas T
 * @since August 22, 2016
 */
public class PriceLevelDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	private List<PriceLevelListDTO> levelListDTOs;

	private boolean activated;
	
	private int sortOrder;

	private LocalDateTime lastModifiedDate;
	
	public String getPid() {
		return pid;
	}

	public String getName() {
		return name;
	}

	public String getAlias() {
		return alias;
	}

	public String getDescription() {
		return description;
	}

	public List<PriceLevelListDTO> getLevelListDTOs() {
		return levelListDTOs;
	}

	public boolean getActivated() {
		return activated;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLevelListDTOs(List<PriceLevelListDTO> levelListDTOs) {
		this.levelListDTOs = levelListDTOs;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
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

		PriceLevelDTO priceLevelDTO = (PriceLevelDTO) o;

		if (!Objects.equals(pid, priceLevelDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "PriceLevelDTO{" + ", pid='" + pid + "'" + ", name='" + name + "'" + ", alias='" + alias + "'"
				+ ", description='" + description + "'" + '}';
	}
}
