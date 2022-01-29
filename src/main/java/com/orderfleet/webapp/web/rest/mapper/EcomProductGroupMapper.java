package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.EcomProductGroup;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;

/**
 * Mapper for the entity ProductGroup and its DTO ProductGroupDTO.
 * 
 * @author Anish
 * @since May 14, 2020
 */
@Component
public abstract class EcomProductGroupMapper {
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

//	@Mapping(target = "image", ignore = true)
	public abstract EcomProductGroupDTO productGroupToProductGroupDTO(EcomProductGroup ecomProductGroup);

	public abstract List<EcomProductGroupDTO> productGroupsToProductGroupDTOs(List<EcomProductGroup> ecomProductGroups);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract EcomProductGroup productGroupDTOToProductGroup(EcomProductGroupDTO ecomProductGroupDTO);

	public abstract List<EcomProductGroup> productGroupDTOsToProductGroups(List<EcomProductGroupDTO> ecomProductGroupDTOs);

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
