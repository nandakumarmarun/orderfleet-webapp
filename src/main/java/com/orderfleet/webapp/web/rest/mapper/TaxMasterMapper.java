package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.TaxMaster;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.TaxMasterDTO;

/**
 * Mapper for the entity TaxMaster and its DTO TaxMasterDTO.
 * 
 * @author Shaheer
 * @since May 07, 2016
 */
@Component
public abstract class TaxMasterMapper {
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	public abstract TaxMasterDTO taxMasterToTaxMasterDTO(TaxMaster taxMaster);

	public abstract List<TaxMasterDTO> taxMastersToTaxMasterDTOs(List<TaxMaster> taxMasters);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract TaxMaster taxMasterDTOToTaxMaster(TaxMasterDTO taxMasterDTO);

	public abstract	List<TaxMaster> taxMasterDTOsToTaxMasters(List<TaxMasterDTO> taxMasterDTOs);

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
