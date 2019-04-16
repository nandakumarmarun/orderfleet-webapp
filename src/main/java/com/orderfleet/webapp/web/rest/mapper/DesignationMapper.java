package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.Designation;
import com.orderfleet.webapp.web.rest.dto.DesignationDTO;

/**
 * Mapper for the entity Designation and its DTO DesignationDTO.
 * 
 * @author Muhammed Riyas T
 * @since May 24, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface DesignationMapper {

	DesignationDTO designationToDesignationDTO(Designation designation);

	List<DesignationDTO> designationsToDesignationDTOs(List<Designation> designations);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	Designation designationDTOToDesignation(DesignationDTO designationDTO);

	List<Designation> designationDTOsToDesignations(List<DesignationDTO> designationDTOs);

}
