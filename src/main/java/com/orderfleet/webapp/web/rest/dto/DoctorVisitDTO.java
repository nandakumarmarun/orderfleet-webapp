package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DoctorVisitDTO {

	private String empName;

	private String doctorVisitNo;

	private String attendance;

	private String startTime;

	private String endTime;

	private String route;

	private Double totalDistance;

	private Double fare;

	private String travelExpense;

	private String foodExpense;

	private String totalExpense;

	private String totalTravelled;

	public DoctorVisitDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DoctorVisitDTO(String empName, String doctorVisitNo, String attendance, String startTime,
			String endTime, String route, Double totalDistance, Double fare, String travelExpense,
			String foodExpense, String totalExpense, String totalTravelled) {
		super();
		this.empName = empName;
		this.doctorVisitNo = doctorVisitNo;
		this.attendance = attendance;
		this.startTime = startTime;
		this.endTime = endTime;
		this.route = route;
		this.totalDistance = totalDistance;
		this.fare = fare;
		this.travelExpense = travelExpense;
		this.foodExpense = foodExpense;
		this.totalExpense = totalExpense;
		this.totalTravelled = totalTravelled;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getDoctorVisitNo() {
		return doctorVisitNo;
	}

	public void setDoctorVisitNo(String doctorVisitNo) {
		this.doctorVisitNo = doctorVisitNo;
	}

	

	public String getAttendance() {
		return attendance;
	}

	public void setAttendance(String attendance) {
		this.attendance = attendance;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public Double getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(Double totalDistance) {
		this.totalDistance = totalDistance;
	}

	public Double getFare() {
		return fare;
	}

	public void setFare(Double fare) {
		this.fare = fare;
	}

	public String getTravelExpense() {
		return travelExpense;
	}

	public void setTravelExpense(String travelExpense) {
		this.travelExpense = travelExpense;
	}

	public String getFoodExpense() {
		return foodExpense;
	}

	public void setFoodExpense(String foodExpense) {
		this.foodExpense = foodExpense;
	}

	public String getTotalExpense() {
		return totalExpense;
	}

	public void setTotalExpense(String totalExpense) {
		this.totalExpense = totalExpense;
	}

	public String getTotalTravelled() {
		return totalTravelled;
	}

	public void setTotalTravelled(String totalTravelled) {
		this.totalTravelled = totalTravelled;
	}

	@Override
	public String toString() {
		return "DoctorVisitDTO [empName=" + empName + ", doctorVisitNo=" + doctorVisitNo + ", attendance=" + attendance
				+ ", startTime=" + startTime + ", endTime=" + endTime + ", route=" + route + ", totalDistance="
				+ totalDistance + ", fare=" + fare + ", travelExpense=" + travelExpense + ", foodExpense=" + foodExpense
				+ ", totalExpense=" + totalExpense + ", totalTravelled=" + totalTravelled + "]";
	}

}
