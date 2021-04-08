package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class ParamsReceiptOdoo {
	private boolean create;

	private String payment_type;

	private long partner_id;

	private double paid_amount;

	private String transaction_type;

	private long location_id;

	private long salesman_id;

	private String reference;

	private String cheque_number;

	private String cheque_date;

	private boolean pdc_state;

	private List<OdooVoucherLine> voucher_lines;

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	public String getPayment_type() {
		return payment_type;
	}

	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}

	public long getPartner_id() {
		return partner_id;
	}

	public void setPartner_id(long partner_id) {
		this.partner_id = partner_id;
	}

	public double getPaid_amount() {
		return paid_amount;
	}

	public void setPaid_amount(double paid_amount) {
		this.paid_amount = paid_amount;
	}

	public String getTransaction_type() {
		return transaction_type;
	}

	public void setTransaction_type(String transaction_type) {
		this.transaction_type = transaction_type;
	}

	public long getLocation_id() {
		return location_id;
	}

	public void setLocation_id(long location_id) {
		this.location_id = location_id;
	}

	public long getSalesman_id() {
		return salesman_id;
	}

	public void setSalesman_id(long salesman_id) {
		this.salesman_id = salesman_id;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public List<OdooVoucherLine> getVoucher_lines() {
		return voucher_lines;
	}

	public void setVoucher_lines(List<OdooVoucherLine> voucher_lines) {
		this.voucher_lines = voucher_lines;
	}

	public String getCheque_number() {
		return cheque_number;
	}

	public void setCheque_number(String cheque_number) {
		this.cheque_number = cheque_number;
	}

	public String getCheque_date() {
		return cheque_date;
	}

	public void setCheque_date(String cheque_date) {
		this.cheque_date = cheque_date;
	}

	public boolean isPdc_state() {
		return pdc_state;
	}

	public void setPdc_state(boolean pdc_state) {
		this.pdc_state = pdc_state;
	}

	@Override
	public String toString() {
		return "ParamsReceiptOdoo [create=" + create + ", payment_type=" + payment_type + ", partner_id=" + partner_id
				+ ", paid_amount=" + paid_amount + ", transaction_type=" + transaction_type + ", location_id="
				+ location_id + ", salesman_id=" + salesman_id + ", reference=" + reference + ", cheque_number="
				+ cheque_number + ", cheque_date=" + cheque_date + ", pdc_state=" + pdc_state + ", voucher_lines="
				+ voucher_lines + "]";
	}

}
