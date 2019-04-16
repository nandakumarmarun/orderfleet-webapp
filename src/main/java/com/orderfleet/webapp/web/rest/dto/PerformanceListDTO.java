package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class PerformanceListDTO {

	private List<PerformanceDTO> listPerformanceReport;
	private List<String> monthList;

	public List<PerformanceDTO> getListPerformanceReport() {
		return listPerformanceReport;
	}

	public void setListPerformanceReport(List<PerformanceDTO> listPerformanceReport) {
		this.listPerformanceReport = listPerformanceReport;
	}

	public List<String> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<String> monthList) {
		this.monthList = monthList;
	}

}
