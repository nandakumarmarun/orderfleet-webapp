package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class PerformanceDTO {

	private Long id;
	private String activityName;
	private Long activityId;
	private boolean isManual;
	private double totalTarget;
	private double totalAchieved;
	private double achievedPercentage;
	private int count;
	private List<MonthlyPerformance> months;

	public PerformanceDTO() {
		super();
	}

	public PerformanceDTO(String activityName, List<MonthlyPerformance> months, double totalTarget,
			double totalAchieved, double achievedPercentage, int count) {
		super();
		this.activityName = activityName;
		this.months = months;
		this.totalTarget = totalTarget;
		this.totalAchieved = totalAchieved;
		this.achievedPercentage = achievedPercentage;
		this.count = count;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public List<MonthlyPerformance> getMonths() {
		return months;
	}

	public void setMonths(List<MonthlyPerformance> months) {
		this.months = months;
	}

	public double getTotalTarget() {
		return totalTarget;
	}

	public void setTotalTarget(double totalTarget) {
		this.totalTarget = totalTarget;
	}

	public double getTotalAchieved() {
		return totalAchieved;
	}

	public void setTotalAchieved(double totalAchieved) {
		this.totalAchieved = totalAchieved;
	}

	public double getAchievedPercentage() {
		return achievedPercentage;
	}

	public void setAchievedPercentage(double achievedPercentage) {
		this.achievedPercentage = achievedPercentage;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean getIsManual() {
		return isManual;
	}

	public void setIsManual(boolean isManual) {
		this.isManual = isManual;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

}
