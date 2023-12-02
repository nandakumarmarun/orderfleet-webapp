package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.util.List;

public class SalesComparisonList {
    private String fromDate1;
    private String fromDate2;
    private String toDate;
    private String toDate1;
    private String period;
    private List<SalesComparisonDTO> salesComparisonDTOS;

    public String getFromDate1() {
        return fromDate1;
    }

    public void setFromDate1(String fromDate1) {
        this.fromDate1 = fromDate1;
    }

    public String getFromDate2() {
        return fromDate2;
    }

    public void setFromDate2(String fromDate2) {
        this.fromDate2 = fromDate2;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getToDate1() {
        return toDate1;
    }

    public void setToDate1(String toDate1) {
        this.toDate1 = toDate1;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public List<SalesComparisonDTO> getSalesComparisonDTOS() {
        return salesComparisonDTOS;
    }

    public void setSalesComparisonDTOS(List<SalesComparisonDTO> salesComparisonDTOS) {
        this.salesComparisonDTOS = salesComparisonDTOS;
    }


    @Override
    public String toString() {
        return "SalesComparisonList{" +
                "fromDate1='" + fromDate1 + '\'' +
                ", fromDate2='" + fromDate2 + '\'' +
                ", toDate='" + toDate + '\'' +
                ", toDate1='" + toDate1 + '\'' +
                ", period='" + period + '\'' +
                ", salesComparisonDTOS=" + salesComparisonDTOS +
                '}';
    }
}
