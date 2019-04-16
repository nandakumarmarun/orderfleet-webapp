package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Map;

public class SalesPerformaceDTO {

	private List<String> monthList;

	private Map<String, List<SalesTargetGroupUserTargetDTO>> salesTargetGroupUserTargets;

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

}
