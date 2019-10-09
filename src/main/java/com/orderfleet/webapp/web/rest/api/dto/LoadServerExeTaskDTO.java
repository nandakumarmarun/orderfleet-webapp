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
public class LoadServerExeTaskDTO {

	private List<ExecutiveTaskExecutionDTO> executiveTaskExecutionDTOs = new ArrayList<>();

	private int visitCount;

	private int inventoryVoucherCount;

	private int accountingVoucherCount;

	private int dynamicDocumentCount;

	private double inventoryVoucherTotal;

	private double accountingVoucherTotal;

	public List<ExecutiveTaskExecutionDTO> getExecutiveTaskExecutionDTOs() {
		return executiveTaskExecutionDTOs;
	}

	public void setExecutiveTaskExecutionDTOs(List<ExecutiveTaskExecutionDTO> executiveTaskExecutionDTOs) {
		this.executiveTaskExecutionDTOs = executiveTaskExecutionDTOs;
	}

	public int getInventoryVoucherCount() {
		return inventoryVoucherCount;
	}

	public void setInventoryVoucherCount(int inventoryVoucherCount) {
		this.inventoryVoucherCount = inventoryVoucherCount;
	}

	public int getAccountingVoucherCount() {
		return accountingVoucherCount;
	}

	public void setAccountingVoucherCount(int accountingVoucherCount) {
		this.accountingVoucherCount = accountingVoucherCount;
	}

	public double getInventoryVoucherTotal() {
		return inventoryVoucherTotal;
	}

	public void setInventoryVoucherTotal(double inventoryVoucherTotal) {
		this.inventoryVoucherTotal = inventoryVoucherTotal;
	}

	public double getAccountingVoucherTotal() {
		return accountingVoucherTotal;
	}

	public void setAccountingVoucherTotal(double accountingVoucherTotal) {
		this.accountingVoucherTotal = accountingVoucherTotal;
	}

	public int getDynamicDocumentCount() {
		return dynamicDocumentCount;
	}

	public void setDynamicDocumentCount(int dynamicDocumentCount) {
		this.dynamicDocumentCount = dynamicDocumentCount;
	}

	public int getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}

}
