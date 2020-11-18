package com.orderfleet.webapp.web.vendor.odoo.dto;

public class ResponseMessageOdooInvoice {

	private String id;

	private String reference;

	private int status_code;

	private String status_message;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public int getStatus_code() {
		return status_code;
	}

	public void setStatus_code(int status_code) {
		this.status_code = status_code;
	}

	public String getStatus_message() {
		return status_message;
	}

	public void setStatus_message(String status_message) {
		this.status_message = status_message;
	}

	@Override
	public String toString() {
		return "ResponseMessageOdooInvoice [id=" + id + ", reference=" + reference + ", status_code=" + status_code
				+ ", status_message=" + status_message + "]";
	}
	
	

}
