package com.orderfleet.webapp.web.tally.dto;

import com.orderfleet.webapp.domain.GstLedger;

public class GstLedgerDTO {

	private String name;
	private String taxType;
	private String accountType;
	private double taxRate;
	private boolean activated;
	private String guid;
	
	public GstLedgerDTO() {
		super();
		
	}
	
	public GstLedgerDTO(GstLedger gstLedger) {
		super();
		this.name = gstLedger.getName();
		this.taxType = gstLedger.getTaxType();
		this.taxRate = gstLedger.getTaxRate();
		//this.accountType = gstLedger.getAccountType();
		this.activated = gstLedger.isActivated();
		this.guid = gstLedger.getGuid();
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTaxType() {
		return taxType;
	}
	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public double getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}
	
	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	
	

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	@Override
	public String toString() {
		return "GstLedgerDTO [name=" + name + ", taxType=" + taxType + ", accountType=" + accountType + ", taxRate="
				+ taxRate + ", activated=" + activated + ", guid=" + guid + "]";
	}

	
	
}
