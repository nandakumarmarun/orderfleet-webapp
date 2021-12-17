package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

public class ResultOdooReceipt {

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
		return "ResultOdooInvoice [status=" + status + ", message=" + message + "]";
	}

}
