package com.orderfleet.webapp.web.vendor.odoo.dto;

public class OdooAccountProfileCreditTerms {

	private long division_id;

	private String division_name;

	private long credit_limit;

	private String payment_term;

	private long credit;

	private long payment_term_days;

	public long getDivision_id() {
		return division_id;
	}

	public void setDivision_id(long division_id) {
		this.division_id = division_id;
	}

	public String getDivision_name() {
		return division_name;
	}

	public void setDivision_name(String division_name) {
		this.division_name = division_name;
	}

	public long getCredit_limit() {
		return credit_limit;
	}

	public void setCredit_limit(long credit_limit) {
		this.credit_limit = credit_limit;
	}

	public String getPayment_term() {
		return payment_term;
	}

	public void setPayment_term(String payment_term) {
		this.payment_term = payment_term;
	}

	public long getCredit() {
		return credit;
	}

	public void setCredit(long credit) {
		this.credit = credit;
	}

	public long getPayment_term_days() {
		return payment_term_days;
	}

	public void setPayment_term_days(long payment_term_days) {
		this.payment_term_days = payment_term_days;
	}

}
