package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.TaskUserNotificationSetting;

/**
 * Spring Data JPA repository for the TaskUserNotificationSetting entity.
 * 
 */
public interface TaskUserNotificationSettingRepository extends JpaRepository<TaskUserNotificationSetting, Long> {

	Optional<TaskUserNotificationSetting> findOneByPid(String pid);

	@Query("select taskUserNotificationSetting from TaskUserNotificationSetting taskUserNotificationSetting where taskUserNotificationSetting.company.id = ?#{principal.companyId}")
	List<TaskUserNotificationSetting> findAllByCompanyId();

	TaskUserNotificationSetting findByExecutorPidAndTaskNotificationSettingPid(String executorPid, String taskNotificationSettingPid);
	
	List<TaskUserNotificationSetting> findByTaskNotificationSettingPid(String taskNotificationSettingPid);

}
