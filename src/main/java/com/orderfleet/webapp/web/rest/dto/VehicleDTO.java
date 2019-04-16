package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.Vehicle;

public class VehicleDTO {

	private String pid;
	private String name;
	private String registrationNumber;
	private String description;
	private String stockLocationPid;
	private String stockLocationName;
	
	
	public VehicleDTO() {
		super();
	}
	
	public VehicleDTO(Vehicle vehicle) {
		super();
		this.pid = vehicle.getPid();
		this.name = vehicle.getName();
		this.registrationNumber = vehicle.getRegistrationNumber();
		this.description = vehicle.getDescription();
		if(vehicle.getStockLocation() != null){
			this.stockLocationPid = vehicle.getStockLocation().getPid();
			this.stockLocationName = vehicle.getStockLocation().getName();
		}
	}


	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRegistrationNumber() {
		return registrationNumber;
	}
	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStockLocationPid() {
		return stockLocationPid;
	}
	public void setStockLocationPid(String stockLocationPid) {
		this.stockLocationPid = stockLocationPid;
	}
	public String getStockLocationName() {
		return stockLocationName;
	}
	public void setStockLocationName(String stockLocationName) {
		this.stockLocationName = stockLocationName;
	}
	
	
}
