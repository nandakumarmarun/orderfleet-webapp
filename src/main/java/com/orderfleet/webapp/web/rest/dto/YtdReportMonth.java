package com.orderfleet.webapp.web.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class YtdReportMonth {

	private String month;
	private long tc;
	private long pc;
	private double volume;
	private double amount;
	private double efficiency;
	private List<YtdReportWeek> weeks = new ArrayList<YtdReportWeek>();

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
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

	public List<YtdReportWeek> getWeeks() {
		return weeks;
	}

	public void setWeeks(List<YtdReportWeek> weeks) {
		this.weeks = weeks;
	}

	public double getEfficiency() {
		return efficiency;
	}

	public void setEfficiency(double efficiency) {
		this.efficiency = efficiency;
	}

}
