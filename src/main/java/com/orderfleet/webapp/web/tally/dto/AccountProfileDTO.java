package com.orderfleet.webapp.web.tally.dto;


public class AccountProfileDTO {

	private String name;
	private String description;
	private String accountTypeName;
	private String address;
	private String phone1;
	private String defaultPriceLevelName;
	private double closingBalance;
	private String guid;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAccountTypeName() {
		return accountTypeName;
	}
	public void setAccountTypeName(String accountTypeName) {
		this.accountTypeName = accountTypeName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone1() {
		return phone1;
	}
	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}
	public String getDefaultPriceLevelName() {
		return defaultPriceLevelName;
	}
	public void setDefaultPriceLevelName(String defaultPriceLevelName) {
		this.defaultPriceLevelName = defaultPriceLevelName;
	}
	public double getClosingBalance() {
		return closingBalance;
	}
	public void setClosingBalance(double closingBalance) {
		this.closingBalance = closingBalance;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	
}
