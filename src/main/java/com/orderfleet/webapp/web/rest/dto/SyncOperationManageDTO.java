package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.SyncOperationType;

/**
 * 
 * A DTO Object For SyncOperation.
 *
 * @author Sarath
 * @since Feb 26, 2018
 *
 */
public class SyncOperationManageDTO {

	private SyncOperationType operationType;
	private LocalDateTime lastSyncStartedDate;
	private LocalDateTime lastSyncCompletedDate;
	private double lastSyncTime;
	private boolean completed = false;
	private String companyName;
	private String companyPid;
	private boolean user = false;
	private boolean document = false;
	private boolean reset = false;

	public SyncOperationManageDTO() {
		super();
	}

	public SyncOperationManageDTO(SyncOperation syncOperation) {
		super();
		this.operationType = syncOperation.getOperationType();
		this.lastSyncStartedDate = syncOperation.getLastSyncStartedDate();
		this.lastSyncCompletedDate = syncOperation.getLastSyncCompletedDate();
		this.lastSyncTime = syncOperation.getLastSyncTime();
		this.completed = syncOperation.getCompleted();
		this.companyName = syncOperation.getCompany().getLegalName();
		this.companyPid = syncOperation.getCompany().getPid();
		this.user = syncOperation.getUser();
		this.document = syncOperation.getDocument();
		this.reset = syncOperation.getReset();
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

	public double getLastSyncTime() {
		return lastSyncTime;
	}

	public void setLastSyncTime(double lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	public boolean isUser() {
		return user;
	}

	public void setUser(boolean user) {
		this.user = user;
	}

	public boolean isDocument() {
		return document;
	}

	public void setDocument(boolean document) {
		this.document = document;
	}

	public boolean isReset() {
		return reset;
	}

	public void setReset(boolean reset) {
		this.reset = reset;
	}

	@Override
	public String toString() {
		return "SyncOperationManageDTO [operationType=" + operationType + ", lastSyncStartedDate=" + lastSyncStartedDate
				+ ", lastSyncCompletedDate=" + lastSyncCompletedDate + ", lastSyncTime=" + lastSyncTime + ", completed="
				+ completed + ", companyName=" + companyName + ", companyPid=" + companyPid + ", user=" + user
				+ ", document=" + document + ", reset=" + reset + "]";
	}

}
