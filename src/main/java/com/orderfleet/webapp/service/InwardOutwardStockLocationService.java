package com.orderfleet.webapp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;

/**
 * Service for InwardOutwardStockLocationService
 *
 * @author fahad
 * @since Feb 22, 2017
 */
public interface InwardOutwardStockLocationService {

	
	void saveInwardOutwardStockLocation(String inwardOutwardStockLocationPids);

	Page<StockLocationDTO> findAllByCompany(Pageable pageable);

	List<StockLocationDTO> findAllByCompany();
}
