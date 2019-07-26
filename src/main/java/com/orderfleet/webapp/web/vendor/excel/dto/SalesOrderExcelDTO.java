package com.orderfleet.webapp.web.vendor.excel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesOrderExcelDTO {

	private String billNo;
	private String date;
	private String customerCode;
	private String itemCode;
	private Double quantity;
	private Double rate;
	private Double discPer;
	private Double discPrice;
	private Double taxPer;
	private Double total;
	private Double CGSTAmt;
	private Double SGSTAmt;
	private String inventoryPid;
	private String customerName;
	private Double mrp;
	private Double freeQuantity;

	public SalesOrderExcelDTO() {

	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public Double getDiscPer() {
		return discPer;
	}

	public void setDiscPer(Double discPer) {
		this.discPer = discPer;
	}

	public Double getTaxPer() {
		return taxPer;
	}

	public void setTaxPer(Double taxPer) {
		this.taxPer = taxPer;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getCGSTAmt() {
		return CGSTAmt;
	}

	public void setCGSTAmt(Double cGSTAmt) {
		CGSTAmt = cGSTAmt;
	}

	public Double getSGSTAmt() {
		return SGSTAmt;
	}

	public void setSGSTAmt(Double sGSTAmt) {
		SGSTAmt = sGSTAmt;
	}

	public String getInventoryPid() {
		return inventoryPid;
	}

	public void setInventoryPid(String inventoryPid) {
		this.inventoryPid = inventoryPid;
	}

	public Double getDiscPrice() {
		return discPrice;
	}

	public void setDiscPrice(Double discPrice) {
		this.discPrice = discPrice;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Double getMrp() {
		return mrp;
	}

	public void setMrp(Double mrp) {
		this.mrp = mrp;
	}

	public Double getFreeQuantity() {
		return freeQuantity;
	}

	public void setFreeQuantity(Double freeQuantity) {
		this.freeQuantity = freeQuantity;
	}

	@Override
	public String toString() {
		return "SalesOrderExcelDTO [billNo=" + billNo + ", date=" + date + ", customerCode=" + customerCode
				+ ", itemCode=" + itemCode + ", quantity=" + quantity + ", rate=" + rate + ", discPer=" + discPer
				+ ", discPrice=" + discPrice + ", taxPer=" + taxPer + ", total=" + total + ", CGSTAmt=" + CGSTAmt
				+ ", SGSTAmt=" + SGSTAmt + ", inventoryPid=" + inventoryPid + ", customerName=" + customerName
				+ ", mrp=" + mrp + ", freeQuantity=" + freeQuantity + "]";
	}
	
}
