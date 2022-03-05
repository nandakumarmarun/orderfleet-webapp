package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"GetStockResult"})
public class StockResponseFocus {
	
 @JsonProperty("GetStockResult")	
 private GetStockResult getStockResult;
 
 @JsonProperty("GetStockResult")	
public GetStockResult getGetStockResult() {
	return getStockResult;
}
 @JsonProperty("GetStockResult")	
public void setGetStockResult(GetStockResult getStockResult) {
	this.getStockResult = getStockResult;
}
 
 
 
}
