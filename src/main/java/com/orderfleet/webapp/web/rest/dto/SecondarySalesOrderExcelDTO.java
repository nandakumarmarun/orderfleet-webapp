package com.orderfleet.webapp.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SecondarySalesOrderExcelDTO {

	private String billNo;
	private String date;
	private String receiverName;
	private String supplierName;
	private String supplierEmail;
	private String itemName;
	private Double quantity;
	private Double rate;
	private Double discPer;
	private Double discPrice;
	private Double taxPer;
	private Double total;
	private Double CGSTAmt;
	private Double SGSTAmt;
	private String inventoryPid;
	private String employeeName;
	private String refDocNo;
	private Double freeQuantity;
	private Long companyId;

	public SecondarySalesOrderExcelDTO() {

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

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSupplierEmail() {
		return supplierEmail;
	}

	public void setSupplierEmail(String supplierEmail) {
		this.supplierEmail = supplierEmail;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
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

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getRefDocNo() {
		return refDocNo;
	}

	public void setRefDocNo(String refDocNo) {
		this.refDocNo = refDocNo;
	}

	public Double getFreeQuantity() {
		return freeQuantity;
	}

	public void setFreeQuantity(Double freeQuantity) {
		this.freeQuantity = freeQuantity;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

}
