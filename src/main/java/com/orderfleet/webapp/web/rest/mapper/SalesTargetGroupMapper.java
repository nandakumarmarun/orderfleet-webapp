package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupDTO;

/**
 * Mapper for the entity SalesTargetGroup and its DTO SalesTargetGroupDTO.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface SalesTargetGroupMapper {

	SalesTargetGroupDTO salesTargetGroupToSalesTargetGroupDTO(SalesTargetGroup salesTargetGroup);

	List<SalesTargetGroupDTO> salesTargetGroupsToSalesTargetGroupDTOs(List<SalesTargetGroup> salesTargetGroups);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	SalesTargetGroup salesTargetGroupDTOToSalesTargetGroup(SalesTargetGroupDTO salesTargetGroupDTO);

	List<SalesTargetGroup> salesTargetGroupDTOsToSalesTargetGroups(List<SalesTargetGroupDTO> salesTargetGroupDTOs);

}
