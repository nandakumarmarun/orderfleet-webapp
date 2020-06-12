package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.InventoryVoucherHeaderHistory;
import com.orderfleet.webapp.domain.enums.SalesManagementStatus;
import com.orderfleet.webapp.domain.enums.SendSalesOrderEmailStatus;
import com.orderfleet.webapp.domain.enums.SourceModule;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

/**
 * A DTO for the InventoryVoucherHeader entity.
 * 
 * @author Muhammed Riyas T
 * @since July 19, 2016
 */
public class InventoryVoucherHeaderDTO {

	private String pid;

	private String documentNumberLocal;

	private String documentNumberServer;

	private String documentPid;

	private String documentName;

	private LocalDateTime createdDate;

	private LocalDateTime documentDate;

	private String receiverAccountPid;

	private String receiverAccountName;

	private String receiverAccountAlias;

	private String supplierAccountPid;

	private String supplierAccountName;

	private String employeePid;

	private String employeeName;

	private String userName;

	private double documentTotal;

	private double documentTotalUpdated;

	private double documentVolume;

	private double documentVolumeUpdated;

	private double docDiscountPercentage;

	private double docDiscountAmount;

	private List<InventoryVoucherDetailDTO> inventoryVoucherDetails;

	private List<InventoryVoucherHeaderDTO> history;

	private Boolean status;

	private String priceLevelPid;

	private String priceLevelName;

	private SourceModule sourceModule;

	private String referenceDocumentNumber;

	private String referenceDocumentType;

	private String processStatus;

	private Long orderStatusId;

	private String orderStatusName;

	private String visitRemarks; // executive task execution remarks

	// SaveOrUpdate Dashboard update
	private Boolean isNew = Boolean.FALSE;

	// status for tally download
	private TallyDownloadStatus tallyDownloadStatus = TallyDownloadStatus.PENDING;

	private SalesManagementStatus salesManagementStatus = SalesManagementStatus.DEFAULT;

	private SendSalesOrderEmailStatus sendSalesOrderEmailStatus = SendSalesOrderEmailStatus.NOT_SENT;

	private long orderNumber;
	private String customeraddress;
	private String customerEmail;
	private String customerPhone;

	private boolean pdfDownloadStatus;
	private boolean updatedStatus;
	private LocalDateTime clientDate;

	public InventoryVoucherHeaderDTO() {
		super();
	}

