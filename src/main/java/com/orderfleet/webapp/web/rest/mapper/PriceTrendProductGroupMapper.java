package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.PriceTrendProductGroup;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductGroupDTO;

/**
 * Mapper for the entity PriceTrendProductGroup and its DTO
 * PriceTrendProductGroupDTO.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
@Component
public abstract class PriceTrendProductGroupMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	public abstract PriceTrendProductGroupDTO priceTrendProductGroupToPriceTrendProductGroupDTO(
			PriceTrendProductGroup priceTrendProductGroup);

	public abstract List<PriceTrendProductGroupDTO> priceTrendProductGroupsToPriceTrendProductGroupDTOs(
			List<PriceTrendProductGroup> priceTrendProductGroups);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract PriceTrendProductGroup priceTrendProductGroupDTOToPriceTrendProductGroup(
			PriceTrendProductGroupDTO priceTrendProductGroupDTO);

	public abstract List<PriceTrendProductGroup> priceTrendProductGroupDTOsToPriceTrendProductGroups(
			List<PriceTrendProductGroupDTO> priceTrendProductGroupDTOs);
	
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
