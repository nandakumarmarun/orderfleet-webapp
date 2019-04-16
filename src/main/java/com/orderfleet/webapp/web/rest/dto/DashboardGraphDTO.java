package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class DashboardGraphDTO {

	private List<String> labels;
	
	private List<Object[]> lineGraphDatas;

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public List<Object[]> getLineGraphDatas() {
		return lineGraphDatas;
	}

	public void setLineGraphDatas(List<Object[]> lineGraphDatas) {
		this.lineGraphDatas = lineGraphDatas;
	}

}
