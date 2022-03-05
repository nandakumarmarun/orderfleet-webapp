package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Item_Code","Warehouse_Code" })
public class StockRequstFocus {
@JsonProperty("Item_Code")	
 private String itemCode;

@JsonProperty("Warehouse_Code")	
 private String warehouseCode;
@JsonProperty("Item_Code")	
public String getItemCode() {
	return itemCode;
}
@JsonProperty("Item_Code")	
public void setItemCode(String itemCode) {
	this.itemCode = itemCode;
}
@JsonProperty("Warehouse_Code")	
public String getWarehouseCode() {
	return warehouseCode;
}
@JsonProperty("Warehouse_Code")	
public void setWarehouseCode(String warehouseCode) {
	this.warehouseCode = warehouseCode;
}


 
}
