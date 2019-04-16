package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.web.rest.dto.DivisionDTO;

/**
 * Mapper for the entity Division and its DTO DivisionDTO.
 * 
 * @author Shaheer
 * @since May 07, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface DivisionMapper {

    DivisionDTO divisionToDivisionDTO(Division division);

    List<DivisionDTO> divisionsToDivisionDTOs(List<Division> divisions);

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "id", ignore = true)
    Division divisionDTOToDivision(DivisionDTO divisionDTO);

    List<Division> divisionDTOsToDivisions(List<DivisionDTO> divisionDTOs);

}
