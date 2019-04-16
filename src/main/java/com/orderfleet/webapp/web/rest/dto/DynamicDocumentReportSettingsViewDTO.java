package com.orderfleet.webapp.web.rest.dto;

/**
 * A DTO for the DynamicDocument entity.
 *
 * @author Sarath
 * @since Sep 11, 2017
 *
 */
public class DynamicDocumentReportSettingsViewDTO {

	private String formElementPid;

	private String formElementName;
	
	private String value;

	private String colour;

	private int sortOrder;
	
	public DynamicDocumentReportSettingsViewDTO() {
		super();
	}


	public DynamicDocumentReportSettingsViewDTO(String formElementPid, String formElementName, String value, String colour,int sortOrder) {
		super();
		this.formElementPid = formElementPid;
		this.formElementName = formElementName;
		this.value = value;
		this.colour = colour;
		this.sortOrder = sortOrder;
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


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getColour() {
		return colour;
	}


	public void setColour(String colour) {
		this.colour = colour;
	}


	public int getSortOrder() {
		return sortOrder;
	}


	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}


	@Override
	public String toString() {
		return "DynamicDocumentReportViewDTO [formElementPid=" + formElementPid + ", formElementName=" + formElementName
				+ ", value=" + value + ", colour=" + colour + ", sortOrder=" + sortOrder + "]";
	}

	

	

}
