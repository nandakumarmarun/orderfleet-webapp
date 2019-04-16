package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.ActivityNotification;
import com.orderfleet.webapp.web.rest.api.dto.ActivityNotificationDTO;
@Mapper(componentModel = "spring", uses = {})
public interface ActivityNotificationMapper {

	ActivityNotificationDTO activityNotificationToActivityNotificationDTO(ActivityNotification activityNotification);

	    List<ActivityNotificationDTO> activityNotificationsToActivityNotificationDTOs(List<ActivityNotification> activityNotifications);

	    @Mapping(target = "company", ignore = true)
	    @Mapping(target = "id", ignore = true)
	    ActivityNotification activityNotificationDTOToActivityNotification(ActivityNotificationDTO activityNotificationDTO);

	    List<ActivityNotification> activityNotificationDTOsToActivityNotifications(List<ActivityNotificationDTO> activityNotificationDTOs);

	
}
