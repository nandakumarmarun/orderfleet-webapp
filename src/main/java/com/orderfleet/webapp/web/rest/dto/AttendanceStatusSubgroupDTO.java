package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.enums.AttendanceStatus;

/**
 *Dto for AttendanceStatusSubgroup
 *
 * @author fahad
 * @since Jul 25, 2017
 */
public class AttendanceStatusSubgroupDTO {

	private Long id;
	
	private AttendanceStatus attendanceStatus;
	
	private String name;
	
	private String code;
	
	private String description;
	
	private int sortOrder;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AttendanceStatus getAttendanceStatus() {
		return attendanceStatus;
	}

	public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
		this.attendanceStatus = attendanceStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public String toString() {
		return "AttendanceStatusSubgroupDTO [id=" + id + ", attendanceStatus=" + attendanceStatus + ", name=" + name
				+ ", code=" + code + ", description=" + description + "]";
	}

}
