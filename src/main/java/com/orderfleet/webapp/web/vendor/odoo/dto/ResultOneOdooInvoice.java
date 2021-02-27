package com.orderfleet.webapp.web.vendor.odoo.dto;

public class ResultOneOdooInvoice {
	private long status;
	
	private ResponseMessageOneOdooInvoice message;
	
	private long id;

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public ResponseMessageOneOdooInvoice getMessage() {
		return message;
	}

	public void setMessage(ResponseMessageOneOdooInvoice message) {
		this.message = message;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ResultOneOdooInvoice [status=" + status + ", message=" + message + ", id=" + id + "]";
	}
}
