package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "OutStandingReportALLResult" })
public class OutStandingResponseFocus {

	@JsonProperty("OutStandingReportALLResult")
	private OutStandingReportALLResult outStandingReportALLResult;

	@JsonProperty("OutStandingReportALLResult")
	public OutStandingReportALLResult getOutStandingReportALLResult() {
		return outStandingReportALLResult;
	}

	@JsonProperty("OutStandingReportALLResult")
	public void setOutStandingReportALLResult(OutStandingReportALLResult outStandingReportALLResult) {
		this.outStandingReportALLResult = outStandingReportALLResult;
	}
}
