package com.orderfleet.webapp.web.rest.dto;

public class OverdueReportPeriod {

	private String name;

	private double amount;

	public OverdueReportPeriod() {
	}

	public OverdueReportPeriod(String name, double amount) {
		super();
		this.name = name;
		this.amount = amount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
