package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

public class ResultOdooTaxList {
	private long status;

	private String message;

	private List<OdooTaxList> response;

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<OdooTaxList> getResponse() {
		return response;
	}

	public void setResponse(List<OdooTaxList> response) {
		this.response = response;
	}
}
