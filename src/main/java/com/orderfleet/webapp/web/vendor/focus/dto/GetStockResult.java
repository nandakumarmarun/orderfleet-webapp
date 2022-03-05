package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"Count", "Product", "iStatus","sMessage"})
public class GetStockResult {
@JsonProperty("Count")	
 private long count;
@JsonProperty("Product")	
 private List<StockProduct> stockProducts;
@JsonProperty("iStatus")	
 private long isStatus;
 
@JsonProperty("sMessage")	
 private String sMassage;
 
 @JsonProperty("Count")	
 
public long getCount() {
	return count;
}
 
 @JsonProperty("Count")	
public void setCount(long count) {
	this.count = count;
}

 @JsonProperty("Product")	
public List<StockProduct> getStockProducts() {
	return stockProducts;
}

 @JsonProperty("Product")	
public void setStockProducts(List<StockProduct> stockProducts) {
	this.stockProducts = stockProducts;
}

 @JsonProperty("iStatus")	
public long getIsStatus() {
	return isStatus;
}

 @JsonProperty("iStatus")	
public void setIsStatus(long isStatus) {
	this.isStatus = isStatus;
}

 @JsonProperty("sMessage")	
public String getsMassage() {
	return sMassage;
}

 @JsonProperty("sMessage")	
public void setsMassage(String sMassage) {
	this.sMassage = sMassage;
}
 
 
}
