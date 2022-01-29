package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;


import com.orderfleet.webapp.domain.CompetitorProfile;
import com.orderfleet.webapp.web.rest.dto.CompetitorProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.CompetitorProfileMapper;
@Component
public class CompetitorProfileMapperImpl extends CompetitorProfileMapper {

	 @Override
	    public CompetitorProfileDTO competitorProfileToCompetitorProfileDTO(CompetitorProfile competitorProfile) {
	        if ( competitorProfile == null ) {
	            return null;
	        }

	        CompetitorProfileDTO competitorProfileDTO = new CompetitorProfileDTO();

	        competitorProfileDTO.setActivated( competitorProfile.getActivated() );
	        competitorProfileDTO.setPid( competitorProfile.getPid() );
	        competitorProfileDTO.setName(competitorProfileName( competitorProfile ));
	        competitorProfileDTO.setAlias( competitorProfile.getAlias() );
	        competitorProfileDTO.setDescription( competitorProfile.getDescription() );
	        competitorProfileDTO.setLastModifiedDate( competitorProfile.getLastModifiedDate() );
	        competitorProfileDTO.setChartColor( competitorProfile.getChartColor() );

	        return competitorProfileDTO;
	    }

	    @Override
	    public List<CompetitorProfileDTO> competitorProfilesToCompetitorProfileDTOs(List<CompetitorProfile> competitorProfiles) {
	        if ( competitorProfiles == null ) {
	            return null;
	        }

	        List<CompetitorProfileDTO> list = new ArrayList<CompetitorProfileDTO>();
	        for ( CompetitorProfile competitorProfile : competitorProfiles ) {
	            list.add( competitorProfileToCompetitorProfileDTO( competitorProfile ) );
	        }

	        return list;
	    }

	    @Override
	    public CompetitorProfile competitorProfileDTOToCompetitorProfile(CompetitorProfileDTO competitorProfileDTO) {
	        if ( competitorProfileDTO == null ) {
	            return null;
	        }

	        CompetitorProfile competitorProfile = new CompetitorProfile();

	        competitorProfile.setPid( competitorProfileDTO.getPid() );
	        competitorProfile.setName( competitorProfileDTO.getName() );
	        competitorProfile.setAlias( competitorProfileDTO.getAlias() );
	        competitorProfile.setDescription( competitorProfileDTO.getDescription() );
	        competitorProfile.setActivated( competitorProfileDTO.getActivated() );
	        competitorProfile.setChartColor( competitorProfileDTO.getChartColor() );

	        return competitorProfile;
	    }

	    @Override
	    public List<CompetitorProfile> competitorProfileDTOsToCompetitorProfiles(List<CompetitorProfileDTO> competitorProfileDTOs) {
	        if ( competitorProfileDTOs == null ) {
	            return null;
	        }

	        List<CompetitorProfile> list = new ArrayList<CompetitorProfile>();
	        for ( CompetitorProfileDTO competitorProfileDTO : competitorProfileDTOs ) {
	            list.add( competitorProfileDTOToCompetitorProfile( competitorProfileDTO ) );
	        }

	        return list;
	    }
	    private String competitorProfileName(CompetitorProfile competitorProfile) {
	        if( competitorProfile.getDescription()!=null && getCompanyCofig() && ! competitorProfile.getDescription().equals("common")) {
	        return  competitorProfile.getDescription();
	        }
	       
	    return  competitorProfile.getName();
	    }
}
