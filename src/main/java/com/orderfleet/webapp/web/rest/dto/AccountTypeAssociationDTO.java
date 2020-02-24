package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.AccountTypeAssociation;

public class AccountTypeAssociationDTO {

	private String accountTypePid;
	private String accountTypeName;
	private String associatedAccountTypePid;
	private String associatedAccountTypeName;
	private LocalDateTime lastModifiedDate;

	public AccountTypeAssociationDTO() {
		super();
	}

	public AccountTypeAssociationDTO(AccountTypeAssociation accountTypeAssociation) {
		this.accountTypePid = accountTypeAssociation.getAccountType().getPid();
		this.accountTypeName = accountTypeAssociation.getAccountType().getName();
		this.associatedAccountTypePid = accountTypeAssociation.getAssociatedAccountType().getPid();
		this.associatedAccountTypeName = accountTypeAssociation.getAssociatedAccountType().getName();
		this.lastModifiedDate = accountTypeAssociation.getLastModifiedDate();
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

	public String getAssociatedAccountTypePid() {
		return associatedAccountTypePid;
	}

	public void setAssociatedAccountTypePid(String associatedAccountTypePid) {
		this.associatedAccountTypePid = associatedAccountTypePid;
	}

	public String getAssociatedAccountTypeName() {
		return associatedAccountTypeName;
	}

	public void setAssociatedAccountTypeName(String associatedAccountTypeName) {
		this.associatedAccountTypeName = associatedAccountTypeName;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
