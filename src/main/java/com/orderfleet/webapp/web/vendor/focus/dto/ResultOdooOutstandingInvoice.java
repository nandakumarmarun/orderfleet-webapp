package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

public class ResultOdooOutstandingInvoice {

	private long status;

	private String message;

	private List<OdooOutstandingInvoice> response;

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

	public List<OdooOutstandingInvoice> getResponse() {
		return response;
	}

	public void setResponse(List<OdooOutstandingInvoice> response) {
		this.response = response;
	}
}
