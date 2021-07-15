package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class ResultOdoo {

	private long status;

	private long message;

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public long getMessage() {
		return message;
	}

	public void setMessage(long message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ResultOdoo [status=" + status + ", message=" + message + "]";
	}

}
