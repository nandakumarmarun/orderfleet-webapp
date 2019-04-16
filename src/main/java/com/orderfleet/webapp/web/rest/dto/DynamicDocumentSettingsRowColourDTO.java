package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.DynamicDocumentSettingsRowColour;

/**
 * A DTO For DynamicDocumentSettingsRowColour.
 *
 * @author Sarath
 * @since Aug 29, 2017
 *
 */
public class DynamicDocumentSettingsRowColourDTO {

	private String dynamicDocumentSettingsHeaderPid;

	private String formElementPid;

	private String formElementName;

	private Long formElementValueId;

	private String formElementValueName;

	private String colour;

	public DynamicDocumentSettingsRowColourDTO() {
		super();
	}

	public DynamicDocumentSettingsRowColourDTO(DynamicDocumentSettingsRowColour documentSettingsRowColour) {
		super();
		this.dynamicDocumentSettingsHeaderPid = documentSettingsRowColour.getDynamicDocumentSettingsHeader().getPid();
		this.formElementPid = documentSettingsRowColour.getFormElement().getPid();
		this.formElementName = documentSettingsRowColour.getFormElement().getName();
		this.colour = documentSettingsRowColour.getColour();
		this.formElementValueId = documentSettingsRowColour.getFormElementValue().getId();
		this.formElementValueName = documentSettingsRowColour.getFormElementValue().getName();
	}

	public String getDynamicDocumentSettingsHeaderPid() {
		return dynamicDocumentSettingsHeaderPid;
	}

	public void setDynamicDocumentSettingsHeaderPid(String dynamicDocumentSettingsHeaderPid) {
		this.dynamicDocumentSettingsHeaderPid = dynamicDocumentSettingsHeaderPid;
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

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}

	public Long getFormElementValueId() {
		return formElementValueId;
	}

	public void setFormElementValueId(Long formElementValueId) {
		this.formElementValueId = formElementValueId;
	}

	public String getFormElementValueName() {
		return formElementValueName;
	}

	public void setFormElementValueName(String formElementValueName) {
		this.formElementValueName = formElementValueName;
	}

	@Override
	public String toString() {
		return "DynamicDocumentSettingsRowColourDTO [dynamicDocumentSettingsColumnsDTO="
				+ dynamicDocumentSettingsHeaderPid + ", dynamicDocumentSettingsHeaderDTO=" + formElementPid
				+ ", formElementName=" + formElementName + ", colour=" + colour + "]";
	}

}
