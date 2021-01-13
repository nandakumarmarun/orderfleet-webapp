package com.orderfleet.webapp.web.rest.dto;

/**
 *
 * @author Sarath
 * @since Oct 15, 2016
 */
public class SalesMonthlyProductGroupTargetDTO {

	private String userPid;
	private String productGroupPid;
	private String productGroupName;
	private String salesProductGroupUserTragetPid;
	private double volume;
	private double amount;
	private String monthAndYear;

	public SalesMonthlyProductGroupTargetDTO() {
		super();
	}

	public SalesMonthlyProductGroupTargetDTO(String userPid, String productGroupPid, String productGroupName,
			String salesProductGroupUserTragetPid, double volume, double amount, String monthAndYear) {
		super();
		this.userPid = userPid;
		this.productGroupPid = productGroupPid;
		this.productGroupName = productGroupName;
		this.salesProductGroupUserTragetPid = salesProductGroupUserTragetPid;
		this.volume = volume;
		this.amount = amount;
		this.monthAndYear = monthAndYear;
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
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

	public String getSalesProductGroupUserTragetPid() {
		return salesProductGroupUserTragetPid;
	}

	public void setSalesProductGroupUserTragetPid(String salesProductGroupUserTragetPid) {
		this.salesProductGroupUserTragetPid = salesProductGroupUserTragetPid;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getMonthAndYear() {
		return monthAndYear;
	}

	public void setMonthAndYear(String monthAndYear) {
		this.monthAndYear = monthAndYear;
	}

	@Override
	public String toString() {
		return "SalesMonthlyTargetDTO [userPid=" + userPid + ", productGroupPid=" + productGroupPid
				+ ", productGroupName=" + productGroupName + ", salesProductGroupUserTragetPid="
				+ salesProductGroupUserTragetPid + ", volume=" + volume + ", amount=" + amount + ", monthAndYear="
				+ monthAndYear + "]";
	}

}
