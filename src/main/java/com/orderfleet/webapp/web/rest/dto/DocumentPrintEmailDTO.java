package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.DocumentPrintEmail;

public class DocumentPrintEmailDTO {
	
	private Long documentPrintEmailId;
	private String printFilePath;
	private String emailFilePath;
	private String documentPid;
	private String documentName;
	private String companyPid;
	private String name;
	
	public DocumentPrintEmailDTO() {
		super();
	}

	public DocumentPrintEmailDTO(DocumentPrintEmail documentPrintEmail) {
		super();
		this.companyPid=documentPrintEmail.getCompany().getPid();
		this.documentPid=documentPrintEmail.getDocument().getPid();
		this.documentName=documentPrintEmail.getDocument().getName();
		this.emailFilePath=documentPrintEmail.getEmailFilePath();
		this.printFilePath=documentPrintEmail.getPrintFilePath();
		this.documentPrintEmailId=documentPrintEmail.getId();
		this.name=documentPrintEmail.getName();
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrintFilePath() {
		return printFilePath;
	}
	public void setPrintFilePath(String printFilePath) {
		this.printFilePath = printFilePath;
	}
	public String getEmailFilePath() {
		return emailFilePath;
	}
	public void setEmailFilePath(String emailFilePath) {
		this.emailFilePath = emailFilePath;
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
	

	public Long getDocumentPrintEmailId() {
		return documentPrintEmailId;
	}

	public void setDocumentPrintEmailId(Long documentPrintEmailId) {
		this.documentPrintEmailId = documentPrintEmailId;
	}

	@Override
	public String toString() {
		return "DocumentPrintEmailDTO [documentPrintEmailId=" + documentPrintEmailId + ", printFilePath="
				+ printFilePath + ", emailFilePath=" + emailFilePath + ", documentPid=" + documentPid
				+ ", documentName=" + documentName + ", companyPid=" + companyPid + ", name=" + name + "]";
	}

	
	
	
}
