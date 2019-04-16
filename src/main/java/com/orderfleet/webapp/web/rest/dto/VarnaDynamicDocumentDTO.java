package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

public class VarnaDynamicDocumentDTO {

	private LocalDateTime dateTime;
	
	private String orderedNo;
	
	private String customerName;
	
	private String glass;
	
	private String quantity;
	
	private String holes;
	
	private String polishing;
	
	private String etching;
	
	private String acidWorks;
	
	private String cutOuts;
	
	private String type;
	
	private String polishTypeValue;
	
	private String polishingAreas;

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public String getOrderedNo() {
		return orderedNo;
	}

	public void setOrderedNo(String orderedNo) {
		this.orderedNo = orderedNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getGlass() {
		return glass;
	}

	public void setGlass(String glass) {
		this.glass = glass;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getHoles() {
		return holes;
	}

	public void setHoles(String holes) {
		this.holes = holes;
	}

	public String getPolishing() {
		return polishing;
	}

	public void setPolishing(String polishing) {
		this.polishing = polishing;
	}

	public String getEtching() {
		return etching;
	}

	public void setEtching(String etching) {
		this.etching = etching;
	}

	public String getAcidWorks() {
		return acidWorks;
	}

	public void setAcidWorks(String acidWorks) {
		this.acidWorks = acidWorks;
	}

	public String getCutOuts() {
		return cutOuts;
	}

	public void setCutOuts(String cutOuts) {
		this.cutOuts = cutOuts;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPolishTypeValue() {
		return polishTypeValue;
	}

	public void setPolishTypeValue(String polishTypeValue) {
		this.polishTypeValue = polishTypeValue;
	}

	public String getPolishingAreas() {
		return polishingAreas;
	}

	public void setPolishingAreas(String polishingAreas) {
		this.polishingAreas = polishingAreas;
	}
	
	
}
