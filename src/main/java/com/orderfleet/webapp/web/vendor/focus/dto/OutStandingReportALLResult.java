package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Count","OutSReport", "iStatus", "sMessage" })
public class OutStandingReportALLResult {

	@JsonProperty("OutSReport")
	private List<OutStandingFocus> OutStandingFocus;

	@JsonProperty("Count")
	private long count;

	@JsonProperty("iStatus")
	private long isStatus;

	@JsonProperty("sMessage")
	private String sMessage;

	@JsonProperty("OutSReport")
	public List<OutStandingFocus> getOutStandingFocus() {
		return OutStandingFocus;
	}

	@JsonProperty("OutSReport")
	public void setOutStandingFocus(List<OutStandingFocus> outStandingFocus) {
		OutStandingFocus = outStandingFocus;
	}

	@JsonProperty("Count")
	public long getCount() {
		return count;
	}

	@JsonProperty("Count")
	public void setCount(long count) {
		this.count = count;
	}

	@JsonProperty("iStatus")
	public long getIsStatus() {
		return isStatus;
	}

	@JsonProperty("iStatus")
	public void setIsStatus(long isStatus) {
		this.isStatus = isStatus;
	}

	@JsonProperty("sMessage")
	public String getsMessage() {
		return sMessage;
	}

	@JsonProperty("sMessage")
	public void setsMessage(String sMessage) {
		this.sMessage = sMessage;
	}

}
