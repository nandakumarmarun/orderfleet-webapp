package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"Base_Unit", "CGST", "Cess", "GSTPer", "HSN_Code", "IGST", "Item_Code", "Item_Name", "Item_Type", "SGST", "Sales_Unit", "Selling_Rate"})
public class ProductProfileNewFocus {
	@JsonProperty("Base_Unit")
	private String baseUnits;
	
	@JsonProperty("CGST")
	private double cgst;
	
	@JsonProperty("Cess")
	private double cess;
	
	@JsonProperty("GSTPer")
	private double gstPer;
	
	@JsonProperty("HSN_Code")
	private String hsnCode;
	
	@JsonProperty("IGST")
	private double igst;
	
	@JsonProperty("Item_Code")
	private String itemCode;
	
	@JsonProperty("Item_Name")
	private String itemName;
	
	@JsonProperty("Item_Type")
	private String itemType;
	
	@JsonProperty("SGST")
	private double sgst;
	
	@JsonProperty("Sales_Unit")
	private String salesUnit;
	
	@JsonProperty("Selling_Rate")
	private double sellingRate;
	
	@JsonProperty("Item_Width")
	public String itemwidth;
	
	@JsonProperty("Base_Unit")
	public String getBaseUnits() {
		return baseUnits;
	}
	@JsonProperty("Base_Unit")
	public void setBaseUnits(String baseUnits) {
		this.baseUnits = baseUnits;
	}
	
	@JsonProperty("CGST")
	public double getCgst() {
		return cgst;
	}
	@JsonProperty("CGST")
	public void setCgst(double cgst) {
		this.cgst = cgst;
	}
	@JsonProperty("Cess")
	public double getCess() {
		return cess;
	}
	@JsonProperty("Cess")
	public void setCess(double cess) {
		this.cess = cess;
	}
	
	@JsonProperty("GSTPer")
	public double getGstPer() {
		return gstPer;
	}
	
	@JsonProperty("GSTPer")
	public void setGstPer(double gstPer) {
		this.gstPer = gstPer;
	}
	
	@JsonProperty("HSN_Code")
	public String getHsnCode() {
		return hsnCode;
	}
	@JsonProperty("HSN_Code")
	public void setHsnCode(String hsnCode) {
		this.hsnCode = hsnCode;
	}
	
	@JsonProperty("IGST")
	public double getIgst() {
		return igst;
	}
	@JsonProperty("IGST")
	public void setIgst(double igst) {
		this.igst = igst;
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
	
	@JsonProperty("Item_Type")
	public String getItemType() {
		return itemType;
	}
	
	@JsonProperty("Item_Type")
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	
	@JsonProperty("SGST")
	public double getSgst() {
		return sgst;
	}
	@JsonProperty("SGST")
	public void setSgst(double sgst) {
		this.sgst = sgst;
	}
	
	@JsonProperty("Sales_Unit")
	public String getSalesUnit() {
		return salesUnit;
	}
	@JsonProperty("Sales_Unit")
	public void setSalesUnit(String salesUnit) {
		this.salesUnit = salesUnit;
	}
	
	@JsonProperty("Selling_Rate")
	public double getSellingRate() {
		return sellingRate;
	}
	@JsonProperty("Selling_Rate")
	public void setSellingRate(double sellingRate) {
		this.sellingRate = sellingRate;
	}
	
	

}
