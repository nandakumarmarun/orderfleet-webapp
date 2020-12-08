package com.orderfleet.webapp.web.vendor.sap.pravesh.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "ReceiptNo", "customerCode", "customerName", "DealerCode", "DealerName", "OrderNo", "ReceiptDate",
		"RefNo", "PayMode", "PayRefNo", "Amount", "SalesMan", "Remarks" })
public class ReceiptSapPravesh {

	@JsonProperty("ReceiptNo")
	private long receiptNo;
	@JsonProperty("customerCode")
	private String customerCode;
	@JsonProperty("customerName")
	private String customerName;
	@JsonProperty("DealerCode")
	private String dealerCode;
	@JsonProperty("DealerName")
	private String dealerName;
	@JsonProperty("OrderNo")
	private String orderNo;
	@JsonProperty("ReceiptDate")
	private String receiptDate;
	@JsonProperty("RefNo")
	private String refNo;
	@JsonProperty("PayMode")
	private String payMode;
	@JsonProperty("PayRefNo")
	private String payRefNo;
	@JsonProperty("Amount")
	private double amount;
	@JsonProperty("SalesMan")
	private String salesMan;
	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("ReceiptNo")
	public long getReceiptNo() {
		return receiptNo;
	}

	@JsonProperty("ReceiptNo")
	public void setReceiptNo(long receiptNo) {
		this.receiptNo = receiptNo;
	}

	@JsonProperty("customerCode")
	public String getCustomerCode() {
		return customerCode;
	}

	@JsonProperty("customerCode")
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	@JsonProperty("customerName")
	public String getCustomerName() {
		return customerName;
	}

	@JsonProperty("customerName")
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@JsonProperty("DealerCode")
	public String getDealerCode() {
		return dealerCode;
	}

	@JsonProperty("DealerCode")
	public void setDealerCode(String dealerCode) {
		this.dealerCode = dealerCode;
	}

	@JsonProperty("DealerName")
	public String getDealerName() {
		return dealerName;
	}

	@JsonProperty("DealerName")
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	@JsonProperty("OrderNo")
	public String getOrderNo() {
		return orderNo;
	}

	@JsonProperty("OrderNo")
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@JsonProperty("ReceiptDate")
	public String getReceiptDate() {
		return receiptDate;
	}

	@JsonProperty("ReceiptDate")
	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}

	@JsonProperty("RefNo")
	public String getRefNo() {
		return refNo;
	}

	@JsonProperty("RefNo")
	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	@JsonProperty("PayMode")
	public String getPayMode() {
		return payMode;
	}

	@JsonProperty("PayMode")
	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	@JsonProperty("PayRefNo")
	public String getPayRefNo() {
		return payRefNo;
	}

	@JsonProperty("PayRefNo")
	public void setPayRefNo(String payRefNo) {
		this.payRefNo = payRefNo;
	}

	@JsonProperty("Amount")
	public double getAmount() {
		return amount;
	}

	@JsonProperty("Amount")
	public void setAmount(double amount) {
		this.amount = amount;
	}

	@JsonProperty("SalesMan")
	public String getSalesMan() {
		return salesMan;
	}

	@JsonProperty("SalesMan")
	public void setSalesMan(String salesMan) {
		this.salesMan = salesMan;
	}

	@JsonProperty("Remarks")
	public String getRemarks() {
		return remarks;
	}

	@JsonProperty("Remarks")
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		return "ReceiptSapPravesh [receiptNo=" + receiptNo + ", customerCode=" + customerCode + ", customerName="
				+ customerName + ", receiptDate=" + receiptDate + ", refNo=" + refNo + ", payMode=" + payMode
				+ ", payRefNo=" + payRefNo + ", amount=" + amount + ", salesMan=" + salesMan + ", remarks=" + remarks
				+ "]";
	}

}
