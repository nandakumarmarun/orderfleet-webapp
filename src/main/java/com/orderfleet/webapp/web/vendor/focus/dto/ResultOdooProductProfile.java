package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

public class ResultOdooProductProfile {

	private long status;

	private String message;

	private List<OdooProductProfile> response;

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

	public List<OdooProductProfile> getResponse() {
		return response;
	}

	public void setResponse(List<OdooProductProfile> response) {
		this.response = response;
	}

}
