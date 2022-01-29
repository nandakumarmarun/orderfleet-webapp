package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.FormElementValue;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.FormElementValueDTO;

/**
 * Mapper for the entity FormElementValue and its DTO FormElementValueDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Component
public abstract class FormElementValueMapper {
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;


	public abstract FormElementValueDTO formElementValueToFormElementValueDTO(FormElementValue formElementValue);

	public abstract List<FormElementValueDTO> formElementValuesToFormElementValueDTOs(List<FormElementValue> formElementValues);

//	@Mapping(target = "formElement", ignore = true)
//	@Mapping(target = "sortOrder", ignore = true)
	public abstract FormElementValue formElementValueDTOToFormElementValue(FormElementValueDTO formElementValueDTO);

	public abstract Set<FormElementValue> formElementValueDTOsToFormElementValues(Set<FormElementValueDTO> formElementValueDTOs);
	
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
