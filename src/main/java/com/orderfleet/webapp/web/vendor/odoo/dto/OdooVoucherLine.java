package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class OdooVoucherLine {

	private double amount;

	private long voucher_id;

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public long getVoucher_id() {
		return voucher_id;
	}

	public void setVoucher_id(long voucher_id) {
		this.voucher_id = voucher_id;
	}

}
