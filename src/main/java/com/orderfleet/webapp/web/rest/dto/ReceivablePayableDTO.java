package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;

/**
 * A DTO for the ReceivablePayableDTO
 * 
 * @author Sarath
 * @since Aug 16, 2016
 */
public class ReceivablePayableDTO {

	private String pid;

	private String accountPid;

	private String accountName;

	private String accountType;

	private String accountAddress;

	private ReceivablePayableType receivablePayableType;

	private String referenceDocumentNumber;

	private LocalDate referenceDocumentDate;

	private String referenceDocumentType;

	private double referenceDocumentAmount;

	private double referenceDocumentBalanceAmount;
	
	private double referenceDocumentFinalBalanceAmount;

	private String remarks;

	private Long billOverDue;

	private LocalDateTime lastModifiedDate;

	private double closingBalance;

	public ReceivablePayableDTO() {
		super();
	}

	public ReceivablePayableDTO(ReceivablePayable receivablePayable) {
		super();
		this.pid = receivablePayable.getPid();
		this.accountPid = receivablePayable.getAccountProfile().getPid();
		this.closingBalance = receivablePayable.getAccountProfile().getClosingBalance();
		this.accountName = receivablePayable.getAccountProfile().getName();
		this.accountType = receivablePayable.getAccountProfile().getAccountType().getName();
		this.accountAddress = receivablePayable.getAccountProfile().getAddress();
		this.receivablePayableType = receivablePayable.getReceivablePayableType();
		this.referenceDocumentNumber = receivablePayable.getReferenceDocumentNumber();
		this.referenceDocumentDate = receivablePayable.getReferenceDocumentDate();
		this.referenceDocumentType = receivablePayable.getReferenceDocumentType();
		this.referenceDocumentAmount = receivablePayable.getReferenceDocumentAmount();
		this.referenceDocumentBalanceAmount = receivablePayable.getReferenceDocumentBalanceAmount();
		this.remarks = receivablePayable.getRemarks();
		this.billOverDue = receivablePayable.getBillOverDue();
		this.lastModifiedDate = receivablePayable.getLastModifiedDate();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getAccountPid() {
		return accountPid;
	}

	public void setAccountPid(String accountPid) {
		this.accountPid = accountPid;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountAddress() {
		return accountAddress;
	}

	public void setAccountAddress(String accountAddress) {
		this.accountAddress = accountAddress;
	}

	public ReceivablePayableType getReceivablePayableType() {
		return receivablePayableType;
	}

	public void setReceivablePayableType(ReceivablePayableType receivablePayableType) {
		this.receivablePayableType = receivablePayableType;
	}

	public String getReferenceDocumentNumber() {
		return referenceDocumentNumber;
	}

	public void setReferenceDocumentNumber(String referenceDocumentNumber) {
		this.referenceDocumentNumber = referenceDocumentNumber;
	}

	public LocalDate getReferenceDocumentDate() {
		return referenceDocumentDate;
	}

	public void setReferenceDocumentDate(LocalDate referenceDocumentDate) {
		this.referenceDocumentDate = referenceDocumentDate;
	}

	public String getReferenceDocumentType() {
		return referenceDocumentType;
	}

	public void setReferenceDocumentType(String referenceDocumentType) {
		this.referenceDocumentType = referenceDocumentType;
	}

	public double getReferenceDocumentAmount() {
		return referenceDocumentAmount;
	}

	public void setReferenceDocumentAmount(double referenceDocumentAmount) {
		this.referenceDocumentAmount = referenceDocumentAmount;
	}

	public double getReferenceDocumentBalanceAmount() {
		return referenceDocumentBalanceAmount;
	}

	public void setReferenceDocumentBalanceAmount(double referenceDocumentBalanceAmount) {
		this.referenceDocumentBalanceAmount = referenceDocumentBalanceAmount;
	}
	
	public double getReferenceDocumentFinalBalanceAmount() {
		return referenceDocumentFinalBalanceAmount;
	}

	public void setReferenceDocumentFinalBalanceAmount(double referenceDocumentFinalBalanceAmount) {
		this.referenceDocumentFinalBalanceAmount = referenceDocumentFinalBalanceAmount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getBillOverDue() {
		return billOverDue;
	}

	public void setBillOverDue(Long billOverDue) {
		this.billOverDue = billOverDue;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public double getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(double closingBalance) {
		this.closingBalance = closingBalance;
	}

	@Override
	public String toString() {
		return "ReceivablePayableDTO [accountName=" + accountName + ", receivablePayableType=" + receivablePayableType
				+ ", closingBalance=" + closingBalance + "]";
	}

	

	
}
