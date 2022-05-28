package com.orderfleet.webapp.web.rest.api.dto;

import java.time.LocalDateTime;

public class InvoiceDTO {
	
	private String invoiceNo;
	private LocalDateTime invoiceDate;
	private Double documentTotal;
	
	public InvoiceDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InvoiceDTO(String invoiceNo, LocalDateTime invoiceDate, Double documentTotal) {
		super();
		this.invoiceNo = invoiceNo;
		this.invoiceDate = invoiceDate;
		this.documentTotal = documentTotal;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public LocalDateTime getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(LocalDateTime invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Double getDocumentTotal() {
		return documentTotal;
	}

	public void setDocumentTotal(Double documentTotal) {
		this.documentTotal = documentTotal;
	}
	
	

}
