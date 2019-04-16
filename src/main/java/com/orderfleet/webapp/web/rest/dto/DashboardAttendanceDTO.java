package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.DashboardAttendance;
import com.orderfleet.webapp.domain.enums.AttendanceStatus;

public class DashboardAttendanceDTO {
	
	private Long id;
	
	private String name;
	
	private AttendanceStatus attendanceStatus;
	
	private Long attSSubgroupId;
	
	private String attSSubgroupName;

	public DashboardAttendanceDTO() {
		super();
	}

	public DashboardAttendanceDTO(DashboardAttendance dashboardAttendance) {
		super();
		this.id = dashboardAttendance.getId();
		this.name = dashboardAttendance.getName();
		if(dashboardAttendance.getAttendanceStatus()!=null){
		this.attendanceStatus = dashboardAttendance.getAttendanceStatus();
		}
		if(dashboardAttendance.getAttendanceStatusSubgroup()!=null){
			this.attSSubgroupId = dashboardAttendance.getAttendanceStatusSubgroup().getId();
			this.attSSubgroupName = dashboardAttendance.getAttendanceStatusSubgroup().getName();
		}
		
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

	public AttendanceStatus getAttendanceStatus() {
		return attendanceStatus;
	}

	public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
		this.attendanceStatus = attendanceStatus;
	}

	public Long getAttSSubgroupId() {
		return attSSubgroupId;
	}

	public void setAttSSubgroupId(Long attSSubgroupId) {
		this.attSSubgroupId = attSSubgroupId;
	}

	public String getAttSSubgroupName() {
		return attSSubgroupName;
	}

	public void setAttSSubgroupName(String attSSubgroupName) {
		this.attSSubgroupName = attSSubgroupName;
	}

	@Override
	public String toString() {
		return "DashboardAttendanceDTO [id=" + id + ", name=" + name + ", attendanceStatus=" + attendanceStatus
				+ ", attSSubgroupId=" + attSSubgroupId + ", attSSubgroupName=" + attSSubgroupName + "]";
	}
	
	
}
