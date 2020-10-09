package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class OdooInvoice {

	private long location_id;

	private long partner_id;

	private String invoice_date;

	private String reference;

	private List<OdooInvoiceLine> invoice_lines;

	public long getLocation_id() {
		return location_id;
	}

	public void setLocation_id(long location_id) {
		this.location_id = location_id;
	}

	public long getPartner_id() {
		return partner_id;
	}

	public void setPartner_id(long partner_id) {
		this.partner_id = partner_id;
	}

	public String getInvoice_date() {
		return invoice_date;
	}

	public void setInvoice_date(String invoice_date) {
		this.invoice_date = invoice_date;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public List<OdooInvoiceLine> getInvoice_lines() {
		return invoice_lines;
	}

	public void setInvoice_lines(List<OdooInvoiceLine> invoice_lines) {
		this.invoice_lines = invoice_lines;
	}

}
