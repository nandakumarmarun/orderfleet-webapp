package com.orderfleet.webapp.web.rest.dto;

import java.util.Objects;

import com.orderfleet.webapp.domain.AccountProfileDynamicDocumentAccountprofile;

/**
 * A DTO for the AccountProfileDynamicDocumentAccountprofile entity.
 *
 * @author Sarath
 * @since Feb 5, 2018
 *
 */
public class AccountProfileDynamicDocumentAccountprofileDTO {

	private Long id;

	private String accountProfleField;

	private String formElementPid;

	private String formElementName;

	private String documentPid;

	private String documentName;

	private String formPid;

	private String formName;

	public AccountProfileDynamicDocumentAccountprofileDTO() {
		super();
	}

	public AccountProfileDynamicDocumentAccountprofileDTO(
			AccountProfileDynamicDocumentAccountprofile accountProfileDynamicDocumentAccountprofile) {
		super();
		this.id = accountProfileDynamicDocumentAccountprofile.getId();
		this.accountProfleField = accountProfileDynamicDocumentAccountprofile.getAccountProfleField();
		this.formElementPid = accountProfileDynamicDocumentAccountprofile.getFormElement().getPid();
		this.formElementName = accountProfileDynamicDocumentAccountprofile.getFormElement().getName();
		this.formName = accountProfileDynamicDocumentAccountprofile.getForm().getName();
		this.formPid = accountProfileDynamicDocumentAccountprofile.getForm().getPid();
		this.formName = accountProfileDynamicDocumentAccountprofile.getForm().getName();
		this.documentPid = accountProfileDynamicDocumentAccountprofile.getDocument().getPid();
		this.documentName = accountProfileDynamicDocumentAccountprofile.getDocument().getName();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccountProfleField() {
		return accountProfleField;
	}

	public void setAccountProfleField(String accountProfleField) {
		this.accountProfleField = accountProfleField;
	}

	public String getFormElementPid() {
		return formElementPid;
	}

	public void setFormElementPid(String formElementPid) {
		this.formElementPid = formElementPid;
	}

	public String getFormElementName() {
		return formElementName;
	}

	public void setFormElementName(String formElementName) {
		this.formElementName = formElementName;
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

	public String getFormPid() {
		return formPid;
	}

	public void setFormPid(String formPid) {
		this.formPid = formPid;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		AccountProfileDynamicDocumentAccountprofileDTO adaDTO = (AccountProfileDynamicDocumentAccountprofileDTO) o;

		return Objects.equals(documentName, adaDTO.getDocumentName())
				&& Objects.equals(documentPid, adaDTO.getDocumentPid()) && Objects.equals(formPid, adaDTO.getFormPid())
				&& Objects.equals(formName, adaDTO.getFormName());

	}

	@Override
	public int hashCode() {
		return (this.documentName.hashCode() + this.documentPid.hashCode() + this.formPid.hashCode()
				+ this.formName.hashCode());
	}

	@Override
	public String toString() {
		return "AccountProfileDynamicDocumentAccountprofileDTO [id=" + id + ", accountProfleField=" + accountProfleField
				+ ", formElementPid=" + formElementPid + ", formElementName=" + formElementName + "]";
	}

}
