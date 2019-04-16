package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * download xls of inventoryVoucher
 *
 * @author Sarath
 * @since Jan 14, 2017
 */
public class InventoryVoucherXlsDownloadDTO {

	private String pid;
	private String documentNumberLocal;
	private String documentNumberServer;
	private String documentName;
	private LocalDateTime createdDate;
	private LocalDateTime documentDate;
	private String receiverAccountName;
	private String supplierAccountName;
	private String employeeName;
	private String userName;
	private double documentTotal;
	private double documentVolume;
	private double docDiscountPercentage;
	private double docDiscountAmount;

	private Long detailId;
	private String productName;
	private double quantity;
	private double freeQuantity;
	private double sellingRate;
	private double mrp;
	private BigDecimal purchaseRate;
	private double taxPercentage;
	private double discountPercentage;
	private String batchNumber;
	private LocalDateTime batchDate;
	private double rowTotal;
	private double discountAmount;
	private double taxAmount;
	private String length;
	private String width;
	private String thickness;
	private String size;
	private String color;
	private String sourceStockLocationName;
	private String destinationStockLocationName;
	private Long referenceInventoryVoucherDetailId;
	private String remarks;

	public InventoryVoucherXlsDownloadDTO() {
		super();
	}

	public InventoryVoucherXlsDownloadDTO(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO, InventoryVoucherDetailDTO inventoryVoucherDetailDTO) {
		super();
		this.pid = inventoryVoucherHeaderDTO.getPid();
		this.documentNumberLocal = inventoryVoucherHeaderDTO.getDocumentNumberLocal();
		this.documentNumberServer = inventoryVoucherHeaderDTO.getDocumentNumberServer();
		this.documentName = inventoryVoucherHeaderDTO.getDocumentName();
		this.createdDate = inventoryVoucherHeaderDTO.getCreatedDate();
		this.documentDate = inventoryVoucherHeaderDTO.getDocumentDate();
		this.receiverAccountName = inventoryVoucherHeaderDTO.getReceiverAccountName();
		this.supplierAccountName = inventoryVoucherHeaderDTO.getSupplierAccountName();
		this.employeeName = inventoryVoucherHeaderDTO.getEmployeeName();
		this.userName = inventoryVoucherHeaderDTO.getUserName();
		this.documentTotal = inventoryVoucherHeaderDTO.getDocumentTotal();
		this.documentVolume = inventoryVoucherHeaderDTO.getDocumentVolume();
		this.docDiscountPercentage = inventoryVoucherHeaderDTO.getDocDiscountPercentage();
		this.docDiscountAmount = inventoryVoucherHeaderDTO.getDocDiscountAmount();
		this.detailId = inventoryVoucherDetailDTO.getDetailId();
		this.productName = inventoryVoucherDetailDTO.getProductName();
		this.quantity = inventoryVoucherDetailDTO.getQuantity();
		this.freeQuantity = inventoryVoucherDetailDTO.getFreeQuantity();
		this.sellingRate = inventoryVoucherDetailDTO.getSellingRate();
		this.mrp = inventoryVoucherDetailDTO.getMrp();
		this.purchaseRate = inventoryVoucherDetailDTO.getPurchaseRate();
		this.taxPercentage = inventoryVoucherDetailDTO.getTaxPercentage();
		this.discountPercentage = inventoryVoucherDetailDTO.getDiscountPercentage();
		this.batchNumber = inventoryVoucherDetailDTO.getBatchNumber();
		this.batchDate = inventoryVoucherDetailDTO.getBatchDate();
		this.rowTotal = inventoryVoucherDetailDTO.getRowTotal();
		this.discountAmount = inventoryVoucherDetailDTO.getDiscountAmount();
		this.taxAmount = inventoryVoucherDetailDTO.getTaxAmount();
		this.length = inventoryVoucherDetailDTO.getLength();
		this.width = inventoryVoucherDetailDTO.getWidth();
		this.thickness = inventoryVoucherDetailDTO.getThickness();
		this.size = inventoryVoucherDetailDTO.getSize();
		this.color = inventoryVoucherDetailDTO.getColor();
		this.sourceStockLocationName = inventoryVoucherDetailDTO.getSourceStockLocationName();
		this.destinationStockLocationName = inventoryVoucherDetailDTO.getDestinationStockLocationName();
		this.referenceInventoryVoucherDetailId = inventoryVoucherDetailDTO.getReferenceInventoryVoucherDetailId();
		this.remarks = inventoryVoucherDetailDTO.getRemarks();
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

	public String getReceiverAccountName() {
		return receiverAccountName;
	}

	public void setReceiverAccountName(String receiverAccountName) {
		this.receiverAccountName = receiverAccountName;
	}

	public String getSupplierAccountName() {
		return supplierAccountName;
	}

	public void setSupplierAccountName(String supplierAccountName) {
		this.supplierAccountName = supplierAccountName;
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

	public double getDocDiscountPercentage() {
		return docDiscountPercentage;
	}

	public void setDocDiscountPercentage(double docDiscountPercentage) {
		this.docDiscountPercentage = docDiscountPercentage;
	}

	public double getDocDiscountAmount() {
		return docDiscountAmount;
	}

	public void setDocDiscountAmount(double docDiscountAmount) {
		this.docDiscountAmount = docDiscountAmount;
	}

	public Long getDetailId() {
		return detailId;
	}

	public void setDetailId(Long detailId) {
		this.detailId = detailId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getFreeQuantity() {
		return freeQuantity;
	}

	public void setFreeQuantity(double freeQuantity) {
		this.freeQuantity = freeQuantity;
	}

	public double getSellingRate() {
		return sellingRate;
	}

	public void setSellingRate(double sellingRate) {
		this.sellingRate = sellingRate;
	}

	public double getMrp() {
		return mrp;
	}

	public void setMrp(double mrp) {
		this.mrp = mrp;
	}

	public BigDecimal getPurchaseRate() {
		return purchaseRate;
	}

	public void setPurchaseRate(BigDecimal purchaseRate) {
		this.purchaseRate = purchaseRate;
	}

	public double getTaxPercentage() {
		return taxPercentage;
	}

	public void setTaxPercentage(double taxPercentage) {
		this.taxPercentage = taxPercentage;
	}

	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public LocalDateTime getBatchDate() {
		return batchDate;
	}

	public void setBatchDate(LocalDateTime batchDate) {
		this.batchDate = batchDate;
	}

	public double getRowTotal() {
		return rowTotal;
	}

	public void setRowTotal(double rowTotal) {
		this.rowTotal = rowTotal;
	}

	public double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(double taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getThickness() {
		return thickness;
	}

	public void setThickness(String thickness) {
		this.thickness = thickness;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSourceStockLocationName() {
		return sourceStockLocationName;
	}

	public void setSourceStockLocationName(String sourceStockLocationName) {
		this.sourceStockLocationName = sourceStockLocationName;
	}

	public String getDestinationStockLocationName() {
		return destinationStockLocationName;
	}

	public void setDestinationStockLocationName(String destinationStockLocationName) {
		this.destinationStockLocationName = destinationStockLocationName;
	}

	public Long getReferenceInventoryVoucherDetailId() {
		return referenceInventoryVoucherDetailId;
	}

	public void setReferenceInventoryVoucherDetailId(Long referenceInventoryVoucherDetailId) {
		this.referenceInventoryVoucherDetailId = referenceInventoryVoucherDetailId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
