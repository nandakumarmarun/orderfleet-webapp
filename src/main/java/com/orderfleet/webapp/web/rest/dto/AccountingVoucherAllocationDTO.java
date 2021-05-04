package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.AccountingVoucherAllocation;
import com.orderfleet.webapp.domain.enums.PaymentMode;

/**
 * A DTO for the AccountingVoucherAllocationDTO entity.
 * 
 * @author Muhammed Riyas T
 * @since August 17, 2016
 */
public class AccountingVoucherAllocationDTO {

	private String receivablePayablePid;

	private String voucherNumber;

	private String referenceNumber;

	private String referenceDocumentNumber;

	private PaymentMode mode;

	private double amount;

	private String remarks;

	private String receivablePayableId;

	public AccountingVoucherAllocationDTO() {
	}

	public AccountingVoucherAllocationDTO(AccountingVoucherAllocation accountingVoucherAllocation) {
		super();
		this.receivablePayablePid = accountingVoucherAllocation.getReceivablePayablePid();
		this.voucherNumber = accountingVoucherAllocation.getVoucherNumber();
		this.referenceNumber = accountingVoucherAllocation.getReferenceNumber();
		this.referenceDocumentNumber = accountingVoucherAllocation.getReferenceDocumentNumber();
		this.mode = accountingVoucherAllocation.getMode();
		this.amount = accountingVoucherAllocation.getAmount();
		this.remarks = accountingVoucherAllocation.getRemarks();
		this.receivablePayableId = accountingVoucherAllocation.getReceivablePayableId() != null
				&& !accountingVoucherAllocation.getReceivablePayableId().equals("")
						? accountingVoucherAllocation.getReceivablePayableId()
						: "";
	}

	public String getReceivablePayablePid() {
		return receivablePayablePid;
	}

	public void setReceivablePayablePid(String receivablePayablePid) {
		this.receivablePayablePid = receivablePayablePid;
	}

	public String getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getReferenceDocumentNumber() {
		return referenceDocumentNumber;
	}

	public void setReferenceDocumentNumber(String referenceDocumentNumber) {
		this.referenceDocumentNumber = referenceDocumentNumber;
	}

	public PaymentMode getMode() {
		return mode;
	}

	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReceivablePayableId() {
		return receivablePayableId;
	}

	public void setReceivablePayableId(String receivablePayableId) {
		this.receivablePayableId = receivablePayableId;
	}

	@Override
	public String toString() {
		return "AccountingVoucherAllocationDTO [receivablePayablePid=" + receivablePayablePid + ", voucherNumber="
				+ voucherNumber + ", referenceNumber=" + referenceNumber + ", referenceDocumentNumber="
				+ referenceDocumentNumber + ", mode=" + mode + ", amount=" + amount + ", remarks=" + remarks
				+ ", receivablePayableId=" + receivablePayableId + "]";
	}

}
