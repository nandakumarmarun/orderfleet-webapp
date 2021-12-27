package com.orderfleet.webapp.web.vendor.sap.prabhu.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "itemGroupCode", "itemGroupName", "onHand", "salUnitMsr", "weightPerPC", "str1", "str2" })
public class ResponseBodySapProductProfile {

	@JsonProperty("id")
	private long id;
	@JsonProperty("itemGroupCode")
	private String itemGroupCode;
	@JsonProperty("itemGroupName")
	private String itemGroupName;
	@JsonProperty("onHand")
	private long onHand;
	@JsonProperty("salUnitMsr")
	private String salUnitMsr;
	@JsonProperty("weightPerPC")
	private String weightPerPC;
	@JsonProperty("str1")
	private String str1;
	@JsonProperty("str2")
	private String str2;

	@JsonProperty("id")
	public long getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(long id) {
		this.id = id;
	}

	@JsonProperty("itemGroupCode")
	public String getItemGroupCode() {
		return itemGroupCode;
	}

	@JsonProperty("itemGroupCode")
	public void setItemGroupCode(String itemGroupCode) {
		this.itemGroupCode = itemGroupCode;
	}

	@JsonProperty("itemGroupName")
	public String getItemGroupName() {
		return itemGroupName;
	}

	@JsonProperty("itemGroupName")
	public void setItemGroupName(String itemGroupName) {
		this.itemGroupName = itemGroupName;
	}

	@JsonProperty("onHand")
	public long getOnHand() {
		return onHand;
	}

	@JsonProperty("onHand")
	public void setOnHand(long onHand) {
		this.onHand = onHand;
	}

	@JsonProperty("salUnitMsr")
	public String getSalUnitMsr() {
		return salUnitMsr;
	}

	@JsonProperty("salUnitMsr")
	public void setSalUnitMsr(String salUnitMsr) {
		this.salUnitMsr = salUnitMsr;
	}

	@JsonProperty("weightPerPC")
	public String getWeightPerPC() {
		return weightPerPC;
	}

	@JsonProperty("weightPerPC")
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
