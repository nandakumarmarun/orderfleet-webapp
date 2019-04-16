package com.orderfleet.webapp.web.rest.dto;

public class MonthlyPerformance {

	private double target;
	private double achieved;
	private double verified;

	public MonthlyPerformance() {
		super();
	}

	public MonthlyPerformance(double target, double achieved) {
		super();
		this.target = target;
		this.achieved = achieved;
	}

	public double getTarget() {
		return target;
	}

	public void setTarget(double target) {
		this.target = target;
	}

	public double getAchieved() {
		return achieved;
	}

	public void setAchieved(double achieved) {
		this.achieved = achieved;
	}

	public double getVerified() {
		return verified;
	}

	public void setVerified(double verified) {
		this.verified = verified;
	}

}