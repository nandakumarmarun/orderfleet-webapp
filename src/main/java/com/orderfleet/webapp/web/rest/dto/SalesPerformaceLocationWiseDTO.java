package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Map;

public class SalesPerformaceLocationWiseDTO {

	private List<String> dateHeaderList;

	private Map<String, List<Double>> salesGroupAchieved;

	public List<String> getDateHeaderList() {
		return dateHeaderList;
	}

	public void setDateHeaderList(List<String> dateHeaderList) {
		this.dateHeaderList = dateHeaderList;
	}

	public Map<String, List<Double>> getSalesGroupAchieved() {
		return salesGroupAchieved;
	}

	public void setSalesGroupAchieved(Map<String, List<Double>> salesGroupAchieved) {
		this.salesGroupAchieved = salesGroupAchieved;
	}

}
