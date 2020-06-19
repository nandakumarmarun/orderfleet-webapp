package com.orderfleet.webapp.web.vendor.sap.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "dbkey", "latitude", "longitude", "location", "customercode", "customername", "customer_ref",
		"postingdate", "valid_until", "docdate", "SalesCommitDate", "remarks", "itemDetails", "Scode", "ordertype",
		"discount", "shipto", "billTo" })
public class SalesOrderMasterSap {

	@JsonProperty("dbkey")
	private int dbKey;
	@JsonProperty("latitude")
	private String latitude;
	@JsonProperty("longitude")
	private String longitude;
	@JsonProperty("location")
	private String location;
	@JsonProperty("customercode")
	private String customerCode;
	@JsonProperty("customername")
	private String customerName;
	@JsonProperty("customer_ref")
	private String customerRef;
	@JsonProperty("postingdate")
	private String postingDate;
	@JsonProperty("valid_until")
	private String validUntil;
	@JsonProperty("docdate")
	private String docDate;
	@JsonProperty("SalesCommitDate")
	private String salesCommitDate;
	@JsonProperty("remarks")
	private String remarks;
	@JsonProperty("itemDetails")
	private List<SalesOrderItemDetailsSap> itemDetails;
	@JsonProperty("Scode")
	private int sCode;
	@JsonProperty("ordertype")
	private String orderType;
	@JsonProperty("discount")
	private double discount;
	@JsonProperty("shipto")
	private String shipTo;
	@JsonProperty("billTo")
	private String billTo;

	@JsonProperty("dbkey")
	public int getDbKey() {
		return dbKey;
	}

	@JsonProperty("dbkey")
	public void setDbKey(int dbKey) {
		this.dbKey = dbKey;
	}

	@JsonProperty("latitude")
	public String getLatitude() {
		return latitude;
	}

	@JsonProperty("latitude")
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	@JsonProperty("longitude")
	public String getLongitude() {
		return longitude;
	}

	@JsonProperty("longitude")
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@JsonProperty("location")
	public String getLocation() {
		return location;
	}

	@JsonProperty("location")
	public void setLocation(String location) {
		this.location = location;
	}

	@JsonProperty("customercode")
	public String getCustomerCode() {
		return customerCode;
	}

	@JsonProperty("customercode")
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	@JsonProperty("customername")
	public String getCustomerName() {
		return customerName;
	}

	@JsonProperty("customername")
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@JsonProperty("customer_ref")
	public String getCustomerRef() {
		return customerRef;
	}

	@JsonProperty("customer_ref")
	public void setCustomerRef(String customerRef) {
		this.customerRef = customerRef;
	}

	@JsonProperty("postingdate")
	public String getPostingDate() {
		return postingDate;
	}

	@JsonProperty("postingdate")
	public void setPostingDate(String postingDate) {
		this.postingDate = postingDate;
	}

	@JsonProperty("valid_until")
	public String getValidUntil() {
		return validUntil;
	}

	@JsonProperty("valid_until")
	public void setValidUntil(String validUntil) {
		this.validUntil = validUntil;
	}

	@JsonProperty("docdate")
	public String getDocDate() {
		return docDate;
	}

	@JsonProperty("docdate")
	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}

	@JsonProperty("SalesCommitDate")
	public String getSalesCommitDate() {
		return salesCommitDate;
	}

	@JsonProperty("SalesCommitDate")
	public void setSalesCommitDate(String salesCommitDate) {
		this.salesCommitDate = salesCommitDate;
	}

	@JsonProperty("remarks")
	public String getRemarks() {
		return remarks;
	}

	@JsonProperty("remarks")
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@JsonProperty("itemDetails")
	public List<SalesOrderItemDetailsSap> getItemDetails() {
		return itemDetails;
	}

	@JsonProperty("itemDetails")
	public void setItemDetails(List<SalesOrderItemDetailsSap> itemDetails) {
		this.itemDetails = itemDetails;
	}

	@JsonProperty("Scode")
	public int getsCode() {
		return sCode;
	}

	@JsonProperty("Scode")
	public void setsCode(int sCode) {
		this.sCode = sCode;
	}

	@JsonProperty("ordertype")
	public String getOrderType() {
		return orderType;
	}

	@JsonProperty("ordertype")
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	@JsonProperty("discount")
	public double getDiscount() {
		return discount;
	}

	@JsonProperty("discount")
	public void setDiscount(double discount) {
		this.discount = discount;
	}

	@JsonProperty("shipto")
	public String getShipTo() {
		return shipTo;
	}

	@JsonProperty("shipto")
	public void setShipTo(String shipTo) {
		this.shipTo = shipTo;
	}

	@JsonProperty("billTo")
	public String getBillTo() {
		return billTo;
	}

	@JsonProperty("billTo")
	public void setBillTo(String billTo) {
		this.billTo = billTo;
	}

	@Override
	public String toString() {
		return "SalesOrderMasterSap [dbKey=" + dbKey + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", location=" + location + ", customerCode=" + customerCode + ", customerName=" + customerName
				+ ", customerRef=" + customerRef + ", postingDate=" + postingDate + ", validUntil=" + validUntil
				+ ", docDate=" + docDate + ", salesCommitDate=" + salesCommitDate + ", remarks=" + remarks
				+ ", itemDetails=" + itemDetails + ", sCode=" + sCode + ", orderType=" + orderType + ", discount="
				+ discount + ", shipTo=" + shipTo + ", billTo=" + billTo + "]";
	}

}
