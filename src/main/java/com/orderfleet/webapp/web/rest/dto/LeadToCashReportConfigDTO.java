package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.LeadToCashReportConfig;

public class LeadToCashReportConfigDTO {

	private String pid;
	
	private String name;
	
	private int sortOrder;
	
	private String stagetPid;
	
	private String stageName;

	
	
	public LeadToCashReportConfigDTO() {
		super();
	}

	public LeadToCashReportConfigDTO(LeadToCashReportConfig leadToCashReportConfig) {
		super();
		this.pid = leadToCashReportConfig.getPid();
		this.name = leadToCashReportConfig.getName();
		this.sortOrder = leadToCashReportConfig.getSortOrder();
		this.stagetPid = leadToCashReportConfig.getStage().getPid();
		this.stageName = leadToCashReportConfig.getStage().getName();
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

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getStagetPid() {
		return stagetPid;
	}

	public void setStagetPid(String stagetPid) {
		this.stagetPid = stagetPid;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	
	
	
}
