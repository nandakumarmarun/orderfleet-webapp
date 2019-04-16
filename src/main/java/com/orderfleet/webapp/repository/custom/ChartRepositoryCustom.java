package com.orderfleet.webapp.repository.custom;

import java.time.LocalDateTime;

import com.orderfleet.webapp.web.rest.chart.dto.BarCharrtDTO;

public interface ChartRepositoryCustom {
	
	BarCharrtDTO getCompetitorPriceTrendByUser(Long productId, String userPid, LocalDateTime from, LocalDateTime to, String priceTrendName);
	
	BarCharrtDTO getCompetitorPriceTrendByCompetitorProfile(Long productId, String competitorPid, LocalDateTime from, LocalDateTime to, String priceTrendName);

}
