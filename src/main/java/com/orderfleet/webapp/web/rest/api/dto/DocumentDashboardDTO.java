package com.orderfleet.webapp.web.rest.api.dto;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.DocumentDTO;

public class DocumentDashboardDTO {

	DocumentDTO document;
	int dayCount;
	int weekCount;
	int monthCount;
	List<FormFormElementDTO> formFormElementDTOs;

	public DocumentDTO getDocument() {
		return document;
	}

	public void setDocument(DocumentDTO document) {
		this.document = document;
	}

	public int getDayCount() {
		return dayCount;
	}

	public void setDayCount(int dayCount) {
		this.dayCount = dayCount;
	}

	public int getWeekCount() {
		return weekCount;
	}

	public void setWeekCount(int weekCount) {
		this.weekCount = weekCount;
	}

	public int getMonthCount() {
		return monthCount;
	}

	public void setMonthCount(int monthCount) {
		this.monthCount = monthCount;
	}

	public List<FormFormElementDTO> getFormFormElementDTOs() {
		return formFormElementDTOs;
	}

	public void setFormFormElementDTOs(List<FormFormElementDTO> formFormElementDTOs) {
		this.formFormElementDTOs = formFormElementDTOs;
	}

}
