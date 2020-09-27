package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.UnitOfMeasure;

/**
 * A DTO for the ProductGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
public class UnitOfMeasureDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	private String uomId;

	@NotNull
	private boolean activated = true;

	private LocalDateTime lastModifiedDate;

	public UnitOfMeasureDTO() {
		super();
	}

//	public ProductGroupDTO(ProductGroup productGroup) {
//		this(productGroup.getPid(), productGroup.getName(), productGroup.getAlias(), productGroup.getDescription(),
//				productGroup.getLastModifiedDate());
//
//	}

	public UnitOfMeasureDTO(UnitOfMeasure unitOfMeasure) {
		super();
		this.pid = unitOfMeasure.getPid();
		this.name = unitOfMeasure.getName();
		this.alias = unitOfMeasure.getAlias();
		this.description = unitOfMeasure.getDescription();
		this.lastModifiedDate = unitOfMeasure.getLastModifiedDate();
		this.uomId = unitOfMeasure.getUomId();

	}

	public UnitOfMeasureDTO(String pid, String name, String alias, String description, LocalDateTime lastModifiedDate) {
		super();
		this.pid = pid;
		this.name = name;
		this.alias = alias;
		this.description = description;
		this.lastModifiedDate = lastModifiedDate;
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

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}