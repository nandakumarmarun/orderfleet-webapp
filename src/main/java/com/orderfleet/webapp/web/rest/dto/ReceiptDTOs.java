package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReceiptDTOs {

    private List<AccountingData> accountingData;
    private String pid;
    private String executionPid;
    private String accountingPid;
    private String documentPid;
    private String documentName;

    private long documentId;

    private String accountProfileName;
    private String userName;
    private LocalDateTime createdDate;

    private double totalAmount;
    private double outstandingAmount;
    private String documentNumberServer;

   private String remarks;

   private Double documentTotal;

    public ReceiptDTOs() {
    }

    public ReceiptDTOs(List<AccountingData> accountingData, String pid, String executionPid, String accountingPid, String documentPid, String documentName, long documentId, String accountProfileName, String userName, LocalDateTime createdDate, double totalAmount, double outstandingAmount, String documentNumberServer, String remarks) {
        this.accountingData = accountingData;
        this.pid = pid;
        this.executionPid = executionPid;
        this.accountingPid = accountingPid;
        this.documentPid = documentPid;
        this.documentName = documentName;
        this.documentId = documentId;
        this.accountProfileName = accountProfileName;
        this.userName = userName;
        this.createdDate = createdDate;
        this.totalAmount = totalAmount;
        this.outstandingAmount = outstandingAmount;
        this.documentNumberServer = documentNumberServer;
        this.remarks = remarks;
    }

    public List<AccountingData> getAccountingData() {
        return accountingData;
    }

    public void setAccountingData(List<AccountingData> accountingData) {
        this.accountingData = accountingData;
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

    public String getAccountingPid() {
        return accountingPid;
    }

    public void setAccountingPid(String accountingPid) {
        this.accountingPid = accountingPid;
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

    public long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(long documentId) {
        this.documentId = documentId;
    }

    public String getAccountProfileName() {
        return accountProfileName;
    }

    public void setAccountProfileName(String accountProfileName) {
        this.accountProfileName = accountProfileName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(double outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public String getDocumentNumberServer() {
        return documentNumberServer;
    }

    public void setDocumentNumberServer(String documentNumberServer) {
        this.documentNumberServer = documentNumberServer;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Double getDocumentTotal() {
        return documentTotal;
    }

    public void setDocumentTotal(Double documentTotal) {
        this.documentTotal = documentTotal;
    }
}
