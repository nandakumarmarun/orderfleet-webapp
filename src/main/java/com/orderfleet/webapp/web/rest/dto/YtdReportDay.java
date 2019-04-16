package com.orderfleet.webapp.web.rest.dto;

public class YtdReportDay {

	private String day;
	private long tc;
	private long pc;
	private double volume;
	private double amount;
	private double efficiency;

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public long getTc() {
		return tc;
	}

	public void setTc(long tc) {
		this.tc = tc;
	}

	public long getPc() {
		return pc;
	}

	public void setPc(long pc) {
		this.pc = pc;
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

	public double getEfficiency() {
		return efficiency;
	}

	public void setEfficiency(double efficiency) {
		this.efficiency = efficiency;
	}

}
