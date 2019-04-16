package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.RootPlanApprovalRequest;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;

public class RootPlanApprovalRequestDTO {

	private Long id;
	
	private String requestUserPid;
	
	private String requestUserName;
	
	private String approverUserPid;
	
	private String approverUserName;
	
	private Long rootPlanDetailId;
	
	private String taskListName;
	
	private String requestUserMessage;
	
	private String approverUserMessage;
	
	private ApprovalStatus approvalStatus;
	
	private LocalDateTime approvedDate;
	
	private LocalDateTime requestedDate;
	
	private LocalDate plannedDate;

	public RootPlanApprovalRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RootPlanApprovalRequestDTO(Long id, String requestUserPid, String requestUserName, String approverUserPid,
			String approverUserName, Long rootPlanDetailId, String requestUserMessage, String approverUserMessage,
			ApprovalStatus approvalStatus, LocalDateTime approvedDate, LocalDateTime requestedDate,LocalDate plannedDate) {
		super();
		this.id = id;
		this.requestUserPid = requestUserPid;
		this.requestUserName = requestUserName;
		this.approverUserPid = approverUserPid;
		this.approverUserName = approverUserName;
		this.rootPlanDetailId = rootPlanDetailId;
		this.requestUserMessage = requestUserMessage;
		this.approverUserMessage = approverUserMessage;
		this.approvalStatus = approvalStatus;
		this.approvedDate = approvedDate;
		this.requestedDate = requestedDate;
		this.plannedDate=plannedDate;
	}
	
	public RootPlanApprovalRequestDTO(RootPlanApprovalRequest rootPlanApprovalRequest) {
		super();
		this.id = rootPlanApprovalRequest.getId();
		this.requestUserPid = rootPlanApprovalRequest.getRequestUser().getPid();
		this.requestUserName = rootPlanApprovalRequest.getRequestUser().getFirstName();
		if(rootPlanApprovalRequest.getApproverUser()!=null) {
		this.approverUserPid = rootPlanApprovalRequest.getApproverUser().getPid();
		this.approverUserName = rootPlanApprovalRequest.getApproverUser().getFirstName();
		this.approvedDate = rootPlanApprovalRequest.getApprovedDate();
		}
		this.plannedDate=rootPlanApprovalRequest.getPlannedDate();
		this.rootPlanDetailId = rootPlanApprovalRequest.getRootPlanDetail().getId();
		this.requestUserMessage = rootPlanApprovalRequest.getRequestUserMessage();
		this.approverUserMessage = rootPlanApprovalRequest.getApproverUserMessage();
		this.approvalStatus = rootPlanApprovalRequest.getApprovalStatus();
		this.taskListName=rootPlanApprovalRequest.getRootPlanDetail().getTaskList().getName();
		this.requestedDate = rootPlanApprovalRequest.getRequestedDate();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	

	public String getTaskListName() {
		return taskListName;
	}

	public void setTaskListName(String taskListName) {
		this.taskListName = taskListName;
	}

	public String getRequestUserPid() {
		return requestUserPid;
	}

	public void setRequestUserPid(String requestUserPid) {
		this.requestUserPid = requestUserPid;
	}

	public String getRequestUserName() {
		return requestUserName;
	}

	public void setRequestUserName(String requestUserName) {
		this.requestUserName = requestUserName;
	}

	public String getApproverUserPid() {
		return approverUserPid;
	}

	public void setApproverUserPid(String approverUserPid) {
		this.approverUserPid = approverUserPid;
	}

	public String getApproverUserName() {
		return approverUserName;
	}

	public void setApproverUserName(String approverUserName) {
		this.approverUserName = approverUserName;
	}

	public Long getRootPlanDetailId() {
		return rootPlanDetailId;
	}

	public void setRootPlanDetailId(Long rootPlanDetailId) {
		this.rootPlanDetailId = rootPlanDetailId;
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

	public LocalDateTime getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(LocalDateTime approvedDate) {
		this.approvedDate = approvedDate;
	}

	public LocalDateTime getRequestedDate() {
		return requestedDate;
	}

	public void setRequestedDate(LocalDateTime requestedDate) {
		this.requestedDate = requestedDate;
	}

	public LocalDate getPlannedDate() {
		return plannedDate;
	}

	public void setPlannedDate(LocalDate plannedDate) {
		this.plannedDate = plannedDate;
	}

	@Override
	public String toString() {
		return "RootPlanApprovalRequestDTO [id=" + id + ", requestUserPid=" + requestUserPid + ", requestUserName="
				+ requestUserName + ", approverUserPid=" + approverUserPid + ", approverUserName=" + approverUserName
				+ ", rootPlanDetailId=" + rootPlanDetailId + ", taskListName=" + taskListName + ", requestUserMessage="
				+ requestUserMessage + ", approverUserMessage=" + approverUserMessage + ", approvalStatus="
				+ approvalStatus + ", approvedDate=" + approvedDate + ", requestedDate=" + requestedDate
				+ ", plannedDate=" + plannedDate + "]";
	}

}
