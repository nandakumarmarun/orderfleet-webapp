package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Header","Body"})
public class SaleOrderFocusObject {
	
	@JsonProperty("Header")
	private SalesOrderMasterFocus SalesOrderMasterFocus;
	
	@JsonProperty("Body")
	private List<SalesOrderItemDetailsFocus> SalesOrderItemDetailsFocus;

	@JsonProperty("Header")
	public SalesOrderMasterFocus getSalesOrderMasterFocus() {
		return SalesOrderMasterFocus;
	}

	@JsonProperty("Header")
	public void setSalesOrderMasterFocus(SalesOrderMasterFocus salesOrderMasterFocus) {
		SalesOrderMasterFocus = salesOrderMasterFocus;
	}

	@JsonProperty("Body")
	public List<SalesOrderItemDetailsFocus> getSalesOrderItemDetailsFocus() {
		return SalesOrderItemDetailsFocus;
	}

	@JsonProperty("Body")
	public void setSalesOrderItemDetailsFocus(List<SalesOrderItemDetailsFocus> salesOrderItemDetailsFocus) {
		SalesOrderItemDetailsFocus = salesOrderItemDetailsFocus;
	}

	@Override
	public String toString() {
		return "SaleOrderFocusObject [SalesOrderMasterFocus=" + SalesOrderMasterFocus + ", SalesOrderItemDetailsFocus="
				+ SalesOrderItemDetailsFocus + "]";
	}

 
}
