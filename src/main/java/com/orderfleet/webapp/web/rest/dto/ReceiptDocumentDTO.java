package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.ReceiptDocument;

/**
 * A ReceiptDocumentDTO
 * 
 * @author Sarath
 * @since Sep 6, 2016
 */
public class ReceiptDocumentDTO {

	private Long id;
	private String documentPid;
	private String documentName;
	private String companyPid;
	private boolean activated = true;

	public ReceiptDocumentDTO() {
		super();
	}

	public ReceiptDocumentDTO(Long id, String documentPid, String documentName, String companyPid, boolean activated) {
		super();
		this.id = id;
		this.documentPid = documentPid;
		this.documentName = documentName;
		this.companyPid = companyPid;
		this.activated = activated;
	}

	public ReceiptDocumentDTO(ReceiptDocument receiptDocument) {
		super();
		this.id = receiptDocument.getId();
		this.documentPid = receiptDocument.getDocument().getPid();
		this.documentName = receiptDocument.getDocument().getName();
		this.companyPid = receiptDocument.getCompany().getPid();
		this.activated = receiptDocument.isActivated();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	@Override
	public String toString() {
		return "ReceiptDocumentDTO [id=" + id + ", documentPid=" + documentPid + ", documentName=" + documentName
				+ ", companyPid=" + companyPid + ", activated=" + activated + "]";
	}

}
