package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.IncomeExpenseHead;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.IncomeExpenseHeadDTO;

/**
 *Mapper for IncomeExpenseHead
 *
 * @author fahad
 * @since Feb 15, 2017
 */
@Component
public abstract class IncomeExpenseHeadMapper {
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	public abstract IncomeExpenseHeadDTO incomeExpenseHeadToIncomeExpenseHeadDTO(IncomeExpenseHead incomeExpenseHead);

	public abstract List<IncomeExpenseHeadDTO> incomeExpenseHeadsToIncomeExpenseHeadDTOs(List<IncomeExpenseHead> incomeExpenseHeads);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract IncomeExpenseHead incomeExpenseHeadDTOToIncomeExpenseHead(IncomeExpenseHeadDTO incomeExpenseHeadDTO);

	public abstract List<IncomeExpenseHead> incomeExpenseHeadDTOsToIncomeExpenseHeads(List<IncomeExpenseHeadDTO> incomeExpenseHeadDTOs);

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
