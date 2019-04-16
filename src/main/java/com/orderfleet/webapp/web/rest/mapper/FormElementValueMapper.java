package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.FormElementValue;
import com.orderfleet.webapp.web.rest.dto.FormElementValueDTO;

/**
 * Mapper for the entity FormElementValue and its DTO FormElementValueDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface FormElementValueMapper {

	public FormElementValueDTO formElementValueToFormElementValueDTO(FormElementValue formElementValue);

	public List<FormElementValueDTO> formElementValuesToFormElementValueDTOs(List<FormElementValue> formElementValues);

	@Mapping(target = "formElement", ignore = true)
	@Mapping(target = "sortOrder", ignore = true)
	public FormElementValue formElementValueDTOToFormElementValue(FormElementValueDTO formElementValueDTO);

	public Set<FormElementValue> formElementValueDTOsToFormElementValues(Set<FormElementValueDTO> formElementValueDTOs);

}
