package com.orderfleet.webapp.web.vendor.sap.pravesh.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "ItemCode", "ItemName", "LineNum", "Quantity", "TotalAmount", "UnitPrice" })
public class SalesOrderItemDetailsSapPravesh {

	@JsonProperty("ItemCode")
	private String itemCode;
	@JsonProperty("ItemName")
	private String itemName;
	@JsonProperty("LineNum")
	private long lineNo;
	@JsonProperty("Quantity")
	private double quantity;
	@JsonProperty("TotalAmount")
	private double totalAmount;
	@JsonProperty("UnitPrice")
	private double unitPrice;

	@JsonProperty("ItemCode")
	public String getItemCode() {
		return itemCode;
	}

	@JsonProperty("ItemCode")
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	@JsonProperty("ItemName")
	public String getItemName() {
		return itemName;
	}

	@JsonProperty("ItemName")
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	@JsonProperty("LineNum")
	public long getLineNo() {
		return lineNo;
	}

	@JsonProperty("LineNum")
	public void setLineNo(long lineNo) {
		this.lineNo = lineNo;
	}

	@JsonProperty("Quantity")
	public double getQuantity() {
		return quantity;
	}

	@JsonProperty("Quantity")
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	@JsonProperty("TotalAmount")
	public double getTotalAmount() {
		return totalAmount;
	}

	@JsonProperty("TotalAmount")
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	@JsonProperty("UnitPrice")
	public double getUnitPrice() {
		return unitPrice;
	}

	@JsonProperty("UnitPrice")
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	@Override
	public String toString() {
		return "SalesOrderItemDetailsSapPravesh [itemCode=" + itemCode + ", itemName=" + itemName + ", lineNo=" + lineNo
				+ ", quantity=" + quantity + ", totalAmount=" + totalAmount + ", unitPrice=" + unitPrice + "]";
	}

}
