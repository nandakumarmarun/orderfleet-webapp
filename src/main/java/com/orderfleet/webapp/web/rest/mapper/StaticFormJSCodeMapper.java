package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.StaticFormJSCode;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.StaticFormJSCodeDTO;

/**
 * Mapper for the entity StaticFormJSCode and its DTO StaticFormJSCodeDTO.
 * 
 * @author Sarath
 * @since Aug 3, 2016
 */
@Component
public abstract class StaticFormJSCodeMapper {
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

//	@Mapping(source = "document.pid", target = "documentPid")
//	@Mapping(source = "document.name", target = "documentName")
	public abstract StaticFormJSCodeDTO staticFormJSCodeToStaticFormJSCodeDTO(StaticFormJSCode staticFormJSCode);

	public abstract List<StaticFormJSCodeDTO> staticFormJSCodesToStaticFormJSCodeDTOs(
			List<StaticFormJSCode> staticFormJSCodes);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "document", ignore = true)
	public abstract StaticFormJSCode staticFormJSCodeDTOToStaticFormJSCode(StaticFormJSCodeDTO staticFormJSCodeDTO);

	public abstract List<StaticFormJSCode> staticFormJSCodeDTOsToStaticFormJSCodes(
			List<StaticFormJSCodeDTO> staticFormJSCodeDTOs);

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
