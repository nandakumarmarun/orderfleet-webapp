package com.orderfleet.webapp.domain.model;

import java.io.Serializable;

/**
 * This is not a normal entity. this is just a dependency for CompanySetting
 * persisted inside a JSON column
 * 
 * @author Shaheer
 * @since October 21, 2016
 * 
 * @see CompanySetting
 * @see ObjectType
 *
 */
public class DashboardConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	private String dashboardView;

	private int delayTime;

	private int delayTimeTerritory;

	public DashboardConfiguration() {
		super();
	}

	public int getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}

	public String getDashboardView() {
		return dashboardView;
	}

	public void setDashboardView(String dashboardView) {
		this.dashboardView = dashboardView;
	}

	public int getDelayTimeTerritory() {
		return delayTimeTerritory;
	}

	public void setDelayTimeTerritory(int delayTimeTerritory) {
		this.delayTimeTerritory = delayTimeTerritory;
	}
}
