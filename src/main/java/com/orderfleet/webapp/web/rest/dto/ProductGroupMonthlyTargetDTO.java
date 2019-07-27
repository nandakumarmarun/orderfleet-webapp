package com.orderfleet.webapp.web.rest.dto;

/**
 *
 * @author Sarath
 * @since Oct 15, 2016
 */
public class ProductGroupMonthlyTargetDTO {

	private String locationPid;
	private String productGroupPid;
	private String productGroupName;
	private String productGroupLocationTragetPid;
	private double volume;
	private double amount;
	private String monthAndYear;

	public ProductGroupMonthlyTargetDTO() {
		super();
	}

	public ProductGroupMonthlyTargetDTO(String locationPid, String productGroupPid, String productGroupName,
			String productGroupLocationTragetPid, double volume, double amount, String monthAndYear) {
		super();
		this.locationPid = locationPid;
		this.productGroupPid = productGroupPid;
		this.productGroupName = productGroupName;
		this.productGroupLocationTragetPid = productGroupLocationTragetPid;
		this.volume = volume;
		this.amount = amount;
		this.monthAndYear = monthAndYear;
	}

	public String getLocationPid() {
		return locationPid;
	}

	public void setLocationPid(String locationPid) {
		this.locationPid = locationPid;
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

	public String getProductGroupLocationTragetPid() {
		return productGroupLocationTragetPid;
	}

	public void setProductGroupLocationTragetPid(String productGroupLocationTragetPid) {
		this.productGroupLocationTragetPid = productGroupLocationTragetPid;
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
		return "ProductGroupMonthlyTargetDTO [locationPid=" + locationPid + ", productGroupPid=" + productGroupPid
				+ ", productGroupName=" + productGroupName + ", productGroupLocationTragetPid="
				+ productGroupLocationTragetPid + ", volume=" + volume + ", amount=" + amount + ", monthAndYear="
				+ monthAndYear + "]";
	}

}
