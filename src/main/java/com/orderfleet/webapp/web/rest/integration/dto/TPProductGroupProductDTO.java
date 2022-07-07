package com.orderfleet.webapp.web.rest.integration.dto;

public class TPProductGroupProductDTO {

	private Long alterId;
	private String productName;
	private String groupName;

	public Long getAlterId() {
		return alterId;
	}

	public void setAlterId(Long alterId) {
		this.alterId = alterId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
