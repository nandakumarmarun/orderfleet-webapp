package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.enums.SalesOrderStatus;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

public class SalesOrderStatusReportDTO {
	
	private String pid;

	private String documentNumberLocal;

	private String documentNumberServer;
	
	private LocalDateTime serverDate;
	
	private LocalDateTime deliveryDate;

	private String receiverAccountPid;

	private String receiverAccountName;
	
	private String receiverAccountLocation;
	
	private String employeePid;

	private String employeeName;
	
	private String userName;
	
	private String district;
	
	private String location;
	
	private String exicutiveTaskExecutionPid;
	
	private BigDecimal accountProfileLatitude;

	private BigDecimal accountProfileLongitude;
	
	private String territory;
	
	private String territoryPid;
	
	private double documentTotal;
	
	private SalesOrderStatus salesOrderStatus;
	
	private TallyDownloadStatus tallyDownloadStatus;
	
	public String getExicutiveTaskExecutionPid() {
		return exicutiveTaskExecutionPid;
	}

	public void setExicutiveTaskExecutionPid(String exicutiveTaskExecutionPid) {
		this.exicutiveTaskExecutionPid = exicutiveTaskExecutionPid;
	}

	public SalesOrderStatus getSalesOrderStatus() {
		return salesOrderStatus;
	}

	public void setSalesOrderStatus(SalesOrderStatus salesOrderStatus) {
		this.salesOrderStatus = salesOrderStatus;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
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

	public String getReceiverAccountLocation() {
		return receiverAccountLocation;
	}

	public void setReceiverAccountLocation(String receiverAccountLocation) {
		this.receiverAccountLocation = receiverAccountLocation;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTerritoryPid() {
		return territoryPid;
	}

	public void setTerritoryPid(String territoryPid) {
		this.territoryPid = territoryPid;
	}

	public double getDocumentTotal() {
		return documentTotal;
	}

	public void setDocumentTotal(double documentTotal) {
		this.documentTotal = documentTotal;
	}

	public LocalDateTime getServerDate() {
		return serverDate;
	}

	public void setServerDate(LocalDateTime serverDate) {
		this.serverDate = serverDate;
	}

	public LocalDateTime getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(LocalDateTime deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getTerritory() {
		return territory;
	}

	public void setTerritory(String territory) {
		this.territory = territory;
	}

	public TallyDownloadStatus getTallyDownloadStatus() {
		return tallyDownloadStatus;
	}

	public void setTallyDownloadStatus(TallyDownloadStatus tallyDownloadStatus) {
		this.tallyDownloadStatus = tallyDownloadStatus;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public BigDecimal getAccountProfileLatitude() {
		return accountProfileLatitude;
	}

	public void setAccountProfileLatitude(BigDecimal accountProfileLatitude) {
		this.accountProfileLatitude = accountProfileLatitude;
	}

	public BigDecimal getAccountProfileLongitude() {
		return accountProfileLongitude;
	}

	public void setAccountProfileLongitude(BigDecimal accountProfileLongitude) {
		this.accountProfileLongitude = accountProfileLongitude;
	}
}
