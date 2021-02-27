package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class ParamsOneOdooInvoice {
	private boolean create;
	
	private long location_id;
	
	private String invoice_type;
	
	private String journal_type;
	
	private double rounding_amt;
	
	private long partner_id;
	
	private String invoice_date;
	
	private String reference;
	
	private String origin;
	
	private List<OdooInvoiceLine> invoice_lines;

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	public long getLocation_id() {
		return location_id;
	}

	public void setLocation_id(long location_id) {
		this.location_id = location_id;
	}

	public String getInvoice_type() {
		return invoice_type;
	}

	public void setInvoice_type(String invoice_type) {
		this.invoice_type = invoice_type;
	}

	public String getJournal_type() {
		return journal_type;
	}

	public void setJournal_type(String journal_type) {
		this.journal_type = journal_type;
	}

	public double getRounding_amt() {
		return rounding_amt;
	}

	public void setRounding_amt(double rounding_amt) {
		this.rounding_amt = rounding_amt;
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

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public List<OdooInvoiceLine> getInvoice_lines() {
		return invoice_lines;
	}

	public void setInvoice_lines(List<OdooInvoiceLine> invoice_lines) {
		this.invoice_lines = invoice_lines;
	}

	@Override
	public String toString() {
		return "ParamsOneOdooInvoice [create=" + create + ", location_id=" + location_id + ", invoice_type="
				+ invoice_type + ", journal_type=" + journal_type + ", rounding_amt=" + rounding_amt + ", partner_id="
				+ partner_id + ", invoice_date=" + invoice_date + ", reference=" + reference + ", origin=" + origin
				+ ", invoice_lines=" + invoice_lines + "]";
	}
	
	
}
