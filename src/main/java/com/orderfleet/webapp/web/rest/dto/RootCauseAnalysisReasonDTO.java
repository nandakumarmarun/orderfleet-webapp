package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.RootCauseAnalysisReason;

public class RootCauseAnalysisReasonDTO {

	private String pid;

	private String name;

	private String alias;

	private String description;
	
	public RootCauseAnalysisReasonDTO() {
		super();
	}

	public RootCauseAnalysisReasonDTO(RootCauseAnalysisReason rootCauseAnalysisReason) {
		super();
		this.pid = rootCauseAnalysisReason.getPid();
		this.name = rootCauseAnalysisReason.getName();
		this.alias = rootCauseAnalysisReason.getAlias();
		this.description = rootCauseAnalysisReason.getDescription();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
