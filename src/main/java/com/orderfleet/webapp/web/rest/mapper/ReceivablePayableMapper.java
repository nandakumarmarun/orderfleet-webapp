package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;

/**
 * Mapper for the entity ReceivablePayable and its DTO ReceivablePayableDTO.
 * 
 * @author Sarath
 * @since Aug 16, 2016
 */

@Component
public abstract class ReceivablePayableMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
//	@Mapping(source = "accountProfile.pid", target = "accountPid")
//	@Mapping(source = "accountProfile.name", target = "accountName")
//	@Mapping(source = "accountProfile.accountType.name", target = "accountType")
//	@Mapping(source = "accountProfile.address", target = "accountAddress")
	public abstract ReceivablePayableDTO receivablePayableToReceivablePayableDTO(ReceivablePayable receivablePayable);

	public abstract List<ReceivablePayableDTO> receivablePayablesToReceivablePayableDTOs(List<ReceivablePayable> productCategories);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "accountProfile", ignore = true)
	public abstract ReceivablePayable receivablePayableDTOToReceivablePayable(ReceivablePayableDTO receivablePayableDTO);

	public abstract List<ReceivablePayable> receivablePayableDTOsToProductCategories(List<ReceivablePayableDTO> receivablePayableDTOs);

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
