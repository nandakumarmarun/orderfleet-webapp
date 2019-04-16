package com.orderfleet.webapp.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesOrderVenderDTO {
	
	private String soNo;
	@JsonProperty(value="orderDate")
	private String date;	
	private String customerId;	
	private String executiveId;	
	private String itemId;	
	private Double qty;	
	private Double rate;	
	private Double amt;
	private Double discPer;	
	private Double discAmt;	
	private Double netAmt;	
	private Double addnAmount;	
	private Double dednAmount;	
	private Double billAmount;	
	private Double amountRecd;
	private String inventoryPid;
	
	
	public SalesOrderVenderDTO() {
		
	}


	public SalesOrderVenderDTO(InventoryVoucherDetail inventoryVoucherDetail) {
		super();
		this.customerId = inventoryVoucherDetail.getInventoryVoucherHeader().getReceiverAccount().getAlias();
		this.executiveId = inventoryVoucherDetail.getInventoryVoucherHeader().getEmployee().getAlias();
		
		this.itemId = inventoryVoucherDetail.getProduct().getAlias();
		this.qty = inventoryVoucherDetail.getQuantity();
		this.rate = inventoryVoucherDetail.getSellingRate();
		this.amt = inventoryVoucherDetail.getQuantity() * inventoryVoucherDetail.getSellingRate();
		
		this.date = inventoryVoucherDetail.getInventoryVoucherHeader().getDocumentDate() != null
						?inventoryVoucherDetail.getInventoryVoucherHeader().getDocumentDate().toString():null;
		this.discAmt = inventoryVoucherDetail.getInventoryVoucherHeader().getDocDiscountAmount();
		this.discPer = inventoryVoucherDetail.getDiscountPercentage();
		this.netAmt = inventoryVoucherDetail.getRowTotal();
		this.addnAmount = inventoryVoucherDetail.getTaxAmount();
		this.dednAmount = 0.0;
		this.billAmount = inventoryVoucherDetail.getInventoryVoucherHeader().getDocumentTotal();
		this.amountRecd = 0.0;
		inventoryPid = inventoryVoucherDetail.getInventoryVoucherHeader().getPid();
		
	}

	public String getSoNo() {
		return soNo;
	}

	public void setSoNo(String soNo) {
		this.soNo = soNo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getExecutiveId() {
		return executiveId;
	}

	public void setExecutiveId(String executiveId) {
		this.executiveId = executiveId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Double getQty() {
		return qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public Double getAmt() {
		return amt;
	}

	public void setAmt(Double amt) {
		this.amt = amt;
	}

	public Double getDiscPer() {
		return discPer;
	}

	public void setDiscPer(Double discPer) {
		this.discPer = discPer;
	}

	public Double getDiscAmt() {
		return discAmt;
	}

	public void setDiscAmt(Double discAmt) {
		this.discAmt = discAmt;
	}

	public Double getNetAmt() {
		return netAmt;
	}

	public void setNetAmt(Double netAmt) {
		this.netAmt = netAmt;
	}

	public Double getAddnAmount() {
		return addnAmount;
	}

	public void setAddnAmount(Double addnAmount) {
		this.addnAmount = addnAmount;
	}

	public Double getDednAmount() {
		return dednAmount;
	}

	public void setDednAmount(Double dednAmount) {
		this.dednAmount = dednAmount;
	}

	public Double getBillAmount() {
		return billAmount;
	}

	public void setBillAmount(Double billAmount) {
		this.billAmount = billAmount;
	}

	public Double getAmountRecd() {
		return amountRecd;
	}

	public void setAmountRecd(Double amountRecd) {
		this.amountRecd = amountRecd;
	}

	public String getInventoryPid() {
		return inventoryPid;
	}

	public void setInventoryPid(String inventoryPid) {
		this.inventoryPid = inventoryPid;
	}
	
	
	

}
