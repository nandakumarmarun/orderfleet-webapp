package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.AttendanceStatus;

@Entity
@Table(name = "tbl_dashboard_attendance")
public class DashboardAttendance implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_dashboard_attendance_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_dashboard_attendance_id") })
	@GeneratedValue(generator = "seq_dashboard_attendance_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_dashboard_attendance_id')")
	private Long id;

	@Column(name = "name", nullable = false, length = 100)
	private String name;
	
	@NotNull
	@ManyToOne
	private Company company;
	
	@Column(name="attendance_status")
	private AttendanceStatus attendanceStatus;
	
	@ManyToOne
	private AttendanceStatusSubgroup attendanceStatusSubgroup;

	public DashboardAttendance() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}


	public AttendanceStatus getAttendanceStatus() {
		return attendanceStatus;
	}

	public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
		this.attendanceStatus = attendanceStatus;
	}

	public AttendanceStatusSubgroup getAttendanceStatusSubgroup() {
		return attendanceStatusSubgroup;
	}

	public void setAttendanceStatusSubgroup(AttendanceStatusSubgroup attendanceStatusSubgroup) {
		this.attendanceStatusSubgroup = attendanceStatusSubgroup;
	}

	@Override
	public String toString() {
		return "DashboardAttendance [id=" + id + ", name=" + name + ", company=" + company + ", attendanceStatus="
				+ attendanceStatus + ", attendanceStatusSubgroup=" + attendanceStatusSubgroup + "]";
	}
	
	
}
