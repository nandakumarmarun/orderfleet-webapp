package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.AttendanceStatusSubgroup;
import com.orderfleet.webapp.web.rest.dto.AttendanceStatusSubgroupDTO;

/**
 *Mapper for AttendanceStatusSubgroup
 *
 * @author fahad
 * @since Jul 25, 2017
 */
@Mapper(componentModel = "spring", uses = {})
public interface AttendanceStatusSubgroupMapper {

	AttendanceStatusSubgroupDTO attendanceStatusSubgroupToAttendanceStatusSubgroupDTO(AttendanceStatusSubgroup attendanceStatusSubgroup);

	List<AttendanceStatusSubgroupDTO> attendanceStatusSubgroupsToAttendanceStatusSubgroupDTOs(List<AttendanceStatusSubgroup> attendanceStatusSubgroups);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	AttendanceStatusSubgroup attendanceStatusSubgroupDTOToAttendanceStatusSubgroup(AttendanceStatusSubgroupDTO attendanceStatusSubgroupDTO);

	List<AttendanceStatusSubgroup> attendanceStatusSubgroupDTOsToAttendanceStatusSubgroups(List<AttendanceStatusSubgroupDTO> attendanceStatusSubgroupDTOs);
}
