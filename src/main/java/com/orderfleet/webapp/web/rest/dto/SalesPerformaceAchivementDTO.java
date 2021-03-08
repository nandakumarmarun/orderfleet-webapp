package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Map;

public class SalesPerformaceAchivementDTO {

	private List<String> monthList;

	private Map<String, List<SalesProductGroupUserTargetDTO>> salesProductGroupUserTargets;

	public List<String> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<String> monthList) {
		this.monthList = monthList;
	}

	public Map<String, List<SalesProductGroupUserTargetDTO>> getSalesProductGroupUserTargets() {
		return salesProductGroupUserTargets;
	}

	public void setSalesProductGroupUserTargets(
			Map<String, List<SalesProductGroupUserTargetDTO>> salesProductGroupUserTargets) {
		this.salesProductGroupUserTargets = salesProductGroupUserTargets;
	}

	@Override
	public String toString() {
		return "SalesPerformaceAchivementDTO [monthList=" + monthList + ", salesProductGroupUserTargets="
				+ salesProductGroupUserTargets + "]";
	}

}
