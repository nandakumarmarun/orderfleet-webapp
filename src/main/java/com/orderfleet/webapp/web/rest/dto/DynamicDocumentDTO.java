package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

import java.time.LocalDateTime;
import java.util.List;

public class DynamicDocumentDTO {
    private String pid;
    private String executionPid;

    private String dynamicPid;


    private String documentNumberLocal;

    private String documentNumberServer;

    private String documentPid;

    private String documentName;
    private LocalDateTime documentDate;
     private String activityname;
    private String employeePid;
    private String accountName;

    private String accountPhone;

    private String userName;
    private String formName;

     private List<DynamicData> dynamic;


    public DynamicDocumentDTO() {
    }

    public DynamicDocumentDTO(String pid, String executionPid, String dynamicPid, String documentNumberLocal, String documentNumberServer, String documentPid, String documentName, LocalDateTime documentDate, String activityname, String employeePid, String accountName, String accountPhone, String userName, List<DynamicData> dynamic,String formName) {
        this.pid = pid;
        this.executionPid = executionPid;
        this.dynamicPid = dynamicPid;
        this.documentNumberLocal = documentNumberLocal;
        this.documentNumberServer = documentNumberServer;
        this.documentPid = documentPid;
        this.documentName = documentName;
        this.documentDate = documentDate;
        this.activityname = activityname;
        this.employeePid = employeePid;
        this.accountName = accountName;
        this.accountPhone = accountPhone;
        this.userName = userName;
        this.dynamic = dynamic;
        this.formName = formName;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getExecutionPid() {
        return executionPid;
    }

    public void setExecutionPid(String executionPid) {
        this.executionPid = executionPid;
    }

    public String getDynamicPid() {
        return dynamicPid;
    }

    public void setDynamicPid(String dynamicPid) {
        this.dynamicPid = dynamicPid;
    }

    public String getDocumentNumberLocal() {
        return documentNumberLocal;
    }

    public void setDocumentNumberLocal(String documentNumberLocal) {
        this.documentNumberLocal = documentNumberLocal;
    }

    public String getDocumentNumberServer() {
        return documentNumberServer;
    }

    public void setDocumentNumberServer(String documentNumberServer) {
        this.documentNumberServer = documentNumberServer;
    }

    public String getDocumentPid() {
        return documentPid;
    }

    public void setDocumentPid(String documentPid) {
        this.documentPid = documentPid;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public LocalDateTime getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(LocalDateTime documentDate) {
        this.documentDate = documentDate;
    }

    public String getActivityname() {
        return activityname;
    }

    public void setActivityname(String activityname) {
        this.activityname = activityname;
    }

    public String getEmployeePid() {
        return employeePid;
    }

    public void setEmployeePid(String employeePid) {
        this.employeePid = employeePid;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountPhone() {
        return accountPhone;
    }

    public void setAccountPhone(String accountPhone) {
        this.accountPhone = accountPhone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<DynamicData> getDynamic() {
        return dynamic;
    }

    public void setDynamic(List<DynamicData> dynamic) {
        this.dynamic = dynamic;
    }
}
