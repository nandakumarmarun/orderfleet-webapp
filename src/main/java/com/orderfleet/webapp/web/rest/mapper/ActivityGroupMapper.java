package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.ActivityGroup;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupDTO;

/**
 * Mapper for the entity ActivityGroup and its DTO ActivityGroupDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 09, 2016
 */
@Component
public abstract class ActivityGroupMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	public abstract ActivityGroupDTO activityGroupToActivityGroupDTO(ActivityGroup activityGroup);

	public abstract List<ActivityGroupDTO> activityGroupsToActivityGroupDTOs(List<ActivityGroup> activityGroups);

//	@Mapping(target = "activities", ignore = true)
//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract ActivityGroup activityGroupDTOToActivityGroup(ActivityGroupDTO activityGroupDTO);

	public abstract List<ActivityGroup> activityGroupDTOsToActivityGroups(List<ActivityGroupDTO> activityGroupDTOs);

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
