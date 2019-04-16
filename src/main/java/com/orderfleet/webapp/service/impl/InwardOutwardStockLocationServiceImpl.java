package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.InwardOutwardStockLocation;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.InwardOutwardStockLocationRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.InwardOutwardStockLocationService;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;
import com.orderfleet.webapp.web.rest.mapper.StockLocationMapper;

/**
 *Servive for InwardOutwardStockLocationServiceImpl
 *
 * @author fahad
 * @since Feb 22, 2017
 */
@Service
@Transactional
public class InwardOutwardStockLocationServiceImpl implements InwardOutwardStockLocationService{
 
	@Inject 
	private StockLocationRepository stockLocationRepository;
	
	@Inject
	private InwardOutwardStockLocationRepository inwardOutwardStockLocationRepository;
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private StockLocationMapper stockLocationMapper;
	
	@Override
	public void saveInwardOutwardStockLocation(String assignedStockLocations) {
		String[] stockLocations = assignedStockLocations.split(",");
		Company company=companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		List<InwardOutwardStockLocation> inwardOutwardStockLocations=new ArrayList<>();
		for (String stockLocationPid : stockLocations) {
			StockLocation stockLocation = stockLocationRepository.findOneByPid(stockLocationPid).get();
			InwardOutwardStockLocation inwardOutwardStockLocation=new InwardOutwardStockLocation(stockLocation, company);
			inwardOutwardStockLocations.add(inwardOutwardStockLocation);
		}
		inwardOutwardStockLocationRepository.deleteByCompanyId(company.getId());
		inwardOutwardStockLocationRepository.save(inwardOutwardStockLocations);
	}

	@Override
	public Page<StockLocationDTO> findAllByCompany(Pageable pageable) {
		Page<StockLocation> pageStockLocation = inwardOutwardStockLocationRepository.findAllStockLocationByCompanyId(pageable);
		Page<StockLocationDTO> pageStockLocationDTO=new PageImpl<StockLocationDTO>(stockLocationMapper.stockLocationsToStockLocationDTOs(pageStockLocation.getContent()),pageable,pageStockLocation.getTotalElements());
		return pageStockLocationDTO;
		
	}

	@Override
	public List<StockLocationDTO> findAllByCompany() {
		List<StockLocation> stockLocations=inwardOutwardStockLocationRepository.findAllByCompanyId();
		List<StockLocationDTO>stockLocationDTOs=stockLocationMapper.stockLocationsToStockLocationDTOs(stockLocations);
		return stockLocationDTOs;
	}
}
