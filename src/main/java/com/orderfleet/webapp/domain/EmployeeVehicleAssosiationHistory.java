package com.orderfleet.webapp.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_user_vehicle_association_History")
public class EmployeeVehicleAssosiationHistory {

	@Id
	@GenericGenerator(name = "seq_user_vehicle_association_History_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_vehicle_association_History_id") })
	@GeneratedValue(generator = "seq_user_vehicle_association_History_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_vehicle_association_History_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;
	
	@Column(name = "employee_name", nullable = false)
	private String employeeName;
	
	@ManyToOne
	private EmployeeProfile employeeProfile;

	@Column(name = "registration_number", nullable = false)
	private String registrationNumber;
	
	@ManyToOne
	private Vehicle vehicle;

	@ManyToOne
	@NotNull
	private Company company;

	private LocalDateTime createdDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	@Override
	public String toString() {
		return "EmployeeVehicleAssosiationHistory [id=" + id + ", pid=" + pid + ", employeeName=" + employeeName
				+ ", employeeProfile=" + employeeProfile + ", registrationNumber=" + registrationNumber + ", vehicle="
				+ vehicle + ", company=" + company + ", createdDate=" + createdDate + "]";
	}

}
