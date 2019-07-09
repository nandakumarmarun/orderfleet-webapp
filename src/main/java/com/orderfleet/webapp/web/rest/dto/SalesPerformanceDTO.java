package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

/**
 * A DTO for the SalesPerformanceDTO.
 */
public class SalesPerformanceDTO {

	private String pid;

	private String documentNumberLocal;

	private String documentNumberServer;

	private String documentPid;

	private String documentName;

	private LocalDateTime createdDate;

	private LocalDateTime documentDate;

	private String receiverAccountPid;

	private String receiverAccountName;

	private String supplierAccountPid;

	private String supplierAccountName;

	private String employeePid;

	private String employeeName;

	private String userName;

	private double documentTotal;

	private double documentVolume;

	private double totalVolume;

	private boolean pdfDownloadStatus;

	private Boolean status;

	private long orderNumber;

	private TallyDownloadStatus tallyDownloadStatus;

	private String visitRemarks;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getDocumentNumberLocal() {
		return documentNumberLocal;
	}

	public void setDocumentNumberLocal(String documentNumberLocal) {
		this.documentNumberLocal = documentNumberLocal;
	}

	public String getDocumentNumberServer() {
		return documentNumberServer;
	}

	public void setDocumentNumberServer(String documentNumberServer) {
		this.documentNumberServer = documentNumberServer;
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

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(LocalDateTime documentDate) {
		this.documentDate = documentDate;
	}

	public String getReceiverAccountPid() {
		return receiverAccountPid;
	}

	public void setReceiverAccountPid(String receiverAccountPid) {
		this.receiverAccountPid = receiverAccountPid;
	}

	public String getReceiverAccountName() {
		return receiverAccountName;
	}

	public void setReceiverAccountName(String receiverAccountName) {
		this.receiverAccountName = receiverAccountName;
	}

	public String getSupplierAccountPid() {
		return supplierAccountPid;
	}

	public void setSupplierAccountPid(String supplierAccountPid) {
		this.supplierAccountPid = supplierAccountPid;
	}

	public String getSupplierAccountName() {
		return supplierAccountName;
	}

	public void setSupplierAccountName(String supplierAccountName) {
		this.supplierAccountName = supplierAccountName;
	}

	public String getEmployeePid() {
		return employeePid;
	}

	public void setEmployeePid(String employeePid) {
		this.employeePid = employeePid;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public double getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(double totalVolume) {
		this.totalVolume = totalVolume;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public TallyDownloadStatus getTallyDownloadStatus() {
		return tallyDownloadStatus;
	}

	public void setTallyDownloadStatus(TallyDownloadStatus tallyDownloadStatus) {
		this.tallyDownloadStatus = tallyDownloadStatus;
	}

	public String getVisitRemarks() {
		return visitRemarks;
	}

	public void setVisitRemarks(String visitRemarks) {
		this.visitRemarks = visitRemarks;
	}

	public boolean getPdfDownloadStatus() {
		return pdfDownloadStatus;
	}

	public void setPdfDownloadStatus(boolean pdfDownloadStatus) {
		this.pdfDownloadStatus = pdfDownloadStatus;
	}

	public long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(long orderNumber) {
		this.orderNumber = orderNumber;
	}

}
