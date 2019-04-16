package com.orderfleet.webapp.web.tally.dto;

import com.orderfleet.webapp.domain.enums.ReceivablePayableType;

public class ReceivablePayableDTO {

	private String accountName;
	private String referenceDocumentNumber;
	private String referenceDocumentDate;
	private double referenceDocumentBalanceAmount;
	private double referenceDocumentAmount;
	private ReceivablePayableType receivablePayableType;
	private String billDue;
	private Long billOverDue;
	
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getReferenceDocumentNumber() {
		return referenceDocumentNumber;
	}
	public void setReferenceDocumentNumber(String referenceDocumentNumber) {
		this.referenceDocumentNumber = referenceDocumentNumber;
	}
	public String getReferenceDocumentDate() {
		return referenceDocumentDate;
	}
	public void setReferenceDocumentDate(String referenceDocumentDate) {
		this.referenceDocumentDate = referenceDocumentDate;
	}
	public double getReferenceDocumentBalanceAmount() {
		return referenceDocumentBalanceAmount;
	}
	public void setReferenceDocumentBalanceAmount(double referenceDocumentBalanceAmount) {
		this.referenceDocumentBalanceAmount = referenceDocumentBalanceAmount;
	}
	public double getReferenceDocumentAmount() {
		return referenceDocumentAmount;
	}
	public void setReferenceDocumentAmount(double referenceDocumentAmount) {
		this.referenceDocumentAmount = referenceDocumentAmount;
	}
	public ReceivablePayableType getReceivablePayableType() {
		return receivablePayableType;
	}
	public void setReceivablePayableType(ReceivablePayableType receivablePayableType) {
		this.receivablePayableType = receivablePayableType;
	}
	public String getBillDue() {
		return billDue;
	}
	public void setBillDue(String billDue) {
		this.billDue = billDue;
	}
	public Long getBillOverDue() {
		return billOverDue;
	}
	public void setBillOverDue(Long billOverDue) {
		this.billOverDue = billOverDue;
	}
	
	
}
