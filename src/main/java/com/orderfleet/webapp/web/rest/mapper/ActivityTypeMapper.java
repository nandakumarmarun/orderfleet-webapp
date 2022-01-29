package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.ActivityType;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.ActivityTypeDTO;

@Component
public abstract class ActivityTypeMapper {
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	public abstract ActivityTypeDTO activityTypeToActivityTypeDTO(ActivityType activityType);

	public abstract List<ActivityTypeDTO> activityTypesToActivityTypeDTOs(List<ActivityType> activityTypes);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract ActivityType activityTypeDTOToActivityType(ActivityTypeDTO activityTypeDTO);

	public abstract List<ActivityType> activityTypeDTOsToActivityTypes(List<ActivityTypeDTO> activityTypeDTOs);

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
