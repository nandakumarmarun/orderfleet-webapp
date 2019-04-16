package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.orderfleet.webapp.domain.TaskUserSetting;
import com.orderfleet.webapp.domain.enums.ActivityEvent;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;

/**
 * A DTO for the TaskUserSetting entity.
 * 
 * @author Muhammed Riyas T
 * @since October 04, 2016
 */
public class TaskUserSettingDTO {

	private String pid;

	private String executorPid;

	private String executorName;

	private String taskSettingPid;

	private ActivityEvent taskSettingEvent;

	private List<UserDTO> approvers;

	public TaskUserSettingDTO() {
	}

	public TaskUserSettingDTO(TaskUserSetting taskUserSetting) {
		super();
		this.pid = taskUserSetting.getPid();
		this.executorPid = taskUserSetting.getExecutor().getPid();
		this.executorName = taskUserSetting.getExecutor().getFirstName();
		this.taskSettingPid = taskUserSetting.getTaskSetting().getPid();
		this.taskSettingEvent = taskUserSetting.getTaskSetting().getActivityEvent();
		this.approvers = taskUserSetting.getApprovers().stream().map(UserDTO::new).collect(Collectors.toList());
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

	public String getTaskSettingPid() {
		return taskSettingPid;
	}

	public void setTaskSettingPid(String taskSettingPid) {
		this.taskSettingPid = taskSettingPid;
	}

	public ActivityEvent getTaskSettingEvent() {
		return taskSettingEvent;
	}

	public void setTaskSettingEvent(ActivityEvent taskSettingEvent) {
		this.taskSettingEvent = taskSettingEvent;
	}

	public List<UserDTO> getApprovers() {
		return approvers;
	}

	public void setApprovers(List<UserDTO> approvers) {
		this.approvers = approvers;
	}

}
