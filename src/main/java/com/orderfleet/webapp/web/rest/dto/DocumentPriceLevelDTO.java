package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.DocumentPriceLevel;

public class DocumentPriceLevelDTO implements Cloneable {

	private String priceLevelPid;
	private String priceLevelName;
	private String documentPid;
	private String documentName;
	private LocalDateTime lastModifiedDate;

	public DocumentPriceLevelDTO() {
		super();
	}

	public DocumentPriceLevelDTO(DocumentPriceLevel documentPriceLevel) {
		this.priceLevelPid = documentPriceLevel.getPriceLevel().getPid();
		this.priceLevelName = documentPriceLevel.getPriceLevel().getName();
		this.documentPid = documentPriceLevel.getDocument().getPid();
		this.documentName = documentPriceLevel.getDocument().getName();
		this.lastModifiedDate = documentPriceLevel.getLastModifiedDate();
	}

	public String getPriceLevelPid() {
		return priceLevelPid;
	}

	public void setPriceLevelPid(String priceLevelPid) {
		this.priceLevelPid = priceLevelPid;
	}

	public String getPriceLevelName() {
		return priceLevelName;
	}

	public void setPriceLevelName(String priceLevelName) {
		this.priceLevelName = priceLevelName;
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

}
