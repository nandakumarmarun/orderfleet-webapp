package com.orderfleet.webapp.web.rest.api.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.DocumentAccountingVoucherColumn;

/**
 * A DTO DocumentAccountingVoucherColumn
 * 
 * @author Muhammed Riyas T
 * @since July 26, 2016
 */
public class DocumentAccountingVoucherColumnDTO {

	private String documentPid;

	private String documentName;

	private String accountingVoucherColumn;

	private boolean enabled = true;

	private LocalDateTime lastModifiedDate;

	public DocumentAccountingVoucherColumnDTO() {
	}

	public DocumentAccountingVoucherColumnDTO(DocumentAccountingVoucherColumn documentAccountingVoucherColumn) {
		super();
		this.documentPid = documentAccountingVoucherColumn.getDocument().getPid();
		this.documentName = documentAccountingVoucherColumn.getDocument().getName();
		this.accountingVoucherColumn = documentAccountingVoucherColumn.getAccountingVoucherColumn().getName();
		this.enabled = documentAccountingVoucherColumn.getEnabled();
		this.lastModifiedDate = documentAccountingVoucherColumn.getLastModifiedDate();
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

	public String getAccountingVoucherColumn() {
		return accountingVoucherColumn;
	}

	public void setAccountingVoucherColumn(String accountingVoucherColumn) {
		this.accountingVoucherColumn = accountingVoucherColumn;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
