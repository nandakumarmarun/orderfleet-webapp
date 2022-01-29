package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupDTO;

/**
 * Mapper for the entity SalesTargetGroup and its DTO SalesTargetGroupDTO.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
@Component
public abstract class SalesTargetGroupMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	public abstract SalesTargetGroupDTO salesTargetGroupToSalesTargetGroupDTO(SalesTargetGroup salesTargetGroup);

	public abstract List<SalesTargetGroupDTO> salesTargetGroupsToSalesTargetGroupDTOs(List<SalesTargetGroup> salesTargetGroups);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract	SalesTargetGroup salesTargetGroupDTOToSalesTargetGroup(SalesTargetGroupDTO salesTargetGroupDTO);

	public abstract List<SalesTargetGroup> salesTargetGroupDTOsToSalesTargetGroups(List<SalesTargetGroupDTO> salesTargetGroupDTOs);

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
