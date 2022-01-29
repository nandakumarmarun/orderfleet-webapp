package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.ActivityNotification;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.api.dto.ActivityNotificationDTO;
@Component
public abstract class ActivityNotificationMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	public abstract ActivityNotificationDTO activityNotificationToActivityNotificationDTO(ActivityNotification activityNotification);

	public abstract   List<ActivityNotificationDTO> activityNotificationsToActivityNotificationDTOs(List<ActivityNotification> activityNotifications);

//	    @Mapping(target = "company", ignore = true)
//	    @Mapping(target = "id", ignore = true)
	    public abstract  ActivityNotification activityNotificationDTOToActivityNotification(ActivityNotificationDTO activityNotificationDTO);

	    public abstract List<ActivityNotification> activityNotificationDTOsToActivityNotifications(List<ActivityNotificationDTO> activityNotificationDTOs);

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
