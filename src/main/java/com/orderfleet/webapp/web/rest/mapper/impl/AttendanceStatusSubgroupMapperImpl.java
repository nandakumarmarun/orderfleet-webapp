package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AttendanceStatusSubgroup;
import com.orderfleet.webapp.web.rest.dto.AttendanceStatusSubgroupDTO;
import com.orderfleet.webapp.web.rest.mapper.AttendanceStatusSubgroupMapper;

@Component
public class AttendanceStatusSubgroupMapperImpl extends AttendanceStatusSubgroupMapper {

	@Override
	public AttendanceStatusSubgroupDTO attendanceStatusSubgroupToAttendanceStatusSubgroupDTO(
			AttendanceStatusSubgroup attendanceStatusSubgroup) {
		if (attendanceStatusSubgroup == null) {
			return null;
		}

		AttendanceStatusSubgroupDTO attendanceStatusSubgroupDTO = new AttendanceStatusSubgroupDTO();

		attendanceStatusSubgroupDTO.setId(attendanceStatusSubgroup.getId());
		attendanceStatusSubgroupDTO.setAttendanceStatus(attendanceStatusSubgroup.getAttendanceStatus());
		attendanceStatusSubgroupDTO.setName(attendanceStatusSubgroup.getName());
		attendanceStatusSubgroupDTO.setCode(attendanceStatusSubgroup.getCode());
		attendanceStatusSubgroupDTO.setDescription(attendanceStatusSubgroup.getDescription());
		attendanceStatusSubgroupDTO.setSortOrder(attendanceStatusSubgroup.getSortOrder());

		return attendanceStatusSubgroupDTO;
	}

	public AttendanceStatusSubgroupDTO attendanceStatusSubgroupToAttendanceStatusSubgroupDTODescription(
			AttendanceStatusSubgroup attendanceStatusSubgroup) {
		if (attendanceStatusSubgroup == null) {
			return null;
		}

		AttendanceStatusSubgroupDTO attendanceStatusSubgroupDTO = new AttendanceStatusSubgroupDTO();

		attendanceStatusSubgroupDTO.setId(attendanceStatusSubgroup.getId());
		attendanceStatusSubgroupDTO.setAttendanceStatus(attendanceStatusSubgroup.getAttendanceStatus());
		attendanceStatusSubgroupDTO.setName(attendanceStatusSubgroup.getDescription() != null
				&& !attendanceStatusSubgroup.getDescription().equalsIgnoreCase("common")
						? attendanceStatusSubgroup.getDescription()
						: attendanceStatusSubgroup.getName());
		attendanceStatusSubgroupDTO.setCode(attendanceStatusSubgroup.getCode());
		attendanceStatusSubgroupDTO.setDescription(attendanceStatusSubgroup.getDescription());
		attendanceStatusSubgroupDTO.setSortOrder(attendanceStatusSubgroup.getSortOrder());

		return attendanceStatusSubgroupDTO;
	}

	@Override
	public List<AttendanceStatusSubgroupDTO> attendanceStatusSubgroupsToAttendanceStatusSubgroupDTOs(
			List<AttendanceStatusSubgroup> attendanceStatusSubgroups) {
		if (attendanceStatusSubgroups == null) {
			return null;
		}
		List<AttendanceStatusSubgroupDTO> list = new ArrayList<AttendanceStatusSubgroupDTO>();
		if (getCompanyCofig()) {
			for (AttendanceStatusSubgroup attendanceStatusSubgroup : attendanceStatusSubgroups) {
				list.add(attendanceStatusSubgroupToAttendanceStatusSubgroupDTODescription(attendanceStatusSubgroup));
			}
		} else {
			for (AttendanceStatusSubgroup attendanceStatusSubgroup : attendanceStatusSubgroups) {
				list.add(attendanceStatusSubgroupToAttendanceStatusSubgroupDTO(attendanceStatusSubgroup));
			}
		}
		return list;
	}

	@Override
	public AttendanceStatusSubgroup attendanceStatusSubgroupDTOToAttendanceStatusSubgroup(
			AttendanceStatusSubgroupDTO attendanceStatusSubgroupDTO) {
		if (attendanceStatusSubgroupDTO == null) {
			return null;
		}

		AttendanceStatusSubgroup attendanceStatusSubgroup = new AttendanceStatusSubgroup();

		attendanceStatusSubgroup.setAttendanceStatus(attendanceStatusSubgroupDTO.getAttendanceStatus());
		attendanceStatusSubgroup.setName(attendanceStatusSubgroupDTO.getName());
		attendanceStatusSubgroup.setCode(attendanceStatusSubgroupDTO.getCode());
		attendanceStatusSubgroup.setDescription(attendanceStatusSubgroupDTO.getDescription());
		attendanceStatusSubgroup.setSortOrder(attendanceStatusSubgroupDTO.getSortOrder());

		return attendanceStatusSubgroup;
	}

	@Override
	public List<AttendanceStatusSubgroup> attendanceStatusSubgroupDTOsToAttendanceStatusSubgroups(
			List<AttendanceStatusSubgroupDTO> attendanceStatusSubgroupDTOs) {
		if (attendanceStatusSubgroupDTOs == null) {
			return null;
		}

		List<AttendanceStatusSubgroup> list = new ArrayList<AttendanceStatusSubgroup>();
		for (AttendanceStatusSubgroupDTO attendanceStatusSubgroupDTO : attendanceStatusSubgroupDTOs) {
			list.add(attendanceStatusSubgroupDTOToAttendanceStatusSubgroup(attendanceStatusSubgroupDTO));
		}

		return list;
	}

	private String attendanceStatusSubgroupName(AttendanceStatusSubgroup attendanceStatusSubgroup) {
		if (attendanceStatusSubgroup.getDescription() != null && getCompanyCofig()
				&& !attendanceStatusSubgroup.getDescription().equals("common")) {
			return attendanceStatusSubgroup.getDescription();
		}

		return attendanceStatusSubgroup.getName();
	}
}
