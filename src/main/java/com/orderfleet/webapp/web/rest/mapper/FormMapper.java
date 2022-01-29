package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.FormDTO;

/**
 * Mapper for the entity Form and its DTO FormDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
@Component
public abstract class FormMapper {
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;


	public abstract FormDTO formToFormDTO(Form form);

	public abstract List<FormDTO> formsToFormDTOs(List<Form> forms);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract Form formDTOToForm(FormDTO formDTO);

	public abstract List<Form> formDTOsToForms(List<FormDTO> formDTOs);

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
