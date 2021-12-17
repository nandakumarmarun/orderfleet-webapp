package com.orderfleet.webapp.web.vendor.focus.dto;

public class ResponseMessageOdoo {

	private String id;

	private String reference;

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

	@Override
	public String toString() {
		return "ResponseMessageOdooInvoice [id=" + id + ", reference=" + reference + "]";
	}

}
