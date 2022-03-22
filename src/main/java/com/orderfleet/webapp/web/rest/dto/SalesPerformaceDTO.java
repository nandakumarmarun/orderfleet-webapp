package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Map;

public class SalesPerformaceDTO {

	private List<String> monthList;

	private Map<String, List<SalesTargetGroupUserTargetDTO>> salesTargetGroupUserTargets;
	
	private List<Double> totalList;
	private List<Double> achievedList;

	public List<String> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<String> monthList) {
		this.monthList = monthList;
	}

	public Map<String, List<SalesTargetGroupUserTargetDTO>> getSalesTargetGroupUserTargets() {
		return salesTargetGroupUserTargets;
	}

	public void setSalesTargetGroupUserTargets(
			Map<String, List<SalesTargetGroupUserTargetDTO>> salesTargetGroupUserTargets) {
		this.salesTargetGroupUserTargets = salesTargetGroupUserTargets;
	}

	public List<Double> getTotalList() {
		return totalList;
	}

	public void setTotalList(List<Double> totalList) {
		this.totalList = totalList;
	}

	public List<Double> getAchievedList() {
		return achievedList;
	}

	public void setAchievedList(List<Double> achievedList) {
		this.achievedList = achievedList;
	}

}
