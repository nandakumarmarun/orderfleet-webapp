package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.enums.ProcessFlowStatus;
import com.orderfleet.webapp.domain.enums.SalesManagementStatus;
import com.orderfleet.webapp.domain.enums.SalesOrderStatus;
import com.orderfleet.webapp.domain.enums.SendSalesOrderEmailStatus;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

/**
 * A DTO for the SalesPerformanceDTO.
 */
public class SalesPerformanceDTO {

	private String pid;

	private String documentNumberLocal;

	private String documentNumberServer;

	private String documentPid;

	private String documentName;

	private LocalDateTime createdDate;

	private LocalDateTime documentDate;

	private String receiverAccountPid;

	private String receiverAccountName;

	private String receiverAccountPhone;

	private String receiverAccountLocation;

	private String remarks;

	private String supplierAccountPid;

	private String supplierAccountName;

	private String employeePid;

	private String employeeName;

	private String userName;

	private double documentTotal;

	private double documentTotalUpdated;

	private double documentVolume;

	private double documentVolumeUpdated;

	private double totalVolume;

	private boolean pdfDownloadButtonStatus;

	private boolean pdfDownloadStatus;

	private boolean sendSalesOrderEmailStatusColumn;
	
	private boolean sendEmailAutoColumn;

	private boolean sendSalesOrderSapButtonStatus;

	private Boolean status;

	private long orderNumber;

	private TallyDownloadStatus tallyDownloadStatus;

	private SalesManagementStatus salesManagementStatus;

	private SendSalesOrderEmailStatus sendSalesOrderEmailStatus;
	
	private SalesOrderStatus salesOrderStatus;

	private ProcessFlowStatus processFlowStatus;

	private String visitRemarks;

	private boolean updatedStatus;

	private boolean editOrder;

	private double totalWithoutTax; // reduced discount percentage and tax

	private double taxTotal;

	private double paymentReceived;

	private LocalDateTime clientDate;

	private String bookingId;

	private String deliveryDate;

	private long deliveryDateDifference;

	private boolean imageButtonVisible;

	private String dynamicDocumentPid;
	
	private double discountAmount;
	
	private double additonalDiscount;
	
	private double AdditionalDiscountAmount;
	
	private double quantity;
	
	private double discountPercentage;

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

	public double getDocumentTotal() {
		return documentTotal;
	}

	public void setDocumentTotal(double documentTotal) {
		this.documentTotal = documentTotal;
	}

	public double getDocumentVolume() {
		return documentVolume;
	}

	public void setDocumentVolume(double documentVolume) {
		this.documentVolume = documentVolume;
	}

