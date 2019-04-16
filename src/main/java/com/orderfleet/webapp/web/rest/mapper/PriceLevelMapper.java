package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;

/**
 * Mapper for the entity PriceLevel and its DTO PriceLevelDTO.
 * 
 * @author Muhammed Riyas T
 * @since August 22, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface PriceLevelMapper {

	@Mapping(target = "levelListDTOs", ignore = true)
	PriceLevelDTO priceLevelToPriceLevelDTO(PriceLevel priceLevel);

	List<PriceLevelDTO> priceLevelsToPriceLevelDTOs(List<PriceLevel> priceLevels);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	PriceLevel priceLevelDTOToPriceLevel(PriceLevelDTO priceLevelDTO);

	List<PriceLevel> priceLevelDTOsToPriceLevels(List<PriceLevelDTO> priceLevelDTOs);

}
