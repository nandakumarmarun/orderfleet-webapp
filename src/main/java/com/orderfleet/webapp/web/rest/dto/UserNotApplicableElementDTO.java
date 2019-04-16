package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.UserNotApplicableElement;

/**
 * A DTO for the UserNotApplicableElement entity.
 * 
 * @author Muhammed Riyas T
 * @since Jan 18, 2017
 */
public class UserNotApplicableElementDTO {

	private String userPid;

	private String formElementPid;
	private String formElementName;

	private String formPid;
	private String formName;

	private String documentPid;
	private String documentName;

	public UserNotApplicableElementDTO() {
		super();
	}

	public UserNotApplicableElementDTO(UserNotApplicableElement userNotApplicableElement) {
		super();
		this.userPid = userNotApplicableElement.getUser().getPid();
		this.formElementPid = userNotApplicableElement.getFormElement().getPid();
		this.formElementName = userNotApplicableElement.getFormElement().getName();
		this.formPid = userNotApplicableElement.getForm().getPid();
		this.formName = userNotApplicableElement.getForm().getName();
		this.documentPid = userNotApplicableElement.getDocument().getPid();
		this.documentName = userNotApplicableElement.getDocument().getName();
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
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

}
