package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.enums.SyncOperationType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A SyncOperationDTO.
 *
 * @author Sarath
 * @since Mar 14, 2017
 */
public class SyncOperationDTO {

	private List<String> operationTypes;
	private String companyName;

	private SyncOperationType operationType;
	private LocalDateTime lastSyncCompletedDate;

	private LocalDateTime ErpUpdatedDate;

	public SyncOperationDTO() {
		super();
	}

	public SyncOperationDTO(List<String> operationTypes, String companyName) {
		super();
		this.operationTypes = operationTypes;
		this.companyName = companyName;
	}

	public SyncOperationDTO(List<String> operationTypes, String companyName, SyncOperationType operationType, LocalDateTime lastSyncCompletedDate, LocalDateTime erpUpdatedDate) {
		this.operationTypes = operationTypes;
		this.companyName = companyName;
		this.operationType = operationType;
		this.lastSyncCompletedDate = lastSyncCompletedDate;
		ErpUpdatedDate = erpUpdatedDate;
	}

	public List<String> getOperationTypes() {
		return operationTypes;
	}

	public void setOperationTypes(List<String> operationTypes) {
		this.operationTypes = operationTypes;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public SyncOperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(SyncOperationType operationType) {
		this.operationType = operationType;
	}

	public LocalDateTime getLastSyncCompletedDate() {
		return lastSyncCompletedDate;
	}

	public void setLastSyncCompletedDate(LocalDateTime lastSyncCompletedDate) {
		this.lastSyncCompletedDate = lastSyncCompletedDate;
	}

	public LocalDateTime getErpUpdatedDate() {
		return ErpUpdatedDate;
	}

	public void setErpUpdatedDate(LocalDateTime erpUpdatedDate) {
		ErpUpdatedDate = erpUpdatedDate;
	}
}
