package com.orderfleet.webapp.web.vendor.sap.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "ItemGroupCode", "OnHand", "SalUnitMsr", "WeightPerPC", "str1", "str2" })
public class ResponseBodySapProductProfile {

	@JsonProperty("ItemGroupCode")
	private String itemGroupCode;
	@JsonProperty("OnHand")
	private String onHand;
	@JsonProperty("SalUnitMsr")
	private String salUnitMsr;
	@JsonProperty("WeightPerPC")
	private String weightPerPC;
	@JsonProperty("str1")
	private String str1;
	@JsonProperty("str2")
	private String str2;

	@JsonProperty("ItemGroupCode")
	public String getItemGroupCode() {
		return itemGroupCode;
	}

	@JsonProperty("ItemGroupCode")
	public void setItemGroupCode(String itemGroupCode) {
		this.itemGroupCode = itemGroupCode;
	}

	@JsonProperty("OnHand")
	public String getOnHand() {
		return onHand;
	}

	@JsonProperty("OnHand")
	public void setOnHand(String onHand) {
		this.onHand = onHand;
	}

	@JsonProperty("SalUnitMsr")
	public String getSalUnitMsr() {
		return salUnitMsr;
	}

	@JsonProperty("SalUnitMsr")
	public void setSalUnitMsr(String salUnitMsr) {
		this.salUnitMsr = salUnitMsr;
	}

	@JsonProperty("WeightPerPC")
	public String getWeightPerPC() {
		return weightPerPC;
	}

	@JsonProperty("WeightPerPC")
	public void setWeightPerPC(String weightPerPC) {
		this.weightPerPC = weightPerPC;
	}

	@JsonProperty("str1")
	public String getStr1() {
		return str1;
	}

	@JsonProperty("str1")
	public void setStr1(String str1) {
		this.str1 = str1;
	}

	@JsonProperty("str2")
	public String getStr2() {
		return str2;
	}

	@JsonProperty("str2")
	public void setStr2(String str2) {
		this.str2 = str2;
	}

}
