package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.FormElementMasterType;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.FormElementMasterTypeDTO;

/**
 * Mapper for the entity FormElementMasterType and its DTO FormElementMasterTypeDTO.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
@Component
public abstract class FormElementMasterTypeMapper {
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	public abstract	FormElementMasterTypeDTO formElementMasterToFormElementMasterTypeDTO(FormElementMasterType formElementMaster);

	public abstract List<FormElementMasterTypeDTO> formElementMastersToFormElementMasterTypeDTOs(List<FormElementMasterType> formElementMasters);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract FormElementMasterType formElementMasterDTOToFormElementMasterType(FormElementMasterTypeDTO formElementMasterDTO);

	public abstract List<FormElementMasterType> formElementMasterDTOsToFormElementMasterTypes(List<FormElementMasterTypeDTO> formElementMasterDTOs);
	
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
