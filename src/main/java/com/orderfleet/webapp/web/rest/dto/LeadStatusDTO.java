package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LeadStatusDTO {

    private String employeeName;
    private String accountName;
    private LocalDate createdDate;
    private LocalTime createdTime;
    private String leadStatus;
    private String dealVolume;
    private String wonVolume;
    private String lostVolume;
    private String balanceDealVolume;
    private String documentName;

    private  String pid;

    public LeadStatusDTO() {
    }

    public LeadStatusDTO(String employeeName, String accountName, LocalDate createdDate, LocalTime createdTime, String leadStatus, String dealVolume, String wonVolume, String lostVolume, String balanceDealVolume, String documentName, String pid) {
        this.employeeName = employeeName;
        this.accountName = accountName;
        this.createdDate = createdDate;
        this.createdTime = createdTime;
        this.leadStatus = leadStatus;
        this.dealVolume = dealVolume;
        this.wonVolume = wonVolume;
        this.lostVolume = lostVolume;
        this.balanceDealVolume = balanceDealVolume;
        this.documentName = documentName;
        this.pid = pid;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getLeadStatus() {
        return leadStatus;
    }

    public void setLeadStatus(String leadStatus) {
        this.leadStatus = leadStatus;
    }

    public String getDealVolume() {
        return dealVolume;
    }

    public void setDealVolume(String dealVolume) {
        this.dealVolume = dealVolume;
    }

    public String getWonVolume() {
        return wonVolume;
    }

    public void setWonVolume(String wonVolume) {
        this.wonVolume = wonVolume;
    }

    public String getLostVolume() {
        return lostVolume;
    }

    public void setLostVolume(String lostVolume) {
        this.lostVolume = lostVolume;
    }

    public String getBalanceDealVolume() {
        return balanceDealVolume;
    }

    public void setBalanceDealVolume(String balanceDealVolume) {
        this.balanceDealVolume = balanceDealVolume;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "LeadStatusDTO{" +
                "employeeName='" + employeeName + '\'' +
                ", accountName='" + accountName + '\'' +
                ", createdDate=" + createdDate +
                ", createdTime=" + createdTime +
                ", leadStatus='" + leadStatus + '\'' +
                ", dealVolume=" + dealVolume +
                ", wonVolume=" + wonVolume +
                ", lostVolume=" + lostVolume +
                ", balanceDealVolume=" + balanceDealVolume +
                ", documentName='" + documentName + '\'' +
                ", pid='" + pid + '\'' +
                '}';
    }
}
