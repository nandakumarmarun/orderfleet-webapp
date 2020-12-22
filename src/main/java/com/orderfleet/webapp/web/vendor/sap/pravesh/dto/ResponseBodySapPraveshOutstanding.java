package com.orderfleet.webapp.web.vendor.sap.pravesh.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "docEntry", "orderNum", "docDate", "remarks", "customerId", "customerName", "dealerCode", "dealerName", "sapOrderNo", "orderTotal", "receivedAmount",
		"balance" })
public class ResponseBodySapPraveshOutstanding {

	@JsonProperty("docEntry")
	private long docEntry;
	@JsonProperty("orderNum")
	private String orderNum;
	@JsonProperty("docDate")
	private String docDate;
	@JsonProperty("remarks")
	private String remarks;
	@JsonProperty("customerId")
	private String customerId;
	@JsonProperty("customerName")
	private String customerName;
	@JsonProperty("dealerCode")
	private String dealerCode;
	@JsonProperty("dealerName")
	private String dealerName;
	@JsonProperty("sapOrderNo")
	private String sapOrderNo;
	@JsonProperty("orderTotal")
	private double orderTotal;
	@JsonProperty("receivedAmount")
	private double receivedAmount;
	@JsonProperty("balance")
	private double balance;
	
	@JsonProperty("docEntry")
	public long getDocEntry() {
		return docEntry;
	}
	@JsonProperty("docEntry")
	public void setDocEntry(long docEntry) {
		this.docEntry = docEntry;
	}
	@JsonProperty("orderNum")
	public String getOrderNum() {
		return orderNum;
	}
	@JsonProperty("orderNum")
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	@JsonProperty("docDate")
	public String getDocDate() {
		return docDate;
	}
	@JsonProperty("docDate")
	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}
	@JsonProperty("remarks")
	public String getRemarks() {
		return remarks;
	}
	@JsonProperty("remarks")
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@JsonProperty("customerId")
	public String getCustomerId() {
		return customerId;
	}
	@JsonProperty("customerId")
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	@JsonProperty("customerName")
	public String getCustomerName() {
		return customerName;
	}
	@JsonProperty("customerName")
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	@JsonProperty("dealerCode")
	public String getDealerCode() {
		return dealerCode;
	}
	@JsonProperty("dealerCode")
	public void setDealerCode(String dealerCode) {
		this.dealerCode = dealerCode;
	}
	@JsonProperty("dealerName")
	public String getDealerName() {
		return dealerName;
	}
	@JsonProperty("dealerName")
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	@JsonProperty("sapOrderNo")
	public String getSapOrderNo() {
		return sapOrderNo;
	}
	@JsonProperty("sapOrderNo")
	public void setSapOrderNo(String sapOrderNo) {
		this.sapOrderNo = sapOrderNo;
	}
	@JsonProperty("orderTotal")
	public double getOrderTotal() {
		return orderTotal;
	}
	@JsonProperty("orderTotal")
	public void setOrderTotal(double orderTotal) {
		this.orderTotal = orderTotal;
	}
	@JsonProperty("receivedAmount")
	public double getReceivedAmount() {
		return receivedAmount;
	}
	@JsonProperty("receivedAmount")
	public void setReceivedAmount(double receivedAmount) {
		this.receivedAmount = receivedAmount;
	}
	@JsonProperty("balance")
	public double getBalance() {
		return balance;
	}
	@JsonProperty("balance")
	public void setBalance(double balance) {
		this.balance = balance;
	}
}
