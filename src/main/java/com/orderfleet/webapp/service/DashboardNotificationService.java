package com.orderfleet.webapp.service;

import com.orderfleet.webapp.domain.DashboardNotification;
import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.enums.NotificationMessageType;

/**
 * Service Interface for managing DashboardNotification.
 * 
 * @author Shaheer
 * @since January 11, 2017
 */
public interface DashboardNotificationService {

	DashboardNotification saveDashboardNotification(NotificationMessageType notificationType, ExecutiveTaskPlan executiveTaskPlan,boolean dayClose);
	
	void updateReadById(Long id);

}
