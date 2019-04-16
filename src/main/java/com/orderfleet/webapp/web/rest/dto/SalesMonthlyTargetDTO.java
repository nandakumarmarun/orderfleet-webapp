package com.orderfleet.webapp.web.rest.dto;

/**
 *
 * @author Sarath
 * @since Oct 15, 2016
 */
public class SalesMonthlyTargetDTO {

	private String userPid;
	private String salesTargetGroupPid;
	private String salesTargetGroupName;
	private String salesTargetGroupUserTragetPid;
	private double volume;
	private double amount;
	private String monthAndYear;
	private String accountProfilePid;
	private String accountProfileName;

	public SalesMonthlyTargetDTO() {
		super();
	}

	public SalesMonthlyTargetDTO(String userPid, String salesTargetGroupPid, String salesTargetGroupName,
			String salesTargetGroupUserTragetPid, double volume, double amount, String monthAndYear) {
		super();
		this.userPid = userPid;
		this.salesTargetGroupPid = salesTargetGroupPid;
		this.salesTargetGroupName = salesTargetGroupName;
		this.salesTargetGroupUserTragetPid = salesTargetGroupUserTragetPid;
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

	public String getSalesTargetGroupPid() {
		return salesTargetGroupPid;
	}

	public void setSalesTargetGroupPid(String salesTargetGroupPid) {
		this.salesTargetGroupPid = salesTargetGroupPid;
	}

	public String getSalesTargetGroupName() {
		return salesTargetGroupName;
	}

	public void setSalesTargetGroupName(String salesTargetGroupName) {
		this.salesTargetGroupName = salesTargetGroupName;
	}

	public String getSalesTargetGroupUserTragetPid() {
		return salesTargetGroupUserTragetPid;
	}

	public void setSalesTargetGroupUserTragetPid(String salesTargetGroupUserTragetPid) {
		this.salesTargetGroupUserTragetPid = salesTargetGroupUserTragetPid;
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

	
	public String getAccountProfilePid() {
		return accountProfilePid;
	}

	public void setAccountProfilePid(String accountProfilePid) {
		this.accountProfilePid = accountProfilePid;
	}

	public String getAccountProfileName() {
		return accountProfileName;
	}

	public void setAccountProfileName(String accountProfileName) {
		this.accountProfileName = accountProfileName;
	}

	@Override
	public String toString() {
		return "SalesMonthlyTargetDTO [userPid=" + userPid + ", salesTargetGroupPid=" + salesTargetGroupPid
				+ ", salesTargetGroupName=" + salesTargetGroupName + ", salesTargetGroupUserTragetPid="
				+ salesTargetGroupUserTragetPid + ", volume=" + volume + ", amount=" + amount + ", monthAndYear="
				+ monthAndYear + "]";
	}

}
