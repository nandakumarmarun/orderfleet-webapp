package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;

public class SalesTargetAchievedDTO {

	private LocalDate dayDate;
	
	private double targetAmountVolume;
	
	private double achievedAmountVolume;

	public LocalDate getDayDate() {
		return dayDate;
	}

	public void setDayDate(LocalDate dayDate) {
		this.dayDate = dayDate;
	}

	public double getTargetAmountVolume() {
		return targetAmountVolume;
	}

	public void setTargetAmountVolume(double targetAmountVolume) {
		this.targetAmountVolume = targetAmountVolume;
	}

	public double getAchievedAmountVolume() {
		return achievedAmountVolume;
	}

	public void setAchievedAmountVolume(double achievedAmountVolume) {
		this.achievedAmountVolume = achievedAmountVolume;
	}

	@Override
	public String toString() {
		return "SalesTargetAchievedDTO [dayDate=" + dayDate + ", targetAmountVolume=" + targetAmountVolume
				+ ", achievedAmountVolume=" + achievedAmountVolume + "]";
	}
	
	
}
