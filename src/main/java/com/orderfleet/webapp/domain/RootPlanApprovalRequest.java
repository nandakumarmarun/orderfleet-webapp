package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
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
@Table(name = "tbl_root_plan_approval_request")
public class RootPlanApprovalRequest implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_root_plan_approval_request_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_root_plan_approval_request_id") })
	@GeneratedValue(generator = "seq_root_plan_approval_request_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_root_plan_approval_request_id')")
	private Long id;
	
	@NotNull
	@ManyToOne
	private Company company;
	
	@NotNull
	@ManyToOne
	private User requestUser;
	
	@ManyToOne
	private User approverUser;
	
	@NotNull
	@ManyToOne
	private RootPlanDetail rootPlanDetail;
	
	@Column(name = "request_user_message")
	private String requestUserMessage;
	
	@Column(name = "approver_user_message")
	private String approverUserMessage;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "approval_status", nullable = false)
	private ApprovalStatus approvalStatus;

	@Column(name = "requested_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime requestedDate;

	@Column(name = "approved_date")
	@JsonIgnore
	private LocalDateTime approvedDate=null ;
	
	@Column(name = "planned_date")
	private LocalDate plannedDate;
	
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

	public User getRequestUser() {
		return requestUser;
	}

	public void setRequestUser(User requestUser) {
		this.requestUser = requestUser;
	}

	public User getApproverUser() {
		return approverUser;
	}

	public void setApproverUser(User approverUser) {
		this.approverUser = approverUser;
	}

	public RootPlanDetail getRootPlanDetail() {
		return rootPlanDetail;
	}

	public void setRootPlanDetail(RootPlanDetail rootPlanDetail) {
		this.rootPlanDetail = rootPlanDetail;
	}

	public String getRequestUserMessage() {
		return requestUserMessage;
	}

	public void setRequestUserMessage(String requestUserMessage) {
		this.requestUserMessage = requestUserMessage;
	}

	public String getApproverUserMessage() {
		return approverUserMessage;
	}

	public void setApproverUserMessage(String approverUserMessage) {
		this.approverUserMessage = approverUserMessage;
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

	
	
	public LocalDate getPlannedDate() {
		return plannedDate;
	}

	public void setPlannedDate(LocalDate plannedDate) {
		this.plannedDate = plannedDate;
	}

	@Override
	public String toString() {
		return "RootPlanApprovalRequest [id=" + id + ", company=" + company + ", requestUser=" + requestUser
				+ ", approverUser=" + approverUser + ", rootPlanDetail=" + rootPlanDetail + ", requestUserMessage="
				+ requestUserMessage + ", approverUserMessage=" + approverUserMessage + ", approvalStatus="
				+ approvalStatus + ", requestedDate=" + requestedDate + ", approvedDate=" + approvedDate
				+ ", plannedDate=" + plannedDate + "]";
	}

	
}
