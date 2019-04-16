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

@Entity
@Table(name ="tbl_user_restricted_attendance_subgroup")
public class UserRestrictedAttendanceSubgroup implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_restricted_attendance_subgroup_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_restricted_attendance_subgroup_id") })
	@GeneratedValue(generator = "seq_user_restricted_attendance_subgroup_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_restricted_attendance_subgroup_id')")
	private Long id;
	
	@ManyToOne
	@NotNull
	private User user;
	
	@ManyToOne
	@NotNull
	private Company company;
	
	@ManyToOne
	@NotNull
	private AttendanceStatusSubgroup attendanceStatusSubgroup;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public AttendanceStatusSubgroup getAttendanceStatusSubgroup() {
		return attendanceStatusSubgroup;
	}

	public void setAttendanceStatusSubgroup(AttendanceStatusSubgroup attendanceStatusSubgroup) {
		this.attendanceStatusSubgroup = attendanceStatusSubgroup;
	}

	@Override
	public String toString() {
		return "UserRestrictedAttendanceSubgroup [id=" + id + ", user=" + user + ", company=" + company
				+ ", attendanceStatusSubgroup=" + attendanceStatusSubgroup + "]";
	}
	
}
