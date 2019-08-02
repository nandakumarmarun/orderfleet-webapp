package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.orderfleet.webapp.domain.InventoryVoucherDetail;

/**
 * A DTO for the InventoryVoucherDetailDTO entity.
 * 
 * @author Muhammed Riyas T
 * @since July 19, 2016
 */
public class InventoryVoucherDetailDTO {

	private Long detailId;

	private String productPid;

	private String productName;

	private String productCategory;
	
	private List<String> productGroups;
	
	private String productSKU;
	
	private Double productUnitQty;

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

	private String sourceStockLocationPid;

	private String sourceStockLocationName;

	private String destinationStockLocationPid;

	private String destinationStockLocationName;

	private String referenceInventoryVoucherHeaderPid;

	private Long referenceInventoryVoucherDetailId;

	private String remarks;
	
	private String visitRemarks;

	private List<InventoryVoucherBatchDetailDTO> inventoryVoucherBatchDetails;

	private LocalDateTime createdDate;

	private String accountPid;

	private String accountName;

	private String employeeName;

	public InventoryVoucherDetailDTO() {
	}

	public InventoryVoucherDetailDTO(InventoryVoucherDetail inventoryVoucherDetail) {
		super();
		this.detailId = inventoryVoucherDetail.getId();
		if(inventoryVoucherDetail.getProduct() != null) {
			this.productPid = inventoryVoucherDetail.getProduct().getPid();
			this.productName = inventoryVoucherDetail.getProduct().getName();
			this.productCategory = inventoryVoucherDetail.getProduct().getProductCategory().getName();
			this.productSKU = inventoryVoucherDetail.getProduct().getSku();
			this.productUnitQty = inventoryVoucherDetail.getProduct().getUnitQty();
		}
		this.quantity = inventoryVoucherDetail.getQuantity();
		this.freeQuantity = inventoryVoucherDetail.getFreeQuantity();
		this.sellingRate = inventoryVoucherDetail.getSellingRate();
		this.taxPercentage = inventoryVoucherDetail.getTaxPercentage();
		this.discountPercentage = inventoryVoucherDetail.getDiscountPercentage();
		this.batchNumber = inventoryVoucherDetail.getBatchNumber();
		this.purchaseRate = inventoryVoucherDetail.getPurchaseRate();
		this.mrp = inventoryVoucherDetail.getMrp();
		this.batchDate = inventoryVoucherDetail.getBatchDate();
		this.rowTotal = inventoryVoucherDetail.getRowTotal();
		this.discountAmount = inventoryVoucherDetail.getDiscountAmount();
		this.taxAmount = inventoryVoucherDetail.getTaxAmount();
		this.length = inventoryVoucherDetail.getLength();
		this.width = inventoryVoucherDetail.getWidth();
		this.thickness = inventoryVoucherDetail.getThickness();
		this.size = inventoryVoucherDetail.getSize();
		this.color = inventoryVoucherDetail.getColor();

		if (inventoryVoucherDetail.getInventoryVoucherHeader() != null) {
			this.createdDate = inventoryVoucherDetail.getInventoryVoucherHeader().getCreatedDate();
			// Used in Item Wise Sales
			this.accountPid = inventoryVoucherDetail.getInventoryVoucherHeader().getReceiverAccount().getPid();
			this.accountName = inventoryVoucherDetail.getInventoryVoucherHeader().getReceiverAccount().getName();
			if(inventoryVoucherDetail.getInventoryVoucherHeader().getEmployee() != null) {
				this.employeeName = inventoryVoucherDetail.getInventoryVoucherHeader().getEmployee().getName();	
			}
		}
		if (inventoryVoucherDetail.getSourceStockLocation() != null) {
			this.sourceStockLocationPid = inventoryVoucherDetail.getSourceStockLocation().getPid();
			this.sourceStockLocationName = inventoryVoucherDetail.getSourceStockLocation().getName();
		}
		if (inventoryVoucherDetail.getDestinationStockLocation() != null) {
			this.destinationStockLocationPid = inventoryVoucherDetail.getDestinationStockLocation().getPid();
			this.destinationStockLocationName = inventoryVoucherDetail.getDestinationStockLocation().getName();
		}
		if (inventoryVoucherDetail.getRferenceInventoryVoucherHeader() != null)
			this.referenceInventoryVoucherHeaderPid = inventoryVoucherDetail.getRferenceInventoryVoucherHeader()
					.getPid();
		if (inventoryVoucherDetail.getRferenceInventoryVoucherDetail() != null)
			this.referenceInventoryVoucherDetailId = inventoryVoucherDetail.getRferenceInventoryVoucherDetail().getId();
		this.remarks = inventoryVoucherDetail.getRemarks();
		this.inventoryVoucherBatchDetails = inventoryVoucherDetail.getInventoryVoucherBatchDetails().stream()
				.map(InventoryVoucherBatchDetailDTO::new).collect(Collectors.toList());
	}

