package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.ExecutiveTaskExecution;

public class VisitReportView {
	
	private String pid;
	
	private String employeeName;
	
	private String route;
	
	private LocalDateTime attndncTime;
	
	private List<VisitDetailReportView> visitDetailReportView;

	public VisitReportView() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VisitReportView(String pid, String employeeName, String route, LocalDateTime attndncTime,
			List<VisitDetailReportView> visitDetailReportView) {
		super();
		this.pid = pid;
		this.employeeName = employeeName;
		this.route = route;
		this.attndncTime = attndncTime;
		this.visitDetailReportView = visitDetailReportView;
	}

	
	public VisitReportView(ExecutiveTaskExecution executiveTaskExecution)
	{
		super();
	}
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public LocalDateTime getAttndncTime() {
		return attndncTime;
	}

	public void setAttndncTime(LocalDateTime attndncTime) {
		this.attndncTime = attndncTime;
	}

	public List<VisitDetailReportView> getVisitDetailReportView() {
		return visitDetailReportView;
	}

	public void setVisitDetailReportView(List<VisitDetailReportView> visitDetailReportView) {
		this.visitDetailReportView = visitDetailReportView;
	}

	@Override
	public String toString() {
		return "VisitReportView [pid=" + pid + ", employeeName=" + employeeName + ", route=" + route + ", attndncTime="
				+ attndncTime + ", visitDetailReportView=" + visitDetailReportView + "]";
	}
	
	
	
}
