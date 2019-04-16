package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.AccountingVoucherDetail;

/**
 * A DTO For Generate Accounting Voucher (Receipt) To Client APP.
 *
 * @author Sarath
 * @since Oct 29, 2016
 */
public class ReceiptDetailsDTO {

	private String particularsName;
	private double amount;
	private String reference;
	private String date;
	private String chequeNo;
	private String bankName;
	private String remoteId;
	private String ledgerName;
	private String narrationMessage;

	private boolean receiptEnabled;
	private boolean isDailyReceipt;
	private boolean isOptional;
	private boolean isReference;

	public ReceiptDetailsDTO() {
		super();
	}

	public ReceiptDetailsDTO(AccountingVoucherDetail accountingVoucherDetail) {
		super();
		this.particularsName = accountingVoucherDetail.getBy().getName();
		this.amount = accountingVoucherDetail.getAmount();
		this.reference = accountingVoucherDetail.getReferenceNumber();
		this.date = accountingVoucherDetail.getVoucherDate().toString();
		this.chequeNo = accountingVoucherDetail.getInstrumentNumber();
		this.bankName = accountingVoucherDetail.getBankName();
		this.ledgerName = accountingVoucherDetail.getBy().getName();
		this.narrationMessage = accountingVoucherDetail.getRemarks();
	}

	public ReceiptDetailsDTO(String particularsName, double amount, String reference, String date, String chequeNo,
			String bankName, String remoteId, String ledgerName, String narrationMessage, boolean receiptEnabled,
			boolean isDailyReceipt, boolean isOptional, boolean isReference) {
		super();
		this.particularsName = particularsName;
		this.amount = amount;
		this.reference = reference;
		this.date = date;
		this.chequeNo = chequeNo;
		this.bankName = bankName;
		this.remoteId = remoteId;
		this.ledgerName = ledgerName;
		this.narrationMessage = narrationMessage;
		this.receiptEnabled = receiptEnabled;
		this.isDailyReceipt = isDailyReceipt;
		this.isOptional = isOptional;
		this.isReference = isReference;
	}

	public String getParticularsName() {
		return particularsName;
	}

	public void setParticularsName(String particularsName) {
		this.particularsName = particularsName;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(String remoteId) {
		this.remoteId = remoteId;
	}

	public String getLedgerName() {
		return ledgerName;
	}

	public void setLedgerName(String ledgerName) {
		this.ledgerName = ledgerName;
	}

	public String getNarrationMessage() {
		return narrationMessage;
	}

	public void setNarrationMessage(String narrationMessage) {
		this.narrationMessage = narrationMessage;
	}

	public boolean getIsReceiptEnabled() {
		return receiptEnabled;
	}

	public void setIsReceiptEnabled(boolean receiptEnabled) {
		this.receiptEnabled = receiptEnabled;
	}

	public boolean getIsDailyReceipt() {
		return isDailyReceipt;
	}

	public void setIsDailyReceipt(boolean isDailyReceipt) {
		this.isDailyReceipt = isDailyReceipt;
	}

	public boolean getIsOptional() {
		return isOptional;
	}

	public void setIsOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	public boolean getIsReference() {
		return isReference;
	}

	public void setIsReference(boolean isReference) {
		this.isReference = isReference;
	}

	@Override
	public String toString() {
		return "ReceiptDTO [particularsName=" + particularsName + ", amount=" + amount + ", reference=" + reference
				+ ", date=" + date + ", chequeNo=" + chequeNo + ", bankName=" + bankName + ", remoteId=" + remoteId
				+ ", ledgerName=" + ledgerName + ", narrationMessage=" + narrationMessage + ", receiptEnabled="
				+ receiptEnabled + ", isDailyReceipt=" + isDailyReceipt + ", isOptional=" + isOptional
				+ ", isReference=" + isReference + "]";
	}

}
