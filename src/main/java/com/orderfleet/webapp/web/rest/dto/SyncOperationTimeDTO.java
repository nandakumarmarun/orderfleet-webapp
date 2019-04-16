package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.SyncOperationType;

public class SyncOperationTimeDTO {

	private SyncOperationType operationType;
	private LocalDateTime lastSyncStartedDate;
	private LocalDateTime lastSyncCompletedDate;
	private double lastSyncTime;
	private boolean completed;
	
	public SyncOperationTimeDTO() {
		super();
	}
	
	public SyncOperationTimeDTO(SyncOperation syncOperation) {
		super();
		this.completed=syncOperation.getCompleted();
		this.lastSyncCompletedDate=syncOperation.getLastSyncCompletedDate();
		this.lastSyncStartedDate=syncOperation.getLastSyncStartedDate();
		this.operationType=syncOperation.getOperationType();
	}
	
	public SyncOperationType getOperationType() {
		return operationType;
	}
	public void setOperationType(SyncOperationType operationType) {
		this.operationType = operationType;
	}
	public LocalDateTime getLastSyncStartedDate() {
		return lastSyncStartedDate;
	}
	public void setLastSyncStartedDate(LocalDateTime lastSyncStartedDate) {
		this.lastSyncStartedDate = lastSyncStartedDate;
	}
	public LocalDateTime getLastSyncCompletedDate() {
		return lastSyncCompletedDate;
	}
	public void setLastSyncCompletedDate(LocalDateTime lastSyncCompletedDate) {
		this.lastSyncCompletedDate = lastSyncCompletedDate;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public double getLastSyncTime() {
		return lastSyncTime;
	}

	public void setLastSyncTime(double lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}
	
	@Override
	public String toString() {
		return "SyncOperationTimeDTO [operationType=" + operationType + ", lastSyncStartedDate=" + lastSyncStartedDate
				+ ", lastSyncCompletedDate=" + lastSyncCompletedDate + ", lastSyncTime=" + lastSyncTime + ", completed="
				+ completed + "]";
	}
}
