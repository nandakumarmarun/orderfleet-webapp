package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.domain.Notification;
import com.orderfleet.webapp.web.rest.dto.FirebaseResponse;
import com.orderfleet.webapp.web.rest.dto.NotificationDTO;

/**
 * Service Interface for managing Notification.
 * 
 * @author Sarath
 * @since Sep 7, 2016
 */
public interface NotificationService {

	String PID_PREFIX = "NOTIFI-";

	Notification saveNotification(NotificationDTO notificationDTO);

	List<NotificationDTO> getAllNotificationsByIsImportantTrueAndResendTime(Long count);

	void updateNotificationWithFirebaseResponse(Notification notification, String[] usersFcmKeys, FirebaseResponse response);

}
