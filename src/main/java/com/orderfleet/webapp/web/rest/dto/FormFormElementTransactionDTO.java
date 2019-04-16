package com.orderfleet.webapp.web.rest.dto;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.orderfleet.webapp.domain.FormFormElement;

public class FormFormElementTransactionDTO {

	private String formElementPid;

	private String formElementName;

	private String formElementTypeName;

	private Set<FormElementValueDTO> formElementValues;

	private int sortOrder;

	private int reportOrder;

	private boolean editable;

	private boolean validationEnabled;
	
	private boolean visibility;

	public FormFormElementTransactionDTO() {
	}

	public FormFormElementTransactionDTO(FormFormElement formFormElement) {
		this.formElementPid = formFormElement.getFormElement().getPid();
		this.formElementName = formFormElement.getFormElement().getName();
		this.formElementTypeName = formFormElement.getFormElement().getFormElementType().getName();
		this.sortOrder = formFormElement.getSortOrder();
		this.reportOrder = formFormElement.getReportOrder();
		this.editable = formFormElement.getEditable();
		this.validationEnabled = formFormElement.getValidationEnabled();
		this.visibility = formFormElement.getVisibility();
		
		this.formElementValues = formFormElement.getFormElement().getFormElementValues().stream()
				.map(FormElementValueDTO::new).collect(Collectors.toCollection(LinkedHashSet::new));
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

	public String getFormElementTypeName() {
		return formElementTypeName;
	}

	public void setFormElementTypeName(String formElementTypeName) {
		this.formElementTypeName = formElementTypeName;
	}

	public Set<FormElementValueDTO> getFormElementValues() {
		return formElementValues;
	}

	public void setFormElementValues(Set<FormElementValueDTO> formElementValues) {
		this.formElementValues = formElementValues;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public int getReportOrder() {
		return reportOrder;
	}

	public void setReportOrder(int reportOrder) {
		this.reportOrder = reportOrder;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isValidationEnabled() {
		return validationEnabled;
	}

	public void setValidationEnabled(boolean validationEnabled) {
		this.validationEnabled = validationEnabled;
	}

	public boolean getVisibility() {
		return visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}
	
	

}
