package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.web.rest.dto.DivisionDTO;
import com.orderfleet.webapp.web.rest.mapper.DivisionMapper;
@Component
public class DivisionMapperImpl extends DivisionMapper{

	 @Override
	    public DivisionDTO divisionToDivisionDTO(Division division) {
	        if ( division == null ) {
	            return null;
	        }

	        DivisionDTO divisionDTO = new DivisionDTO();

	        divisionDTO.setActivated( division.getActivated() );
	        divisionDTO.setPid( division.getPid() );
	        divisionDTO.setName(division.getName());
	        divisionDTO.setAlias( division.getAlias() );
	        divisionDTO.setDescription( division.getDescription() );

	        return divisionDTO;
	    }
	 public DivisionDTO divisionToDivisionDTODescription(Division division) {
	        if ( division == null ) {
	            return null;
	        }

	        DivisionDTO divisionDTO = new DivisionDTO();

	        divisionDTO.setActivated( division.getActivated() );
	        divisionDTO.setPid( division.getPid() );
	        divisionDTO.setName(division.getDescription() != null && !division.getDescription().equalsIgnoreCase("common")
					? division.getDescription()
					: division.getName());
	        divisionDTO.setAlias( division.getAlias() );
	        divisionDTO.setDescription( division.getDescription() );

	        return divisionDTO;
	    }

	    @Override
	    public List<DivisionDTO> divisionsToDivisionDTOs(List<Division> divisions) {
	        if ( divisions == null ) {
	            return null;
	        }

	        List<DivisionDTO> list = new ArrayList<DivisionDTO>();
	        if(getCompanyCofig())
	        {
	        for ( Division division : divisions ) {
	            list.add( divisionToDivisionDTODescription( division ) );
	        }
	        }
	        else
	        {
	        	 for ( Division division : divisions ) {
	 	            list.add( divisionToDivisionDTO( division ) );
	 	        }
	        }
	        return list;
	    }

	    @Override
	    public Division divisionDTOToDivision(DivisionDTO divisionDTO) {
	        if ( divisionDTO == null ) {
	            return null;
	        }

	        Division division = new Division();

	        division.setActivated( divisionDTO.getActivated() );
	        division.setPid( divisionDTO.getPid() );
	        division.setName( divisionDTO.getName() );
	        division.setAlias( divisionDTO.getAlias() );
	        division.setDescription( divisionDTO.getDescription() );

	        return division;
	    }

	    @Override
	    public List<Division> divisionDTOsToDivisions(List<DivisionDTO> divisionDTOs) {
	        if ( divisionDTOs == null ) {
	            return null;
	        }

	        List<Division> list = new ArrayList<Division>();
	        for ( DivisionDTO divisionDTO : divisionDTOs ) {
	            list.add( divisionDTOToDivision( divisionDTO ) );
	        }

	        return list;
	    }
	    private String divisionName(Division division) {
	        if(division.getDescription()!=null && getCompanyCofig() && !division.getDescription().equals("common")) {
	        return division.getDescription();
	        }
	       
	    return division.getName();
	    }
}
