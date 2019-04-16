package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.PriceTrendProductGroup;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductGroupDTO;

/**
 * Mapper for the entity PriceTrendProductGroup and its DTO
 * PriceTrendProductGroupDTO.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
@Mapper(componentModel = "spring", uses = { PriceTrendProductMapper.class })
public interface PriceTrendProductGroupMapper {

	PriceTrendProductGroupDTO priceTrendProductGroupToPriceTrendProductGroupDTO(
			PriceTrendProductGroup priceTrendProductGroup);

	List<PriceTrendProductGroupDTO> priceTrendProductGroupsToPriceTrendProductGroupDTOs(
			List<PriceTrendProductGroup> priceTrendProductGroups);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	PriceTrendProductGroup priceTrendProductGroupDTOToPriceTrendProductGroup(
			PriceTrendProductGroupDTO priceTrendProductGroupDTO);

	List<PriceTrendProductGroup> priceTrendProductGroupDTOsToPriceTrendProductGroups(
			List<PriceTrendProductGroupDTO> priceTrendProductGroupDTOs);

}
