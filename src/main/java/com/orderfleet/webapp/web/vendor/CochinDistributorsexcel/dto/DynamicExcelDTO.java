package com.orderfleet.webapp.web.vendor.CochinDistributorsexcel.dto;

import java.util.Map;

public class DynamicExcelDTO {

	private String customerAlias;
	private String customerName;
	private String dateTime;
	private String dynamicDocumentPid;
	private Map<String,String> formDetails;
	
	public String getCustomerAlias() {
		return customerAlias;
	}
	public void setCustomerAlias(String customerAlias) {
		this.customerAlias = customerAlias;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getDynamicDocumentPid() {
		return dynamicDocumentPid;
	}
	public void setDynamicDocumentPid(String dynamicDocumentPid) {
		this.dynamicDocumentPid = dynamicDocumentPid;
	}
	public Map<String, String> getFormDetails() {
		return formDetails;
	}
	public void setFormDetails(Map<String, String> formDetails) {
		this.formDetails = formDetails;
	}
	
	
	
}
