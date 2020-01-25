package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.orderfleet.webapp.domain.InventoryVoucherDetail;

/**
 * A DTO For Generate Sales-Order-Items To Client APP.
 *
 * @author Sarath
 * @since Oct 28, 2016
 */
public class SalesOrderItemDTO {

	private Long itemId;
	private String pid;
	private String itemName;
	private String unit;
	private double itemDiscount;
	private double rateOfVat;
	private BigDecimal itemRate;
	private double itemStock;
	private double quantity;
	private double unitQuantity;
	private double itemFreeQuantity;
	private double sellingRate;
	private double mrp;
	private double taxPercentage;
	private double discountPercentage;
	private double rowTotal;
	private double discountAmount;
	private double taxAmount;
	private LocalDateTime batchDate;
	private ProductProfileDTO productProfileDTO;
	private List<InventoryVoucherBatchDetailDTO> inventoryVoucherBatchDetailsDTO;
	private List<OpeningStockDTO> openingStockDTOs;

	private Long detailId;
	private String productPid;
	private String productName;
	private String trimChar;
	private double freeQuantity;
	private BigDecimal purchaseRate;
	private String batchNumber;
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
	private String stockLocationName;
	private double updatedQuantity;
	private double updatedRowTotal;
	private boolean updateStatus;

	public SalesOrderItemDTO() {
		super();
	}

