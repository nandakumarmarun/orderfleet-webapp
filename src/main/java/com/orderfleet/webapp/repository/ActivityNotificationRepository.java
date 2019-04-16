package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.orderfleet.webapp.domain.ActivityNotification;
import com.orderfleet.webapp.domain.enums.NotificationType;

public interface ActivityNotificationRepository extends JpaRepository<ActivityNotification, Long> {
	
	@Query("select activityNotification from ActivityNotification activityNotification where activityNotification.company.id = ?#{principal.companyId}")
	List<ActivityNotification> findAllByCompanyId();
	
	@Query("select activityNotification from ActivityNotification activityNotification where activityNotification.company.id = ?#{principal.companyId} and activityNotification.activity.pid = ?1 and activityNotification.document.pid = ?2 and activityNotification.notificationType = ?3")
	ActivityNotification findByCompanyIdAndActivityPidAndDocumentPidAndNotificationType(String activityPid,String documentPid, NotificationType notificationType);
	
	@Query("select activityNotification from ActivityNotification activityNotification where activityNotification.company.id = ?#{principal.companyId} and activityNotification.activity.pid = ?1 and activityNotification.document.pid = ?2 and activityNotification.notificationType = ?3")
	Optional<ActivityNotification> findOneByCompanyIdAndActivityPidAndDocumentPidAndNotificationType(String activityPid,String documentPid, NotificationType notificationType);

}
