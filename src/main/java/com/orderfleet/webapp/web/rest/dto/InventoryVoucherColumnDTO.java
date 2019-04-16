package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.DocumentInventoryVoucherColumn;

/**
 * A DTO InventoryVoucherColumn
 * 
 * @author Muhammed Riyas T
 * @since July 26, 2016
 */
public class InventoryVoucherColumnDTO {

	private String name;
	private boolean enabled;
	private String label;

	public InventoryVoucherColumnDTO() {
	}

	public InventoryVoucherColumnDTO(DocumentInventoryVoucherColumn documentInventoryVoucherColumn) {
		super();
		this.name = documentInventoryVoucherColumn.getInventoryVoucherColumn().getName();
		this.enabled = documentInventoryVoucherColumn.getEnabled();
		this.label = documentInventoryVoucherColumn.getLabel();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
