package com.orderfleet.webapp.web.rest.dto;

public class ExecutiveTaskExecutionDetailView {

	private String pid;

	private String documentName;

	private double documentTotal;
	
	private double documentVolume;

	private String documentType;
	
	private boolean imageSaved;

	public ExecutiveTaskExecutionDetailView(String pid, String documentName, double documentTotal,
			String documentType) {
		super();
		this.pid = pid;
		this.documentName = documentName;
		this.documentTotal = documentTotal;
		this.documentType = documentType;
	}

	public ExecutiveTaskExecutionDetailView(String pid, String documentName, String documentType, boolean imageSaved) {
		super();
		this.pid = pid;
		this.documentName = documentName;
		this.documentType = documentType;
		this.imageSaved = imageSaved;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public double getDocumentTotal() {
		return documentTotal;
	}

	public void setDocumentTotal(double documentTotal) {
		this.documentTotal = documentTotal;
	}
	
	public double getDocumentVolume() {
		return documentVolume;
	}

	public void setDocumentVolume(double documentVolume) {
		this.documentVolume = documentVolume;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public boolean getImageSaved() {
		return imageSaved;
	}

	public void setImageSaved(boolean imageSaved) {
		this.imageSaved = imageSaved;
	}

	
}
