package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

import com.orderfleet.webapp.domain.TaskNotificationSetting;
import com.orderfleet.webapp.domain.TaskNotificationSettingColumn;
import com.orderfleet.webapp.domain.enums.ActivityEvent;

/**
 *DTO for TaskNotificationSetting
 *
 * @author fahad
 * @since May 31, 2017
 */
public class TaskNotificationSettingDTO {

	private String pid;
	
	private String activityPid;
	
	private String documentPid;
	
	private String activityName;
	
	private String documentName;
	
	
	private ActivityEvent activityEvent;
	
	private List<TaskNotificationSettingColumn> notificationSettingColumns;

	
	
	public TaskNotificationSettingDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TaskNotificationSettingDTO(TaskNotificationSetting taskNotificationSetting) {
		super();
		this.pid = taskNotificationSetting.getPid();
		this.activityPid = taskNotificationSetting.getActivity().getPid();
		this.activityName=taskNotificationSetting.getActivity().getName();
		this.documentPid = taskNotificationSetting.getDocument().getPid();
		this.documentName = taskNotificationSetting.getDocument().getName();
		this.activityEvent = taskNotificationSetting.getActivityEvent();
		this.notificationSettingColumns = (List<TaskNotificationSettingColumn>) taskNotificationSetting.getTaskNotificationSettingColumns();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getActivityPid() {
		return activityPid;
	}

	public void setActivityPid(String activityPid) {
		this.activityPid = activityPid;
	}

	public String getDocumentPid() {
		return documentPid;
	}

	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public ActivityEvent getActivityEvent() {
		return activityEvent;
	}

	public void setActivityEvent(ActivityEvent activityEvent) {
		this.activityEvent = activityEvent;
	}

	public List<TaskNotificationSettingColumn> getNotificationSettingColumns() {
		return notificationSettingColumns;
	}

	public void setNotificationSettingColumns(List<TaskNotificationSettingColumn> notificationSettingColumns) {
		this.notificationSettingColumns = notificationSettingColumns;
	}

	@Override
	public String toString() {
		return "TaskNotificationSettingDTO [pid=" + pid + ", activityPid=" + activityPid + ", documentPid="
				+ documentPid + ", activityName=" + activityName + ", documentName=" + documentName + ", activityEvent="
				+ activityEvent + ", notificationSettingColumns=" + notificationSettingColumns + "]";
	}
	
	
}
