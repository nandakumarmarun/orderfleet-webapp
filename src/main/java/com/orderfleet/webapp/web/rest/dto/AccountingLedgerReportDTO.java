package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.PaymentMode;

/**
 * A DTO for the AccountingLedgerReportDTO entity.
 * 
 * @author Muhammed Riyas T
 * @since Mar 14, 2016
 */
public class AccountingLedgerReportDTO {

	private String userName;

	private String documentName;

	private PaymentMode mode;

	private String byAccountName;

	private String toAccountName;

	private LocalDateTime createdDate;

	/* for show in aac voucher report */
	private double byAmount;

	private double toAmount;
	
	private String incomeExpenseHead;
	
	private String employeeName;

	public AccountingLedgerReportDTO() {
	}

	public AccountingLedgerReportDTO(AccountingVoucherDetail accountingVoucherDetail) {
		super();
		this.userName = accountingVoucherDetail.getAccountingVoucherHeader().getCreatedBy().getFirstName();
		this.documentName = accountingVoucherDetail.getAccountingVoucherHeader().getDocument().getName();
		this.mode = accountingVoucherDetail.getMode();
		this.byAccountName = accountingVoucherDetail.getBy().getName();
		this.toAccountName = accountingVoucherDetail.getTo().getName();
		this.createdDate = accountingVoucherDetail.getAccountingVoucherHeader().getCreatedDate();
		if (accountingVoucherDetail.getAccountingVoucherHeader().getDocument().getActivityAccount()
				.equals(AccountTypeColumn.By)) {
			this.byAmount = accountingVoucherDetail.getAmount();
		} else if (accountingVoucherDetail.getAccountingVoucherHeader().getDocument().getActivityAccount()
				.equals(AccountTypeColumn.To)) {
			this.toAmount = accountingVoucherDetail.getAmount();
		}
		if(accountingVoucherDetail.getIncomeExpenseHead()!=null) {
		this.incomeExpenseHead=accountingVoucherDetail.getIncomeExpenseHead().getName();
		}
		if(accountingVoucherDetail.getAccountingVoucherHeader()!=null) {
			this.employeeName=accountingVoucherDetail.getAccountingVoucherHeader().getEmployee().getName();
		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public PaymentMode getMode() {
		return mode;
	}

	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}

	public String getByAccountName() {
		return byAccountName;
	}

	public void setByAccountName(String byAccountName) {
		this.byAccountName = byAccountName;
	}

	public String getToAccountName() {
		return toAccountName;
	}

	public void setToAccountName(String toAccountName) {
		this.toAccountName = toAccountName;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public double getByAmount() {
		return byAmount;
	}

	public void setByAmount(double byAmount) {
		this.byAmount = byAmount;
	}

	public double getToAmount() {
		return toAmount;
	}

	public void setToAmount(double toAmount) {
		this.toAmount = toAmount;
	}

	public String getIncomeExpenseHead() {
		return incomeExpenseHead;
	}

	public void setIncomeExpenseHead(String incomeExpenseHead) {
		this.incomeExpenseHead = incomeExpenseHead;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	
}
