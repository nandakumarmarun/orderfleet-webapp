package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class ResultOdooInvoice {

	private long status;

	private List<ResponseMessageOdooInvoice> message;

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public List<ResponseMessageOdooInvoice> getMessage() {
		return message;
	}

	public void setMessage(List<ResponseMessageOdooInvoice> message) {
		this.message = message;
	}

}
