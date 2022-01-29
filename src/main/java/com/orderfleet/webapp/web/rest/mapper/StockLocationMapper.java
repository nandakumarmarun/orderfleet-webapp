package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;

/**
 * Mapper for the entity StockLocation and its DTO StockLocationDTO.
 * 
 * @author Muhammed Riyas T
 * @since July 15, 2016
 */
@Component
public abstract class StockLocationMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	public abstract StockLocationDTO stockLocationToStockLocationDTO(StockLocation stockLocation);

	public abstract List<StockLocationDTO> stockLocationsToStockLocationDTOs(List<StockLocation> stockLocations);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract StockLocation stockLocationDTOToStockLocation(StockLocationDTO stockLocationDTO);

	public abstract List<StockLocation> stockLocationDTOsToStockLocations(List<StockLocationDTO> stockLocationDTOs);

	public boolean getCompanyCofig(){
		Optional<CompanyConfiguration> optconfig = companyConfigurationRepository.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if(optconfig.isPresent()) {
		if(Boolean.valueOf(optconfig.get().getValue())) {
		return true;
		}
		}
		return false;
		}
}
