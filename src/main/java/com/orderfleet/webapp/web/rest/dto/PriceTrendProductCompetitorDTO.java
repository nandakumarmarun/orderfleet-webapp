package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.PriceTrendProductCompetitor;

public class PriceTrendProductCompetitorDTO implements Cloneable {

	private String priceTrendProductPid;
	private String priceTrendProductName;
	private String competitorPid;
	private String competitorName;
	private LocalDateTime lastModifiedDate;

	public PriceTrendProductCompetitorDTO() {
		super();
	}

	public PriceTrendProductCompetitorDTO(PriceTrendProductCompetitor priceTrendProductCompetitor) {
		super();
		this.priceTrendProductPid = priceTrendProductCompetitor.getPriceTrendProduct().getPid();
		this.priceTrendProductName = priceTrendProductCompetitor.getPriceTrendProduct().getName();
		this.competitorPid = priceTrendProductCompetitor.getCompetitor().getPid();
		this.competitorName = priceTrendProductCompetitor.getCompetitor().getName();
		this.lastModifiedDate = priceTrendProductCompetitor.getLastModifiedDate();
	}

	public String getPriceTrendProductPid() {
		return priceTrendProductPid;
	}

	public void setPriceTrendProductPid(String priceTrendProductPid) {
		this.priceTrendProductPid = priceTrendProductPid;
	}

	public String getPriceTrendProductName() {
		return priceTrendProductName;
	}

	public void setPriceTrendProductName(String priceTrendProductName) {
		this.priceTrendProductName = priceTrendProductName;
	}

	public String getCompetitorPid() {
		return competitorPid;
	}

	public void setCompetitorPid(String competitorPid) {
		this.competitorPid = competitorPid;
	}

	public String getCompetitorName() {
		return competitorName;
	}

	public void setCompetitorFName(String competitorName) {
		this.competitorName = competitorName;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