	public InventoryVoucherHeaderDTO(InventoryVoucherHeader inventoryVoucherHeader) {
		super();
		this.pid = inventoryVoucherHeader.getPid();
		this.documentNumberLocal = inventoryVoucherHeader.getDocumentNumberLocal();
		this.documentNumberServer = inventoryVoucherHeader.getDocumentNumberServer();
		if (inventoryVoucherHeader.getDocument() != null) {
			this.documentPid = inventoryVoucherHeader.getDocument().getPid();
			this.documentName = inventoryVoucherHeader.getDocument().getName();
		}
		this.createdDate = inventoryVoucherHeader.getCreatedDate();
		this.documentDate = inventoryVoucherHeader.getDocumentDate();
		if (inventoryVoucherHeader.getReceiverAccount() != null) {
			this.receiverAccountPid = inventoryVoucherHeader.getReceiverAccount().getPid();
			this.receiverAccountName = inventoryVoucherHeader.getReceiverAccount().getName();
			this.receiverAccountAlias = inventoryVoucherHeader.getReceiverAccount().getAlias() == null ? ""
					: inventoryVoucherHeader.getReceiverAccount().getAlias();
		}
		this.processStatus = inventoryVoucherHeader.getProcessStatus();
		if (!inventoryVoucherHeader.getInventoryVoucherDetails().isEmpty()) {
			this.inventoryVoucherDetails = inventoryVoucherHeader.getInventoryVoucherDetails().stream()
					.map(InventoryVoucherDetailDTO::new).collect(Collectors.toList());
		}
		if (inventoryVoucherHeader.getSupplierAccount() != null) {
			this.supplierAccountPid = inventoryVoucherHeader.getSupplierAccount().getPid();
			this.supplierAccountName = inventoryVoucherHeader.getSupplierAccount().getName();
		}
		if (inventoryVoucherHeader.getEmployee() != null) {
			this.employeePid = inventoryVoucherHeader.getEmployee().getPid();
			this.employeeName = inventoryVoucherHeader.getEmployee().getName();
		}
		if (inventoryVoucherHeader.getCreatedBy() != null) {
			this.userName = inventoryVoucherHeader.getCreatedBy().getFirstName();
		}
		this.documentTotal = inventoryVoucherHeader.getDocumentTotal();
		this.documentVolume = inventoryVoucherHeader.getDocumentVolume();
		this.documentTotalUpdated = inventoryVoucherHeader.getDocumentTotalUpdated();
		this.documentVolumeUpdated = inventoryVoucherHeader.getDocumentVolumeUpdated();
		this.docDiscountAmount = inventoryVoucherHeader.getDocDiscountAmount();
		this.docDiscountPercentage = inventoryVoucherHeader.getDocDiscountPercentage();
		this.docDiscountPercentage = inventoryVoucherHeader.getDocDiscountPercentage();
		this.status = inventoryVoucherHeader.getStatus();
		if (inventoryVoucherHeader.getPriceLevel() != null) {
			this.priceLevelPid = inventoryVoucherHeader.getPriceLevel().getPid();
			this.priceLevelName = inventoryVoucherHeader.getPriceLevel().getName();
		}
		if (inventoryVoucherHeader.getOrderStatus() != null) {
			this.orderStatusId = inventoryVoucherHeader.getOrderStatus().getId();
			this.orderStatusName = inventoryVoucherHeader.getOrderStatus().getName();
		}
		if (inventoryVoucherHeader.getTallyDownloadStatus() != null) {
			this.tallyDownloadStatus = inventoryVoucherHeader.getTallyDownloadStatus();
		}

		this.orderNumber = inventoryVoucherHeader.getOrderNumber() == null ? 0
				: inventoryVoucherHeader.getOrderNumber();
		this.customeraddress = inventoryVoucherHeader.getReceiverAccount().getAddress();
		this.customerEmail = inventoryVoucherHeader.getReceiverAccount().getEmail1();
		this.customerPhone = inventoryVoucherHeader.getReceiverAccount().getPhone1();

		this.visitRemarks = inventoryVoucherHeader.getExecutiveTaskExecution().getRemarks() == null ? ""
				: inventoryVoucherHeader.getExecutiveTaskExecution().getRemarks();

		this.pdfDownloadStatus = inventoryVoucherHeader.getPdfDownloadStatus();

		if (inventoryVoucherHeader.getSalesManagementStatus() != null) {
			this.salesManagementStatus = inventoryVoucherHeader.getSalesManagementStatus();
		}

		if (inventoryVoucherHeader.getSendSalesOrderEmailStatus() != null) {
			this.sendSalesOrderEmailStatus = inventoryVoucherHeader.getSendSalesOrderEmailStatus();
		}

		this.clientDate = inventoryVoucherHeader.getExecutiveTaskExecution().getSendDate();
		this.updatedStatus = inventoryVoucherHeader.getUpdatedStatus();
	}

