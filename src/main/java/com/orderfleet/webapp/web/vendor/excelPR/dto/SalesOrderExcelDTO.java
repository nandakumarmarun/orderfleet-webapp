package com.orderfleet.webapp.web.vendor.excelPR.dto;

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
	private Double mrp;
	private Double rate;
	private Double discPer;
	private Double discPrice;
	private Double compDiscAmt;
	private Double freeQuantity;
	private Double caseValue;
	private String employeeCode;
	private Double taxPer;
	private Double total;
	private Double CGSTAmt;
	private Double SGSTAmt;
	private String inventoryPid;
	private Double kfcAmt;
	private Double kfcPer;
	private String remarks;
	private String customerName;

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

	public Double getCompDiscAmt() {
		return compDiscAmt;
	}

	public void setCompDiscAmt(Double compDiscAmt) {
		this.compDiscAmt = compDiscAmt;
	}

	public Double getCaseValue() {
		return caseValue;
	}

	public void setCaseValue(Double caseValue) {
		this.caseValue = caseValue;
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public Double getKfcAmt() {
		return kfcAmt;
	}

	public void setKfcAmt(Double kfcAmt) {
		this.kfcAmt = kfcAmt;
	}

	public Double getKfcPer() {
		return kfcPer;
	}

	public void setKfcPer(Double kfcPer) {
		this.kfcPer = kfcPer;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		return "SalesOrderExcelDTO [billNo=" + billNo + ", date=" + date + ", customerCode=" + customerCode
				+ ", itemCode=" + itemCode + ", quantity=" + quantity + ", mrp=" + mrp + ", rate=" + rate + ", discPer="
				+ discPer + ", discPrice=" + discPrice + ", compDiscAmt=" + compDiscAmt + ", freeQuantity="
				+ freeQuantity + ", caseValue=" + caseValue + ", employeeCode=" + employeeCode + ", taxPer=" + taxPer
				+ ", total=" + total + ", CGSTAmt=" + CGSTAmt + ", SGSTAmt=" + SGSTAmt + ", inventoryPid="
				+ inventoryPid + ", kfcAmt=" + kfcAmt + ", kfcPer=" + kfcPer + ", remarks=" + remarks
				+ ", customerName=" + customerName + "]";
	}

}
