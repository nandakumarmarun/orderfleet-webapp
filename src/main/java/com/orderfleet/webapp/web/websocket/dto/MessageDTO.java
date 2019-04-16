package com.orderfleet.webapp.web.websocket.dto;

/**
 * DTO for storing a websocket message.
 * 
 * @author Shaheer
 * @since December 23, 2016
 */
public class MessageDTO {

	private String documentName;

	private double documentTotal;

	private String remarks;

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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}