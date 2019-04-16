package com.orderfleet.webapp.web.rest.dto;

public class AccountTargetDTO {

	private String label;

	private double targetVolume;
	private double achievedVolume;

	private double targetAmount;
	private double achievedAmount;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getTargetVolume() {
		return targetVolume;
	}

	public void setTargetVolume(double targetVolume) {
		this.targetVolume = targetVolume;
	}

	public double getAchievedVolume() {
		return achievedVolume;
	}

	public void setAchievedVolume(double achievedVolume) {
		this.achievedVolume = achievedVolume;
	}

	public double getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(double targetAmount) {
		this.targetAmount = targetAmount;
	}

	public double getAchievedAmount() {
		return achievedAmount;
	}

	public void setAchievedAmount(double achievedAmount) {
		this.achievedAmount = achievedAmount;
	}

}
