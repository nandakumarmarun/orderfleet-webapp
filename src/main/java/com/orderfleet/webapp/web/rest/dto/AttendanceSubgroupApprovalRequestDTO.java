package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.AttendanceSubgroupApprovalRequest;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;

public class AttendanceSubgroupApprovalRequestDTO {

private Long id;
	
	private String requestUserPid;
	
	private String requestUserName;
	
	private String approverUserPid;
	
	private String approverUserName;

	private String requestUserMessage;
	
	private String approverUserMessage;
	
	private ApprovalStatus approvalStatus;
	
	private LocalDateTime approvedDate;
	
	private LocalDateTime requestedDate;
	
	private Long attendanceStatusSubgroupId;
	
	private String attendanceStatusSubgroupName;

	
	public AttendanceSubgroupApprovalRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	public AttendanceSubgroupApprovalRequestDTO(Long id, String requestUserPid, String requestUserName,
			String approverUserPid, String approverUserName, String requestUserMessage, String approverUserMessage,
			ApprovalStatus approvalStatus, LocalDateTime approvedDate, LocalDateTime requestedDate,
			Long attendanceStatusSubgroupId, String attendanceStatusSubgroupName) {
		super();
		this.id = id;
		this.requestUserPid = requestUserPid;
		this.requestUserName = requestUserName;
		this.approverUserPid = approverUserPid;
		this.approverUserName = approverUserName;
		this.requestUserMessage = requestUserMessage;
		this.approverUserMessage = approverUserMessage;
		this.approvalStatus = approvalStatus;
		this.approvedDate = approvedDate;
		this.requestedDate = requestedDate;
		this.attendanceStatusSubgroupId = attendanceStatusSubgroupId;
		this.attendanceStatusSubgroupName = attendanceStatusSubgroupName;
	}

	public AttendanceSubgroupApprovalRequestDTO(AttendanceSubgroupApprovalRequest attendanceSubgroupApprovalRequest) {
		super();
		this.id = attendanceSubgroupApprovalRequest.getId();
		this.requestUserPid = attendanceSubgroupApprovalRequest.getRequestedUser().getPid();
		this.requestUserName = attendanceSubgroupApprovalRequest.getRequestedUser().getFirstName();
		this.requestUserMessage = attendanceSubgroupApprovalRequest.getRequestedUserMessage();
		if(attendanceSubgroupApprovalRequest.getApprovedUser()!=null) {
		this.approverUserPid = attendanceSubgroupApprovalRequest.getApprovedUser().getPid();
		this.approverUserName = attendanceSubgroupApprovalRequest.getApprovedUser().getFirstName();
		this.approverUserMessage = attendanceSubgroupApprovalRequest.getApprovedUserMessage();
		}
		this.approvalStatus = attendanceSubgroupApprovalRequest.getApprovalStatus();
		this.approvedDate = attendanceSubgroupApprovalRequest.getApprovedDate();
		this.requestedDate = attendanceSubgroupApprovalRequest.getRequestedDate();
		this.attendanceStatusSubgroupId = attendanceSubgroupApprovalRequest.getAttendanceStatusSubgroup().getId();
		this.attendanceStatusSubgroupName = attendanceSubgroupApprovalRequest.getAttendanceStatusSubgroup().getName();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getAttendanceStatusSubgroupId() {
		return attendanceStatusSubgroupId;
	}

	public void setAttendanceStatusSubgroupId(Long attendanceStatusSubgroupId) {
		this.attendanceStatusSubgroupId = attendanceStatusSubgroupId;
	}

	public String getAttendanceStatusSubgroupName() {
		return attendanceStatusSubgroupName;
	}

	public void setAttendanceStatusSubgroupName(String attendanceStatusSubgroupName) {
		this.attendanceStatusSubgroupName = attendanceStatusSubgroupName;
	}

	@Override
	public String toString() {
		return "AttendanceSubgroupApprovalRequestDTO [id=" + id + ", requestUserPid=" + requestUserPid
				+ ", requestUserName=" + requestUserName + ", approverUserPid=" + approverUserPid
				+ ", approverUserName=" + approverUserName + ", requestUserMessage=" + requestUserMessage
				+ ", approverUserMessage=" + approverUserMessage + ", approvalStatus=" + approvalStatus
				+ ", approvedDate=" + approvedDate + ", requestedDate=" + requestedDate
				+ ", attendanceStatusSubgroupId=" + attendanceStatusSubgroupId + ", attendanceStatusSubgroupName="
				+ attendanceStatusSubgroupName + "]";
	}
	
}
