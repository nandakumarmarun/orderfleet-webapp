package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.enums.NotificationType;
import com.orderfleet.webapp.web.rest.api.dto.ActivityNotificationDTO;

/**
 * Service for Activity Notification
 *
 * @author fahad
 * @since May 10, 2017
 */
public interface ActivityNotificationService {

	List<ActivityNotificationDTO> findAllActivityNotificationByCompanyId();

	void save(ActivityNotificationDTO activityNotificationDTO);

	Optional<ActivityNotificationDTO> findActivityNotificationByCompanyIdAndActivityPidAndDocumentPidAndNotificationType(
			String activityPid, String documentPid, NotificationType notificationType);

	void delete(String activityPid, String documentPid, String notificationType);

	ActivityNotificationDTO findActivityNotificationById(long id);

	void update(ActivityNotificationDTO activityNotificationDTO);
}
