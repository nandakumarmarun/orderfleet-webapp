package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import com.orderfleet.webapp.domain.ExpenseVoucherDetail;
import com.orderfleet.webapp.domain.ExpenseVoucherHeader;

import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

/**
 * A DTO for the ExpenseVoucherHeader entity.
 * 
 * @author Prashob Sasidharan
 * @since October 29, 2019
 */
public class ExpenseVoucherHeaderDTO {

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

	private List<ExpenseVoucherDetailDTO> expenseVoucherDetails;

	private String documentNumberLocal;

	private String documentNumberServer;

	private String imageRefNo;

	private List<ExpenseVoucherHeaderDTO> history;

	/* for show in aac voucher report */
	private double byAmount;

	private double toAmount;

	// SaveOrUpdate Dashboard update
	private Boolean isNew = Boolean.FALSE;

	private Boolean status;

	private double cashAmount;

	private double chequeAmount;

	private TallyDownloadStatus tallyDownloadStatus = TallyDownloadStatus.PENDING;

	private Boolean imageButtonVisible = Boolean.FALSE;;

	public ExpenseVoucherHeaderDTO() {
		super();
	}

	public ExpenseVoucherHeaderDTO(ExpenseVoucherHeader expenseVoucherHeader) {
		super();
		this.pid = expenseVoucherHeader.getPid();
		if (expenseVoucherHeader.getDocument() != null) {
			this.documentPid = expenseVoucherHeader.getDocument().getPid();
			this.documentName = expenseVoucherHeader.getDocument().getName();
			if (expenseVoucherHeader.getDocument().getActivityAccount() != null
					&& expenseVoucherHeader.getDocument().getActivityAccount().equals(AccountTypeColumn.By)) {
				this.byAmount = expenseVoucherHeader.getTotalAmount();
			} else if (expenseVoucherHeader.getDocument().getActivityAccount() != null
					&& expenseVoucherHeader.getDocument().getActivityAccount().equals(AccountTypeColumn.To)) {
				this.toAmount = expenseVoucherHeader.getTotalAmount();
			}
		}
		if (expenseVoucherHeader.getAccountProfile() != null) {
			this.accountProfilePid = expenseVoucherHeader.getAccountProfile().getPid();
			this.accountProfileName = expenseVoucherHeader.getAccountProfile().getName();
			this.phone = expenseVoucherHeader.getAccountProfile().getPhone1();
		}
		this.createdDate = expenseVoucherHeader.getCreatedDate();
		this.documentDate = expenseVoucherHeader.getDocumentDate();
		if (expenseVoucherHeader.getEmployee() != null) {
			this.employeePid = expenseVoucherHeader.getEmployee().getPid();
			this.employeeName = expenseVoucherHeader.getEmployee().getName();
		}
		if (expenseVoucherHeader.getCreatedBy() != null) {
			this.userName = expenseVoucherHeader.getCreatedBy().getFirstName();
		}

		if (!expenseVoucherHeader.getExpenseVoucherDetails().isEmpty()) {
			this.expenseVoucherDetails = expenseVoucherHeader.getExpenseVoucherDetails().stream()
					.map(ExpenseVoucherDetailDTO::new).collect(Collectors.toList());
		}
		this.totalAmount = expenseVoucherHeader.getTotalAmount();
		this.outstandingAmount = expenseVoucherHeader.getOutstandingAmount();
		this.remarks = expenseVoucherHeader.getRemarks();
		this.documentNumberLocal = expenseVoucherHeader.getDocumentNumberLocal();
		this.documentNumberServer = expenseVoucherHeader.getDocumentNumberServer();
		this.status = expenseVoucherHeader.getStatus();
		for (ExpenseVoucherDetail avd : expenseVoucherHeader.getExpenseVoucherDetails()) {
			if (avd.getMode() == PaymentMode.Cheque || avd.getMode() == PaymentMode.Bank) {
				this.chequeAmount += avd.getAmount();
			} else if (avd.getMode() == PaymentMode.Cash) {
				this.cashAmount += avd.getAmount();
			}
		}

		if (expenseVoucherHeader.getTallyDownloadStatus() != null) {
			this.tallyDownloadStatus = expenseVoucherHeader.getTallyDownloadStatus();
		}

		if (expenseVoucherHeader.getFiles().size() > 0) {
			this.imageButtonVisible = true;
		}
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

	public List<ExpenseVoucherDetailDTO> getExpenseVoucherDetails() {
		return expenseVoucherDetails;
	}

	public void setExpenseVoucherDetails(List<ExpenseVoucherDetailDTO> expenseVoucherDetails) {
		this.expenseVoucherDetails = expenseVoucherDetails;
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

	public List<ExpenseVoucherHeaderDTO> getHistory() {
		return history;
	}

	public void setHistory(List<ExpenseVoucherHeaderDTO> history) {
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

	public Boolean getImageButtonVisible() {
		return imageButtonVisible;
	}

	public void setImageButtonVisible(Boolean imageButtonVisible) {
		this.imageButtonVisible = imageButtonVisible;
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

	public String getImageRefNo() {
		return imageRefNo;
	}

	public void setImageRefNo(String imageRefNo) {
		this.imageRefNo = imageRefNo;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ExpenseVoucherHeaderDTO accountProfileDTO = (ExpenseVoucherHeaderDTO) o;

		if (!Objects.equals(pid, accountProfileDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

}
