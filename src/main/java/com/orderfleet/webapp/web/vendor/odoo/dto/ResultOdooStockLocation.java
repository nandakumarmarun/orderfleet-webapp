package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class ResultOdooStockLocation {

	private long status;

	private String message;

	private List<OdooStockLocation> response;

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

	public List<OdooStockLocation> getResponse() {
		return response;
	}

	public void setResponse(List<OdooStockLocation> response) {
		this.response = response;
	}

}
