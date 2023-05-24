package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Map;

public class SalesPerformaceDTO {

	private List<String> monthList;

	private Map<String, List<SalesTargetGroupUserTargetDTO>> salesTargetGroupUserTargets;
	
	private List<Double> totalList;
	private List<Double> achievedList;
	
	private List<Map<String, List<SalesTargetGroupUserTargetDTO>>> salesTargetFinalList;
	
	private Map<String, List<Map<String, SalesTargetTotalDTO>>>finalList;

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

	public List<Map<String, List<SalesTargetGroupUserTargetDTO>>> getSalesTargetFinalList() {
		return salesTargetFinalList;
	}

	public void setSalesTargetFinalList(List<Map<String, List<SalesTargetGroupUserTargetDTO>>> salesTargetFinalList) {
		this.salesTargetFinalList = salesTargetFinalList;
	}

	public Map<String, List<Map<String, SalesTargetTotalDTO>>> getFinalList() {
		return finalList;
	}

	public void setFinalList(Map<String, List<Map<String, SalesTargetTotalDTO>>> finalList) {
		this.finalList = finalList;
	}

	
		
	}

	

