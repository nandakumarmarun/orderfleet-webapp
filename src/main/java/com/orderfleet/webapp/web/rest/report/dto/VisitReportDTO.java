package com.orderfleet.webapp.web.rest.report.dto;

import java.util.ArrayList;
import java.util.List;

//URL : http://localhost/web/visits-report
public class VisitReportDTO {
	
	private List<String> reportHeaders = new ArrayList<>();
	
	private List<List<String>> reportValues;

	public VisitReportDTO(List<String> reportHeaders, List<List<String>> reportValues) {
		super();
		this.reportHeaders = reportHeaders;
		this.reportValues = reportValues;
	}

	public List<String> getReportHeaders() {
		return reportHeaders;
	}

	public void setReportHeaders(List<String> reportHeaders) {
		this.reportHeaders = reportHeaders;
	}

	public List<List<String>> getReportValues() {
		return reportValues;
	}

	public void setReportValues(List<List<String>> reportValues) {
		this.reportValues = reportValues;
	}

	
}
