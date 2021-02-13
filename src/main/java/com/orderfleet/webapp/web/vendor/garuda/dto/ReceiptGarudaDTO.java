package com.orderfleet.webapp.web.vendor.garuda.dto;

import java.time.format.DateTimeFormatter;

import com.orderfleet.webapp.domain.AccountingVoucherAllocation;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;

public class ReceiptGarudaDTO {
	private String receiptNo;
	
	private String customerCode;
	
	private double amount;

	// private double headerAmount;

	private String date;
	
	private String chequeNo;	

	private String bankName;
	
	private String remarks;
	
	private String chequeDate;	

	private PaymentMode mode;

	private String employeeName;

	private String referenceNo;	

	// private AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO();

	public ReceiptGarudaDTO(AccountingVoucherDetail accountingVoucherDetail) {
		super();
		this.receiptNo = accountingVoucherDetail.getAccountingVoucherHeader().getDocumentNumberServer();
		this.customerCode = accountingVoucherDetail.getBy().getName();
		this.amount = accountingVoucherDetail.getAmount();
		this.referenceNo = accountingVoucherDetail.getReferenceNumber();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		this.date = accountingVoucherDetail.getVoucherDate().format(formatter);
		this.chequeNo = accountingVoucherDetail.getInstrumentNumber();
		this.bankName = accountingVoucherDetail.getBankName();
		this.mode = accountingVoucherDetail.getMode();
		this.remarks = accountingVoucherDetail.getRemarks();
		this.chequeDate = accountingVoucherDetail.getInstrumentDate()==null?"":accountingVoucherDetail.getInstrumentDate().format(formatter);
		this.employeeName = accountingVoucherDetail.getAccountingVoucherHeader().getEmployee().getName();
	}

	public ReceiptGarudaDTO(AccountingVoucherAllocation accountingVoucherAllocation) {
		super();
		this.receiptNo = accountingVoucherAllocation.getAccountingVoucherDetail()
				.getAccountingVoucherHeader().getDocumentNumberServer();
		this.customerCode = accountingVoucherAllocation.getAccountingVoucherDetail().getBy().getName();
		this.amount = accountingVoucherAllocation.getAmount();
		this.referenceNo = accountingVoucherAllocation.getReferenceNumber();
		this.date = accountingVoucherAllocation.getAccountingVoucherDetail().getVoucherDate().toString();
		this.chequeNo = accountingVoucherAllocation.getAccountingVoucherDetail().getInstrumentNumber();
		this.bankName = accountingVoucherAllocation.getAccountingVoucherDetail().getBankName();
		this.remarks = accountingVoucherAllocation.getRemarks();
		this.mode = accountingVoucherAllocation.getMode();
		this.chequeDate = accountingVoucherAllocation.getAccountingVoucherDetail()
				.getInstrumentDate()==null?"":accountingVoucherAllocation.getAccountingVoucherDetail().getInstrumentDate().toString();
		this.employeeName = accountingVoucherAllocation.getAccountingVoucherDetail().getAccountingVoucherHeader().getEmployee().getName();
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
//
//	public double getHeaderAmount() {
//		return headerAmount;
//	}
//
//	public void setHeaderAmount(double headerAmount) {
//		this.headerAmount = headerAmount;
//	}

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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}

	public PaymentMode getMode() {
		return mode;
	}

	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}
	
//	public AccountingVoucherHeaderDTO getAccountingVoucherHeaderDTO() {
//		return accountingVoucherHeaderDTO;
//	}
//
//	public void setAccountingVoucherHeaderDTO(AccountingVoucherHeaderDTO accountingVoucherHeaderDTO) {
//		this.accountingVoucherHeaderDTO = accountingVoucherHeaderDTO;
//	}
//	
}
