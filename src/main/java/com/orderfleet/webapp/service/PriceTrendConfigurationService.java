package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import com.orderfleet.webapp.web.rest.dto.PriceTrendConfigurationDTO;

/**
 * Service Interface for managing PriceTrendConfiguration.
 * 
 * @author Muhammed Riyas T
 * @since Sep 03, 2016
 */
public interface PriceTrendConfigurationService {

	/**
	 * Save a priceTrendConfigurationDTOs.
	 * 
	 * @param priceTrendConfigurationDTOs
	 *            the entity to save
	 * @return the persisted entity
	 */
	void save(List<PriceTrendConfigurationDTO> priceTrendConfigurationDTOs);

	List<PriceTrendConfigurationDTO> findAllByCompany();

	List<PriceTrendConfigurationDTO> findAllByCompanyAndLastModifiedDate(LocalDateTime lastModifiedDate);

}