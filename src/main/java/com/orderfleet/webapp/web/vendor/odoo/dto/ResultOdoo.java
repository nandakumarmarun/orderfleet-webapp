package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class ResultOdoo {

	private long status;

	private String message;

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

	@Override
	public String toString() {
		return "ResultOdoo [status=" + status + ", message=" + message + "]";
	}

}
