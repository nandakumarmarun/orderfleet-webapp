package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.TaskNotificationSetting;
import com.orderfleet.webapp.domain.enums.ActivityEvent;

/** 
 * Repository for TaskNotificationSetting
 *
 * @author fahad
 * @since May 31, 2017
 */
public interface TaskNotificationSettingRepository extends JpaRepository<TaskNotificationSetting, Long> {
	
	Optional<TaskNotificationSetting>  findByActivityPidAndDocumentPidAndActivityEvent(String activityPid, String documentPid, ActivityEvent activityEvent);
	
	@Query("select taskNotificationSetting from TaskNotificationSetting taskNotificationSetting where taskNotificationSetting.company.id = ?#{principal.companyId}")
	List<TaskNotificationSetting> findAllByCompanyId();
	
	Optional<TaskNotificationSetting> findOneByPid(String pid);
	
	List<TaskNotificationSetting> findByActivityPidAndDocumentPid(String activityPid, String documentPid);
}
