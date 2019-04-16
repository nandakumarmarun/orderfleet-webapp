package com.orderfleet.webapp.web.rest.dto;

import java.util.Comparator;

import com.orderfleet.webapp.domain.DynamicDocumentSettingsColumns;

/**
 * A DTO For DynamicDocumentSettingsColumns.
 *
 * @author Sarath
 * @since Aug 29, 2017
 *
 */
public class DynamicDocumentSettingsColumnsDTO implements Comparator<DynamicDocumentSettingsColumnsDTO> {

	private String dynamicDocumentSettingsHeaderPid;

	private String formElementPid;

	private String formElementName;

	private Integer sortOrder = 0;

	public DynamicDocumentSettingsColumnsDTO() {
		super();
	}

	public DynamicDocumentSettingsColumnsDTO(DynamicDocumentSettingsColumns dynamicDocumentSettingsColumns) {
		super();
		this.dynamicDocumentSettingsHeaderPid = dynamicDocumentSettingsColumns.getDynamicDocumentSettingsHeader()
				.getPid();

		this.formElementPid = dynamicDocumentSettingsColumns.getFormElement().getPid();
		this.formElementName = dynamicDocumentSettingsColumns.getFormElement().getName();
		this.sortOrder = dynamicDocumentSettingsColumns.getSortOrder();
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

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public String toString() {
		return "DynamicDocumentSettingsColumnsDTO [dynamicDocumentSettingsHeaderPid=" + dynamicDocumentSettingsHeaderPid
				+ ", formElementPid=" + formElementPid + ", formElementName=" + formElementName + ", sortOrder="
				+ sortOrder + "]";
	}

	@Override
	public int compare(DynamicDocumentSettingsColumnsDTO o1, DynamicDocumentSettingsColumnsDTO o2) {
		return o1.getSortOrder().compareTo(o2.getSortOrder());
	}

}
