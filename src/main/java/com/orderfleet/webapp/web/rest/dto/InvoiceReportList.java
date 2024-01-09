package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.LocationType;

import java.time.LocalDateTime;
import java.util.List;

public class InvoiceReportList {
        private String employeeName;
        private String accountProfileName;
        private String activityName;
        private LocalDateTime punchIn;
        private LocalDateTime clientDate;
        private String timeSpend;
        private LocalDateTime createdDate;
        private String location;
        private String towerLocation;
        private String visitType;
        private boolean withCustomer;
        private Double salesOrderTotalAmount;
        private Double receiptAmount;
        private String vehicleNumber;
        private String remarks;
        private LocationType locationType;
       private boolean mockLocationStatus;
       private DocumentType documentType;
       private String documentName;
       private Double documentTotal;
       private String executionPid;
       private String pid;




    public InvoiceReportList() {

    }

    public InvoiceReportList(String employeeName, String accountProfileName, String activityName, LocalDateTime punchIn, LocalDateTime clientDate, String timeSpend, LocalDateTime createdDate, String location, String towerLocation, String visitType, boolean withCustomer, Double salesOrderTotalAmount, Double receiptAmount, String vehicleNumber, String remarks, LocationType locationType, boolean mockLocationStatus, DocumentType documentType, String documentName, Double documentTotal, String executionPid, String pid) {
        this.employeeName = employeeName;
        this.accountProfileName = accountProfileName;
        this.activityName = activityName;
        this.punchIn = punchIn;
        this.clientDate = clientDate;
        this.timeSpend = timeSpend;
        this.createdDate = createdDate;
        this.location = location;
        this.towerLocation = towerLocation;
        this.visitType = visitType;
        this.withCustomer = withCustomer;
        this.salesOrderTotalAmount = salesOrderTotalAmount;
        this.receiptAmount = receiptAmount;
        this.vehicleNumber = vehicleNumber;
        this.remarks = remarks;
        this.locationType = locationType;
        this.mockLocationStatus = mockLocationStatus;
        this.documentType = documentType;
        this.documentName = documentName;
        this.documentTotal = documentTotal;
        this.executionPid = executionPid;
        this.pid = pid;
    }

    public InvoiceReportList(ExecutiveTaskExecution execution) {
            this.location = execution.getLocation();
            this.towerLocation= execution.getTowerLocation();
    }

    public String getExecutionPid() {
        return executionPid;
    }

    public void setExecutionPid(String executionPid) {
        this.executionPid = executionPid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public Double getDocumentTotal() {
        return documentTotal;
    }

    public void setDocumentTotal(Double documentTotal) {
        this.documentTotal = documentTotal;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getEmployeeName() {
                return employeeName;
        }

        public void setEmployeeName(String employeeName) {
                this.employeeName = employeeName;
        }

        public String getAccountProfileName() {
                return accountProfileName;
        }

        public void setAccountProfileName(String accountProfileName) {
                this.accountProfileName = accountProfileName;
        }

        public String getActivityName() {
                return activityName;
        }

        public void setActivityName(String activityName) {
                this.activityName = activityName;
        }

        public LocalDateTime getPunchIn() {
                return punchIn;
        }

        public void setPunchIn(LocalDateTime punchIn) {
                this.punchIn = punchIn;
        }

        public LocalDateTime getClientDate() {
                return clientDate;
        }

        public void setClientDate(LocalDateTime clientDate) {
                this.clientDate = clientDate;
        }

        public String getTimeSpend() {
                return timeSpend;
        }

        public void setTimeSpend(String timeSpend) {
                this.timeSpend = timeSpend;
        }


        public String getLocation() {
                return location;
        }

        public void setLocation(String location) {
                this.location = location;
        }

        public String getTowerLocation(String towerLocation) {
                return this.towerLocation;
        }

        public void setTowerLocation(String towerLocation) {
                this.towerLocation = towerLocation;
        }

        public String getVisitType() {
                return visitType;
        }

        public void setVisitType(String visitType) {
                this.visitType = visitType;
        }

        public boolean isWithCustomer(boolean withCustomer) {
                return this.withCustomer;
        }

        public void setWithCustomer(boolean withCustomer) {
                this.withCustomer = withCustomer;
        }
        public Double getReceiptAmount() {
                return receiptAmount;
        }

        public void setReceiptAmount(Double receiptAmount) {
                this.receiptAmount = receiptAmount;
        }

        public String getVehicleNumber() {
                return vehicleNumber;
        }

        public void setVehicleNumber(String vehicleNumber) {
                this.vehicleNumber = vehicleNumber;
        }

        public String getRemarks() {
                return remarks;
        }

        public void setRemarks(String remarks) {
                this.remarks = remarks;
        }

    public String getTowerLocation() {
        return towerLocation;
    }

    public boolean isWithCustomer() {
        return withCustomer;
    }

    public boolean isMockLocationStatus() {
        return mockLocationStatus;
    }

    public void setMockLocationStatus(boolean mockLocationStatus) {
        this.mockLocationStatus = mockLocationStatus;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Double getSalesOrderTotalAmount() {
        return salesOrderTotalAmount;
    }

    public void setSalesOrderTotalAmount(Double salesOrderTotalAmount) {
        this.salesOrderTotalAmount = salesOrderTotalAmount;
    }
}
