package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.orderfleet.webapp.domain.TaskUserNotificationSetting;
import com.orderfleet.webapp.domain.enums.ActivityEvent;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;

/**
 *DTO for TaskUserNotificationSetting
 *
 * @author fahad
 * @since May 31, 2017
 */
public class TaskUserNotificationSettingDTO {

	private String pid;

	private String executorPid;

	private String executorName;

	private String taskNotificationSettingPid;

	private ActivityEvent taskNotificationSettingEvent;

	private List<UserDTO> approvers;
	
	private boolean enableTerritory;

	public TaskUserNotificationSettingDTO() {
		super();
	}

	public TaskUserNotificationSettingDTO(TaskUserNotificationSetting taskUserNotificationSetting) {
		super();
		this.pid = taskUserNotificationSetting.getPid();
		this.executorPid = taskUserNotificationSetting.getExecutor().getPid();
		this.executorName = taskUserNotificationSetting.getExecutor().getFirstName();
		this.taskNotificationSettingPid = taskUserNotificationSetting.getTaskNotificationSetting().getPid();
		this.taskNotificationSettingEvent = taskUserNotificationSetting.getTaskNotificationSetting().getActivityEvent();
		this.approvers = taskUserNotificationSetting.getApprovers().stream().map(UserDTO::new).collect(Collectors.toList());
		this.enableTerritory=taskUserNotificationSetting.getEnableTerritory();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getExecutorPid() {
		return executorPid;
	}

	public void setExecutorPid(String executorPid) {
		this.executorPid = executorPid;
	}

	public String getExecutorName() {
		return executorName;
	}

	public void setExecutorName(String executorName) {
		this.executorName = executorName;
	}

	public String getTaskNotificationSettingPid() {
		return taskNotificationSettingPid;
	}

	public void setTaskNotificationSettingPid(String taskNotificationSettingPid) {
		this.taskNotificationSettingPid = taskNotificationSettingPid;
	}

	public ActivityEvent getTaskNotificationSettingEvent() {
		return taskNotificationSettingEvent;
	}

	public void setTaskNotificationSettingEvent(ActivityEvent taskNotificationSettingEvent) {
		this.taskNotificationSettingEvent = taskNotificationSettingEvent;
	}

	public List<UserDTO> getApprovers() {
		return approvers;
	}

	public void setApprovers(List<UserDTO> approvers) {
		this.approvers = approvers;
	}
	
	

	public boolean getEnableTerritory() {
		return enableTerritory;
	}

	public void setEnableTerritory(boolean enableTerritory) {
		this.enableTerritory = enableTerritory;
	}

	@Override
	public String toString() {
		return "TaskUserNotificationSettingDTO [pid=" + pid + ", executorPid=" + executorPid + ", executorName="
				+ executorName + ", taskNotificationSettingPid=" + taskNotificationSettingPid
				+ ", taskNotificationSettingEvent=" + taskNotificationSettingEvent + ", approvers=" + approvers + "]";
	}
	
	
}
