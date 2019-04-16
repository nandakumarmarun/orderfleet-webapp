
package com.orderfleet.webapp.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Anish
 *
 */
@Entity
@Table(name = "tbl_kilometer_calculation")
public class KilometreCalculation {

	
	@Id
	@GenericGenerator(name = "seq_kilometre_calculation_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_kilometre_calculation_id") })
	@GeneratedValue(generator = "seq_kilometre_calculation_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_kilometre_calculation_id')")
	private Long id;

	@Column(name = "KILOMETRE", nullable = false)
	private double kilometre = 0.0;
	
	@Column(name = "METRES")
	private double metres = 0.0;

	@Column(name = "date")
	private LocalDate date;

	@Column(name = "START_LOCATION")
	private String startLocation;

	@Column(name = "END_LOCATION")
	private String endLocation;

	@NotNull
	@ManyToOne
	private User user;

	@NotNull
	@ManyToOne
	private Company company;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();
	
	
	@ManyToOne
	private EmployeeProfile employee;
	
	
	@ManyToOne
	private ExecutiveTaskExecution executiveTaskExecution;

	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getKilometre() {
		return kilometre;
	}

	public void setKilometre(double kilometre) {
		this.kilometre = kilometre;
	}

	public Double getMetres() {
		return metres;
	}

	public void setMetres(Double metres) {
		this.metres = metres;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(String startLocation) {
		this.startLocation = startLocation;
	}

	public String getEndLocation() {
		return endLocation;
	}

	public void setEndLocation(String endLocation) {
		this.endLocation = endLocation;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public EmployeeProfile getEmployee() {
		return employee;
	}

	public void setEmployee(EmployeeProfile employee) {
		this.employee = employee;
	}

	public ExecutiveTaskExecution getExecutiveTaskExecution() {
		return executiveTaskExecution;
	}

	public void setExecutiveTaskExecution(ExecutiveTaskExecution executiveTaskExecution) {
		this.executiveTaskExecution = executiveTaskExecution;
	}
	
	
}
