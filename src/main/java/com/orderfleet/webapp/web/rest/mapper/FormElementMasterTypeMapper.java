package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.FormElementMasterType;
import com.orderfleet.webapp.web.rest.dto.FormElementMasterTypeDTO;

/**
 * Mapper for the entity FormElementMasterType and its DTO FormElementMasterTypeDTO.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface FormElementMasterTypeMapper {

	FormElementMasterTypeDTO formElementMasterToFormElementMasterTypeDTO(FormElementMasterType formElementMaster);

	List<FormElementMasterTypeDTO> formElementMastersToFormElementMasterTypeDTOs(List<FormElementMasterType> formElementMasters);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	FormElementMasterType formElementMasterDTOToFormElementMasterType(FormElementMasterTypeDTO formElementMasterDTO);

	List<FormElementMasterType> formElementMasterDTOsToFormElementMasterTypes(List<FormElementMasterTypeDTO> formElementMasterDTOs);

}
