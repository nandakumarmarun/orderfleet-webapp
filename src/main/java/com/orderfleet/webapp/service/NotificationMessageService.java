package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.domain.NotificationMessage;
import com.orderfleet.webapp.domain.enums.NotificationMessageType;
import com.orderfleet.webapp.domain.model.FirebaseData;
import com.orderfleet.webapp.web.rest.dto.NotificationMessageDTO;

/**
 * Service Interface for managing NotificationMessage.
 * 
 * @author Shaheer
 * @since November 03, 2016
 */
public interface NotificationMessageService {

	String PID_PREFIX = "NM-";
	
	NotificationMessage saveNotificationMessage(FirebaseData data);
	List<NotificationMessageDTO> findAllByNotificationMessageType(NotificationMessageType notificationMessageType);

}
