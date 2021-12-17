package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

public class ResultOdoo {

	private long status;

	private ResponseMessageOdoo message;

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public ResponseMessageOdoo getMessage() {
		return message;
	}

	public void setMessage(ResponseMessageOdoo message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ResultOdoo [status=" + status + ", message=" + message + "]";
	}

}
