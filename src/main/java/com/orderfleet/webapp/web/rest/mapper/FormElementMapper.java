package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.FormElementType;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;

/**
 * Mapper for the entity FormElement and its DTO FormElementDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Component
public abstract class FormElementMapper {
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

//	@Mapping(source = "formElementType.id", target = "formElementTypeId")
//	@Mapping(source = "formElementType.name", target = "formElementTypeName")
	public abstract FormElementDTO formElementToFormElementDTO(FormElement formElement);

	public abstract List<FormElementDTO> formElementsToFormElementDTOs(List<FormElement> formElements);

//	@Mapping(source = "formElementTypeId", target = "formElementType")
//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract FormElement formElementDTOToFormElement(FormElementDTO formElementDTO);

	public abstract List<FormElement> formElementDTOsToFormElements(List<FormElementDTO> formElementDTOs);

	public FormElementType formElementTypeFromId(Long id) {
		if (id == null || id == 0) {
			return null;
		}
		FormElementType formElementType = new FormElementType();
		formElementType.setId(id);
		return formElementType;
	}
	public boolean getCompanyCofig(){
		Optional<CompanyConfiguration> optconfig = companyConfigurationRepository.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if(optconfig.isPresent()) {
		if(Boolean.valueOf(optconfig.get().getValue())) {
		return true;
		}
		}
		return false;
		}
}
