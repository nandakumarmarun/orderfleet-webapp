package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.TaskReferenceDocument;

/**
 * A DTO for the AccountType entity.
 * 
 * @author Muhammed Riyas T
 * @since October 17, 2016
 */
public class TaskReferenceDocumentDTO {

	private String taskPid;

	private String executiveTaskExecutionPid;

	private String refTransactionPid;

	private String refTransDocumentNumber;

	private String refDocumentPid;

	private String refDocumentName;

	public TaskReferenceDocumentDTO() {
		super();
	}

	public TaskReferenceDocumentDTO(TaskReferenceDocument taskReferenceDocument) {
		super();
		this.taskPid = taskReferenceDocument.getTask().getPid();
		this.executiveTaskExecutionPid = taskReferenceDocument.getExecutiveTaskExecution().getPid();
		this.refTransactionPid = taskReferenceDocument.getRefTransactionPid();
		this.refTransDocumentNumber = taskReferenceDocument.getRefTransDocumentNumber();
		this.refDocumentPid = taskReferenceDocument.getRefDocument().getPid();
		this.refDocumentName = taskReferenceDocument.getRefDocument().getName();
	}

	public String getTaskPid() {
		return taskPid;
	}

	public void setTaskPid(String taskPid) {
		this.taskPid = taskPid;
	}

	public String getExecutiveTaskExecutionPid() {
		return executiveTaskExecutionPid;
	}

	public void setExecutiveTaskExecutionPid(String executiveTaskExecutionPid) {
		this.executiveTaskExecutionPid = executiveTaskExecutionPid;
	}

	public String getRefTransactionPid() {
		return refTransactionPid;
	}

	public void setRefTransactionPid(String refTransactionPid) {
		this.refTransactionPid = refTransactionPid;
	}

	public String getRefTransDocumentNumber() {
		return refTransDocumentNumber;
	}

	public void setRefTransDocumentNumber(String refTransDocumentNumber) {
		this.refTransDocumentNumber = refTransDocumentNumber;
	}

	public String getRefDocumentPid() {
		return refDocumentPid;
	}

	public void setRefDocumentPid(String refDocumentPid) {
		this.refDocumentPid = refDocumentPid;
	}

	public String getRefDocumentName() {
		return refDocumentName;
	}

	public void setRefDocumentName(String refDocumentName) {
		this.refDocumentName = refDocumentName;
	}

}
