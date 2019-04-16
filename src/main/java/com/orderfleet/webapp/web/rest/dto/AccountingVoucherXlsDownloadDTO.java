package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.enums.PaymentMode;

/**
 *Dto for downloading  AccountingVoucher
 *
 * @author fahad
 * @since Jul 7, 2017
 */
public class AccountingVoucherXlsDownloadDTO {

	
	private String pid;
	private String documentPid;
	private String documentName;
	private String accountProfilePid;
	private String accountProfileName;
	private LocalDateTime createdDate;
	private LocalDateTime documentDate;
	private String employeePid;
	private String employeeName;
	private String userName;
	private double totalAmount;
	private double outstandingAmount;
	private String remarks;
	private String phone;
	
	private String documentNumberLocal;
	private String documentNumberServer;
	private double byAmount;
	private double toAmount;
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
	private String incomeExpenseHeadPid;
	private String incomeExpenseHeadName;
	
	
	
	
	public AccountingVoucherXlsDownloadDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public AccountingVoucherXlsDownloadDTO(AccountingVoucherHeaderDTO accountingVoucherHeaderDTO,AccountingVoucherDetailDTO accountingVoucherDetailDTO) {
		super();
		this.pid = accountingVoucherHeaderDTO.getPid();
		this.documentPid = accountingVoucherHeaderDTO.getDocumentPid();
		this.documentName = accountingVoucherHeaderDTO.getDocumentName();
		this.accountProfilePid = accountingVoucherHeaderDTO.getAccountProfilePid();
		this.accountProfileName = accountingVoucherHeaderDTO.getAccountProfileName();
		this.phone=accountingVoucherHeaderDTO.getPhone();
		this.createdDate = accountingVoucherHeaderDTO.getCreatedDate();
		this.documentDate = accountingVoucherHeaderDTO.getDocumentDate();
		this.employeePid = accountingVoucherHeaderDTO.getEmployeePid();
		this.employeeName = accountingVoucherHeaderDTO.getEmployeeName();
		this.remarks=accountingVoucherHeaderDTO.getRemarks();
		this.userName = accountingVoucherHeaderDTO.getUserName();
		this.totalAmount = accountingVoucherHeaderDTO.getTotalAmount();
		this.outstandingAmount = accountingVoucherHeaderDTO.getOutstandingAmount();
		this.documentNumberLocal = accountingVoucherHeaderDTO.getDocumentNumberLocal();
		this.documentNumberServer = accountingVoucherHeaderDTO.getDocumentNumberServer();
		this.byAmount = accountingVoucherHeaderDTO.getByAmount();
		this.toAmount = accountingVoucherHeaderDTO.getToAmount();
		this.mode = accountingVoucherDetailDTO.getMode();
		this.amount = accountingVoucherDetailDTO.getAmount();
		this.instrumentNumber = accountingVoucherDetailDTO.getInstrumentNumber();
		this.instrumentDate = accountingVoucherDetailDTO.getInstrumentDate();
		this.bankPid = accountingVoucherDetailDTO.getBankPid();
		this.bankName = accountingVoucherDetailDTO.getBankName();
		this.byAccountPid = accountingVoucherDetailDTO.getByAccountPid();
		this.byAccountName = accountingVoucherDetailDTO.getByAccountName();
		this.toAccountPid = accountingVoucherDetailDTO.getToAccountPid();
		this.toAccountName = accountingVoucherDetailDTO.getToAccountName();
		this.voucherNumber = accountingVoucherDetailDTO.getVoucherNumber();
		this.voucherDate = accountingVoucherDetailDTO.getVoucherDate();
		this.referenceNumber = accountingVoucherDetailDTO.getReferenceNumber();
		this.incomeExpenseHeadPid = accountingVoucherDetailDTO.getIncomeExpenseHeadPid();
		this.incomeExpenseHeadName = accountingVoucherDetailDTO.getIncomeExpenseHeadName();
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getDocumentPid() {
		return documentPid;
	}
	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public String getAccountProfilePid() {
		return accountProfilePid;
	}
	public void setAccountProfilePid(String accountProfilePid) {
		this.accountProfilePid = accountProfilePid;
	}
	public String getAccountProfileName() {
		return accountProfileName;
	}
	public void setAccountProfileName(String accountProfileName) {
		this.accountProfileName = accountProfileName;
	}
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
	public LocalDateTime getDocumentDate() {
		return documentDate;
	}
	public void setDocumentDate(LocalDateTime documentDate) {
		this.documentDate = documentDate;
	}
	public String getEmployeePid() {
		return employeePid;
	}
	public void setEmployeePid(String employeePid) {
		this.employeePid = employeePid;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public double getOutstandingAmount() {
		return outstandingAmount;
	}
	public void setOutstandingAmount(double outstandingAmount) {
		this.outstandingAmount = outstandingAmount;
	}
	public String getDocumentNumberLocal() {
		return documentNumberLocal;
	}
	public void setDocumentNumberLocal(String documentNumberLocal) {
		this.documentNumberLocal = documentNumberLocal;
	}
	public String getDocumentNumberServer() {
		return documentNumberServer;
	}
	public void setDocumentNumberServer(String documentNumberServer) {
		this.documentNumberServer = documentNumberServer;
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
	@Override
	public String toString() {
		return "AccountingVoucherXlsDownloadDTO [pid=" + pid + ", documentPid=" + documentPid + ", documentName="
				+ documentName + ", accountProfilePid=" + accountProfilePid + ", accountProfileName="
				+ accountProfileName + ", createdDate=" + createdDate + ", documentDate=" + documentDate
				+ ", employeePid=" + employeePid + ", employeeName=" + employeeName + ", userName=" + userName
				+ ", totalAmount=" + totalAmount + ", outstandingAmount=" + outstandingAmount + ", documentNumberLocal="
				+ documentNumberLocal + ", documentNumberServer=" + documentNumberServer + ", byAmount=" + byAmount
				+ ", toAmount=" + toAmount + ", mode=" + mode + ", amount=" + amount + ", instrumentNumber="
				+ instrumentNumber + ", instrumentDate=" + instrumentDate + ", bankPid=" + bankPid + ", bankName="
				+ bankName + ", byAccountPid=" + byAccountPid + ", byAccountName=" + byAccountName + ", toAccountPid="
				+ toAccountPid + ", toAccountName=" + toAccountName + ", voucherNumber=" + voucherNumber
				+ ", voucherDate=" + voucherDate + ", referenceNumber=" + referenceNumber + ", incomeExpenseHeadPid="
				+ incomeExpenseHeadPid + ", incomeExpenseHeadName=" + incomeExpenseHeadName + "]";
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	

}