	public SalesOrderItemDTO(InventoryVoucherDetail inventoryVoucherDetail) {
		super();
		this.itemId = inventoryVoucherDetail.getId();
		// this.pid = inventoryVoucherDetail.get;
		this.itemName = inventoryVoucherDetail.getProduct().getName();
		this.unit = inventoryVoucherDetail.getProduct().getSku();
		this.itemDiscount = inventoryVoucherDetail.getDiscountPercentage();
		this.rateOfVat = inventoryVoucherDetail.getProduct().getTaxRate();
		this.itemRate = inventoryVoucherDetail.getProduct().getPrice();
		// this.itemStock = inventoryVoucherDetail.getProduct().getitemStock;
		this.quantity = inventoryVoucherDetail.getQuantity();
		double unitQty = 1;
		if (inventoryVoucherDetail.getProduct().getUnitQty() != null
				&& inventoryVoucherDetail.getProduct().getUnitQty() > 0) {
			unitQty = inventoryVoucherDetail.getProduct().getUnitQty();
		}
		this.unitQuantity = unitQty;

		this.itemFreeQuantity = inventoryVoucherDetail.getFreeQuantity();
		this.sellingRate = inventoryVoucherDetail.getSellingRate();
		// this.mrp = mrp;
		this.trimChar = inventoryVoucherDetail.getProduct().getTrimChar();
		this.taxPercentage = inventoryVoucherDetail.getTaxPercentage();
		this.discountPercentage = inventoryVoucherDetail.getDiscountPercentage();
		this.rowTotal = inventoryVoucherDetail.getRowTotal();
		this.discountAmount = inventoryVoucherDetail.getDiscountAmount();
		this.taxAmount = inventoryVoucherDetail.getTaxAmount();
		this.batchDate = inventoryVoucherDetail.getInventoryVoucherHeader().getDocumentDate();
		ProductProfileDTO productProfileDTO = new ProductProfileDTO(inventoryVoucherDetail.getProduct());
		this.productProfileDTO = productProfileDTO;

		this.detailId = inventoryVoucherDetail.getId();
		this.productPid = inventoryVoucherDetail.getProduct().getPid();
		this.productName = inventoryVoucherDetail.getProduct().getName();
		this.freeQuantity = inventoryVoucherDetail.getFreeQuantity();
		this.batchNumber = inventoryVoucherDetail.getBatchNumber();
		this.purchaseRate = inventoryVoucherDetail.getPurchaseRate();
		this.length = inventoryVoucherDetail.getLength();
		this.width = inventoryVoucherDetail.getWidth();
		this.thickness = inventoryVoucherDetail.getThickness();
		this.size = inventoryVoucherDetail.getSize();
		this.color = inventoryVoucherDetail.getColor();
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
		this.inventoryVoucherBatchDetailsDTO = inventoryVoucherDetail.getInventoryVoucherBatchDetails().stream()
				.map(InventoryVoucherBatchDetailDTO::new).collect(Collectors.toList());
		this.updatedQuantity = inventoryVoucherDetail.getUpdatedQuantity();
		this.updatedRowTotal = inventoryVoucherDetail.getUpdatedRowTotal();
		this.updateStatus = inventoryVoucherDetail.getUpdatedStatus();
		if(inventoryVoucherDetail.getUpdatedStatus()) {
			this.rowTotal = inventoryVoucherDetail.getUpdatedRowTotal();
			this.quantity = inventoryVoucherDetail.getUpdatedQuantity();
		}
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public double getItemDiscount() {
		return itemDiscount;
	}

	public void setItemDiscount(double itemDiscount) {
		this.itemDiscount = itemDiscount;
	}

	public double getRateOfVat() {
		return rateOfVat;
	}

	public void setRateOfVat(double rateOfVat) {
		this.rateOfVat = rateOfVat;
	}

	public BigDecimal getItemRate() {
		return itemRate;
	}

	public void setItemRate(BigDecimal itemRate) {
		this.itemRate = itemRate;
	}

	public double getItemStock() {
		return itemStock;
	}

	public void setItemStock(double itemStock) {
		this.itemStock = itemStock;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getUnitQuantity() {
		return unitQuantity;
	}

	public void setUnitQuantity(double unitQuantity) {
		this.unitQuantity = unitQuantity;
	}

	public double getItemFreeQuantity() {
		return itemFreeQuantity;
	}

	public void setItemFreeQuantity(double itemFreeQuantity) {
		this.itemFreeQuantity = itemFreeQuantity;
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

	public LocalDateTime getBatchDate() {
		return batchDate;
	}

	public void setBatchDate(LocalDateTime batchDate) {
		this.batchDate = batchDate;
	}

	public ProductProfileDTO getProductProfileDTO() {
		return productProfileDTO;
	}

	public void setProductProfileDTO(ProductProfileDTO productProfileDTO) {
		this.productProfileDTO = productProfileDTO;
	}

	public List<InventoryVoucherBatchDetailDTO> getInventoryVoucherBatchDetailsDTO() {
		return inventoryVoucherBatchDetailsDTO;
	}

	public void setInventoryVoucherBatchDetailsDTO(
			List<InventoryVoucherBatchDetailDTO> inventoryVoucherBatchDetailsDTO) {
		this.inventoryVoucherBatchDetailsDTO = inventoryVoucherBatchDetailsDTO;
	}

	public List<OpeningStockDTO> getOpeningStockDTOs() {
		return openingStockDTOs;
	}

	public void setOpeningStockDTOs(List<OpeningStockDTO> openingStockDTOs) {
		this.openingStockDTOs = openingStockDTOs;
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

	public double getFreeQuantity() {
		return freeQuantity;
	}

	public void setFreeQuantity(double freeQuantity) {
		this.freeQuantity = freeQuantity;
	}

	public BigDecimal getPurchaseRate() {
		return purchaseRate;
	}

	public void setPurchaseRate(BigDecimal purchaseRate) {
		this.purchaseRate = purchaseRate;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
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

	public String getTrimChar() {
		return trimChar;
	}

	public void setTrimChar(String trimChar) {
		this.trimChar = trimChar;
	}

	public String getStockLocationName() {
		return stockLocationName;
	}

	public void setStockLocationName(String stockLocationName) {
		this.stockLocationName = stockLocationName;
	}
	
	public double getUpdatedQuantity() {
		return updatedQuantity;
	}

	public void setUpdatedQuantity(double updatedQuantity) {
		this.updatedQuantity = updatedQuantity;
	}

	public double getUpdatedRowTotal() {
		return updatedRowTotal;
	}

	public void setUpdatedRowTotal(double updatedRowTotal) {
		this.updatedRowTotal = updatedRowTotal;
	}

	public boolean isUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(boolean updateStatus) {
		this.updateStatus = updateStatus;
	}

	@Override
	public String toString() {
		return "SalesOrderItemDTO [itemId=" + itemId + ", pid=" + pid + ", itemName=" + itemName + ", unit=" + unit
				+ ", itemDiscount=" + itemDiscount + ", rateOfVat=" + rateOfVat + ", itemRate=" + itemRate
				+ ", itemStock=" + itemStock + ", quantity=" + quantity + ", itemFreeQuantity=" + itemFreeQuantity
				+ ", sellingRate=" + sellingRate + ", mrp=" + mrp + ", taxPercentage=" + taxPercentage
				+ ", discountPercentage=" + discountPercentage + ", rowTotal=" + rowTotal + ", discountAmount="
				+ discountAmount + ", taxAmount=" + taxAmount + ", batchDate=" + batchDate + "]";
	}

}
