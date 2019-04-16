package com.orderfleet.webapp.service;

import com.orderfleet.webapp.web.rest.dto.FinancialClosingReportHolder;

public interface FinancialClosingHeaderService {

	void processFincancialClosing(FinancialClosingReportHolder fClosingReportHolder);

}
