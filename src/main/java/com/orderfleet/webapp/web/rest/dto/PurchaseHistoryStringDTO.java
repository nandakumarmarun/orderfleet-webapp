package com.orderfleet.webapp.web.rest.dto;

public class PurchaseHistoryStringDTO {

	private String accountPid;

	private String productPid;

	private String value;

	public PurchaseHistoryStringDTO(String accountPid, String productPid, String value) {
		super();
		this.accountPid = accountPid;
		this.productPid = productPid;
		this.value = value;
	}

	public String getAccountPid() {
		return accountPid;
	}

	public String getProductPid() {
		return productPid;
	}

	public String getValue() {
		return value;
	}

}
