package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.ActivityStage;

public class ActivityStageDTO {

	private String activityPid;

	private String activityName;

	private String stagePid;

	private String stageName;

	public ActivityStageDTO() {
		super();

	}

	public ActivityStageDTO(ActivityStage activityStage) {
		super();
		this.activityPid = activityStage.getActivity().getPid();
		this.activityName = activityStage.getActivity().getName();
		this.stagePid = activityStage.getStage().getPid();
		this.stageName = activityStage.getStage().getName();
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

	public String getStagePid() {
		return stagePid;
	}

	public void setStagePid(String stagePid) {
		this.stagePid = stagePid;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	
}
