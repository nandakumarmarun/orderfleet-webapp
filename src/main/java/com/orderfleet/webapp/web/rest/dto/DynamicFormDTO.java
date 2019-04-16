package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Map;

public class DynamicFormDTO {

	private List<String> elementNameToShow;

	private List<Map<Integer, String>> elementValues;

	private List<FormDTO> documentForms;

	public List<String> getElementNameToShow() {
		return elementNameToShow;
	}

	public void setElementNameToShow(List<String> elementNameToShow) {
		this.elementNameToShow = elementNameToShow;
	}

	public List<Map<Integer, String>> getElementValues() {
		return elementValues;
	}

	public void setElementValues(List<Map<Integer, String>> elementValues) {
		this.elementValues = elementValues;
	}

	public List<FormDTO> getDocumentForms() {
		return documentForms;
	}

	public void setDocumentForms(List<FormDTO> documentForms) {
		this.documentForms = documentForms;
	}

}
