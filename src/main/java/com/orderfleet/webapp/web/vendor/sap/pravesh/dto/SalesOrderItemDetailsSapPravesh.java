package com.orderfleet.webapp.web.vendor.sap.pravesh.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "itemcode", "itemname", "quantity", "uprice", "taxcode", "warehousecode", "itemtype",
		"arecieved" })
public class SalesOrderItemDetailsSapPravesh {

	@JsonProperty("itemcode")
	private String itemCode;
	@JsonProperty("itemname")
	private String itemName;
	@JsonProperty("quantity")
	private String quantity;
	@JsonProperty("uprice")
	private String uPrice;
	@JsonProperty("taxcode")
	private String taxCode;
	@JsonProperty("warehousecode")
	private String wareHouseCode;
	@JsonProperty("itemtype")
	private String itemtype;
	@JsonProperty("arecieved")
	private String arecieved;

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
	public String getQuantity() {
		return quantity;
	}

	@JsonProperty("quantity")
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	@JsonProperty("uprice")
	public String getuPrice() {
		return uPrice;
	}

	@JsonProperty("uprice")
	public void setuPrice(String uPrice) {
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

	@JsonProperty("itemtype")
	public String getItemtype() {
		return itemtype;
	}

	@JsonProperty("itemtype")
	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	@JsonProperty("arecieved")
	public String getArecieved() {
		return arecieved;
	}

	@JsonProperty("arecieved")
	public void setArecieved(String arecieved) {
		this.arecieved = arecieved;
	}

	@Override
	public String toString() {
		return "SalesOrderItemDetailsSap [itemCode=" + itemCode + ", itemName=" + itemName + ", quantity=" + quantity
				+ ", uPrice=" + uPrice + ", taxCode=" + taxCode + ", wareHouseCode=" + wareHouseCode + ", itemtype="
				+ itemtype + ", arecieved=" + arecieved + "]";
	}

}
