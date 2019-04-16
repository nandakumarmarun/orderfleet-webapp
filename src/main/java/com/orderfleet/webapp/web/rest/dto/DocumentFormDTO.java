package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.DocumentForms;

public class DocumentFormDTO implements Cloneable {

	private String formPid;
	private String formName;
	private String documentPid;
	private String documentName;
	private LocalDateTime lastModifiedDate;
	private int sortOrder;

	public DocumentFormDTO() {
		super();
	}

	public DocumentFormDTO(DocumentForms documentForms) {
		this.formPid = documentForms.getForm().getPid();
		this.formName = documentForms.getForm().getName();
		this.documentPid = documentForms.getDocument().getPid();
		this.documentName = documentForms.getDocument().getName();
		this.lastModifiedDate = documentForms.getLastModifiedDate();
		this.sortOrder=documentForms.getSortOrder();
	}

	public String getFormPid() {
		return formPid;
	}

	public void setFormPid(String formPid) {
		this.formPid = formPid;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
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

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public String toString() {
		return "DocumentFormDTO [formPid=" + formPid + ", formName=" + formName + ", documentPid=" + documentPid
				+ ", documentName=" + documentName + ", lastModifiedDate=" + lastModifiedDate + ", sortOrder="
				+ sortOrder + "]";
	}
	

}
