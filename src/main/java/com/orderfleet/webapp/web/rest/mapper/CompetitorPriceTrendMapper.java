package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.CompetitorPriceTrend;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.CompetitorPriceTrendDTO;

/**
 * Mapper for the entity CompetitorPriceTrend and its DTO
 * CompetitorPriceTrendDTO.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
@Component
public abstract class CompetitorPriceTrendMapper {
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

//	@Mapping(target = "priceTrendProductGroupPid", source = "priceTrendProductGroup.pid")
//	@Mapping(target = "priceTrendProductGroupName", source = "priceTrendProductGroup.name")
//	@Mapping(target = "priceTrendProductPid", source = "priceTrendProduct.pid")
//	@Mapping(target = "priceTrendProductName", source = "priceTrendProduct.name")
//	@Mapping(target = "competitorProfilePid", source = "competitorProfile.pid")
//	@Mapping(target = "competitorProfileName", source = "competitorProfile.name")
//	@Mapping(target = "userName", source = "user.firstName")
	public abstract CompetitorPriceTrendDTO competitorPriceTrendToCompetitorPriceTrendDTO(CompetitorPriceTrend competitorPriceTrend);

	public abstract List<CompetitorPriceTrendDTO> competitorPriceTrendsToCompetitorPriceTrendDTOs(
			List<CompetitorPriceTrend> competitorPriceTrends);

//	@Mapping(target = "user", ignore = true)
//	@Mapping(target = "priceTrendProductGroup", ignore = true)
//	@Mapping(target = "priceTrendProduct", ignore = true)
//	@Mapping(target = "competitorProfile", ignore = true)
//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract CompetitorPriceTrend competitorPriceTrendDTOToCompetitorPriceTrend(CompetitorPriceTrendDTO competitorPriceTrendDTO);

	public abstract List<CompetitorPriceTrend> competitorPriceTrendDTOsToCompetitorPriceTrends(
			List<CompetitorPriceTrendDTO> competitorPriceTrendDTOs);

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
