package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

public class QueryTime {

    private  LocalDateTime startLCTime;
    private String startTime;
    private String startDate;

    public QueryTime(LocalDateTime startLCTime, String startTime, String startDate) {
        this.startLCTime = startLCTime;
        this.startTime = startTime;
        this.startDate = startDate;
    }

    public LocalDateTime getStartLCTime() {
        return startLCTime;
    }

    public void setStartLCTime(LocalDateTime startLCTime) {
        this.startLCTime = startLCTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "QueryTime{" +
                "startLCTime=" + startLCTime +
                ", startTime='" + startTime + '\'' +
                ", startDate='" + startDate + '\'' +
                '}';
    }
}
