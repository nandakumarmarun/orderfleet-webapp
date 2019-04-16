package com.orderfleet.webapp.web.rest.chart.dto;

import java.util.List;

public class BarChartDataDTO {

	private String label;
	
	private List<String> datas;
	
	public BarChartDataDTO(String label, List<String> datas) {
		super();
		this.label = label;
		this.datas = datas;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<String> getDatas() {
		return datas;
	}

	public void setDatas(List<String> datas) {
		this.datas = datas;
	}
	
}
