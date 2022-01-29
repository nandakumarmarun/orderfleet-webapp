package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.CompetitorProfile;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.CompetitorProfileDTO;

/**
 * Mapper for the entity CompetitorProfile and its DTO CompetitorProfileDTO.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
@Component
public abstract class CompetitorProfileMapper {
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	public abstract CompetitorProfileDTO competitorProfileToCompetitorProfileDTO(CompetitorProfile competitorProfile);

	public abstract List<CompetitorProfileDTO> competitorProfilesToCompetitorProfileDTOs(List<CompetitorProfile> competitorProfiles);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract CompetitorProfile competitorProfileDTOToCompetitorProfile(CompetitorProfileDTO competitorProfileDTO);

	public abstract List<CompetitorProfile> competitorProfileDTOsToCompetitorProfiles(List<CompetitorProfileDTO> competitorProfileDTOs);

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
