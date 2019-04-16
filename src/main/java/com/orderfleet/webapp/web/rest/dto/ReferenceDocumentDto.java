package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.ReferenceDocument;

public class ReferenceDocumentDto {
	
	private String documentPid;
	
	private String documentName;
	
	private String refDocumentPid;
	
	private String refDocumentName;
	
	public ReferenceDocumentDto() {
		// TODO Auto-generated constructor stub
	}
	
	public ReferenceDocumentDto(ReferenceDocument referenceDocument) {
		this.documentPid = referenceDocument.getDocument().getPid();
		this.documentName = referenceDocument.getDocument().getName();
		this.refDocumentPid = referenceDocument.getReferenceDocument().getPid();
		this.refDocumentName = referenceDocument.getReferenceDocument().getName();
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
