package com.orderfleet.webapp.web.rest.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;

/**
 * A LoadServerSentItemDTO DTO.
 * 
 * @author Prashob Sasidharan
 * @since August 22, 2019
 */
public class LoadServerSentItemDTO {

	private String documentType;

	private List<InventoryVoucherHeaderDTO> inventoryVouchers = new ArrayList<>();

	private List<AccountingVoucherHeaderDTO> accountingVouchers = new ArrayList<>();

	private List<DynamicDocumentHeaderDTO> dynamicDocuments = new ArrayList<>();

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public List<InventoryVoucherHeaderDTO> getInventoryVouchers() {
		return inventoryVouchers;
	}

	public void setInventoryVouchers(List<InventoryVoucherHeaderDTO> inventoryVouchers) {
		this.inventoryVouchers = inventoryVouchers;
	}

	public List<AccountingVoucherHeaderDTO> getAccountingVouchers() {
		return accountingVouchers;
	}

	public void setAccountingVouchers(List<AccountingVoucherHeaderDTO> accountingVouchers) {
		this.accountingVouchers = accountingVouchers;
	}

	public List<DynamicDocumentHeaderDTO> getDynamicDocuments() {
		return dynamicDocuments;
	}

	public void setDynamicDocuments(List<DynamicDocumentHeaderDTO> dynamicDocuments) {
		this.dynamicDocuments = dynamicDocuments;
	}

}
