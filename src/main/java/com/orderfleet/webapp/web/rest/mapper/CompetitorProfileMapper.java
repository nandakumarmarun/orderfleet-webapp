package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.CompetitorProfile;
import com.orderfleet.webapp.web.rest.dto.CompetitorProfileDTO;

/**
 * Mapper for the entity CompetitorProfile and its DTO CompetitorProfileDTO.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface CompetitorProfileMapper {

	CompetitorProfileDTO competitorProfileToCompetitorProfileDTO(CompetitorProfile competitorProfile);

	List<CompetitorProfileDTO> competitorProfilesToCompetitorProfileDTOs(List<CompetitorProfile> competitorProfiles);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	CompetitorProfile competitorProfileDTOToCompetitorProfile(CompetitorProfileDTO competitorProfileDTO);

	List<CompetitorProfile> competitorProfileDTOsToCompetitorProfiles(List<CompetitorProfileDTO> competitorProfileDTOs);

}
