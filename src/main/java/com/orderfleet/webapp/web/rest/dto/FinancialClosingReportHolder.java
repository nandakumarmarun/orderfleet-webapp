package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class FinancialClosingReportHolder {
	
	private List<FinancialClosingReportDTO> financialClosings;
	
	private List<FinancialClosingReportDTO> pettyCashClosings;
	
	private double fClosingTotal;
	
	private double pettyCashTotal;
	
	private double openingBalance = 0.0;
	
	private String selectedUserPid;

	public List<FinancialClosingReportDTO> getFinancialClosings() {
		return financialClosings;
	}

	public void setFinancialClosings(List<FinancialClosingReportDTO> financialClosings) {
		this.financialClosings = financialClosings;
	}

	public List<FinancialClosingReportDTO> getPettyCashClosings() {
		return pettyCashClosings;
	}

	public void setPettyCashClosings(List<FinancialClosingReportDTO> pettyCashClosings) {
		this.pettyCashClosings = pettyCashClosings;
	}

	public double getfClosingTotal() {
		return fClosingTotal;
	}

	public void setfClosingTotal(double fClosingTotal) {
		this.fClosingTotal = fClosingTotal;
	}

	public double getPettyCashTotal() {
		return pettyCashTotal;
	}

	public void setPettyCashTotal(double pettyCashTotal) {
		this.pettyCashTotal = pettyCashTotal;
	}

	public double getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(double openingBalance) {
		this.openingBalance = openingBalance;
	}

	public String getSelectedUserPid() {
		return selectedUserPid;
	}

	public void setSelectedUserPid(String selectedUserPid) {
		this.selectedUserPid = selectedUserPid;
	}

}
