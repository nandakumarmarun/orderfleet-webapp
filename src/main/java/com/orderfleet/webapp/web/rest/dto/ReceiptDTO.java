package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.AccountingVoucherAllocation;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.enums.PaymentMode;

/**
 * A DTO For Generate Accounting Voucher (Receipt) To Client APP.
 *
 * @author Sarath
 * @since Oct 29, 2016
 */
public class ReceiptDTO {

	private String accountingVoucherHeaderPid;
	private String particularsName;
	private double amount;
	private double detailAmount;
	private Long detailId;
	private double headerAmount;
	private String reference;
	private String date;
	private String chequeNo;
	private String bankName;
	private String remoteId;
	private String ledgerName;
	private String trimChar;
	private String narrationMessage;
	private String userName;
	private String chequeDate;
	private PaymentMode mode;
	private AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO();
	private String employeeName;
	private String provisionalReceiptNo;
	private String employeeAlias;
	private String godownName;

	public ReceiptDTO() {
		super();
	}

	public ReceiptDTO(AccountingVoucherDetail accountingVoucherDetail) {
		super();
		this.accountingVoucherHeaderPid = accountingVoucherDetail.getAccountingVoucherHeader().getPid();
		this.particularsName = accountingVoucherDetail.getBy().getName();
		this.amount = accountingVoucherDetail.getAmount();
		this.detailId = accountingVoucherDetail.getId();
		this.reference = accountingVoucherDetail.getReferenceNumber();
		this.date = accountingVoucherDetail.getVoucherDate().toString();
		this.chequeNo = accountingVoucherDetail.getInstrumentNumber();
		this.bankName = accountingVoucherDetail.getBankName();
		this.ledgerName = accountingVoucherDetail.getBy().getName();
		String remarks = accountingVoucherDetail.getRemarks();
		this.narrationMessage = remarks == null ? "" : remarks;
		this.userName = accountingVoucherDetail.getAccountingVoucherHeader().getEmployee().getName();
		this.provisionalReceiptNo = accountingVoucherDetail.getProvisionalReceiptNo();

		if (accountingVoucherDetail.getMode() == PaymentMode.Cheque
				|| accountingVoucherDetail.getMode() == PaymentMode.Bank) {
			this.mode = PaymentMode.Bank;
		} else {
			this.mode = accountingVoucherDetail.getMode();
		}

//		this.accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO(
//				accountingVoucherDetail.getAccountingVoucherHeader());
		this.trimChar = accountingVoucherDetail.getBy().getTrimChar();
		this.chequeDate = accountingVoucherDetail.getInstrumentDate() == null ? ""
				: accountingVoucherDetail.getInstrumentDate().toString();
		this.employeeName = accountingVoucherDetail.getAccountingVoucherHeader().getEmployee().getName();
		this.employeeAlias = accountingVoucherDetail.getAccountingVoucherHeader().getEmployee().getAlias();
	}

	public ReceiptDTO(AccountingVoucherAllocation accountingVoucherAllocation) {
		super();
		this.accountingVoucherHeaderPid = accountingVoucherAllocation.getAccountingVoucherDetail()
				.getAccountingVoucherHeader().getPid();
		this.particularsName = accountingVoucherAllocation.getAccountingVoucherDetail().getBy().getName();
		this.amount = accountingVoucherAllocation.getAmount();
		this.detailAmount = accountingVoucherAllocation.getAccountingVoucherDetail().getAmount();
		this.detailId = accountingVoucherAllocation.getAccountingVoucherDetail().getId();
		this.reference = accountingVoucherAllocation.getReferenceNumber();
		this.date = accountingVoucherAllocation.getAccountingVoucherDetail().getVoucherDate().toString();
		this.chequeNo = accountingVoucherAllocation.getAccountingVoucherDetail().getInstrumentNumber();
		this.bankName = accountingVoucherAllocation.getAccountingVoucherDetail().getBankName();
		this.ledgerName = accountingVoucherAllocation.getAccountingVoucherDetail().getBy().getName();

		String remarks = accountingVoucherAllocation.getRemarks();

		if (remarks != null && remarks.contains("&")) {
			remarks = remarks.replace("&", "and ");
		}
		this.narrationMessage = remarks == null ? "" : remarks;
		this.userName = accountingVoucherAllocation.getAccountingVoucherDetail().getAccountingVoucherHeader()
				.getEmployee().getName();
		if (accountingVoucherAllocation.getMode() == PaymentMode.Cheque
				|| accountingVoucherAllocation.getMode() == PaymentMode.Bank) {
			this.mode = PaymentMode.Bank;
		} else {
			this.mode = accountingVoucherAllocation.getMode();
		}
//		this.accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO(
//				accountingVoucherAllocation.getAccountingVoucherDetail().getAccountingVoucherHeader());
		this.chequeDate = accountingVoucherAllocation.getAccountingVoucherDetail().getInstrumentDate() == null ? ""
				: accountingVoucherAllocation.getAccountingVoucherDetail().getInstrumentDate().toString();
		this.employeeName = accountingVoucherAllocation.getAccountingVoucherDetail().getAccountingVoucherHeader()
				.getEmployee().getName();

		this.employeeAlias = accountingVoucherAllocation.getAccountingVoucherDetail().getAccountingVoucherHeader()
				.getEmployee().getAlias();
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

	public double getDetailAmount() {
		return detailAmount;
	}

	public void setDetailAmount(double detailAmount) {
		this.detailAmount = detailAmount;
	}

	public Long getDetailId() {
		return detailId;
	}

	public void setDetailId(Long detailId) {
		this.detailId = detailId;
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

	public String getAccountingVoucherHeaderPid() {
		return accountingVoucherHeaderPid;
	}

	public void setAccountingVoucherHeaderPid(String accountingVoucherHeaderPid) {
		this.accountingVoucherHeaderPid = accountingVoucherHeaderPid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public PaymentMode getMode() {
		return mode;
	}

	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}

	public AccountingVoucherHeaderDTO getAccountingVoucherHeaderDTO() {
		return accountingVoucherHeaderDTO;
	}

	public void setAccountingVoucherHeaderDTO(AccountingVoucherHeaderDTO accountingVoucherHeaderDTO) {
		this.accountingVoucherHeaderDTO = accountingVoucherHeaderDTO;
	}

	public String getTrimChar() {
		return trimChar;
	}

	public void setTrimChar(String trimChar) {
		this.trimChar = trimChar;
	}

	public String getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public double getHeaderAmount() {
		return headerAmount;
	}

	public void setHeaderAmount(double headerAmount) {
		this.headerAmount = headerAmount;
	}

	public String getProvisionalReceiptNo() {
		return provisionalReceiptNo;
	}

	public void setProvisionalReceiptNo(String provisionalReceiptNo) {
		this.provisionalReceiptNo = provisionalReceiptNo;
	}

	public String getEmployeeAlias() {
		return employeeAlias;
	}

	public void setEmployeeAlias(String employeeAlias) {
		this.employeeAlias = employeeAlias;
	}

	public String getGodownName() {
		return godownName;
	}

	public void setGodownName(String godownName) {
		this.godownName = godownName;
	}

	@Override
	public String toString() {
		return "ReceiptDTO [amount=" + amount + ", headerAmount=" + headerAmount + ", chequeNo=" + chequeNo
				+ ", bankName=" + bankName + ", remoteId=" + remoteId + ", ledgerName=" + ledgerName
				+ ", narrationMessage=" + narrationMessage + ", employeeName=" + employeeName
				+ ", provisionalReceiptNo=" + provisionalReceiptNo + ", employeeAlias=" + employeeAlias + "]";
	}

}
