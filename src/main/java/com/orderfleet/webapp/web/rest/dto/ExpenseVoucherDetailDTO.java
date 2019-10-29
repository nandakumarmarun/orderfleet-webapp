package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.orderfleet.webapp.domain.ExpenseVoucherDetail;
import com.orderfleet.webapp.domain.enums.PaymentMode;

/**
 * A DTO for the ExpenseVoucherDetailDTO entity.
 * 
 * @author Prashob Sasidharan
 * @since October 29, 2019
 */
public class ExpenseVoucherDetailDTO {

	private PaymentMode mode;

	private double amount;

	private String instrumentNumber;

	private LocalDateTime instrumentDate;

	private String bankPid;

	private String bankName;

	private String byAccountPid;

	private String byAccountName;

	private String toAccountPid;

	private String toAccountName;

	private String voucherNumber;

	private LocalDateTime voucherDate;

	private String referenceNumber;

	private String remarks;

	private String incomeExpenseHeadPid;

	private String incomeExpenseHeadName;

	private String provisionalReceiptNo;

	public ExpenseVoucherDetailDTO() {
	}

	public ExpenseVoucherDetailDTO(ExpenseVoucherDetail accountingVoucherDetail) {
		super();
		this.mode = accountingVoucherDetail.getMode();
		this.amount = accountingVoucherDetail.getAmount();
		this.instrumentNumber = accountingVoucherDetail.getInstrumentNumber();
		this.instrumentDate = accountingVoucherDetail.getInstrumentDate();
		if (accountingVoucherDetail.getBank() != null) {
			this.bankPid = accountingVoucherDetail.getBank().getPid();
			this.bankName = accountingVoucherDetail.getBank().getName();
		} else {
			this.bankName = accountingVoucherDetail.getBankName();
		}
		this.byAccountPid = accountingVoucherDetail.getBy().getPid();
		this.byAccountName = accountingVoucherDetail.getBy().getName();
		this.toAccountPid = accountingVoucherDetail.getTo().getPid();
		this.toAccountName = accountingVoucherDetail.getTo().getName();
		this.voucherNumber = accountingVoucherDetail.getVoucherNumber();
		this.voucherDate = accountingVoucherDetail.getVoucherDate();
		this.referenceNumber = accountingVoucherDetail.getReferenceNumber();
		this.remarks = accountingVoucherDetail.getRemarks();
		if (accountingVoucherDetail.getIncomeExpenseHead() != null) {
			this.incomeExpenseHeadPid = accountingVoucherDetail.getIncomeExpenseHead().getPid();
			this.incomeExpenseHeadName = accountingVoucherDetail.getIncomeExpenseHead().getName();
		}
		this.provisionalReceiptNo = accountingVoucherDetail.getProvisionalReceiptNo();
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

	public String getInstrumentNumber() {
		return instrumentNumber;
	}

	public void setInstrumentNumber(String instrumentNumber) {
		this.instrumentNumber = instrumentNumber;
	}

	public LocalDateTime getInstrumentDate() {
		return instrumentDate;
	}

	public void setInstrumentDate(LocalDateTime instrumentDate) {
		this.instrumentDate = instrumentDate;
	}

	public String getBankPid() {
		return bankPid;
	}

	public void setBankPid(String bankPid) {
		this.bankPid = bankPid;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getByAccountPid() {
		return byAccountPid;
	}

	public void setByAccountPid(String byAccountPid) {
		this.byAccountPid = byAccountPid;
	}

	public String getByAccountName() {
		return byAccountName;
	}

	public void setByAccountName(String byAccountName) {
		this.byAccountName = byAccountName;
	}

	public String getToAccountPid() {
		return toAccountPid;
	}

	public void setToAccountPid(String toAccountPid) {
		this.toAccountPid = toAccountPid;
	}

	public String getToAccountName() {
		return toAccountName;
	}

	public void setToAccountName(String toAccountName) {
		this.toAccountName = toAccountName;
	}

	public String getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

	public LocalDateTime getVoucherDate() {
		return voucherDate;
	}

	public void setVoucherDate(LocalDateTime voucherDate) {
		this.voucherDate = voucherDate;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getIncomeExpenseHeadPid() {
		return incomeExpenseHeadPid;
	}

	public void setIncomeExpenseHeadPid(String incomeExpenseHeadPid) {
		this.incomeExpenseHeadPid = incomeExpenseHeadPid;
	}

	public String getIncomeExpenseHeadName() {
		return incomeExpenseHeadName;
	}

	public void setIncomeExpenseHeadName(String incomeExpenseHeadName) {
		this.incomeExpenseHeadName = incomeExpenseHeadName;
	}

	public String getProvisionalReceiptNo() {
		return provisionalReceiptNo;
	}

	public void setProvisionalReceiptNo(String provisionalReceiptNo) {
		this.provisionalReceiptNo = provisionalReceiptNo;
	}

}
