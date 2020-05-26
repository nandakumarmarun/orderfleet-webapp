package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.EcomProductGroup;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;

/**
 * Mapper for the entity ProductGroup and its DTO ProductGroupDTO.
 * 
 * @author Anish
 * @since May 14, 2020
 */
@Mapper(componentModel = "spring", uses = {})
public interface EcomProductGroupMapper {

	@Mapping(target = "image", ignore = true)
	EcomProductGroupDTO productGroupToProductGroupDTO(EcomProductGroup ecomProductGroup);

	List<EcomProductGroupDTO> productGroupsToProductGroupDTOs(List<EcomProductGroup> ecomProductGroups);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	EcomProductGroup productGroupDTOToProductGroup(EcomProductGroupDTO ecomProductGroupDTO);

	List<EcomProductGroup> productGroupDTOsToProductGroups(List<EcomProductGroupDTO> ecomProductGroupDTOs);

}
