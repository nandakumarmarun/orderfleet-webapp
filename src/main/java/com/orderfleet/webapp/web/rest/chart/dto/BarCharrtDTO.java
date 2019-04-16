package com.orderfleet.webapp.web.rest.chart.dto;

import java.util.ArrayList;
import java.util.List;

public class BarCharrtDTO {
	
	private String titleText;
	//chart bar label
	List<String> labels;
	//chart bar color
	List<String> colors;
	
	List<Object[]> barChartDatas = new ArrayList<>();
	
	public String getTitleText() {
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public List<String> getColors() {
		return colors;
	}

	public void setColors(List<String> colors) {
		this.colors = colors;
	}

	public List<Object[]> getBarChartDatas() {
		return barChartDatas;
	}

	public void setBarChartDatas(List<Object[]> barChartDatas) {
		this.barChartDatas = barChartDatas;
	}

}
