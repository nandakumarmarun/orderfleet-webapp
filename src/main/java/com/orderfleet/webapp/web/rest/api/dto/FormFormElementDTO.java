package com.orderfleet.webapp.web.rest.api.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.FormFormElement;

/**
 * A DTO FormFormElement
 * 
 * @author Muhammed Riyas T
 * @since July 25, 2016
 */
public class FormFormElementDTO {

	private String formPid;

	private String formName;

	private String formElementPid;

	private String formElementName;

	private int sortOrder;

	private boolean editable;

	private boolean validationEnabled;

	private boolean mandatory;
	
	private boolean visibility;

	private LocalDateTime lastModifiedDate;

	public FormFormElementDTO() {
	}

	public FormFormElementDTO(FormFormElement formFormElement) {
		super();
		this.formPid = formFormElement.getForm().getPid();
		this.formName = formFormElement.getForm().getName();
		this.formElementPid = formFormElement.getFormElement().getPid();
		this.formElementName = formFormElement.getFormElement().getName();
		this.sortOrder = formFormElement.getSortOrder();
		this.editable = formFormElement.getEditable();
		this.validationEnabled = formFormElement.getValidationEnabled();
		this.lastModifiedDate = formFormElement.getLastModifiedDate();
		this.mandatory = formFormElement.getMandatory();
		this.visibility=formFormElement.getVisibility();
		
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

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public boolean getEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean getValidationEnabled() {
		return validationEnabled;
	}

	public void setValidationEnabled(boolean validationEnabled) {
		this.validationEnabled = validationEnabled;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public boolean getMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean getVisibility() {
		return visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

}
