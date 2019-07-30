package com.orderfleet.webapp.web.rest.dto;

/**
 *
 * @author Sarath
 * @since Oct 15, 2016
 */
public class UserWiseSalesMonthlyTargetDTO {

	private String userPid;
	private String userName;
	private String userWiseSalesTargetPid;
	private double volume;
	private double amount;
	private String monthAndYear;

	public UserWiseSalesMonthlyTargetDTO() {
		super();
	}

	public UserWiseSalesMonthlyTargetDTO(String userPid, String userName, String userWiseSalesTargetPid, double volume,
			double amount, String monthAndYear) {
		super();
		this.userPid = userPid;
		this.userName = userName;
		this.userWiseSalesTargetPid = userWiseSalesTargetPid;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserWiseSalesTargetPid() {
		return userWiseSalesTargetPid;
	}

	public void setUserWiseSalesTargetPid(String userWiseSalesTargetPid) {
		this.userWiseSalesTargetPid = userWiseSalesTargetPid;
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
		return "UserWiseSalesMonthlyTargetDTO [userPid=" + userPid + ", userName=" + userName
				+ ", userWiseSalesTargetPid=" + userWiseSalesTargetPid + ", volume=" + volume + ", amount=" + amount
				+ ", monthAndYear=" + monthAndYear + "]";
	}

}
