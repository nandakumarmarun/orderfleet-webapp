package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.Bank;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.BankDTO;

/**
 * Mapper for the entity Bank and its DTO BankDTO.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
@Component
public abstract class BankMapper {
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	public abstract BankDTO bankToBankDTO(Bank bank);

	public abstract List<BankDTO> banksToBankDTOs(List<Bank> banks);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract Bank bankDTOToBank(BankDTO bankDTO);

	public abstract List<Bank> bankDTOsToBanks(List<BankDTO> bankDTOs);
	
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
