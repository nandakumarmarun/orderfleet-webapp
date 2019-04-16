package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.enums.DashboardItemType;
import com.orderfleet.webapp.domain.enums.TargetType;
import com.orderfleet.webapp.domain.enums.TaskPlanType;

/**
 * A DTO for the DashboardSummaryData.
 * 
 * @author Muhammed Riyas T
 * @since Sep 07, 2016
 */
public class DashboardSummaryDTO {

	private String dashboardItemPid;

	private String label;

	private DashboardItemType dashboardItemType;

	private TaskPlanType taskPlanType;

	private long achieved;

	private long scheduled;

	private long count;

	private double amount;

	private double volume;
	
	private boolean numberCircle;
	
	private TargetType targetType = TargetType.AMOUNT;
	
	private double targetAmount;
	
	private double targetVolume;

	private double targetAchievedAmount;

	private double targetAchievedVolume;
	
	private double targetAverageVolume;

	public String getDashboardItemPid() {
		return dashboardItemPid;
	}

	public void setDashboardItemPid(String dashboardItemPid) {
		this.dashboardItemPid = dashboardItemPid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
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

	public DashboardItemType getDashboardItemType() {
		return dashboardItemType;
	}

	public void setDashboardItemType(DashboardItemType dashboardItemType) {
		this.dashboardItemType = dashboardItemType;
	}

	public TaskPlanType getTaskPlanType() {
		return taskPlanType;
	}

	public void setTaskPlanType(TaskPlanType taskPlanType) {
		this.taskPlanType = taskPlanType;
	}

	public long getScheduled() {
		return scheduled;
	}

	public void setScheduled(long scheduled) {
		this.scheduled = scheduled;
	}

	public long getAchieved() {
		return achieved;
	}

	public void setAchieved(long achieved) {
		this.achieved = achieved;
	}

	public boolean getNumberCircle() {
		return numberCircle;
	}

	public void setNumberCircle(boolean numberCircle) {
		this.numberCircle = numberCircle;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}

	public double getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(double targetAmount) {
		this.targetAmount = targetAmount;
	}

	public double getTargetVolume() {
		return targetVolume;
	}

	public void setTargetVolume(double targetVolume) {
		this.targetVolume = targetVolume;
	}

	public double getTargetAchievedAmount() {
		return targetAchievedAmount;
	}

	public void setTargetAchievedAmount(double targetAchievedAmount) {
		this.targetAchievedAmount = targetAchievedAmount;
	}

	public double getTargetAchievedVolume() {
		return targetAchievedVolume;
	}

	public void setTargetAchievedVolume(double targetAchievedVolume) {
		this.targetAchievedVolume = targetAchievedVolume;
	}

	public double getTargetAverageVolume() {
		return targetAverageVolume;
	}

	public void setTargetAverageVolume(double targetAverageVolume) {
		this.targetAverageVolume = targetAverageVolume;
	}

}
