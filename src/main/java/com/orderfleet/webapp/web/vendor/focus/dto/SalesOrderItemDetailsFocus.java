package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Item", "Unit", "Brand_Deva", "Length_Type","Mtr_Conv","Length_in_Feet","Length_in_Inch","Length", "Alt_Qty", "Width",
		"Quantity", "Unit_Conversion","Sales_Qty","Mtr_Pcs","Selling_Rate_InclTax" })
public class SalesOrderItemDetailsFocus {

	@JsonProperty("Item")
	private String item;
	
	@JsonProperty("Unit")
	private String unit;
	
	@JsonProperty("Brand_Deva")
	private String brandDeva;
	
	@JsonProperty("Length_Type")
	private String lengthType;
	
	@JsonProperty("Mtr_Conv")
	private double mtrConv;
	
	@JsonProperty("Length_in_Feet")
	private double lengthInFeet;
	
	@JsonProperty("Length_in_Inch")
	private double lengthInInch;
	
	@JsonProperty("Length")
	private double length;
	
	@JsonProperty("Alt_Qty")
	private double altQty;
	
	@JsonProperty("Width")
	private double width;
	
	@JsonProperty("Quantity")
	private double quantity;
	
	@JsonProperty("Unit_Conversion")
	private double unitConversion;
	
	@JsonProperty("Sales_Qty")
	private double salesQty;
	
	@JsonProperty("Mtr_Pcs")
	private double mtrPcs;
	
	@JsonProperty("Selling_Rate_InclTax")
	private double sellingRateInclTax;

	@JsonProperty("Item")
	public String getItem() {
		return item;
	}
	@JsonProperty("Item")
	public void setItem(String item) {
		this.item = item;
	}

	@JsonProperty("Unit")
	public String getUnit() {
		return unit;
	}

	@JsonProperty("Unit")
	public void setUnit(String unit) {
		this.unit = unit;
	}

	@JsonProperty("Brand_Deva")
	public String getBrandDeva() {
		return brandDeva;
	}

	@JsonProperty("Brand_Deva")
	public void setBrandDeva(String brandDeva) {
		this.brandDeva = brandDeva;
	}

	@JsonProperty("Length_Type")
	public String getLengthType() {
		return lengthType;
	}

	@JsonProperty("Length_Type")
	public void setLengthType(String lengthType) {
		this.lengthType = lengthType;
	}

	@JsonProperty("Mtr_Conv")
	public double getMtrConv() {
		return mtrConv;
	}

	@JsonProperty("Mtr_Conv")
	public void setMtrConv(double mtrConv) {
		this.mtrConv = mtrConv;
	}

	@JsonProperty("Length_in_Feet")
	public double getLengthInFeet() {
		return lengthInFeet;
	}

	@JsonProperty("Length_in_Feet")
	public void setLengthInFeet(double lengthInFeet) {
		this.lengthInFeet = lengthInFeet;
	}

	@JsonProperty("Length_in_Inch")
	public double getLengthInInch() {
		return lengthInInch;
	}

	@JsonProperty("Length_in_Inch")
	public void setLengthInInch(double lengthInInch) {
		this.lengthInInch = lengthInInch;
	}

	@JsonProperty("Length")
	public double getLength() {
		return length;
	}

	@JsonProperty("Length")
	public void setLength(double length) {
		this.length = length;
	}

	@JsonProperty("Alt_Qty")
	public double getAltQty() {
		return altQty;
	}

	@JsonProperty("Alt_Qty")
	public void setAltQty(double altQty) {
		this.altQty = altQty;
	}

	@JsonProperty("Width")
	public double getWidth() {
		return width;
	}

	@JsonProperty("Width")
	public void setWidth(double width) {
		this.width = width;
	}

	@JsonProperty("Quantity")
	public double getQuantity() {
		return quantity;
	}

	@JsonProperty("Quantity")
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	@JsonProperty("Unit_Conversion")
	public double getUnitConversion() {
		return unitConversion;
	}

	@JsonProperty("Unit_Conversion")
	public void setUnitConversion(double unitConversion) {
		this.unitConversion = unitConversion;
	}

	@JsonProperty("Selling_Rate_InclTax")
	public double getSellingRateInclTax() {
		return sellingRateInclTax;
	}

	@JsonProperty("Selling_Rate_InclTax")
	public void setSellingRateInclTax(double sellingRateInclTax) {
		this.sellingRateInclTax = sellingRateInclTax;
	}

	@JsonProperty("Mtr_Pcs")
	public double getMtrPcs() {
		return mtrPcs;
	}
	
	public double getSalesQty() {
		return salesQty;
	}
	public void setSalesQty(double salesQty) {
		this.salesQty = salesQty;
	}
	@JsonProperty("Mtr_Pcs")
	public void setMtrPcs(double mtrPcs) {
		this.mtrPcs = mtrPcs;
	}
	@Override
	public String toString() {
		return "SalesOrderItemDetailsFocus [item=" + item + ", unit=" + unit + ", brandDeva=" + brandDeva
				+ ", lengthType=" + lengthType + ", mtrConv=" + mtrConv + ", lengthInFeet=" + lengthInFeet
				+ ", lengthInInch=" + lengthInInch + ", length=" + length + ", altQty=" + altQty + ", width=" + width
				+ ", quantity=" + quantity + ", unitConversion=" + unitConversion + ", sellingRateInclTax="
				+ sellingRateInclTax + ", salesQty=" + salesQty + ", mtrPcs=" + mtrPcs + "]";
	}
	
}
