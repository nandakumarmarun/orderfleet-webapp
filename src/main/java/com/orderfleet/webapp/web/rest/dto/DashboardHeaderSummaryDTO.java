package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class DashboardHeaderSummaryDTO {

	List<DashboardSummaryDTO> daySummaryDatas;
	List<DashboardSummaryDTO> weekSummaryDatas;
	List<DashboardSummaryDTO> monthSummaryDatas;

	public List<DashboardSummaryDTO> getDaySummaryDatas() {
		return daySummaryDatas;
	}

	public void setDaySummaryDatas(List<DashboardSummaryDTO> daySummaryDatas) {
		this.daySummaryDatas = daySummaryDatas;
	}

	public List<DashboardSummaryDTO> getWeekSummaryDatas() {
		return weekSummaryDatas;
	}

	public void setWeekSummaryDatas(List<DashboardSummaryDTO> weekSummaryDatas) {
		this.weekSummaryDatas = weekSummaryDatas;
	}

	public List<DashboardSummaryDTO> getMonthSummaryDatas() {
		return monthSummaryDatas;
	}

	public void setMonthSummaryDatas(List<DashboardSummaryDTO> monthSummaryDatas) {
		this.monthSummaryDatas = monthSummaryDatas;
	}

}
