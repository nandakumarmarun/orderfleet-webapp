package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.AccountingVoucherAllocation;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;

public class ReceiptVendorDTO {

	private String receiptDate;
	private String customerId;
	private double amount;
	private String accountingVoucherHeaderPid;

	public ReceiptVendorDTO() {
		super();
	}

	public ReceiptVendorDTO(String receiptDate, String customerId, double amount, String accountingVoucherHeaderPid) {
		super();
		this.receiptDate = receiptDate;
		this.customerId = customerId;
		this.amount = amount;
		this.accountingVoucherHeaderPid = accountingVoucherHeaderPid;
	}

	public ReceiptVendorDTO(AccountingVoucherDetail accountingVoucherDetail) {

		this.amount = accountingVoucherDetail.getAmount();
		this.receiptDate = accountingVoucherDetail.getVoucherDate().toString();
		this.customerId = accountingVoucherDetail.getAccountingVoucherHeader().getAccountProfile().getAlias();
		this.accountingVoucherHeaderPid = accountingVoucherDetail.getAccountingVoucherHeader().getPid();
	}

	public ReceiptVendorDTO(AccountingVoucherAllocation accountingVoucherAllocation) {
		this.amount = accountingVoucherAllocation.getAmount();
		this.receiptDate = accountingVoucherAllocation.getAccountingVoucherDetail().getVoucherDate().toString();
		this.customerId = accountingVoucherAllocation.getAccountingVoucherDetail().getAccountingVoucherHeader().getAccountProfile().getAlias();
		this.accountingVoucherHeaderPid = accountingVoucherAllocation.getAccountingVoucherDetail().getAccountingVoucherHeader().getPid();
	}

	
	public String getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getAccountingVoucherHeaderPid() {
		return accountingVoucherHeaderPid;
	}

	public void setAccountingVoucherHeaderPid(String accountingVoucherHeaderPid) {
		this.accountingVoucherHeaderPid = accountingVoucherHeaderPid;
	}
	
}
