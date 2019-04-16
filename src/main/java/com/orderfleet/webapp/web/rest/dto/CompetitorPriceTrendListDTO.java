package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class CompetitorPriceTrendListDTO {

	List<CompetitorPriceTrendDTO> competitorPriceTrends;

	public List<CompetitorPriceTrendDTO> getCompetitorPriceTrends() {
		return competitorPriceTrends;
	}

	public void setCompetitorPriceTrends(List<CompetitorPriceTrendDTO> competitorPriceTrends) {
		this.competitorPriceTrends = competitorPriceTrends;
	}

}
