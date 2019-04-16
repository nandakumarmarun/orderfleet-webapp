package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.CompetitorPriceTrend;
import com.orderfleet.webapp.web.rest.dto.CompetitorPriceTrendDTO;

/**
 * Mapper for the entity CompetitorPriceTrend and its DTO
 * CompetitorPriceTrendDTO.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface CompetitorPriceTrendMapper {

	@Mapping(target = "priceTrendProductGroupPid", source = "priceTrendProductGroup.pid")
	@Mapping(target = "priceTrendProductGroupName", source = "priceTrendProductGroup.name")
	@Mapping(target = "priceTrendProductPid", source = "priceTrendProduct.pid")
	@Mapping(target = "priceTrendProductName", source = "priceTrendProduct.name")
	@Mapping(target = "competitorProfilePid", source = "competitorProfile.pid")
	@Mapping(target = "competitorProfileName", source = "competitorProfile.name")
	@Mapping(target = "userName", source = "user.firstName")
	CompetitorPriceTrendDTO competitorPriceTrendToCompetitorPriceTrendDTO(CompetitorPriceTrend competitorPriceTrend);

	List<CompetitorPriceTrendDTO> competitorPriceTrendsToCompetitorPriceTrendDTOs(
			List<CompetitorPriceTrend> competitorPriceTrends);

	@Mapping(target = "user", ignore = true)
	@Mapping(target = "priceTrendProductGroup", ignore = true)
	@Mapping(target = "priceTrendProduct", ignore = true)
	@Mapping(target = "competitorProfile", ignore = true)
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	CompetitorPriceTrend competitorPriceTrendDTOToCompetitorPriceTrend(CompetitorPriceTrendDTO competitorPriceTrendDTO);

	List<CompetitorPriceTrend> competitorPriceTrendDTOsToCompetitorPriceTrends(
			List<CompetitorPriceTrendDTO> competitorPriceTrendDTOs);

}
