package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class ResultOdooPriceLevel {

	private long status;

	private String message;

	private List<OdooPriceLevel> response;

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

	public List<OdooPriceLevel> getResponse() {
		return response;
	}

	public void setResponse(List<OdooPriceLevel> response) {
		this.response = response;
	}

}
