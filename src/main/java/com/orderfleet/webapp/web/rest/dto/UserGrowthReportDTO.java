package com.orderfleet.webapp.web.rest.dto;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserGrowthReportDTO {
	
	private List<String> htmlTblHeadermonths = new ArrayList<>();

	private Map<String, Map<YearMonth, Long>> companyMonthWiseCount = new LinkedHashMap<>();
	
	public List<String> getHtmlTblHeadermonths() {
		return htmlTblHeadermonths;
	}

	public void setHtmlTblHeadermonths(List<String> htmlTblHeadermonths) {
		this.htmlTblHeadermonths = htmlTblHeadermonths;
	}

	public Map<String, Map<YearMonth, Long>> getCompanyMonthWiseCount() {
		return companyMonthWiseCount;
	}

	public void setCompanyMonthWiseCount(Map<String, Map<YearMonth, Long>> companyMonthWiseCount) {
		this.companyMonthWiseCount = companyMonthWiseCount;
	}
	
}
