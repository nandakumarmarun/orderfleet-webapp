package com.orderfleet.webapp.web.rest.dto;

import java.util.List;


/**
 * a DynamicDocumentSettingsColumns for web view.
 *
 * @author Sarath
 * @since Sep 6, 2017
 *
 */
public class DynamicDocumentSettingsListDTO {

	private List<FormElementDTO> allDynamicDocumentSettingsColumns;
	private List<DynamicDocumentSettingsColumnsDTO> trueDynamicDocumentSettingsColumns;
	private List<DynamicDocumentSettingsRowColourDTO> truedynamicDocumentSettingsRowColourDTOs;

	public DynamicDocumentSettingsListDTO() {
		super();
	}

	public DynamicDocumentSettingsListDTO(List<FormElementDTO> allDynamicDocumentSettingsColumns,
			List<DynamicDocumentSettingsColumnsDTO> trueDynamicDocumentSettingsColumns,
			List<DynamicDocumentSettingsRowColourDTO> truedynamicDocumentSettingsRowColourDTOs) {
		super();
		this.allDynamicDocumentSettingsColumns = allDynamicDocumentSettingsColumns;
		this.trueDynamicDocumentSettingsColumns = trueDynamicDocumentSettingsColumns;
		this.truedynamicDocumentSettingsRowColourDTOs = truedynamicDocumentSettingsRowColourDTOs;
	}

	public List<FormElementDTO> getAllDynamicDocumentSettingsColumns() {
		return allDynamicDocumentSettingsColumns;
	}

	public void setAllDynamicDocumentSettingsColumns(List<FormElementDTO> allDynamicDocumentSettingsColumns) {
		this.allDynamicDocumentSettingsColumns = allDynamicDocumentSettingsColumns;
	}

	public List<DynamicDocumentSettingsColumnsDTO> getTrueDynamicDocumentSettingsColumns() {
		return trueDynamicDocumentSettingsColumns;
	}

	public void setTrueDynamicDocumentSettingsColumns(
			List<DynamicDocumentSettingsColumnsDTO> trueDynamicDocumentSettingsColumns) {
		this.trueDynamicDocumentSettingsColumns = trueDynamicDocumentSettingsColumns;
	}

	public List<DynamicDocumentSettingsRowColourDTO> getTruedynamicDocumentSettingsRowColourDTOs() {
		return truedynamicDocumentSettingsRowColourDTOs;
	}

	public void setTruedynamicDocumentSettingsRowColourDTOs(
			List<DynamicDocumentSettingsRowColourDTO> truedynamicDocumentSettingsRowColourDTOs) {
		this.truedynamicDocumentSettingsRowColourDTOs = truedynamicDocumentSettingsRowColourDTOs;
	}

	@Override
	public String toString() {
		return "DynamicDocumentSettingsListDTO [allDynamicDocumentSettingsColumns=" + allDynamicDocumentSettingsColumns
				+ ", trueDynamicDocumentSettingsColumns=" + trueDynamicDocumentSettingsColumns
				+ ", truedynamicDocumentSettingsRowColourDTOs=" + truedynamicDocumentSettingsRowColourDTOs + "]";
	}

}
