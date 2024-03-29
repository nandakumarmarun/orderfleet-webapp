package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.domain.enums.SalesManagementStatus;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;

/**
 * A DTO for the AccountingVoucherHeader entity.
 * 
 * @author Muhammed Riyas T
 * @since July 27, 2016
 */
public class AccountingVoucherHeaderDTO {

	@Inject
	CompanyConfigurationRepository companyConfigurationRepository;
	
	private long id;

	private String pid;

	private String documentPid;

	private String documentName;

	private long documentId;

	private long accountProfileId;

	private String accountProfilePid;

	private String accountProfileName;
	
	private String customerCode;

	private LocalDateTime createdDate;

	private LocalDateTime documentDate;

	private String phone;

	private String employeePid;

	private String employeeName;
	
	private String employeeProfileCode;

	private String userName;

	private String userPid;

	private double totalAmount;

	private double outstandingAmount;

	private String remarks;

	private List<AccountingVoucherDetailDTO> accountingVoucherDetails;

	private String documentNumberLocal;

	private String documentNumberServer;

	private String imageRefNo;

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

	private SalesManagementStatus salesManagementStatus = SalesManagementStatus.DEFAULT;

	private Boolean imageButtonVisible = Boolean.FALSE;;

	private String supplierAccountPid;

	private String orderReferenceNumber;

	private boolean sendToOdoo;
	private String description;
	
	private LocalDateTime time;

	private Long companyId;

	private String companyPid;

