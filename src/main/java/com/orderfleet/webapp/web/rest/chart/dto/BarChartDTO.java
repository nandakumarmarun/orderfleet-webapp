package com.orderfleet.webapp.web.rest.chart.dto;

//refer https://eventbrite.github.io/britecharts/tutorial-bar.html
public class BarChartDTO {

	// required
	private String name;

	// required
	private String value;

	public BarChartDTO(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
