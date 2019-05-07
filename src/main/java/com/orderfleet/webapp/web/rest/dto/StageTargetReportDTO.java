package com.orderfleet.webapp.web.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.domain.enums.StageNameType;
import com.orderfleet.webapp.domain.enums.StageType;

public class StageTargetReportDTO {

	private String pid;

	private String stageName;

	private long achieved;

	private long target;

	private double percentage;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public long getAchieved() {
		return achieved;
	}

	public void setAchieved(long achieved) {
		this.achieved = achieved;
	}

	public long getTarget() {
		return target;
	}

	public void setTarget(long target) {
		this.target = target;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

}