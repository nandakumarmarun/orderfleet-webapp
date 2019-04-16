package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.RootPlanSubgroupApprove;
import com.orderfleet.webapp.domain.enums.AttendanceStatus;

/**
 * DTO for managing RootPlanSubgroupApprove.
 * 
 * @author fahad
 * @since Aug 28, 2017
 */
public class RootPlanSubgroupApproveDTO {

	private Long id;
	
	private Long attendanceStatusSubgroupId;
	
	private String attendanceStatusSubgroupName;
	
	private AttendanceStatus attendanceStatus;
	
	private String userPid;
	
	private String userName;
	
	private boolean approvalRequired;
	
	private boolean rootPlanBased;
	
	private int sortOrder;

	public RootPlanSubgroupApproveDTO() {
		super();
	}

	public RootPlanSubgroupApproveDTO(Long id, Long attendanceStatusSubgroupId, String attendanceStatusSubgroupName,
			String userPid, String userName, boolean approvalRequired, boolean rootPlanBased) {
		super();
		this.id = id;
		this.attendanceStatusSubgroupId = attendanceStatusSubgroupId;
		this.attendanceStatusSubgroupName = attendanceStatusSubgroupName;
		this.userPid = userPid;
		this.userName = userName;
		this.approvalRequired = approvalRequired;
		this.rootPlanBased = rootPlanBased;
	}
	
	public RootPlanSubgroupApproveDTO(RootPlanSubgroupApprove rootPlanSubgroupApprove) {
		super();
		this.id = rootPlanSubgroupApprove.getId();
		this.attendanceStatusSubgroupId = rootPlanSubgroupApprove.getAttendanceStatusSubgroup().getId();
		this.attendanceStatusSubgroupName = rootPlanSubgroupApprove.getAttendanceStatusSubgroup().getName();
		this.attendanceStatus=rootPlanSubgroupApprove.getAttendanceStatusSubgroup().getAttendanceStatus();
		this.userPid = rootPlanSubgroupApprove.getUser().getPid();
		this.userName = rootPlanSubgroupApprove.getUser().getFirstName();
		this.approvalRequired = rootPlanSubgroupApprove.getApprovalRequired();
		this.rootPlanBased = rootPlanSubgroupApprove.getRootPlanBased();
		this.sortOrder = rootPlanSubgroupApprove.getAttendanceStatusSubgroup().getSortOrder();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public AttendanceStatus getAttendanceStatus() {
		return attendanceStatus;
	}

	public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
		this.attendanceStatus = attendanceStatus;
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean getApprovalRequired() {
		return approvalRequired;
	}

	public void setApprovalRequired(boolean approvalRequired) {
		this.approvalRequired = approvalRequired;
	}

	public boolean getRootPlanBased() {
		return rootPlanBased;
	}

	public void setRootPlanBased(boolean rootPlanBased) {
		this.rootPlanBased = rootPlanBased;
	}
	

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public String toString() {
		return "RootPlanSubgroupApproveDTO [id=" + id + ", attendanceStatusSubgroupId=" + attendanceStatusSubgroupId
				+ ", attendanceStatusSubgroupName=" + attendanceStatusSubgroupName + ", attendanceStatus="
				+ attendanceStatus + ", userPid=" + userPid + ", userName=" + userName + ", approvalRequired="
				+ approvalRequired + ", rootPlanBased=" + rootPlanBased + "]";
	}

}