	private String companyName;
	
	

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
			this.description =accountingVoucherHeader.getAccountProfile().getDescription();
			this.phone = accountingVoucherHeader.getAccountProfile().getPhone1();
			this.customerCode=accountingVoucherHeader.getAccountProfile().getCustomerCode();
		}
		
		this.createdDate = accountingVoucherHeader.getCreatedDate();
		this.time =accountingVoucherHeader.getCreatedDate();
		this.documentDate = accountingVoucherHeader.getDocumentDate();
		if (accountingVoucherHeader.getEmployee() != null) {
			this.employeePid = accountingVoucherHeader.getEmployee().getPid();
			this.employeeName = accountingVoucherHeader.getEmployee().getName();
			this.employeeProfileCode=accountingVoucherHeader.getEmployee().getUser().getLogin();
		}
		if (accountingVoucherHeader.getCreatedBy() != null) {
			this.userName = accountingVoucherHeader.getCreatedBy().getFirstName();
		}

		if (!accountingVoucherHeader.getAccountingVoucherDetails().isEmpty()) {
			this.accountingVoucherDetails = accountingVoucherHeader.getAccountingVoucherDetails().stream()
					.map(AccountingVoucherDetailDTO::new).collect(Collectors.toList());
		}
		this.totalAmount = accountingVoucherHeader.getTotalAmount();
		this.outstandingAmount = accountingVoucherHeader.getOutstandingAmount();
		this.remarks = accountingVoucherHeader.getRemarks();
		this.documentNumberLocal = accountingVoucherHeader.getDocumentNumberLocal();
		this.documentNumberServer = accountingVoucherHeader.getDocumentNumberServer();
		this.status = accountingVoucherHeader.getStatus();
		for (AccountingVoucherDetail avd : accountingVoucherHeader.getAccountingVoucherDetails()) {
			if (avd.getMode() == PaymentMode.Cheque || avd.getMode() == PaymentMode.Bank) {
				this.chequeAmount += avd.getAmount();
			} else if (avd.getMode() == PaymentMode.Cash) {
				this.cashAmount += avd.getAmount();
			}
		}

		if (accountingVoucherHeader.getTallyDownloadStatus() != null) {
			this.tallyDownloadStatus = accountingVoucherHeader.getTallyDownloadStatus();
		}

		if (accountingVoucherHeader.getSalesManagementStatus() != null) {
			this.salesManagementStatus = accountingVoucherHeader.getSalesManagementStatus();
		}

		if (accountingVoucherHeader.getFiles().size() > 0) {
			this.imageButtonVisible = true;
		}
	}

	public AccountingVoucherHeaderDTO(Company company, AccountingVoucherHeader accountingVoucherHeader) {

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
			this.description =accountingVoucherHeader.getAccountProfile().getDescription();
			this.phone = accountingVoucherHeader.getAccountProfile().getPhone1();
			this.customerCode=accountingVoucherHeader.getAccountProfile().getCustomerCode();
		}

		this.createdDate = accountingVoucherHeader.getCreatedDate();
		this.time =accountingVoucherHeader.getCreatedDate();
		this.documentDate = accountingVoucherHeader.getDocumentDate();
		if (accountingVoucherHeader.getEmployee() != null) {
			this.employeePid = accountingVoucherHeader.getEmployee().getPid();
			this.employeeName = accountingVoucherHeader.getEmployee().getName();
			this.employeeProfileCode=accountingVoucherHeader.getEmployee().getUser().getLogin();
		}
		if (accountingVoucherHeader.getCreatedBy() != null) {
			this.userName = accountingVoucherHeader.getCreatedBy().getFirstName();
			this.userPid = accountingVoucherHeader.getCreatedBy().getPid();
		}

		if (!accountingVoucherHeader.getAccountingVoucherDetails().isEmpty()) {
			this.accountingVoucherDetails = accountingVoucherHeader.getAccountingVoucherDetails().stream()
					.map(AccountingVoucherDetailDTO::new).collect(Collectors.toList());
		}
		this.totalAmount = accountingVoucherHeader.getTotalAmount();
		this.outstandingAmount = accountingVoucherHeader.getOutstandingAmount();
		this.remarks = accountingVoucherHeader.getRemarks();
		this.documentNumberLocal = accountingVoucherHeader.getDocumentNumberLocal();
		this.documentNumberServer = accountingVoucherHeader.getDocumentNumberServer();
		this.status = accountingVoucherHeader.getStatus();
		for (AccountingVoucherDetail avd : accountingVoucherHeader.getAccountingVoucherDetails()) {
			if (avd.getMode() == PaymentMode.Cheque || avd.getMode() == PaymentMode.Bank) {
				this.chequeAmount += avd.getAmount();
			} else if (avd.getMode() == PaymentMode.Cash) {
				this.cashAmount += avd.getAmount();
			}
		}

		if (accountingVoucherHeader.getTallyDownloadStatus() != null) {
			this.tallyDownloadStatus = accountingVoucherHeader.getTallyDownloadStatus();
		}

		if (accountingVoucherHeader.getSalesManagementStatus() != null) {
			this.salesManagementStatus = accountingVoucherHeader.getSalesManagementStatus();
		}

		if (accountingVoucherHeader.getFiles().size() > 0) {
			this.imageButtonVisible = true;
		}
		if(company != null){
			this.companyId = company.getId();
			this.companyName = company.getLegalName();
			this.companyPid =  company.getPid();
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
		this.description=accountingVoucherHeader.getAccountProfile().getDescription();
	}

	public long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	public long getAccountProfileId() {
		return accountProfileId;
	}

	public void setAccountProfileId(long accountProfileId) {
		this.accountProfileId = accountProfileId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public SalesManagementStatus getSalesManagementStatus() {
		return salesManagementStatus;
	}

	public void setSalesManagementStatus(SalesManagementStatus salesManagementStatus) {
		this.salesManagementStatus = salesManagementStatus;
	}

	public String getSupplierAccountPid() {
		return supplierAccountPid;
	}

	public void setSupplierAccountPid(String supplierAccountPid) {
		this.supplierAccountPid = supplierAccountPid;
	}

	public String getOrderReferenceNumber() {
		return orderReferenceNumber;
	}

	public void setOrderReferenceNumber(String orderReferenceNumber) {
		this.orderReferenceNumber = orderReferenceNumber;
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

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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

	public boolean getSendToOdoo() {
		return sendToOdoo;
	}

	public void setSendToOdoo(boolean sendToOdoo) {
		this.sendToOdoo = sendToOdoo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getEmployeeProfileCode() {
		return employeeProfileCode;
	}

	public void setEmployeeProfileCode(String employeeProfileCode) {
		this.employeeProfileCode = employeeProfileCode;
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
