package com.orderfleet.webapp.web.rest.api.dto;

import java.util.List;

import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;

public final class ExecutiveTaskSubmissionTransactionWrapper {
	
	private final TaskSubmissionResponse taskSubmissionResponse;
	
	private final ExecutiveTaskExecution executiveTaskExecution;
	
	private final List<InventoryVoucherHeader> inventoryVouchers; 
	
	private final List<AccountingVoucherHeader> accountingVouchers;
	
	private final List<DynamicDocumentHeader> dynamicDocuments; 
	
	private final boolean dashboardUpdate;
	
	public ExecutiveTaskSubmissionTransactionWrapper(TaskSubmissionResponse taskSubmissionResponse,
			ExecutiveTaskExecution executiveTaskExecution, List<InventoryVoucherHeader> inventoryVouchers,
			List<AccountingVoucherHeader> accountingVouchers, List<DynamicDocumentHeader> dynamicDocuments,
			boolean dashboardUpdate) {
		super();
		this.taskSubmissionResponse = taskSubmissionResponse;
		this.executiveTaskExecution = executiveTaskExecution;
		this.inventoryVouchers = inventoryVouchers;
		this.accountingVouchers = accountingVouchers;
		this.dynamicDocuments = dynamicDocuments;
		this.dashboardUpdate = dashboardUpdate;
	}

	public TaskSubmissionResponse getTaskSubmissionResponse() {
		return taskSubmissionResponse;
	}

	public ExecutiveTaskExecution getExecutiveTaskExecution() {
		return executiveTaskExecution;
	}

	public List<InventoryVoucherHeader> getInventoryVouchers() {
		return inventoryVouchers;
	}

	public List<AccountingVoucherHeader> getAccountingVouchers() {
		return accountingVouchers;
	}

	public List<DynamicDocumentHeader> getDynamicDocuments() {
		return dynamicDocuments;
	}

	public boolean isDashboardUpdate() {
		return dashboardUpdate;
	}

}
