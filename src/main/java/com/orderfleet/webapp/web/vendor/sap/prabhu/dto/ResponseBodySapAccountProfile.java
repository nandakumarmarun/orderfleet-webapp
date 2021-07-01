package com.orderfleet.webapp.web.vendor.sap.prabhu.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "number", "name", "outStandingAmount", "status", "address", "city", "phone", "email",
		"location", "divisionId", "divisionName" })
public class ResponseBodySapAccountProfile {

	@JsonProperty("id")
	private long id;
	@JsonProperty("number")
	private String number;
	@JsonProperty("name")
	private String name;
	@JsonProperty("outStandingAmount")
	private double outStandingAmount;
	@JsonProperty("status")
	private boolean status;
	@JsonProperty("address")
	private String address;
	@JsonProperty("city")
	private String city;
	@JsonProperty("phone")
	private String phone;
	@JsonProperty("email")
	private String email;
	@JsonProperty("location")
	private String location;
	@JsonProperty("divisionId")
	private String divisionId;
	@JsonProperty("divisionName")
	private String divisionName;

	@JsonProperty("id")
	public long getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(long id) {
		this.id = id;
	}

	@JsonProperty("number")
	public String getNumber() {
		return number;
	}

	@JsonProperty("number")
	public void setNumber(String number) {
		this.number = number;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("outStandingAmount")
	public double getOutStandingAmount() {
		return outStandingAmount;
	}

	@JsonProperty("outStandingAmount")
	public void setOutStandingAmount(double outStandingAmount) {
		this.outStandingAmount = outStandingAmount;
	}

	@JsonProperty("status")
	public boolean getStatus() {
		return status;
	}

	@JsonProperty("status")
	public void setStatus(boolean status) {
		this.status = status;
	}

	@JsonProperty("address")
	public String getAddress() {
		return address;
	}

	@JsonProperty("address")
	public void setAddress(String address) {
		this.address = address;
	}

	@JsonProperty("city")
	public String getCity() {
		return city;
	}

	@JsonProperty("city")
	public void setCity(String city) {
		this.city = city;
	}

	@JsonProperty("phone")
	public String getPhone() {
		return phone;
	}

	@JsonProperty("phone")
	public void setPhone(String phone) {
		this.phone = phone;
	}

	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@JsonProperty("email")
	public void setEmail(String email) {
		this.email = email;
	}

	@JsonProperty("location")
	public String getLocation() {
		return location;
	}

	@JsonProperty("location")
	public void setLocation(String location) {
		this.location = location;
	}

	@JsonProperty("divisionId")
	public String getDivisionId() {
		return divisionId;
	}

	@JsonProperty("divisionId")
	public void setDivisionId(String divisionId) {
		this.divisionId = divisionId;
	}

	@JsonProperty("divisionName")
	public String getDivisionName() {
		return divisionName;
	}

	@JsonProperty("divisionName")
	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonPropertyOrder({ "Address", "Cellular", "Email", "SaleEmployeeCode", "SaleEmployeeName", "str1", "str2" })
//public class ResponseBodySapAccountProfile {
//
//	@JsonProperty("Address")
//	private String address;
//	@JsonProperty("Cellular")
//	private String cellular;
//	@JsonProperty("Email")
//	private String email;
//	@JsonProperty("SaleEmployeeCode")
//	private String salesEmployeeCode;
//	@JsonProperty("SaleEmployeeName")
//	private String salesEmployeeName;
//	@JsonProperty("str1")
//	private String str1;
//	@JsonProperty("str2")
//	private String str2;
//
//	@JsonProperty("Address")
//	public String getAddress() {
//		return address;
//	}
//
//	@JsonProperty("Address")
//	public void setAddress(String address) {
//		this.address = address;
//	}
//
//	@JsonProperty("Cellular")
//	public String getCellular() {
//		return cellular;
//	}
//
//	@JsonProperty("Cellular")
//	public void setCellular(String cellular) {
//		this.cellular = cellular;
//	}
//
//	@JsonProperty("Email")
//	public String getEmail() {
//		return email;
//	}
//
//	@JsonProperty("Email")
//	public void setEmail(String email) {
//		this.email = email;
//	}
//
//	@JsonProperty("SaleEmployeeCode")
//	public String getSalesEmployeeCode() {
//		return salesEmployeeCode;
//	}
//
//	@JsonProperty("SaleEmployeeCode")
//	public void setSalesEmployeeCode(String salesEmployeeCode) {
//		this.salesEmployeeCode = salesEmployeeCode;
//	}
//
//	@JsonProperty("SaleEmployeeName")
//	public String getSalesEmployeeName() {
//		return salesEmployeeName;
//	}
//
//	@JsonProperty("SaleEmployeeName")
//	public void setSalesEmployeeName(String salesEmployeeName) {
//		this.salesEmployeeName = salesEmployeeName;
//	}
//
//	@JsonProperty("str1")
//	public String getStr1() {
//		return str1;
//	}
//
//	@JsonProperty("str1")
//	public void setStr1(String str1) {
//		this.str1 = str1;
//	}
//
//	@JsonProperty("str2")
//	public String getStr2() {
//		return str2;
//	}
//
//	@JsonProperty("str2")
//	public void setStr2(String str2) {
//		this.str2 = str2;
//	}

}
