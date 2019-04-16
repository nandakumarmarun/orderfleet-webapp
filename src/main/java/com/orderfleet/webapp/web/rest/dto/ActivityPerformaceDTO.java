package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Map;

public class ActivityPerformaceDTO {
	
	private List<String> monthList;
	
	private Map<String, List<ActivityUserTargetDTO>> activityUserTargets;
	
	public List<String> getMonthList() {
		return monthList;
	}
	
	public void setMonthList(List<String> monthList) {
		this.monthList = monthList;
	}

	public Map<String, List<ActivityUserTargetDTO>> getActivityUserTargets() {
		return activityUserTargets;
	}

	public void setActivityUserTargets(Map<String, List<ActivityUserTargetDTO>> activityUserTargets) {
		this.activityUserTargets = activityUserTargets;
	}
	
}
