package com.orderfleet.webapp.web.rest.api.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.DocumentInventoryVoucherColumn;

/**
 * A DTO DocumentInventoryVoucherColumn
 * 
 * @author Muhammed Riyas T
 * @since July 26, 2016
 */
public class DocumentInventoryVoucherColumnDTO {

	private String documentPid;

	private String documentName;

	private String inventoryVoucherColumn;

	private boolean enabled = true;

	private LocalDateTime lastModifiedDate;

	private String label;

	public DocumentInventoryVoucherColumnDTO() {
	}

	public DocumentInventoryVoucherColumnDTO(DocumentInventoryVoucherColumn documentInventoryVoucherColumn) {
		super();
		this.documentPid = documentInventoryVoucherColumn.getDocument().getPid();
		this.documentName = documentInventoryVoucherColumn.getDocument().getName();
		this.inventoryVoucherColumn = documentInventoryVoucherColumn.getInventoryVoucherColumn().getName();
		this.enabled = documentInventoryVoucherColumn.getEnabled();
		this.lastModifiedDate = documentInventoryVoucherColumn.getLastModifiedDate();
		this.label = documentInventoryVoucherColumn.getLabel() == null
				|| documentInventoryVoucherColumn.getLabel().equalsIgnoreCase("") ? null
						: documentInventoryVoucherColumn.getLabel();
	}

	public String getDocumentPid() {
		return documentPid;
	}

	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getInventoryVoucherColumn() {
		return inventoryVoucherColumn;
	}

	public void setInventoryVoucherColumn(String inventoryVoucherColumn) {
		this.inventoryVoucherColumn = inventoryVoucherColumn;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
