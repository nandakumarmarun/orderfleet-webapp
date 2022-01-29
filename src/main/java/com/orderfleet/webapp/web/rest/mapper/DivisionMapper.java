package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.DivisionDTO;

/**
 * Mapper for the entity Division and its DTO DivisionDTO.
 * 
 * @author Shaheer
 * @since May 07, 2016
 */
@Component
public abstract class DivisionMapper {
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	public abstract DivisionDTO divisionToDivisionDTO(Division division);

	public abstract List<DivisionDTO> divisionsToDivisionDTOs(List<Division> divisions);

//    @Mapping(target = "company", ignore = true)
//    @Mapping(target = "id", ignore = true)
    public abstract  Division divisionDTOToDivision(DivisionDTO divisionDTO);

    public abstract List<Division> divisionDTOsToDivisions(List<DivisionDTO> divisionDTOs);
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
