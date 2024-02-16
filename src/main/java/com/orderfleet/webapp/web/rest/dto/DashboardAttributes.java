package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class DashboardAttributes {

    private List<DashboardSummaryDTO> dashboardSummaryDTOS;


    public List<DashboardSummaryDTO> getDashboardSummaryDTOS() {
        return dashboardSummaryDTOS;
    }

    public void setDashboardSummaryDTOS(List<DashboardSummaryDTO> dashboardSummaryDTOS) {
        this.dashboardSummaryDTOS = dashboardSummaryDTOS;
    }
}
