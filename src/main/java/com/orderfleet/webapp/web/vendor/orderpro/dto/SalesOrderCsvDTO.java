package com.orderfleet.webapp.web.vendor.orderpro.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesOrderCsvDTO {

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

	public SalesOrderCsvDTO() {

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

}
