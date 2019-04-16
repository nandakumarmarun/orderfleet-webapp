package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Sarath
 * @since Sep 14, 2017
 *
 */
public class DynamicDocumentReportViewDTO {

	private List<String> elementNameToShow;

	private List<Map<Integer, DynamicDocumentReportSettingsViewDTO>> elementValues;

	public DynamicDocumentReportViewDTO() {
		super();
	}

	public List<String> getElementNameToShow() {
		return elementNameToShow;
	}

	public void setElementNameToShow(List<String> elementNameToShow) {
		this.elementNameToShow = elementNameToShow;
	}

	public List<Map<Integer, DynamicDocumentReportSettingsViewDTO>> getElementValues() {
		return elementValues;
	}

	public void setElementValues(List<Map<Integer, DynamicDocumentReportSettingsViewDTO>> elementValues) {
		this.elementValues = elementValues;
	}

}
