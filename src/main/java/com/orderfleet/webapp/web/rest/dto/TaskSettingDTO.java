package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.TaskSetting;
import com.orderfleet.webapp.domain.enums.ActivityEvent;

/**
 * A DTO for the TaskSetting entity.
 * 
 * @author Muhammed Riyas T
 * @since October 04, 2016
 */
public class TaskSettingDTO {

	private String pid;

	private String activityPid;

	private String activityName;

	private String documentPid;

	private String documentName;

	private ActivityEvent activityEvent;

	private String taskActivityPid;

	private String taskActivityName;

	private String formElementPid;

	private String startDateColumn;

	private String script;

	private boolean required;

	private boolean createPlan;

	public TaskSettingDTO() {
	}

	public TaskSettingDTO(TaskSetting taskSetting) {
		super();
		this.pid = taskSetting.getPid();
		this.activityPid = taskSetting.getActivity().getPid();
		this.activityName = taskSetting.getActivity().getName();
		this.documentPid = taskSetting.getDocument().getPid();
		this.documentName = taskSetting.getDocument().getName();
		this.activityEvent = taskSetting.getActivityEvent();
		this.taskActivityPid = taskSetting.getTaskActivity().getPid();
		this.taskActivityName = taskSetting.getTaskActivity().getName();
		this.formElementPid = taskSetting.getFormElementPid();
		this.startDateColumn = taskSetting.getStartDateColumn();
		this.script = taskSetting.getScript();
		this.required = taskSetting.getRequired();
		this.createPlan = taskSetting.getCreatePlan();
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

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getDocumentPid() {
		return documentPid;
	}

	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
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

	public String getTaskActivityPid() {
		return taskActivityPid;
	}

	public void setTaskActivityPid(String taskActivityPid) {
		this.taskActivityPid = taskActivityPid;
	}

	public String getTaskActivityName() {
		return taskActivityName;
	}

	public void setTaskActivityName(String taskActivityName) {
		this.taskActivityName = taskActivityName;
	}

	public String getFormElementPid() {
		return formElementPid;
	}

	public void setFormElementPid(String formElementPid) {
		this.formElementPid = formElementPid;
	}

	public String getStartDateColumn() {
		return startDateColumn;
	}

	public void setStartDateColumn(String startDateColumn) {
		this.startDateColumn = startDateColumn;
	}

	public boolean getRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public boolean getCreatePlan() {
		return createPlan;
	}

	public void setCreatePlan(boolean createPlan) {
		this.createPlan = createPlan;
	}

}