	public InventoryVoucherHeaderDTO(InventoryVoucherHeaderHistory inventoryVoucherHeader) {
		super();
		this.pid = inventoryVoucherHeader.getPid();
		this.documentNumberLocal = inventoryVoucherHeader.getDocumentNumberLocal();
		this.documentNumberServer = inventoryVoucherHeader.getDocumentNumberServer();
		this.documentPid = inventoryVoucherHeader.getDocument().getPid();
		this.documentName = inventoryVoucherHeader.getDocument().getName();
		this.createdDate = inventoryVoucherHeader.getCreatedDate();
		this.documentDate = inventoryVoucherHeader.getDocumentDate();
		this.receiverAccountPid = inventoryVoucherHeader.getReceiverAccount().getPid();
		this.receiverAccountName = inventoryVoucherHeader.getReceiverAccount().getName();
		if (inventoryVoucherHeader.getSupplierAccount() != null) {
			this.supplierAccountPid = inventoryVoucherHeader.getSupplierAccount().getPid();
			this.supplierAccountName = inventoryVoucherHeader.getSupplierAccount().getName();
		}
		if (inventoryVoucherHeader.getEmployee() != null) {
			this.employeePid = inventoryVoucherHeader.getEmployee().getPid();
			this.employeeName = inventoryVoucherHeader.getEmployee().getName();
		}
		this.userName = inventoryVoucherHeader.getCreatedBy().getFirstName();
		this.documentTotal = inventoryVoucherHeader.getDocumentTotal();
		this.documentVolume = inventoryVoucherHeader.getDocumentVolume();
		this.docDiscountAmount = inventoryVoucherHeader.getDocDiscountAmount();
		this.docDiscountPercentage = inventoryVoucherHeader.getDocDiscountPercentage();
		if (inventoryVoucherHeader.getPriceLevel() != null) {
			this.priceLevelPid = inventoryVoucherHeader.getPriceLevel().getPid();
			this.priceLevelName = inventoryVoucherHeader.getPriceLevel().getName();
		}
		this.isNew = false;
	}

