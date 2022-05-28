package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.Vehicle;

public class EmployeeVehicleAssosiationHistoryDTO {

	private String pid;

	private EmployeeProfile employeeProfile;

	private String registrationNumber;

	@ManyToOne
	private String employeeName;

	private Vehicle vehicle;

	private Company company;

	private LocalDateTime createdDate;

	public EmployeeVehicleAssosiationHistoryDTO() {
		super();
	}

	public EmployeeVehicleAssosiationHistoryDTO(String pid, EmployeeProfile employeeProfile, Vehicle vehicle,
			Company company, LocalDateTime createdDate) {
		super();
		this.pid = pid;
		this.employeeProfile = employeeProfile;
		this.vehicle = vehicle;
		this.company = company;
		this.createdDate = createdDate;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public EmployeeProfile getEmployeeProfile() {
		return employeeProfile;
	}

	public void setEmployeeProfile(EmployeeProfile employeeProfile) {
		this.employeeProfile = employeeProfile;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	@Override
	public String toString() {
		return "EmployeeVehicleAssosiationHistoryDTO [pid=" + pid + ", employeeProfile=" + employeeProfile
				+ ", registrationNumber=" + registrationNumber + ", employeeName=" + employeeName + ", vehicle="
				+ vehicle + ", company=" + company + ", createdDate=" + createdDate + "]";
	}

}
