package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;

/**
 * Mapper for the entity AccountType and its DTO AccountTypeDTO.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
@Component
public abstract class AccountTypeMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	public abstract AccountTypeDTO accountTypeToAccountTypeDTO(AccountType accountType);

	public abstract List<AccountTypeDTO> accountTypesToAccountTypeDTOs(List<AccountType> accountTypes);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract AccountType accountTypeDTOToAccountType(AccountTypeDTO accountTypeDTO);

	public abstract List<AccountType> accountTypeDTOsToAccountTypes(List<AccountTypeDTO> accountTypeDTOs);

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
