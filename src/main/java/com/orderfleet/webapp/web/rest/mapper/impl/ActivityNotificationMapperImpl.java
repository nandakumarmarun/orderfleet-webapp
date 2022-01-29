package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.ActivityNotification;
import com.orderfleet.webapp.web.rest.api.dto.ActivityNotificationDTO;
import com.orderfleet.webapp.web.rest.mapper.ActivityNotificationMapper;
@Component
public class ActivityNotificationMapperImpl extends ActivityNotificationMapper{

	 @Override
	    public ActivityNotificationDTO activityNotificationToActivityNotificationDTO(ActivityNotification activityNotification) {
	        if ( activityNotification == null ) {
	            return null;
	        }

	        ActivityNotificationDTO activityNotificationDTO = new ActivityNotificationDTO();

	        if ( activityNotification.getId() != null ) {
	            activityNotificationDTO.setId( activityNotification.getId() );
	        }
	        activityNotificationDTO.setNotificationType( activityNotification.getNotificationType() );
	        activityNotificationDTO.setSendCustomer( activityNotification.getSendCustomer() );
	        activityNotificationDTO.setOther( activityNotification.getOther() );
	        activityNotificationDTO.setPhoneNumbers( activityNotification.getPhoneNumbers() );

	        return activityNotificationDTO;
	    }

	    @Override
	    public List<ActivityNotificationDTO> activityNotificationsToActivityNotificationDTOs(List<ActivityNotification> activityNotifications) {
	        if ( activityNotifications == null ) {
	            return null;
	        }

	        List<ActivityNotificationDTO> list = new ArrayList<ActivityNotificationDTO>();
	        for ( ActivityNotification activityNotification : activityNotifications ) {
	            list.add( activityNotificationToActivityNotificationDTO( activityNotification ) );
	        }

	        return list;
	    }

	    @Override
	    public ActivityNotification activityNotificationDTOToActivityNotification(ActivityNotificationDTO activityNotificationDTO) {
	        if ( activityNotificationDTO == null ) {
	            return null;
	        }

	        ActivityNotification activityNotification = new ActivityNotification();

	        activityNotification.setNotificationType( activityNotificationDTO.getNotificationType() );
	        activityNotification.setSendCustomer( activityNotificationDTO.getSendCustomer() );
	        activityNotification.setOther( activityNotificationDTO.getOther() );
	        activityNotification.setPhoneNumbers( activityNotificationDTO.getPhoneNumbers() );

	        return activityNotification;
	    }

	    @Override
	    public List<ActivityNotification> activityNotificationDTOsToActivityNotifications(List<ActivityNotificationDTO> activityNotificationDTOs) {
	        if ( activityNotificationDTOs == null ) {
	            return null;
	        }

	        List<ActivityNotification> list = new ArrayList<ActivityNotification>();
	        for ( ActivityNotificationDTO activityNotificationDTO : activityNotificationDTOs ) {
	            list.add( activityNotificationDTOToActivityNotification( activityNotificationDTO ) );
	        }

	        return list;
	    }
}
