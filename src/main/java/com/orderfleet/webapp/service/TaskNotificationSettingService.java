package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.enums.ActivityEvent;
import com.orderfleet.webapp.web.rest.dto.TaskNotificationSettingDTO;

/**
 *Service for TaskNotificationSetting
 *
 * @author fahad
 * @since May 31, 2017
 */
public interface TaskNotificationSettingService {

	String PID_PREFIX = "TKNST-";
	
	TaskNotificationSettingDTO save(TaskNotificationSettingDTO taskNotificationSettingDTO);
	
	Optional<TaskNotificationSettingDTO>  findByActivityPidAndDocumentPidAndActivityEvent(String activityPid, String documentPid, ActivityEvent activityEvent);
	
	List<TaskNotificationSettingDTO> findAllByCompanyId();
	
	Optional<TaskNotificationSettingDTO> findOneByPid(String pid);
	
	TaskNotificationSettingDTO update(TaskNotificationSettingDTO taskNotificationSettingDTO);
	
	void delete(String pid);
	
	List<TaskNotificationSettingDTO> findByActivityPidAndDocumentPid(String activityPid, String documentPid);
}
