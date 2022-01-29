package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AttendanceStatusSubgroup;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.AttendanceStatusSubgroupDTO;

/**
 *Mapper for AttendanceStatusSubgroup
 *
 * @author fahad
 * @since Jul 25, 2017
 */
@Component
public abstract class AttendanceStatusSubgroupMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	public abstract AttendanceStatusSubgroupDTO attendanceStatusSubgroupToAttendanceStatusSubgroupDTO(AttendanceStatusSubgroup attendanceStatusSubgroup);

	public abstract List<AttendanceStatusSubgroupDTO> attendanceStatusSubgroupsToAttendanceStatusSubgroupDTOs(List<AttendanceStatusSubgroup> attendanceStatusSubgroups);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract AttendanceStatusSubgroup attendanceStatusSubgroupDTOToAttendanceStatusSubgroup(AttendanceStatusSubgroupDTO attendanceStatusSubgroupDTO);

	public abstract List<AttendanceStatusSubgroup> attendanceStatusSubgroupDTOsToAttendanceStatusSubgroups(List<AttendanceStatusSubgroupDTO> attendanceStatusSubgroupDTOs);
	
	public boolean getCompanyCofig(){
		Optional<CompanyConfiguration> optconfig = companyConfigurationRepository.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if(optconfig.isPresent()) {
		if(Boolean.valueOf(optconfig.get().getValue())) {
		return true;
		}
		}
		return false;
		}
}
