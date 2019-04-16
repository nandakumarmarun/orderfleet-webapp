package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class OverdueReportAccountDTO {

	private String pid;
	
	private String name;

	private List<OverdueReportBillDTO> overdueReportBills;

	private List<OverdueReportPeriod> overdueReportPeriods;

	public OverdueReportAccountDTO() {
	}

	public OverdueReportAccountDTO(String pid, String name, List<OverdueReportBillDTO> overdueReportBills,
			List<OverdueReportPeriod> overdueReportPeriods) {
		super();
		this.pid = pid;
		this.name = name;
		this.overdueReportBills = overdueReportBills;
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

	public List<OverdueReportBillDTO> getOverdueReportBills() {
		return overdueReportBills;
	}

	public void setOverdueReportBills(List<OverdueReportBillDTO> overdueReportBills) {
		this.overdueReportBills = overdueReportBills;
	}

	public List<OverdueReportPeriod> getOverdueReportPeriods() {
		return overdueReportPeriods;
	}

	public void setOverdueReportPeriods(List<OverdueReportPeriod> overdueReportPeriods) {
		this.overdueReportPeriods = overdueReportPeriods;
	}

}
