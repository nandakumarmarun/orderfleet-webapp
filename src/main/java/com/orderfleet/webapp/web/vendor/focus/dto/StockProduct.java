package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Available_Stock", "Item_Code", "Item_Name" , "Warehouse_Code", "Warehouse_Name" })
public class StockProduct {
	@JsonProperty("Available_Stock")	
	 private double availableStock;
	@JsonProperty("Item_Code")
	 private String itemCode;
	@JsonProperty("Item_Name")
	 private String itemName;
	@JsonProperty("Warehouse_Code")
	 private String warehouseCode;
	@JsonProperty("Warehouse_Name")
	 private String warehouseName;
	 
	 @JsonProperty("Available_Stock")
	public double getAvailableStock() {
		return availableStock;
	}
	 @JsonProperty("Available_Stock")
	public void setAvailableStock(double availableStock) {
		this.availableStock = availableStock;
	}
	 @JsonProperty("Item_Code")
	public String getItemCode() {
		return itemCode;
	}
	 @JsonProperty("Item_Code")
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	 @JsonProperty("Item_Name")
	public String getItemName() {
		return itemName;
	}
	 @JsonProperty("Item_Name")
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	 @JsonProperty("Warehouse_Code")
	public String getWarehouseCode() {
		return warehouseCode;
	}
	 @JsonProperty("Warehouse_Code")
	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}
		@JsonProperty("Warehouse_Name")
	public String getWarehouseName() {
		return warehouseName;
	}
		@JsonProperty("Warehouse_Name")
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

}
