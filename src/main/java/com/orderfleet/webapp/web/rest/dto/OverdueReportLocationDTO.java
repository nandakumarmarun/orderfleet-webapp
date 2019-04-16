package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class OverdueReportLocationDTO {
	
	private String pid;

	private String name;

	private List<OverdueReportLocationDTO> overdueReportLocations;

	private List<OverdueReportAccountDTO> overdueReportAccounts;

	private List<OverdueReportPeriod> overdueReportPeriods;

	public OverdueReportLocationDTO() {

	}

	public OverdueReportLocationDTO(String pid, String name, List<OverdueReportLocationDTO> overdueReportLocations,
			List<OverdueReportAccountDTO> overdueReportAccounts, List<OverdueReportPeriod> overdueReportPeriods) {
		super();
		this.pid = pid;
		this.name = name;
		this.overdueReportLocations = overdueReportLocations;
		this.overdueReportAccounts = overdueReportAccounts;
		this.overdueReportPeriods = overdueReportPeriods;
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

	public List<OverdueReportLocationDTO> getOverdueReportLocations() {
		return overdueReportLocations;
	}

	public void setOverdueReportLocations(List<OverdueReportLocationDTO> overdueReportLocations) {
		this.overdueReportLocations = overdueReportLocations;
	}

	public List<OverdueReportAccountDTO> getOverdueReportAccounts() {
		return overdueReportAccounts;
	}

	public void setOverdueReportAccounts(List<OverdueReportAccountDTO> overdueReportAccounts) {
		this.overdueReportAccounts = overdueReportAccounts;
	}

	public List<OverdueReportPeriod> getOverdueReportPeriods() {
		return overdueReportPeriods;
	}

	public void setOverdueReportPeriods(List<OverdueReportPeriod> overdueReportPeriods) {
		this.overdueReportPeriods = overdueReportPeriods;
	}

}
