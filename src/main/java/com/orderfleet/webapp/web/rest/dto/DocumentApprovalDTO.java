package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.DocumentApproval;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;

/**
 * A DTO for the DocumentApproval entity.
 * 
 * @author Muhammed Riyas T
 * @since November 23, 2016
 */
public class DocumentApprovalDTO {

	private String pid;

	private String documentApprovalLevelPid;

	private String documentApprovalLevelName;

	private String documentPid;

	private String documentName;

	private boolean completed;

	private ApprovalStatus approvalStatus;

	private String remarks;

	public DocumentApprovalDTO() {

	}

	public DocumentApprovalDTO(DocumentApproval documentApproval) {
		super();
		this.pid = documentApproval.getPid();
		this.documentApprovalLevelPid = documentApproval.getDocumentApprovalLevel().getPid();
		this.documentApprovalLevelName = documentApproval.getDocumentApprovalLevel().getName();
		this.documentPid = documentApproval.getDocument().getPid();
		this.documentName = documentApproval.getDocument().getName();
		this.completed = documentApproval.getCompleted();
		this.approvalStatus = documentApproval.getApprovalStatus();
		this.remarks = documentApproval.getRemarks();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getDocumentApprovalLevelPid() {
		return documentApprovalLevelPid;
	}

	public void setDocumentApprovalLevelPid(String documentApprovalLevelPid) {
		this.documentApprovalLevelPid = documentApprovalLevelPid;
	}

	public String getDocumentApprovalLevelName() {
		return documentApprovalLevelName;
	}

	public void setDocumentApprovalLevelName(String documentApprovalLevelName) {
		this.documentApprovalLevelName = documentApprovalLevelName;
	}

	public String getDocumentPid() {
		return documentPid;
	}

	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
