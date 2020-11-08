package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Map;

public class LocationWiseProductGroupPerformanceDTO {
	private List<String> firstMonthList;

	private Map<String, List<ProductGroupLocationTargetDTO>> firstProductGroupLocationTargets;
	
	private List<String> secondMonthList;

	private Map<String, List<ProductGroupLocationTargetDTO>> secondProductGroupLocationTargets;

	public List<String> getFirstMonthList() {
		return firstMonthList;
	}

	public void setFirstMonthList(List<String> firstMonthList) {
		this.firstMonthList = firstMonthList;
	}

	public Map<String, List<ProductGroupLocationTargetDTO>> getFirstProductGroupLocationTargets() {
		return firstProductGroupLocationTargets;
	}

	public void setFirstProductGroupLocationTargets(
			Map<String, List<ProductGroupLocationTargetDTO>> firstProductGroupLocationTargets) {
		this.firstProductGroupLocationTargets = firstProductGroupLocationTargets;
	}

	public List<String> getSecondMonthList() {
		return secondMonthList;
	}

	public void setSecondMonthList(List<String> secondMonthList) {
		this.secondMonthList = secondMonthList;
	}

	public Map<String, List<ProductGroupLocationTargetDTO>> getSecondProductGroupLocationTargets() {
		return secondProductGroupLocationTargets;
	}

	public void setSecondProductGroupLocationTargets(
			Map<String, List<ProductGroupLocationTargetDTO>> secondProductGroupLocationTargets) {
		this.secondProductGroupLocationTargets = secondProductGroupLocationTargets;
	}
	
	
}
