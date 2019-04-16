package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.PriceLevelAccountProductGroup;

public class PriceLevelAccountProductGroupDTO {

	private String pid;

	private String accountPid;

	private String accountName;

	private String priceLevelPid;

	private String priceLevelName;

	private String productGroupPid;

	private String productGroupName;

	public PriceLevelAccountProductGroupDTO() {
		super();
	}

	public PriceLevelAccountProductGroupDTO(PriceLevelAccountProductGroup priceLevelAccountProductGroup) {
		super();
		this.pid = priceLevelAccountProductGroup.getPid();
		this.accountPid = priceLevelAccountProductGroup.getAccountProfile().getPid();
		this.accountName = priceLevelAccountProductGroup.getAccountProfile().getName();
		this.priceLevelPid = priceLevelAccountProductGroup.getPriceLevel().getPid();
		this.priceLevelName = priceLevelAccountProductGroup.getPriceLevel().getName();
		this.productGroupPid = priceLevelAccountProductGroup.getProductGroup().getPid();
		this.productGroupName = priceLevelAccountProductGroup.getProductGroup().getName();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getAccountPid() {
		return accountPid;
	}

	public void setAccountPid(String accountPid) {
		this.accountPid = accountPid;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getPriceLevelPid() {
		return priceLevelPid;
	}

	public void setPriceLevelPid(String priceLevelPid) {
		this.priceLevelPid = priceLevelPid;
	}

	public String getPriceLevelName() {
		return priceLevelName;
	}

	public void setPriceLevelName(String priceLevelName) {
		this.priceLevelName = priceLevelName;
	}

	public String getProductGroupPid() {
		return productGroupPid;
	}

	public void setProductGroupPid(String productGroupPid) {
		this.productGroupPid = productGroupPid;
	}

	public String getProductGroupName() {
		return productGroupName;
	}

	public void setProductGroupName(String productGroupName) {
		this.productGroupName = productGroupName;
	}

	@Override
	public String toString() {
		return "PriceLevelAccountProductGroupDTO [pid=" + pid + ", accountPid=" + accountPid + ", accountName="
				+ accountName + ", priceLevelPid=" + priceLevelPid + ", priceLevelName=" + priceLevelName
				+ ", productGroupPid=" + productGroupPid + ", productGroupName=" + productGroupName + "]";
	}

}