	public double getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(double totalVolume) {
		this.totalVolume = totalVolume;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public TallyDownloadStatus getTallyDownloadStatus() {
		return tallyDownloadStatus;
	}

	public void setTallyDownloadStatus(TallyDownloadStatus tallyDownloadStatus) {
		this.tallyDownloadStatus = tallyDownloadStatus;
	}

	public String getVisitRemarks() {
		return visitRemarks;
	}

	public void setVisitRemarks(String visitRemarks) {
		this.visitRemarks = visitRemarks;
	}

	public boolean isPdfDownloadButtonStatus() {
		return pdfDownloadButtonStatus;
	}

	public void setPdfDownloadButtonStatus(boolean pdfDownloadButtonStatus) {
		this.pdfDownloadButtonStatus = pdfDownloadButtonStatus;
	}

	public boolean getPdfDownloadStatus() {
		return pdfDownloadStatus;
	}

	public void setPdfDownloadStatus(boolean pdfDownloadStatus) {
		this.pdfDownloadStatus = pdfDownloadStatus;
	}

	public long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public SalesManagementStatus getSalesManagementStatus() {
		return salesManagementStatus;
	}

	public void setSalesManagementStatus(SalesManagementStatus salesManagementStatus) {
		this.salesManagementStatus = salesManagementStatus;
	}

	public double getDocumentTotalUpdated() {
		return documentTotalUpdated;
	}

	public void setDocumentTotalUpdated(double documentTotalUpdated) {
		this.documentTotalUpdated = documentTotalUpdated;
	}

	public double getDocumentVolumeUpdated() {
		return documentVolumeUpdated;
	}

	public void setDocumentVolumeUpdated(double documentVolumeUpdated) {
		this.documentVolumeUpdated = documentVolumeUpdated;
	}

	public boolean isUpdatedStatus() {
		return updatedStatus;
	}

	public void setUpdatedStatus(boolean updatedStatus) {
		this.updatedStatus = updatedStatus;
	}

	public boolean getEditOrder() {
		return editOrder;
	}

	public void setEditOrder(boolean editOrder) {
		this.editOrder = editOrder;
	}

	public boolean getSendSalesOrderEmailStatusColumn() {
		return sendSalesOrderEmailStatusColumn;
	}

	public void setSendSalesOrderEmailStatusColumn(boolean sendSalesOrderEmailStatusColumn) {
		this.sendSalesOrderEmailStatusColumn = sendSalesOrderEmailStatusColumn;
	}

	public SendSalesOrderEmailStatus getSendSalesOrderEmailStatus() {
		return sendSalesOrderEmailStatus;
	}

	public void setSendSalesOrderEmailStatus(SendSalesOrderEmailStatus sendSalesOrderEmailStatus) {
		this.sendSalesOrderEmailStatus = sendSalesOrderEmailStatus;
	}

	public double getTotalWithoutTax() {
		return totalWithoutTax;
	}

	public void setTotalWithoutTax(double totalWithoutTax) {
		this.totalWithoutTax = totalWithoutTax;
	}

	public double getTaxTotal() {
		return taxTotal;
	}

	public void setTaxTotal(double taxTotal) {
		this.taxTotal = taxTotal;
	}

	public LocalDateTime getClientDate() {
		return clientDate;
	}

	public void setClientDate(LocalDateTime clientDate) {
		this.clientDate = clientDate;
	}

	public boolean getSendSalesOrderSapButtonStatus() {
		return sendSalesOrderSapButtonStatus;
	}

	public void setSendSalesOrderSapButtonStatus(boolean sendSalesOrderSapButtonStatus) {
		this.sendSalesOrderSapButtonStatus = sendSalesOrderSapButtonStatus;
	}

	public ProcessFlowStatus getProcessFlowStatus() {
		return processFlowStatus;
	}

	public void setProcessFlowStatus(ProcessFlowStatus processFlowStatus) {
		this.processFlowStatus = processFlowStatus;
	}

	public double getPaymentReceived() {
		return paymentReceived;
	}

	public void setPaymentReceived(double paymentReceived) {
		this.paymentReceived = paymentReceived;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public long getDeliveryDateDifference() {
		return deliveryDateDifference;
	}

	public void setDeliveryDateDifference(long deliveryDateDifference) {
		this.deliveryDateDifference = deliveryDateDifference;
	}

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public boolean isImageButtonVisible() {
		return imageButtonVisible;
	}

	public void setImageButtonVisible(boolean imageButtonVisible) {
		this.imageButtonVisible = imageButtonVisible;
	}

	public String getDynamicDocumentPid() {
		return dynamicDocumentPid;
	}

	public void setDynamicDocumentPid(String dynamicDocumentPid) {
		this.dynamicDocumentPid = dynamicDocumentPid;
	}

	public String getReceiverAccountPhone() {
		return receiverAccountPhone;
	}

	public void setReceiverAccountPhone(String receiverAccountPhone) {
		this.receiverAccountPhone = receiverAccountPhone;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReceiverAccountLocation() {
		return receiverAccountLocation;
	}

	public void setReceiverAccountLocation(String receiverAccountLocation) {
		this.receiverAccountLocation = receiverAccountLocation;
	}

	public double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public double getAdditonalDiscount() {
		return additonalDiscount;
	}

	public void setAdditonalDiscount(double additonalDiscount) {
		this.additonalDiscount = additonalDiscount;
	}

	public double getAdditionalDiscountAmount() {
		return AdditionalDiscountAmount;
	}

	public void setAdditionalDiscountAmount(double additionalDiscountAmount) {
		AdditionalDiscountAmount = additionalDiscountAmount;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public SalesOrderStatus getSalesOrderStatus() {
		return salesOrderStatus;
	}

	public void setSalesOrderStatus(SalesOrderStatus salesOrderStatus) {
		this.salesOrderStatus = salesOrderStatus;
	}

	public boolean isSendEmailAutoColumn() {
		return sendEmailAutoColumn;
	}

	public void setSendEmailAutoColumn(boolean sendEmailAutoColumn) {
		this.sendEmailAutoColumn = sendEmailAutoColumn;
	}


	
}
