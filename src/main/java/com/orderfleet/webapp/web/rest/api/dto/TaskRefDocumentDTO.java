package com.orderfleet.webapp.web.rest.api.dto;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;

public class TaskRefDocumentDTO {

	private List<UserTaskDTO> userTaskList;

	private InventoryVoucherHeaderDTO inventoryVoucher;

	private AccountingVoucherHeaderDTO accountingVoucher;

	private DynamicDocumentHeaderDTO dynamicDocument;

	public List<UserTaskDTO> getUserTaskList() {
		return userTaskList;
	}

	public void setUserTaskList(List<UserTaskDTO> userTaskList) {
		this.userTaskList = userTaskList;
	}

	public InventoryVoucherHeaderDTO getInventoryVoucher() {
		return inventoryVoucher;
	}

	public void setInventoryVoucher(InventoryVoucherHeaderDTO inventoryVoucher) {
		this.inventoryVoucher = inventoryVoucher;
	}

	public AccountingVoucherHeaderDTO getAccountingVoucher() {
		return accountingVoucher;
	}

	public void setAccountingVoucher(AccountingVoucherHeaderDTO accountingVoucher) {
		this.accountingVoucher = accountingVoucher;
	}

	public DynamicDocumentHeaderDTO getDynamicDocument() {
		return dynamicDocument;
	}

	public void setDynamicDocument(DynamicDocumentHeaderDTO dynamicDocument) {
		this.dynamicDocument = dynamicDocument;
	}

}
