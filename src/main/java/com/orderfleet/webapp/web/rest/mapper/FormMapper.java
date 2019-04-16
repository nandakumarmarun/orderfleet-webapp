package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.web.rest.dto.FormDTO;

/**
 * Mapper for the entity Form and its DTO FormDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
@Mapper(componentModel = "spring", uses = { FormElementMapper.class })
public interface FormMapper {

	FormDTO formToFormDTO(Form form);

	List<FormDTO> formsToFormDTOs(List<Form> forms);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	Form formDTOToForm(FormDTO formDTO);

	List<Form> formDTOsToForms(List<FormDTO> formDTOs);

}
