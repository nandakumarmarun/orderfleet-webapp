package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * A DTO for receiving filter data from chart UI.
 * 
 */
public class CompetitorPriceTrendChartFilterDTO {

	private String userPid;
	
	private String competitorPid;

	private String filterBy;

	private LocalDate fromDate;

	private LocalDate toDate;
	
	private List<String> priceLevelNames;
	
	private String ptProductGroupPid;

	public String getUserPid() {
		return userPid;
	}

	public String getCompetitorPid() {
		return competitorPid;
	}

	public void setCompetitorPid(String competitorPid) {
		this.competitorPid = competitorPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getFilterBy() {
		return filterBy;
	}

	public void setFilterBy(String filterBy) {
		this.filterBy = filterBy;
	}

	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}

	public List<String> getPriceLevelNames() {
		return priceLevelNames;
	}

	public void setPriceLevelNames(List<String> priceLevelNames) {
		this.priceLevelNames = priceLevelNames;
	}

	public String getPtProductGroupPid() {
		return ptProductGroupPid;
	}

	public void setPtProductGroupPid(String ptProductGroupPid) {
		this.ptProductGroupPid = ptProductGroupPid;
	}
	
}
