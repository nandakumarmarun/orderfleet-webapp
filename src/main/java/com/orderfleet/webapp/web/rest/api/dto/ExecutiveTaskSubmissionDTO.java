package com.orderfleet.webapp.web.rest.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;

/**
 * A ExecutiveTaskSubmission DTO.
 * 
 * @author Muhammed Riyas T
 * @since July 27, 2016
 */
public class ExecutiveTaskSubmissionDTO {

	private ExecutiveTaskExecutionDTO executiveTaskExecutionDTO;

	private List<InventoryVoucherHeaderDTO> inventoryVouchers = new ArrayList<>();

	private List<AccountingVoucherHeaderDTO> accountingVouchers = new ArrayList<>();

	private List<DynamicDocumentHeaderDTO> dynamicDocuments = new ArrayList<>();

	private boolean updateDashboard = true;

	public ExecutiveTaskExecutionDTO getExecutiveTaskExecutionDTO() {
		return executiveTaskExecutionDTO;
	}

	public void setExecutiveTaskExecutionDTO(ExecutiveTaskExecutionDTO executiveTaskExecutionDTO) {
		this.executiveTaskExecutionDTO = executiveTaskExecutionDTO;
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

	public boolean getUpdateDashboard() {
		return updateDashboard;
	}

	public void setUpdateDashboard(boolean updateDashboard) {
		this.updateDashboard = updateDashboard;
	}

	@Override
	public String toString() {
		return "ExecutiveTaskSubmissionDTO [executiveTaskExecutionDTO=" + executiveTaskExecutionDTO
				+ ", inventoryVouchers=" + inventoryVouchers + ", accountingVouchers=" + accountingVouchers
				+ ", dynamicDocuments=" + dynamicDocuments + ", updateDashboard=" + updateDashboard + "]";
	}
	
}
