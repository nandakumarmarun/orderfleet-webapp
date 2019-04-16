package com.orderfleet.webapp.web.rest.dto;


import com.orderfleet.webapp.domain.UserRestrictedAttendanceSubgroup;

public class UserRestrictedAttendanceSubgroupDTO {

	private Long id;
	
	private String userPid;
	
	private String userName;
	
	private Long attendanceSubgroupId;
	
	private String attendanceSubgroupName;
	
	
	public UserRestrictedAttendanceSubgroupDTO() {
		super();
	}

	public UserRestrictedAttendanceSubgroupDTO(Long id, String userPid, String userName, Long attendanceSubgroupId,
			String attendanceSubgroupName) {
		super();
		this.id = id;
		this.userPid = userPid;
		this.userName = userName;
		this.attendanceSubgroupId = attendanceSubgroupId;
		this.attendanceSubgroupName = attendanceSubgroupName;
	}
	
	
	public UserRestrictedAttendanceSubgroupDTO(UserRestrictedAttendanceSubgroup userRestrictedAttendanceSubgroup) {
		super();
		this.id = userRestrictedAttendanceSubgroup.getId();
		this.userPid = userRestrictedAttendanceSubgroup.getUser().getPid();
		this.userName = userRestrictedAttendanceSubgroup.getUser().getFirstName();
		this.attendanceSubgroupId = userRestrictedAttendanceSubgroup.getAttendanceStatusSubgroup().getId();
		this.attendanceSubgroupName = userRestrictedAttendanceSubgroup.getAttendanceStatusSubgroup().getName();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getAttendanceSubgroupId() {
		return attendanceSubgroupId;
	}

	public void setAttendanceSubgroupId(Long attendanceSubgroupId) {
		this.attendanceSubgroupId = attendanceSubgroupId;
	}

	public String getAttendanceSubgroupName() {
		return attendanceSubgroupName;
	}

	public void setAttendanceSubgroupName(String attendanceSubgroupName) {
		this.attendanceSubgroupName = attendanceSubgroupName;
	}

	@Override
	public String toString() {
		return "UserRestrictedAttendanceSubgroupDTO [id=" + id + ", userPid=" + userPid + ", userName=" + userName
				+ ", attendanceSubgroupId=" + attendanceSubgroupId + ", attendanceSubgroupName="
				+ attendanceSubgroupName + "]";
	}
	
	
}
