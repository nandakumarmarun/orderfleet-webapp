package com.orderfleet.webapp.web.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class YtdReportWeek {

	private String week;
	private long tc;
	private long pc;
	private double volume;
	private double amount;
	private double efficiency;
	private List<YtdReportDay> days = new ArrayList<YtdReportDay>();

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
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

	public List<YtdReportDay> getDays() {
		return days;
	}

	public void setDays(List<YtdReportDay> days) {
		this.days = days;
	}

	public double getEfficiency() {
		return efficiency;
	}

	public void setEfficiency(double efficiency) {
		this.efficiency = efficiency;
	}

}
