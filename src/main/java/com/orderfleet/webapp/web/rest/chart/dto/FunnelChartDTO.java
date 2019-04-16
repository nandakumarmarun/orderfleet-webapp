package com.orderfleet.webapp.web.rest.chart.dto;

//refer https://github.com/jakezatecky/d3-funnel
public class FunnelChartDTO {

	//required
	private String label;
	
	//required
	private String value;
	
	//Hex only
	private String backgroundColor;
	
	//eg: 'USD: $150'
	private String formattedValue;
	
	private boolean hideLabel;
	
	//Hex only
	private String labelColor;

	public FunnelChartDTO(String label, String value) {
		super();
		this.label = label;
		this.value = value;
	}

	public FunnelChartDTO(String label, String value, String backgroundColor) {
		super();
		this.label = label;
		this.value = value;
		this.backgroundColor = backgroundColor;
	}
			
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getFormattedValue() {
		return formattedValue;
	}

	public void setFormattedValue(String formattedValue) {
		this.formattedValue = formattedValue;
	}

	public boolean isHideLabel() {
		return hideLabel;
	}

	public void setHideLabel(boolean hideLabel) {
		this.hideLabel = hideLabel;
	}

	public String getLabelColor() {
		return labelColor;
	}

	public void setLabelColor(String labelColor) {
		this.labelColor = labelColor;
	}
}
