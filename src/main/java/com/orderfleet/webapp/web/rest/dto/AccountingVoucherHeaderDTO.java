package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.AccountingVoucherHeaderHistory;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

/**
 * A DTO for the AccountingVoucherHeader entity.
 * 
 * @author Muhammed Riyas T
 * @since July 27, 2016
 */
public class AccountingVoucherHeaderDTO {

	private String pid;

	private String documentPid;

	private String documentName;

	private String accountProfilePid;

	private String accountProfileName;

	private LocalDateTime createdDate;

	private LocalDateTime documentDate;

	private String phone;

	private String employeePid;

	private String employeeName;

	private String userName;

	private double totalAmount;

	private double outstandingAmount;

	private String remarks;

	private List<AccountingVoucherDetailDTO> accountingVoucherDetails;

	private String documentNumberLocal;

	private String documentNumberServer;

	private List<AccountingVoucherHeaderDTO> history;

	/* for show in aac voucher report */
	private double byAmount;

	private double toAmount;

	// SaveOrUpdate Dashboard update
	private Boolean isNew = Boolean.FALSE;

	private Boolean status;
	
	private double cashAmount;
	
	private double chequeAmount;
	
	private TallyDownloadStatus tallyDownloadStatus = TallyDownloadStatus.PENDING;

	public AccountingVoucherHeaderDTO() {
		super();
	}

	public AccountingVoucherHeaderDTO(AccountingVoucherHeader accountingVoucherHeader) {
		super();
		this.pid = accountingVoucherHeader.getPid();
		if (accountingVoucherHeader.getDocument() != null) {
			this.documentPid = accountingVoucherHeader.getDocument().getPid();
			this.documentName = accountingVoucherHeader.getDocument().getName();
			if (accountingVoucherHeader.getDocument().getActivityAccount() != null
					&& accountingVoucherHeader.getDocument().getActivityAccount().equals(AccountTypeColumn.By)) {
				this.byAmount = accountingVoucherHeader.getTotalAmount();
			} else if (accountingVoucherHeader.getDocument().getActivityAccount() != null
					&& accountingVoucherHeader.getDocument().getActivityAccount().equals(AccountTypeColumn.To)) {
				this.toAmount = accountingVoucherHeader.getTotalAmount();
			}
		}
		if (accountingVoucherHeader.getAccountProfile() != null) {
			this.accountProfilePid = accountingVoucherHeader.getAccountProfile().getPid();
			this.accountProfileName = accountingVoucherHeader.getAccountProfile().getName();
			this.phone = accountingVoucherHeader.getAccountProfile().getPhone1();
		}
		this.createdDate = accountingVoucherHeader.getCreatedDate();
		this.documentDate = accountingVoucherHeader.getDocumentDate();
		if (accountingVoucherHeader.getEmployee() != null) {
			this.employeePid = accountingVoucherHeader.getEmployee().getPid();
			this.employeeName = accountingVoucherHeader.getEmployee().getName();
		}
		if (accountingVoucherHeader.getCreatedBy() != null) {
			this.userName = accountingVoucherHeader.getCreatedBy().getFirstName();
		}
		this.totalAmount = accountingVoucherHeader.getTotalAmount();
		this.outstandingAmount = accountingVoucherHeader.getOutstandingAmount();
		this.remarks = accountingVoucherHeader.getRemarks();
		this.documentNumberLocal = accountingVoucherHeader.getDocumentNumberLocal();
		this.documentNumberServer = accountingVoucherHeader.getDocumentNumberServer();
		this.status = accountingVoucherHeader.getStatus();
		for(AccountingVoucherDetail avd : accountingVoucherHeader.getAccountingVoucherDetails()) {
			if(avd.getMode() == PaymentMode.Bank) {
				this.chequeAmount += avd.getAmount();
			}else if(avd.getMode() == PaymentMode.Cash) {
				this.cashAmount += avd.getAmount();
			}
		}
		
		if (accountingVoucherHeader.getTallyDownloadStatus() != null) {
			this.tallyDownloadStatus = accountingVoucherHeader.getTallyDownloadStatus();
		}
	}

	public AccountingVoucherHeaderDTO(AccountingVoucherHeaderHistory accountingVoucherHeader) {
		super();
		this.pid = accountingVoucherHeader.getPid();
		this.documentPid = accountingVoucherHeader.getDocument().getPid();
		this.documentName = accountingVoucherHeader.getDocument().getName();
		this.accountProfilePid = accountingVoucherHeader.getAccountProfile().getPid();
		this.accountProfileName = accountingVoucherHeader.getAccountProfile().getName();
		this.createdDate = accountingVoucherHeader.getCreatedDate();
		this.documentDate = accountingVoucherHeader.getDocumentDate();
		if (accountingVoucherHeader.getEmployee() != null) {
			this.employeePid = accountingVoucherHeader.getEmployee().getPid();
			this.employeeName = accountingVoucherHeader.getEmployee().getName();
		}
		this.userName = accountingVoucherHeader.getCreatedBy().getFirstName();
		this.totalAmount = accountingVoucherHeader.getTotalAmount();
		this.outstandingAmount = accountingVoucherHeader.getOutstandingAmount();
		this.remarks = accountingVoucherHeader.getRemarks();
		this.documentNumberLocal = accountingVoucherHeader.getDocumentNumberLocal();
		this.documentNumberServer = accountingVoucherHeader.getDocumentNumberServer();
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<AccountingVoucherDetailDTO> getAccountingVoucherDetails() {
		return accountingVoucherDetails;
	}

	public void setAccountingVoucherDetails(List<AccountingVoucherDetailDTO> accountingVoucherDetails) {
		this.accountingVoucherDetails = accountingVoucherDetails;
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

	public List<AccountingVoucherHeaderDTO> getHistory() {
		return history;
	}

	public void setHistory(List<AccountingVoucherHeaderDTO> history) {
		this.history = history;
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

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
	
	

	public double getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(double cashAmount) {
		this.cashAmount = cashAmount;
	}

	public double getChequeAmount() {
		return chequeAmount;
	}

	public void setChequeAmount(double chequeAmount) {
		this.chequeAmount = chequeAmount;
	}
	
	public TallyDownloadStatus getTallyDownloadStatus() {
		return tallyDownloadStatus;
	}

	public void setTallyDownloadStatus(TallyDownloadStatus tallyDownloadStatus) {
		this.tallyDownloadStatus = tallyDownloadStatus;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		AccountingVoucherHeaderDTO accountProfileDTO = (AccountingVoucherHeaderDTO) o;

		if (!Objects.equals(pid, accountProfileDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

}
