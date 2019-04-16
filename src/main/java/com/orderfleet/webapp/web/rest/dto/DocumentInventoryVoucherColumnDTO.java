package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

/**
 * A DTO DocumentInventoryVoucherColumn
 * 
 * @author Muhammed Riyas T
 * @since July 26, 2016
 */
public class DocumentInventoryVoucherColumnDTO {

	private String documentPid;

	private List<InventoryVoucherColumnDTO> inventoryVoucherColumns;

	public DocumentInventoryVoucherColumnDTO() {

	}

	public DocumentInventoryVoucherColumnDTO(String documentPid,
			List<InventoryVoucherColumnDTO> inventoryVoucherColumns) {
		super();
		this.documentPid = documentPid;
		this.inventoryVoucherColumns = inventoryVoucherColumns;
	}

	public String getDocumentPid() {
		return documentPid;
	}

	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}

	public List<InventoryVoucherColumnDTO> getInventoryVoucherColumns() {
		return inventoryVoucherColumns;
	}

	public void setInventoryVoucherColumns(List<InventoryVoucherColumnDTO> inventoryVoucherColumns) {
		this.inventoryVoucherColumns = inventoryVoucherColumns;
	}

}
