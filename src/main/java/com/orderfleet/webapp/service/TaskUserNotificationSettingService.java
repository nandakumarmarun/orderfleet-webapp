package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.TaskUserNotificationSetting;
import com.orderfleet.webapp.web.rest.dto.TaskUserNotificationSettingDTO;

public interface TaskUserNotificationSettingService {

	String PID_PREFIX = "TUNST-";
	
	List<TaskUserNotificationSettingDTO> findAllByCompanyId();
	
	TaskUserNotificationSettingDTO save(TaskUserNotificationSettingDTO taskUserNotificationSettingDTO);
	
	TaskUserNotificationSettingDTO update(TaskUserNotificationSettingDTO taskUserNotificationSettingDTO);
	
	Optional<TaskUserNotificationSettingDTO> findOneByPid(String pid);
	
	void delete(String pid);
	
	TaskUserNotificationSetting findByExecutorPidAndTaskNotificationSettingPid(String executivePid,String taskNotificationSettingPid);
}
