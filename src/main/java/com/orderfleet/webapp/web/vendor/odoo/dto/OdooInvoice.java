package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class OdooInvoice {

	private long location_id;

	private long partner_id;

	private String invoice_date;

	private String reference;

	private double rounding_amt;

	private String journal_type;

	private String type;

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

	public double getRounding_amt() {
		return rounding_amt;
	}

	public void setRounding_amt(double rounding_amt) {
		this.rounding_amt = rounding_amt;
	}

	public String getJournal_type() {
		return journal_type;
	}

	public void setJournal_type(String journal_type) {
		this.journal_type = journal_type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
