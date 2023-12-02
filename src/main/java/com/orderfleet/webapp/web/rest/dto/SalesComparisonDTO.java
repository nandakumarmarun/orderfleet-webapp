package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;

public class SalesComparisonDTO {

    private String salesGroupName;
    private Double targetTotal1;
    private Double targetTotal2;
    private Double achievedTotal1;
    private Double achievedTotal2;

    public String getSalesGroupName() {
        return salesGroupName;
    }

    public void setSalesGroupName(String salesGroupName) {
        this.salesGroupName = salesGroupName;
    }

    public Double getTargetTotal1() {
        return targetTotal1;
    }

    public void setTargetTotal1(Double targetTotal1) {
        this.targetTotal1 = targetTotal1;
    }

    public Double getTargetTotal2() {
        return targetTotal2;
    }

    public void setTargetTotal2(Double targetTotal2) {
        this.targetTotal2 = targetTotal2;
    }

    public Double getAchievedTotal1() {
        return achievedTotal1;
    }

    public void setAchievedTotal1(Double achievedTotal1) {
        this.achievedTotal1 = achievedTotal1;
    }

    public Double getAchievedTotal2() {
        return achievedTotal2;
    }

    public void setAchievedTotal2(Double achievedTotal2) {
        this.achievedTotal2 = achievedTotal2;
    }


    @Override
    public String toString() {
        return "SalesComparisonDTO{" +
                "salesGroupName='" + salesGroupName + '\'' +
                ", targetTotal1=" + targetTotal1 +
                ", targetTotal2=" + targetTotal2 +
                ", achievedTotal1=" + achievedTotal1 +
                ", achievedTotal2=" + achievedTotal2 +
                '}';
    }
}
