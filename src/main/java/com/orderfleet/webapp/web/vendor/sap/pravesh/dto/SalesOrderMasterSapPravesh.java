package com.orderfleet.webapp.web.vendor.sap.pravesh.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "customerCode", "customerName", "docDate", "docNum", "refNo", "Remarks", "salesOrderDetails" })
public class SalesOrderMasterSapPravesh {

	@JsonProperty("customerCode")
	private String customerCode;
	@JsonProperty("customerName")
	private String customerName;
	@JsonProperty("docDate")
	private String docDate;
	@JsonProperty("docNum")
	private String docNum;
	@JsonProperty("refNo")
	private String refNo;
	@JsonProperty("Remarks")
	private String remarks;
	@JsonProperty("salesOrderDetails")
	private List<SalesOrderItemDetailsSapPravesh> salesOrderDetails;

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

	@JsonProperty("docDate")
	public String getDocDate() {
		return docDate;
	}

	@JsonProperty("docDate")
	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}

	@JsonProperty("docNum")
	public String getDocNum() {
		return docNum;
	}

	@JsonProperty("docNum")
	public void setDocNum(String docNum) {
		this.docNum = docNum;
	}

	@JsonProperty("refNo")
	public String getRefNo() {
		return refNo;
	}

	@JsonProperty("refNo")
	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	@JsonProperty("Remarks")
	public String getRemarks() {
		return remarks;
	}

	@JsonProperty("Remarks")
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@JsonProperty("salesOrderDetails")
	public List<SalesOrderItemDetailsSapPravesh> getSalesOrderDetails() {
		return salesOrderDetails;
	}

	@JsonProperty("salesOrderDetails")
	public void setSalesOrderDetails(List<SalesOrderItemDetailsSapPravesh> salesOrderDetails) {
		this.salesOrderDetails = salesOrderDetails;
	}

	@Override
	public String toString() {
		return "SalesOrderMasterSapPravesh [customerCode=" + customerCode + ", customerName=" + customerName
				+ ", docDate=" + docDate + ", docNum=" + docNum + ", remarks=" + remarks + ", salesOrderDetails="
				+ salesOrderDetails + "]";
	}

}
