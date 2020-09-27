package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class ResultOdooUnitOfMeasure {

	private long status;

	private String message;

	private List<OdooUnitOfMeasure> response;

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

	public List<OdooUnitOfMeasure> getResponse() {
		return response;
	}

	public void setResponse(List<OdooUnitOfMeasure> response) {
		this.response = response;
	}

}
