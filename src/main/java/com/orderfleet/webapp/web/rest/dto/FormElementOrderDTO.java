package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.FormFormElement;

/**
 * A FormElementOrder DTO.
 * 
 * @author Muhammed Riyas T
 * @since July 22, 2016
 */
public class FormElementOrderDTO {

	private String formElementPid;

	private int sortOrder;

	private int reportOrder;

	private boolean editable;

	private boolean validationEnabled;
	
	private boolean mandatory;
	
	private boolean visibility;

	public FormElementOrderDTO() {
	}

	public FormElementOrderDTO(FormFormElement formFormElement) {
		this(formFormElement.getFormElement().getPid(), formFormElement.getSortOrder(), formFormElement
				.getReportOrder(), formFormElement.getEditable(), formFormElement.getValidationEnabled(),formFormElement.getMandatory(),formFormElement.getVisibility());
	}

	public FormElementOrderDTO(String formElementPid, int sortOrder, int reportOrder, boolean editable,
			boolean validationEnabled,boolean mandatory, boolean visibility) {
		super();
		this.formElementPid = formElementPid;
		this.sortOrder = sortOrder;
		this.reportOrder = reportOrder;
		this.editable = editable;
		this.validationEnabled = validationEnabled;
		this.mandatory=mandatory;
		this.visibility=visibility;
	}

	public String getFormElementPid() {
		return formElementPid;
	}

	public void setFormElementPid(String formElementPid) {
		this.formElementPid = formElementPid;
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

	public int getReportOrder() {
		return reportOrder;
	}

	public void setReportOrder(int reportOrder) {
		this.reportOrder = reportOrder;
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

	@Override
	public String toString() {
		return "FormElementOrder{" + ", sortOrder='" + sortOrder + "'" + ", formElementPid='" + formElementPid + "'"
				+ '}';
	}

}
