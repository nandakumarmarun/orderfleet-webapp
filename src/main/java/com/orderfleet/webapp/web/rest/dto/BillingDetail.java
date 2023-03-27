package com.orderfleet.webapp.web.rest.dto;

public class BillingDetail {
	
	private String slabName;
	
	private Double slabRate;
	
	private String headerPid;

	private int activeUserCount;
	private String noOfMonth;

	private double totalAmount;


	public BillingDetail() {
	}

	public BillingDetail(String slabName, Double slabRate, String headerPid, int activeUserCount, String noOfMonth,int totalAmount) {
		this.slabName = slabName;
		this.slabRate = slabRate;
		this.headerPid = headerPid;
		this.activeUserCount = activeUserCount;
		this.noOfMonth = noOfMonth;
		this.totalAmount = totalAmount;
	}

	public String getSlabName() {
		return slabName;
	}

	public void setSlabName(String slabName) {
		this.slabName = slabName;
	}

	public Double getSlabRate() {
		return slabRate;
	}

	public void setSlabRate(Double slabRate) {
		this.slabRate = slabRate;
	}

	public String getHeaderPid() {
		return headerPid;
	}

	public void setHeaderPid(String headerPid) {
		this.headerPid = headerPid;
	}

	public int getActiveUserCount() {
		return activeUserCount;
	}

	public void setActiveUserCount(int activeUserCount) {
		this.activeUserCount = activeUserCount;
	}

	public String getNoOfMonth() {
		return noOfMonth;
	}

	public void setNoOfMonth(String noOfMonth) {
		this.noOfMonth = noOfMonth;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	public String toString() {
		return "BillingDetail{" +
				"slabName='" + slabName + '\'' +
				", slabRate=" + slabRate +
				", headerPid='" + headerPid + '\'' +
				", activeUserCount=" + activeUserCount +
				", noOfMonth='" + noOfMonth + '\'' +
				", totalAmount=" + totalAmount +
				'}';
	}
}