	public InventoryVoucherHeaderDTO(String pid, String documentNumberLocal, String documentNumberServer,
			String documentPid, String documentName, LocalDateTime createdDate, LocalDateTime documentDate,
			String receiverAccountPid, String receiverAccountName, String supplierAccountPid,
			String supplierAccountName, String employeePid, String employeeName, String userName, double documentTotal,
			double documentVolume) {
		super();
		this.pid = pid;
		this.documentNumberLocal = documentNumberLocal;
		this.documentNumberServer = documentNumberServer;
		this.documentPid = documentPid;
		this.documentName = documentName;
		this.createdDate = createdDate;
		this.documentDate = documentDate;
		this.receiverAccountPid = receiverAccountPid;
		this.receiverAccountName = receiverAccountName;
		this.supplierAccountPid = supplierAccountPid;
		this.supplierAccountName = supplierAccountName;
		this.employeePid = employeePid;
		this.employeeName = employeeName;
		this.userName = userName;
		this.documentTotal = documentTotal;
		this.documentVolume = documentVolume;
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

	public boolean getPdfDownloadStatus() {
		return pdfDownloadStatus;
	}

	public void setPdfDownloadStatus(boolean pdfDownloadStatus) {
		this.pdfDownloadStatus = pdfDownloadStatus;
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

	public String getReceiverAccountPid() {
		return receiverAccountPid;
	}

	public void setReceiverAccountPid(String receiverAccountPid) {
		this.receiverAccountPid = receiverAccountPid;
	}

	public String getReceiverAccountName() {
		return receiverAccountName;
	}

	public void setReceiverAccountName(String receiverAccountName) {
		this.receiverAccountName = receiverAccountName;
	}

	public String getVisitRemarks() {
		return visitRemarks;
	}

	public void setVisitRemarks(String visitRemarks) {
		this.visitRemarks = visitRemarks;
	}

	public String getSupplierAccountPid() {
		return supplierAccountPid;
	}

	public void setSupplierAccountPid(String supplierAccountPid) {
		this.supplierAccountPid = supplierAccountPid;
	}

	public String getSupplierAccountName() {
		return supplierAccountName;
	}

	public void setSupplierAccountName(String supplierAccountName) {
		this.supplierAccountName = supplierAccountName;
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

	public SalesManagementStatus getSalesManagementStatus() {
		return salesManagementStatus;
	}

	public void setSalesManagementStatus(SalesManagementStatus salesManagementStatus) {
		this.salesManagementStatus = salesManagementStatus;
	}

	public double getDocumentTotal() {
		return documentTotal;
	}

	public void setDocumentTotal(double documentTotal) {
		this.documentTotal = documentTotal;
	}

	public double getDocumentTotalUpdated() {
		return documentTotalUpdated;
	}

	public void setDocumentTotalUpdated(double documentTotalUpdated) {
		this.documentTotalUpdated = documentTotalUpdated;
	}

	public double getDocumentVolume() {
		return documentVolume;
	}

	public void setDocumentVolume(double documentVolume) {
		this.documentVolume = documentVolume;
	}

	public double getDocumentVolumeUpdated() {
		return documentVolumeUpdated;
	}

	public void setDocumentVolumeUpdated(double documentVolumeUpdated) {
		this.documentVolumeUpdated = documentVolumeUpdated;
	}

	public double getDocDiscountAmount() {
		return docDiscountAmount;
	}

	public void setDocDiscountAmount(double docDiscountAmount) {
		this.docDiscountAmount = docDiscountAmount;
	}

	public double getDocDiscountPercentage() {
		return docDiscountPercentage;
	}

	public void setDocDiscountPercentage(double docDiscountPercentage) {
		this.docDiscountPercentage = docDiscountPercentage;
	}

	public List<InventoryVoucherDetailDTO> getInventoryVoucherDetails() {
		return inventoryVoucherDetails;
	}

	public void setInventoryVoucherDetails(List<InventoryVoucherDetailDTO> inventoryVoucherDetails) {
		this.inventoryVoucherDetails = inventoryVoucherDetails;
	}

	public List<InventoryVoucherHeaderDTO> getHistory() {
		return history;
	}

	public void setHistory(List<InventoryVoucherHeaderDTO> history) {
		this.history = history;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getPriceLevelPid() {
		return priceLevelPid;
	}

	public void setPriceLevelPid(String priceLevelPid) {
		this.priceLevelPid = priceLevelPid;
	}

	public String getPriceLevelName() {
		return priceLevelName;
	}

	public void setPriceLevelName(String priceLevelName) {
		this.priceLevelName = priceLevelName;
	}

	public SourceModule getSourceModule() {
		return sourceModule;
	}

	public void setSourceModule(SourceModule sourceModule) {
		this.sourceModule = sourceModule;
	}

	public String getReferenceDocumentNumber() {
		return referenceDocumentNumber;
	}

	public void setReferenceDocumentNumber(String referenceDocumentNumber) {
		this.referenceDocumentNumber = referenceDocumentNumber;
	}

	public String getReferenceDocumentType() {
		return referenceDocumentType;
	}

	public void setReferenceDocumentType(String referenceDocumentType) {
		this.referenceDocumentType = referenceDocumentType;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public Long getOrderStatusId() {
		return orderStatusId;
	}

	public void setOrderStatusId(Long orderStatusId) {
		this.orderStatusId = orderStatusId;
	}

	public String getOrderStatusName() {
		return orderStatusName;
	}

	public void setOrderStatusName(String orderStatusName) {
		this.orderStatusName = orderStatusName;
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	public TallyDownloadStatus getTallyDownloadStatus() {
		return tallyDownloadStatus;
	}

	public void setTallyDownloadStatus(TallyDownloadStatus tallyDownloadStatus) {
		this.tallyDownloadStatus = tallyDownloadStatus;
	}

	public long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getCustomeraddress() {
		return customeraddress;
	}

	public void setCustomeraddress(String customeraddress) {
		this.customeraddress = customeraddress;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public boolean getUpdatedStatus() {
		return updatedStatus;
	}

	public void setUpdatedStatus(boolean updatedStatus) {
		this.updatedStatus = updatedStatus;
	}

	public SendSalesOrderEmailStatus getSendSalesOrderEmailStatus() {
		return sendSalesOrderEmailStatus;
	}

	public void setSendSalesOrderEmailStatus(SendSalesOrderEmailStatus sendSalesOrderEmailStatus) {
		this.sendSalesOrderEmailStatus = sendSalesOrderEmailStatus;
	}

	public LocalDateTime getClientDate() {
		return clientDate;
	}

	public void setClientDate(LocalDateTime clientDate) {
		this.clientDate = clientDate;
	}

	public String getReceiverAccountAlias() {
		return receiverAccountAlias;
	}

	public void setReceiverAccountAlias(String receiverAccountAlias) {
		this.receiverAccountAlias = receiverAccountAlias;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		InventoryVoucherHeaderDTO accountProfileDTO = (InventoryVoucherHeaderDTO) o;

		if (!Objects.equals(pid, accountProfileDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

}
