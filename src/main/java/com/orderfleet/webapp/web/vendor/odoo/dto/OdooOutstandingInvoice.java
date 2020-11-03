package com.orderfleet.webapp.web.vendor.odoo.dto;

public class OdooOutstandingInvoice {
	
	private String date_due;
	private String date_original;
	private long id;
	private double amount_original;
	private String invoice_ref;
	private String customer_id;
	private double amount_unreconciled;
	
	public String getDate_due() {
		return date_due;
	}
	public void setDate_due(String date_due) {
		this.date_due = date_due;
	}
	public String getDate_original() {
		return date_original;
	}
	public void setDate_original(String date_original) {
		this.date_original = date_original;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public double getAmount_original() {
		return amount_original;
	}
	public void setAmount_original(double amount_original) {
		this.amount_original = amount_original;
	}
	public String getInvoice_ref() {
		return invoice_ref;
	}
	public void setInvoice_ref(String invoice_ref) {
		this.invoice_ref = invoice_ref;
	}
	public String getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}
	public double getAmount_unreconciled() {
		return amount_unreconciled;
	}
	public void setAmount_unreconciled(double amount_unreconciled) {
		this.amount_unreconciled = amount_unreconciled;
	}
	
	
}
