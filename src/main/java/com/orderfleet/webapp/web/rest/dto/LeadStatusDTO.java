package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LeadStatusDTO {

    private String employeeName;
    private String accountName;
    private LocalDateTime createdDate;
    private String leadStatus;
    private Double dealVolume;
    private Double wonVolume;
    private Double lostVolume;
    private Double balanceDealVolume;
    private String documentName;

    private  String pid;

    public LeadStatusDTO() {
    }

    public LeadStatusDTO(String employeeName, String accountName, LocalDateTime createdDate, String leadStatus, Double dealVolume, Double wonVolume, Double lostVolume, Double balanceDealVolume, String documentName, String pid) {
        this.employeeName = employeeName;
        this.accountName = accountName;
        this.createdDate = createdDate;

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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }


    public String getLeadStatus() {
        return leadStatus;
    }

    public void setLeadStatus(String leadStatus) {
        this.leadStatus = leadStatus;
    }

    public Double getDealVolume() {
        return dealVolume;
    }

    public void setDealVolume(Double dealVolume) {
        this.dealVolume = dealVolume;
    }

    public Double getWonVolume() {
        return wonVolume;
    }

    public void setWonVolume(Double wonVolume) {
        this.wonVolume = wonVolume;
    }

    public Double getLostVolume() {
        return lostVolume;
    }

    public void setLostVolume(Double lostVolume) {
        this.lostVolume = lostVolume;
    }

    public Double getBalanceDealVolume() {
        return balanceDealVolume;
    }

    public void setBalanceDealVolume(Double balanceDealVolume) {
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
