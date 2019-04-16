package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;

@Entity
@Table(name = "tbl_attendance_subgroup_approval_request")
public class AttendanceSubgroupApprovalRequest implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_attendance_subgroup_activity_approval_request_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_attendance_subgroup_activity_approval_request_id") })
	@GeneratedValue(generator = "seq_attendance_subgroup_activity_approval_request_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_attendance_subgroup_activity_approval_request_id')")
	private Long id;
	
	@NotNull
	@ManyToOne
	private Company company;
	
	@NotNull
	@ManyToOne
	private User requestedUser;
	
	@ManyToOne
	private User approvedUser;
	
	@NotNull
	@ManyToOne
	private AttendanceStatusSubgroup attendanceStatusSubgroup;
	
	@Column(name = "request_user_message")
	private String requestedUserMessage;
	
	@Column(name = "approver_user_message")
	private String approvedUserMessage;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "approval_status", nullable = false)
	private ApprovalStatus approvalStatus;

	@Column(name = "requested_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime requestedDate;

	@Column(name = "approved_date")
	@JsonIgnore
	private LocalDateTime approvedDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public User getRequestedUser() {
		return requestedUser;
	}

	public void setRequestedUser(User requestedUser) {
		this.requestedUser = requestedUser;
	}

	public User getApprovedUser() {
		return approvedUser;
	}

	public void setApprovedUser(User approvedUser) {
		this.approvedUser = approvedUser;
	}

	public AttendanceStatusSubgroup getAttendanceStatusSubgroup() {
		return attendanceStatusSubgroup;
	}

	public void setAttendanceStatusSubgroup(AttendanceStatusSubgroup attendanceStatusSubgroup) {
		this.attendanceStatusSubgroup = attendanceStatusSubgroup;
	}

	public String getRequestedUserMessage() {
		return requestedUserMessage;
	}

	public void setRequestedUserMessage(String requestedUserMessage) {
		this.requestedUserMessage = requestedUserMessage;
	}

	public String getApprovedUserMessage() {
		return approvedUserMessage;
	}

	public void setApprovedUserMessage(String approvedUserMessage) {
		this.approvedUserMessage = approvedUserMessage;
	}

	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public LocalDateTime getRequestedDate() {
		return requestedDate;
	}

	public void setRequestedDate(LocalDateTime requestedDate) {
		this.requestedDate = requestedDate;
	}

	public LocalDateTime getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(LocalDateTime approvedDate) {
		this.approvedDate = approvedDate;
	}
	
}
