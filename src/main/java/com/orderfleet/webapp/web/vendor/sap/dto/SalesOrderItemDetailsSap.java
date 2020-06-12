package com.orderfleet.webapp.web.vendor.sap.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "itemcode", "itemname", "quantity", "uprice", "taxcode", "warehousecode" })
public class SalesOrderItemDetailsSap {

	@JsonProperty("itemcode")
	private String itemCode;
	@JsonProperty("itemname")
	private String itemName;
	@JsonProperty("quantity")
	private double quantity;
	@JsonProperty("uprice")
	private double uPrice;
	@JsonProperty("taxcode")
	private String taxCode;
	@JsonProperty("warehousecode")
	private String wareHouseCode;

	@JsonProperty("itemcode")
	public String getItemCode() {
		return itemCode;
	}

	@JsonProperty("itemcode")
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	@JsonProperty("itemname")
	public String getItemName() {
		return itemName;
	}

	@JsonProperty("itemname")
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	@JsonProperty("quantity")
	public double getQuantity() {
		return quantity;
	}

	@JsonProperty("quantity")
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	@JsonProperty("uprice")
	public double getuPrice() {
		return uPrice;
	}

	@JsonProperty("uprice")
	public void setuPrice(double uPrice) {
		this.uPrice = uPrice;
	}

	@JsonProperty("taxcode")
	public String getTaxCode() {
		return taxCode;
	}

	@JsonProperty("taxcode")
	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	@JsonProperty("warehousecode")
	public String getWareHouseCode() {
		return wareHouseCode;
	}

	@JsonProperty("warehousecode")
	public void setWareHouseCode(String wareHouseCode) {
		this.wareHouseCode = wareHouseCode;
	}

	@Override
	public String toString() {
		return "SalesOrderItemDetailsSap [itemCode=" + itemCode + ", itemName=" + itemName + ", quantity=" + quantity
				+ "]";
	}

}
