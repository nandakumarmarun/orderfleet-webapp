package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.CompetitorProfileDTO;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductCompetitorDTO;

/**
 * Spring Data JPA repository for the PriceTrendProductCompetitor entity.
 * 
 * @author Muhammed Riyas T
 * @since August 30, 2016
 */
public interface PriceTrendProductCompetitorService {

	void save(String priceTrendProductPid, String assignedCompetitors);

	List<CompetitorProfileDTO> findCompetitorsByPriceTrendProductPid(String priceTrendProductPid);

	List<PriceTrendProductCompetitorDTO> findAllByCompany();

	List<PriceTrendProductCompetitorDTO> findAllByCompanyAndLastModifiedDate(LocalDateTime lastModifiedDate);

}
