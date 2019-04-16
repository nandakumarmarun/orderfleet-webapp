package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A EmployeeProfileLocation.
 * 
 * @author Muhammed Riyas T
 * @since August 31, 2016
 */
@Entity
@Table(name = "tbl_employee_profile_location")
public class EmployeeProfileLocation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_employee_profile_location_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_employee_profile_location_id") })
	@GeneratedValue(generator = "seq_employee_profile_location_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_employee_profile_location_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private EmployeeProfile employeeProfile;

	@NotNull
	@ManyToOne
	private Location location;

	public EmployeeProfileLocation() {
		super();
	}

	public EmployeeProfileLocation(EmployeeProfile employeeProfile, Location location) {
		super();
		this.employeeProfile = employeeProfile;
		this.location = location;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EmployeeProfile getEmployeeProfile() {
		return employeeProfile;
	}

	public void setEmployeeProfile(EmployeeProfile employeeProfile) {
		this.employeeProfile = employeeProfile;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		EmployeeProfileLocation employeeProfileLocation = (EmployeeProfileLocation) o;
		if (employeeProfileLocation.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, employeeProfileLocation.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
