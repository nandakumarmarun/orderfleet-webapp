package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.InventoryVoucherBatchDetail;

/**
 * A DTO for the InventoryVoucherBatchDetailDTO entity.
 *
 * @author Sarath
 * @since Dec 7, 2016
 */
public class InventoryVoucherBatchDetailDTO {

	private Long inventoryVoucherDetailId;

	private String productProfilePid;

	private String productProfileName;
	
	private String batchNumber;

	private LocalDateTime batchDate;

	private String remarks;

	private double quantity;

	private String stockLocationPid;

	private String stockLocationName;

	public InventoryVoucherBatchDetailDTO() {
		super();
	}

	public InventoryVoucherBatchDetailDTO(InventoryVoucherBatchDetail inventoryVoucherBatchDetail) {
		super();
		if(inventoryVoucherBatchDetail.getInventoryVoucherDetail() != null){
			this.inventoryVoucherDetailId = inventoryVoucherBatchDetail.getInventoryVoucherDetail().getId();
		}
		this.productProfilePid = inventoryVoucherBatchDetail.getProductProfile().getPid();
		this.productProfileName = inventoryVoucherBatchDetail.getProductProfile().getName();
		this.batchNumber = inventoryVoucherBatchDetail.getBatchNumber();
		this.batchDate = inventoryVoucherBatchDetail.getBatchDate();
		this.remarks = inventoryVoucherBatchDetail.getRemarks();
		this.quantity = inventoryVoucherBatchDetail.getQuantity();
		if (inventoryVoucherBatchDetail.getStockLocation() != null) {
			this.stockLocationPid = inventoryVoucherBatchDetail.getStockLocation().getPid();
			this.stockLocationName = inventoryVoucherBatchDetail.getStockLocation().getName();
		}
	}

	public Long getInventoryVoucherDetailId() {
		return inventoryVoucherDetailId;
	}

	public void setInventoryVoucherDetailId(Long inventoryVoucherDetailId) {
		this.inventoryVoucherDetailId = inventoryVoucherDetailId;
	}

	public String getProductProfilePid() {
		return productProfilePid;
	}

	public void setProductProfilePid(String productProfilePid) {
		this.productProfilePid = productProfilePid;
	}

	public String getProductProfileName() {
		return productProfileName;
	}

	public void setProductProfileName(String productProfileName) {
		this.productProfileName = productProfileName;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public String getStockLocationPid() {
		return stockLocationPid;
	}

	public void setStockLocationPid(String stockLocationPid) {
		this.stockLocationPid = stockLocationPid;
	}

	public String getStockLocationName() {
		return stockLocationName;
	}

	public void setStockLocationName(String stockLocationName) {
		this.stockLocationName = stockLocationName;
	}

	
	@Override
	public String toString() {
		return "InventoryVoucherBatchDetailDTO [inventoryVoucherDetailId=" + inventoryVoucherDetailId
				+ ", productProfilePid=" + productProfilePid + ", productProfileName=" + productProfileName
				+ ", batchNumber=" + batchNumber + ", batchDate=" + batchDate + ", remarks=" + remarks + ", quantity="
				+ quantity + "]";
	}

	

}
