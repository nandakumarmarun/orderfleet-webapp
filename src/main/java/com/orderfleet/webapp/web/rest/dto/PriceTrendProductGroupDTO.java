package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A DTO for the PriceTrendProductGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
public class PriceTrendProductGroupDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	private List<PriceTrendProductDTO> priceTrendProducts;

	private boolean activated;

	private LocalDateTime lastModifiedDate;

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<PriceTrendProductDTO> getPriceTrendProducts() {
		return priceTrendProducts;
	}

	public void setPriceTrendProducts(List<PriceTrendProductDTO> priceTrendProducts) {
		this.priceTrendProducts = priceTrendProducts;
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

		PriceTrendProductGroupDTO priceTrendProductGroupDTO = (PriceTrendProductGroupDTO) o;

		if (!Objects.equals(pid, priceTrendProductGroupDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "PriceTrendProductGroupDTO{" + ", pid='" + pid + "'" + ", name='" + name + "'" + ", alias='" + alias
				+ "'" + ", description='" + description + "'" + '}';
	}
}