	public Long getDetailId() {
		return detailId;
	}

	public void setDetailId(Long detailId) {
		this.detailId = detailId;
	}

	public String getProductPid() {
		return productPid;
	}

	public void setProductPid(String productPid) {
		this.productPid = productPid;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public List<String> getProductGroups() {
		return productGroups;
	}

	public void setProductGroups(List<String> productGroups) {
		this.productGroups = productGroups;
	}

	public String getProductSKU() {
		return productSKU;
	}

	public void setProductSKU(String productSKU) {
		this.productSKU = productSKU;
	}

	public Double getProductUnitQty() {
		return productUnitQty;
	}

	public void setProductUnitQty(Double productUnitQty) {
		this.productUnitQty = productUnitQty;
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

	public String getSourceStockLocationPid() {
		return sourceStockLocationPid;
	}

	public void setSourceStockLocationPid(String sourceStockLocationPid) {
		this.sourceStockLocationPid = sourceStockLocationPid;
	}

	public String getSourceStockLocationName() {
		return sourceStockLocationName;
	}

	public void setSourceStockLocationName(String sourceStockLocationName) {
		this.sourceStockLocationName = sourceStockLocationName;
	}

	public String getDestinationStockLocationPid() {
		return destinationStockLocationPid;
	}

	public void setDestinationStockLocationPid(String destinationStockLocationPid) {
		this.destinationStockLocationPid = destinationStockLocationPid;
	}

	public String getDestinationStockLocationName() {
		return destinationStockLocationName;
	}

	public void setDestinationStockLocationName(String destinationStockLocationName) {
		this.destinationStockLocationName = destinationStockLocationName;
	}

	public String getReferenceInventoryVoucherHeaderPid() {
		return referenceInventoryVoucherHeaderPid;
	}

	public void setReferenceInventoryVoucherHeaderPid(String referenceInventoryVoucherHeaderPid) {
		this.referenceInventoryVoucherHeaderPid = referenceInventoryVoucherHeaderPid;
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

	public List<InventoryVoucherBatchDetailDTO> getInventoryVoucherBatchDetails() {
		return inventoryVoucherBatchDetails;
	}

	public void setInventoryVoucherBatchDetails(List<InventoryVoucherBatchDetailDTO> inventoryVoucherBatchDetails) {
		this.inventoryVoucherBatchDetails = inventoryVoucherBatchDetails;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getAccountPid() {
		return accountPid;
	}

	public void setAccountPid(String accountPid) {
		this.accountPid = accountPid;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getVisitRemarks() {
		return visitRemarks;
	}

	public void setVisitRemarks(String visitRemarks) {
		this.visitRemarks = visitRemarks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((productName == null) ? 0 : productName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InventoryVoucherDetailDTO other = (InventoryVoucherDetailDTO) obj;
		if (productName == null) {
			if (other.productName != null)
				return false;
		} else if (!productName.equals(other.productName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InventoryVoucherDetailDTO [detailId=" + detailId + ", productPid=" + productPid + ", productName="
				+ productName + ", productCategory=" + productCategory + ", productGroups=" + productGroups
				+ ", productSKU=" + productSKU + ", productUnitQty=" + productUnitQty + ", quantity=" + quantity
				+ ", freeQuantity=" + freeQuantity + ", sellingRate=" + sellingRate + ", mrp=" + mrp + ", purchaseRate="
				+ purchaseRate + ", taxPercentage=" + taxPercentage + ", discountPercentage=" + discountPercentage
				+ ", batchNumber=" + batchNumber + ", batchDate=" + batchDate + ", rowTotal=" + rowTotal
				+ ", discountAmount=" + discountAmount + ", taxAmount=" + taxAmount + ", length=" + length + ", width="
				+ width + ", thickness=" + thickness + ", size=" + size + ", color=" + color
				+ ", sourceStockLocationPid=" + sourceStockLocationPid + ", sourceStockLocationName="
				+ sourceStockLocationName + ", destinationStockLocationPid=" + destinationStockLocationPid
				+ ", destinationStockLocationName=" + destinationStockLocationName
				+ ", referenceInventoryVoucherHeaderPid=" + referenceInventoryVoucherHeaderPid
				+ ", referenceInventoryVoucherDetailId=" + referenceInventoryVoucherDetailId + ", remarks=" + remarks
				+ ", visitRemarks=" + visitRemarks + ", inventoryVoucherBatchDetails=" + inventoryVoucherBatchDetails
				+ ", createdDate=" + createdDate + ", accountPid=" + accountPid + ", accountName=" + accountName
				+ ", employeeName=" + employeeName + "]";
	}
	
	
	

}
