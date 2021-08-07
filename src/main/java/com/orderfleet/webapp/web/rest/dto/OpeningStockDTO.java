package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import com.orderfleet.webapp.domain.OpeningStock;

public class OpeningStockDTO {

	private String pid;
	private String batchNumber;
	private String stockLocationPid;
	private String stockLocationName;
	private String productProfilePid;
	private String productProfileName;
	private double quantity;
	private LocalDateTime createdDate;
	private LocalDateTime openingStockDate;
	private boolean activated;
	private LocalDateTime lastModifiedDate;
	
	//private double freeStock;
	private double reservedStock;

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public OpeningStockDTO() {
		super();
	}
	

	

	
	public double getReservedStock() {
		return reservedStock;
	}

	public void setReservedStock(double reservedStock) {
		this.reservedStock = reservedStock;
	}

	public OpeningStockDTO(OpeningStock openingStock) {
		this(openingStock.getPid(), openingStock.getBatchNumber(), openingStock.getStockLocation().getPid(),
				openingStock.getStockLocation().getName(), openingStock.getProductProfile().getPid(),
				openingStock.getProductProfile().getName(), openingStock.getQuantity(), openingStock.getCreatedDate(),
				openingStock.getOpeningStockDate(), openingStock.getLastModifiedDate(),openingStock.getReservedStock());
	}

	public OpeningStockDTO(String pid, String batchNumber, String stockLocationPid, String stockLocationName,
			String productProfilePid, String productProfileName, double quantity, LocalDateTime createdDate,
			LocalDateTime openingStockDate, LocalDateTime lastModifiedDate,double reservedStock) {
		super();
		this.pid = pid;
		this.batchNumber = batchNumber;
		this.stockLocationPid = stockLocationPid;
		this.stockLocationName = stockLocationName;
		this.productProfilePid = productProfilePid;
		this.productProfileName = productProfileName;
		this.quantity = quantity;
		this.createdDate = createdDate;
		this.openingStockDate = openingStockDate;
		this.lastModifiedDate = lastModifiedDate;
		
	
		this.reservedStock=reservedStock;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
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

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getOpeningStockDate() {
		return openingStockDate;
	}

	public void setOpeningStockDate(LocalDateTime openingStockDate) {
		this.openingStockDate = openingStockDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		OpeningStockDTO openingStockDTO = (OpeningStockDTO) o;

		if (!Objects.equals(pid, openingStockDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "OpeningStockDTO [pid=" + pid + ", batchNumber=" + batchNumber + ", stockLocationPid=" + stockLocationPid
				+ ", stockLocationName=" + stockLocationName + ", productProfilePid=" + productProfilePid
				+ ", productProfileName=" + productProfileName + ", quantity=" + quantity + ", createdDate="
				+ createdDate + ", openingStockDate=" + openingStockDate + ", activated=" + activated
				+ ", lastModifiedDate=" + lastModifiedDate + ", reservedStock=" + reservedStock + "]";
	}

	



	

}
