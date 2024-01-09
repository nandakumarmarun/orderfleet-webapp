package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class SalesOrderDTOs {

    private List<InventoryData> inventoryData;
    private String pid;
    private String executionPid;
    private String inventoryPid;

    private String userName;

    private String documentNumberServer;

    private String documentPid;

    private String documentName;
    private LocalDateTime documentDate;

    private String receiverAccountPid;

    private String receiverAccountName;

    private String supplierAccountPid;
    private String supplierAccountName;
    private String employeePid;
    private double documentTotal;
    private double documentVolume;
    private double docDiscountPercentage;
    private double docDiscountAmount;


    public SalesOrderDTOs() {
    }

    public SalesOrderDTOs(List<InventoryData> inventoryData, String pid, String executionPid, String inventoryPid, String userName, String documentNumberServer, String documentPid, String documentName, LocalDateTime documentDate, String receiverAccountPid, String receiverAccountName, String supplierAccountPid, String supplierAccountName, String employeePid, double documentTotal, double documentVolume, double docDiscountPercentage, double docDiscountAmount) {
        this.inventoryData = inventoryData;
        this.pid = pid;
        this.executionPid = executionPid;
        this.inventoryPid = inventoryPid;
        this.userName = userName;
        this.documentNumberServer = documentNumberServer;
        this.documentPid = documentPid;
        this.documentName = documentName;
        this.documentDate = documentDate;
        this.receiverAccountPid = receiverAccountPid;
        this.receiverAccountName = receiverAccountName;
        this.supplierAccountPid = supplierAccountPid;
        this.supplierAccountName = supplierAccountName;
        this.employeePid = employeePid;
        this.documentTotal = documentTotal;
        this.documentVolume = documentVolume;
        this.docDiscountPercentage = docDiscountPercentage;
        this.docDiscountAmount = docDiscountAmount;
    }

    public List<InventoryData> getInventoryData() {
        return inventoryData;
    }

    public void setInventoryData(List<InventoryData> inventoryData) {
        this.inventoryData = inventoryData;
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

    public String getInventoryPid() {
        return inventoryPid;
    }

    public void setInventoryPid(String inventoryPid) {
        this.inventoryPid = inventoryPid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getReceiverAccountPid() {
        return receiverAccountPid;
    }

    public void setReceiverAccountPid(String receiverAccountPid) {
        this.receiverAccountPid = receiverAccountPid;
    }

    public String getReceiverAccountName() {
        return receiverAccountName;
    }

    public void setReceiverAccountName(String receiverAccountName) {
        this.receiverAccountName = receiverAccountName;
    }

    public String getSupplierAccountPid() {
        return supplierAccountPid;
    }

    public void setSupplierAccountPid(String supplierAccountPid) {
        this.supplierAccountPid = supplierAccountPid;
    }

    public String getSupplierAccountName() {
        return supplierAccountName;
    }

    public void setSupplierAccountName(String supplierAccountName) {
        this.supplierAccountName = supplierAccountName;
    }

    public String getEmployeePid() {
        return employeePid;
    }

    public void setEmployeePid(String employeePid) {
        this.employeePid = employeePid;
    }

    public double getDocumentTotal() {
        return documentTotal;
    }

    public void setDocumentTotal(double documentTotal) {
        this.documentTotal = documentTotal;
    }

    public double getDocumentVolume() {
        return documentVolume;
    }

    public void setDocumentVolume(double documentVolume) {
        this.documentVolume = documentVolume;
    }

    public double getDocDiscountPercentage() {
        return docDiscountPercentage;
    }

    public void setDocDiscountPercentage(double docDiscountPercentage) {
        this.docDiscountPercentage = docDiscountPercentage;
    }

    public double getDocDiscountAmount() {
        return docDiscountAmount;
    }

    public void setDocDiscountAmount(double docDiscountAmount) {
        this.docDiscountAmount = docDiscountAmount;
    }
}
