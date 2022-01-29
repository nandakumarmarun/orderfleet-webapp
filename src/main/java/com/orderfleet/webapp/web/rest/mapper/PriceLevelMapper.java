package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;

/**
 * Mapper for the entity PriceLevel and its DTO PriceLevelDTO.
 * 
 * @author Muhammed Riyas T
 * @since August 22, 2016
 */
@Component
public abstract class PriceLevelMapper {
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

//	@Mapping(target = "levelListDTOs", ignore = true)
	public abstract PriceLevelDTO priceLevelToPriceLevelDTO(PriceLevel priceLevel);

	public abstract List<PriceLevelDTO> priceLevelsToPriceLevelDTOs(List<PriceLevel> priceLevels);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract PriceLevel priceLevelDTOToPriceLevel(PriceLevelDTO priceLevelDTO);

	public abstract List<PriceLevel> priceLevelDTOsToPriceLevels(List<PriceLevelDTO> priceLevelDTOs);
	
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
