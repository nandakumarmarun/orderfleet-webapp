package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.DynamicDocumentHeaderHistory;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

/**
 * A DTO for the InventoryVoucherHeader entity.
 * 
 * @author Sarath
 * @since Nov 4, 2016
 */
public class DynamicDocumentHeaderDTO {

	private String pid;

	private String documentNumberLocal;

	private String documentNumberServer;

	private String documentPid;

	private String documentName;

	private LocalDateTime createdDate;

	private LocalDateTime documentDate;

	private String employeePid;

	private String employeeName;

	private String userName;

	private String userPid;

	private List<FilledFormDTO> filledForms;

	private String activityName;

	private String accountName;

	private List<DynamicDocumentHeaderDTO> history;

	private String emplyeePhone;

	private String accountAddress;

	private String accountPhone;

	private String accountEmail;

	// only used to store comma separated names for print file path.
	private String printEmailDocumentNames;

	// SaveOrUpdate Dashboard update
	private Boolean isNew = Boolean.FALSE;

	private boolean imageButtonVisible = false;// for displaying image button if images exist

	private TallyDownloadStatus tallyDownloadStatus = TallyDownloadStatus.PENDING;

	public DynamicDocumentHeaderDTO() {
		super();
	}

	public DynamicDocumentHeaderDTO(DynamicDocumentHeader dynamicDocumentHeader) {
		super();
		this.pid = dynamicDocumentHeader.getPid();
		this.documentNumberLocal = dynamicDocumentHeader.getDocumentNumberLocal();
		this.documentNumberServer = dynamicDocumentHeader.getDocumentNumberServer();
		if (dynamicDocumentHeader.getDocument() != null) {
			this.documentPid = dynamicDocumentHeader.getDocument().getPid();
			this.documentName = dynamicDocumentHeader.getDocument().getName();
		}
		this.createdDate = dynamicDocumentHeader.getCreatedDate();
		this.documentDate = dynamicDocumentHeader.getDocumentDate();
		if (dynamicDocumentHeader.getEmployee() != null) {
			this.employeePid = dynamicDocumentHeader.getEmployee().getPid();
			this.employeeName = dynamicDocumentHeader.getEmployee().getName();
			this.emplyeePhone = dynamicDocumentHeader.getEmployee().getPhone();
		}
		if (dynamicDocumentHeader.getCreatedBy() != null) {
			this.userName = dynamicDocumentHeader.getCreatedBy().getFirstName();
			this.userPid = dynamicDocumentHeader.getCreatedBy().getPid();
		}
		if (dynamicDocumentHeader.getExecutiveTaskExecution() != null) {
			this.activityName = dynamicDocumentHeader.getExecutiveTaskExecution().getActivity().getName();
			this.accountName = dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile().getName();
			this.accountAddress = dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile().getAddress();
			this.accountPhone = dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile().getPhone1();
			this.accountEmail = dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile().getEmail1();
		}
		if (dynamicDocumentHeader.getTallyDownloadStatus() != null) {
			this.tallyDownloadStatus = dynamicDocumentHeader.getTallyDownloadStatus();
		}

//		this.filledForms = dynamicDocumentHeader.getFilledForms().stream().map(FilledFormDTO::new)
//				.collect(Collectors.toList());
	}

	public DynamicDocumentHeaderDTO(DynamicDocumentHeaderHistory dynamicDocumentHeader) {
		super();
		this.pid = dynamicDocumentHeader.getPid();
		this.documentNumberLocal = dynamicDocumentHeader.getDocumentNumberLocal();
		this.documentNumberServer = dynamicDocumentHeader.getDocumentNumberServer();
		this.documentPid = dynamicDocumentHeader.getDocument().getPid();
		this.documentName = dynamicDocumentHeader.getDocument().getName();
		this.createdDate = dynamicDocumentHeader.getCreatedDate();
		this.documentDate = dynamicDocumentHeader.getDocumentDate();
		if (dynamicDocumentHeader.getEmployee() != null) {
			this.employeePid = dynamicDocumentHeader.getEmployee().getPid();
			this.employeeName = dynamicDocumentHeader.getEmployee().getName();
			this.emplyeePhone = dynamicDocumentHeader.getEmployee().getPhone();
		}
		this.userName = dynamicDocumentHeader.getCreatedBy().getFirstName();
		this.userPid = dynamicDocumentHeader.getCreatedBy().getPid();
		this.activityName = dynamicDocumentHeader.getExecutiveTaskExecution().getActivity().getName();
		this.accountName = dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile().getName();
		this.accountAddress = dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile().getAddress();
		this.accountPhone = dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile().getPhone1();
		this.accountEmail = dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile().getEmail1();

	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
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

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public List<FilledFormDTO> getFilledForms() {
		return filledForms;
	}

	public void setFilledForms(List<FilledFormDTO> filledForms) {
		this.filledForms = filledForms;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public List<DynamicDocumentHeaderDTO> getHistory() {
		return history;
	}

	public void setHistory(List<DynamicDocumentHeaderDTO> history) {
		this.history = history;
	}

	public String getEmplyeePhone() {
		return emplyeePhone;
	}

	public void setEmplyeePhone(String emplyeePhone) {
		this.emplyeePhone = emplyeePhone;
	}

	public String getAccountAddress() {
		return accountAddress;
	}

	public void setAccountAddress(String accountAddress) {
		this.accountAddress = accountAddress;
	}

	public String getAccountPhone() {
		return accountPhone;
	}

	public void setAccountPhone(String accountPhone) {
		this.accountPhone = accountPhone;
	}

	public String getAccountEmail() {
		return accountEmail;
	}

	public void setAccountEmail(String accountEmail) {
		this.accountEmail = accountEmail;
	}

	public String getPrintEmailDocumentNames() {
		return printEmailDocumentNames;
	}

	public void setPrintEmailDocumentNames(String printEmailDocumentNames) {
		this.printEmailDocumentNames = printEmailDocumentNames;
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isImageButtonVisible() {
		return imageButtonVisible;
	}

	public void setImageButtonVisible(boolean imageButtonVisible) {
		this.imageButtonVisible = imageButtonVisible;
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
		DynamicDocumentHeaderDTO accountProfileDTO = (DynamicDocumentHeaderDTO) o;
		if (!Objects.equals(pid, accountProfileDTO.pid))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

}
