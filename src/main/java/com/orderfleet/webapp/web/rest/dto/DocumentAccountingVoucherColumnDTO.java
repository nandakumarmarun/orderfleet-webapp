package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

/**
 * A DTO DocumentAccountingVoucherColumn
 * 
 * @author Muhammed Riyas T
 * @since July 26, 2016
 */
public class DocumentAccountingVoucherColumnDTO {

	private String documentPid;

	private List<AccountingVoucherColumnDTO> accountingVoucherColumns;

	public DocumentAccountingVoucherColumnDTO() {

	}

	public DocumentAccountingVoucherColumnDTO(String documentPid,
			List<AccountingVoucherColumnDTO> accountingVoucherColumns) {
		super();
		this.documentPid = documentPid;
		this.accountingVoucherColumns = accountingVoucherColumns;
	}

	public String getDocumentPid() {
		return documentPid;
	}

	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}

	public List<AccountingVoucherColumnDTO> getAccountingVoucherColumns() {
		return accountingVoucherColumns;
	}

	public void setAccountingVoucherColumns(List<AccountingVoucherColumnDTO> accountingVoucherColumns) {
		this.accountingVoucherColumns = accountingVoucherColumns;
	}

}
