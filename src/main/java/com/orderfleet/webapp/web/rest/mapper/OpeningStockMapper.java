package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;

/**
 * Mapper for the entity OpeningStock and its DTO OpeningStockDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 02, 2016
 */
@Component
public abstract class OpeningStockMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

//	@Mapping(source = "stockLocation.pid", target = "stockLocationPid")
//	@Mapping(source = "stockLocation.name", target = "stockLocationName")
//	@Mapping(source = "productProfile.pid", target = "productProfilePid")
//	@Mapping(source = "productProfile.name", target = "productProfileName")
	public abstract OpeningStockDTO openingStockToOpeningStockDTO(OpeningStock openingStock);

	public abstract List<OpeningStockDTO> openingStocksToOpeningStockDTOs(List<OpeningStock> openingStocks);

//	@Mapping(source = "stockLocationPid", target = "stockLocation")
//	@Mapping(source = "productProfilePid", target = "productProfile")
//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract OpeningStock openingStockDTOToOpeningStock(OpeningStockDTO openingStockDTO);

	public abstract List<OpeningStock> openingStockDTOsToOpeningStocks(List<OpeningStockDTO> openingStockDTOs);

	public StockLocation stockLocationFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return stockLocationRepository.findOneByPid(pid).map(stockLocation -> stockLocation).orElse(null);
	}

	public ProductProfile productProfileFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return productProfileRepository.findOneByPid(pid).map(productProfile -> productProfile).orElse(null);
	}
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
