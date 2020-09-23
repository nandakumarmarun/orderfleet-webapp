package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class ResultOdooAccountProfile {

	private long status;

	private String message;

	private List<OdooAccountProfile> response;

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

	public List<OdooAccountProfile> getResponse() {
		return response;
	}

	public void setResponse(List<OdooAccountProfile> response) {
		this.response = response;
	}

}
