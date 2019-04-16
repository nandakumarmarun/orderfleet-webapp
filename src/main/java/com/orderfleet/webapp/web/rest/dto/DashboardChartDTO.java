package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

import com.orderfleet.webapp.domain.enums.ChartType;
import com.orderfleet.webapp.web.rest.chart.dto.BarChartDTO;
import com.orderfleet.webapp.web.rest.chart.dto.FunnelChartDTO;

public class DashboardChartDTO {
	
	private String chartLabel = "";
	
	private ChartType chartType;
	
	private List<FunnelChartDTO> funnelChartDtos;
	
	private List<BarChartDTO> barChartDtos;

	public String getChartLabel() {
		return chartLabel;
	}

	public void setChartLabel(String chartLabel) {
		this.chartLabel = chartLabel;
	}

	public ChartType getChartType() {
		return chartType;
	}

	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}

	public List<FunnelChartDTO> getFunnelChartDtos() {
		return funnelChartDtos;
	}

	public void setFunnelChartDtos(List<FunnelChartDTO> funnelChartDtos) {
		this.funnelChartDtos = funnelChartDtos;
	}

	public List<BarChartDTO> getBarChartDtos() {
		return barChartDtos;
	}

	public void setBarChartDtos(List<BarChartDTO> barChartDtos) {
		this.barChartDtos = barChartDtos;
	}

}
