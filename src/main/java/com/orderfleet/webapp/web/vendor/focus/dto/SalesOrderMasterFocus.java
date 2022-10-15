package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Doc_No", "Date", "CustomerAc", "Business_Unit", "Fiscal_Year", "Employee", "Route_Code", "IsIGST",
		"Narration", "Delivery_Site" })
public class SalesOrderMasterFocus {

	@JsonProperty("Doc_No")
	private String docNo;

	@JsonProperty("Date")
	private String date;

	@JsonProperty("CustomerAc")
	private String customerAc;

	@JsonProperty("Business_Unit")
	private String businessUnit;

	@JsonProperty("Fiscal_Year")
	private String fiscalYear;

	@JsonProperty("Employee")
	private String employee;

	@JsonProperty("Route_Code")
	private String routeCode;

	@JsonProperty("IsIGST")
	private String isIGST;

	@JsonProperty("Narration")
	private String Narration;

	@JsonProperty("Delivery_Site")
	private String Delivery_Site;

	@JsonProperty("SNRDOCID")
	private String snrDocId;

	@JsonProperty("SNRDOCDATE")
	private String snrDocDate;

	@JsonProperty("Doc_No")
	public String getdocNo() {
		return docNo;
	}

	@JsonProperty("Doc_No")
	public void setdocNo(String docNo) {
		this.docNo = docNo;
	}

	@JsonProperty("Date")
	public String getDate() {
		return date;
	}

	@JsonProperty("Date")
	public void setDate(String date) {
		this.date = date;
	}

	@JsonProperty("CustomerAc")
	public String getCustomerAc() {
		return customerAc;
	}

	@JsonProperty("CustomerAc")
	public void setCustomerAc(String customerAc) {
		this.customerAc = customerAc;
	}

	@JsonProperty("Business_Unit")
	public String getBusinessUnit() {
		return businessUnit;
	}

	@JsonProperty("Business_Unit")
	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	@JsonProperty("Fiscal_Year")
	public String getFiscalYear() {
		return fiscalYear;
	}

	@JsonProperty("Fiscal_Year")
	public void setFiscalYear(String fiscalYear) {
		this.fiscalYear = fiscalYear;
	}

	@JsonProperty("Employee")
	public String getEmployee() {
		return employee;
	}

	@JsonProperty("Employee")
	public void setEmployee(String employee) {
		this.employee = employee;
	}

	@JsonProperty("Route_Code")
	public String getRouteCode() {
		return routeCode;
	}

	@JsonProperty("Route_Code")
	public void setRouteCode(String routeCode) {
		this.routeCode = routeCode;
	}

	@JsonProperty("IsIGST")
	public String getIsIGST() {
		return isIGST;
	}

	@JsonProperty("IsIGST")
	public void setIsIGST(String isIGST) {
		this.isIGST = isIGST;
	}

	@JsonProperty("Narration")
	public String getNarration() {
		return Narration;
	}

	@JsonProperty("Narration")
	public void setNarration(String narration) {
		Narration = narration;
	}

	@JsonProperty("Delivery_Site")
	public String getDelivery_Site() {
		return Delivery_Site;
	}

	@JsonProperty("Delivery_Site")
	public void setDelivery_Site(String delivery_Site) {
		Delivery_Site = delivery_Site;
	}

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public String getSnrDocId() {
		return snrDocId;
	}

	public void setSnrDocId(String snrDocId) {
		this.snrDocId = snrDocId;
	}

	public String getSnrDocDate() {
		return snrDocDate;
	}

	public void setSnrDocDate(String snrDocDate) {
		this.snrDocDate = snrDocDate;
	}

	@Override
	public String toString() {
		return "SalesOrderMasterFocus [docNo=" + docNo + ", date=" + date + ", customerAc=" + customerAc
				+ ", businessUnit=" + businessUnit + ", fiscalYear=" + fiscalYear + ", employee=" + employee
				+ ", routeCode=" + routeCode + ", isIGST=" + isIGST + ", Narration=" + Narration + ", Delivery_Site="
				+ Delivery_Site + "]";
	}
}
