package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.DocumentAccountType;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;

/**
 * A DTO for the DocumentAccountType entity.
 * 
 * @author Muhammed Riyas T
 * @since August 11, 2016
 */
public class DocumentAccountTypeDTO {

	private String documentPid;

	private String documentName;

	private String accountTypePid;

	private String accountTypeName;

	private AccountTypeColumn accountTypeColumn;

	private LocalDateTime lastModifiedDate;

	public DocumentAccountTypeDTO() {

	}

	public DocumentAccountTypeDTO(DocumentAccountType documentAccountType) {
		super();
		this.documentPid = documentAccountType.getDocument().getPid();
		this.documentName = documentAccountType.getDocument().getName();
		this.accountTypePid = documentAccountType.getAccountType().getPid();
		this.accountTypeName = documentAccountType.getAccountType().getName();
		this.accountTypeColumn = documentAccountType.getAccountTypeColumn();
		this.lastModifiedDate = documentAccountType.getLastModifiedDate();
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

	public String getAccountTypePid() {
		return accountTypePid;
	}

	public void setAccountTypePid(String accountTypePid) {
		this.accountTypePid = accountTypePid;
	}

	public String getAccountTypeName() {
		return accountTypeName;
	}

	public void setAccountTypeName(String accountTypeName) {
		this.accountTypeName = accountTypeName;
	}

	public AccountTypeColumn getAccountTypeColumn() {
		return accountTypeColumn;
	}

	public void setAccountTypeColumn(AccountTypeColumn accountTypeColumn) {
		this.accountTypeColumn = accountTypeColumn;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
