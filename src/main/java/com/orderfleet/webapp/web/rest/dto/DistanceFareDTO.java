package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.DistanceFare;

public class DistanceFareDTO {

	private String pid;
	private String vehicleType;
	private Double fare;
	
	public DistanceFareDTO() {
		super();
	}
	
	public DistanceFareDTO(DistanceFare distanceFare) {
		super();
		this.pid = distanceFare.getPid();
		this.vehicleType = distanceFare.getVehicleType();
		this.fare = distanceFare.getFare();
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public Double getFare() {
		return fare;
	}
	public void setFare(Double fare) {
		this.fare = fare;
	}
	
	
}
