package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;

/**
 * Mapper for the entity Activity and its DTO ActivityDTO.
 * 
 * @author Muhammed Riyas T
 * @since May 19, 2016
 */
@Component
public abstract class ActivityMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	public abstract ActivityDTO activityToActivityDTO(Activity activity);

	public abstract List<ActivityDTO> activitiesToActivityDTOs(List<Activity> activities);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract Activity activityDTOToActivity(ActivityDTO activityDTO);

	public abstract List<Activity> activityDTOsToActivities(List<ActivityDTO> activityDTOs);
	
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
