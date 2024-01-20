package com.orderfleet.webapp.web.rest.api.dto;

import java.time.LocalDate;

public class UserTargetMonthDTO {

    private LocalDate CurrentMonth;
    private String userPid;
    private String userName;
    private String companyId;
    private String employeePid;
    private String employeeName;
    private Double userTarget;

    public LocalDate getCurrentMonth() {
        return CurrentMonth;
    }

    public void setCurrentMonth(LocalDate currentMonth) {
        CurrentMonth = currentMonth;
    }

    public String getUserPid() {
        return userPid;
    }

    public void setUserPid(String userPid) {
        this.userPid = userPid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getEmployeePid() {
        return employeePid;
    }

    public void setEmployeePid(String employeePid) {
        this.employeePid = employeePid;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }


    public Double getUserTarget() {
        return userTarget;
    }

    public void setUserTarget(Double userTarget) {
        this.userTarget = userTarget;
    }
}
