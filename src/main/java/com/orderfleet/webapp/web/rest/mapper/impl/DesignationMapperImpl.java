package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import com.orderfleet.webapp.domain.Designation;
import com.orderfleet.webapp.web.rest.dto.DesignationDTO;
import com.orderfleet.webapp.web.rest.mapper.DesignationMapper;
@Component
public class DesignationMapperImpl extends DesignationMapper{

	 @Override
	    public DesignationDTO designationToDesignationDTO(Designation designation) {
	        if ( designation == null ) {
	            return null;
	        }

	        DesignationDTO designationDTO = new DesignationDTO();

	        designationDTO.setActivated( designation.getActivated() );
	        designationDTO.setPid( designation.getPid() );
	        designationDTO.setName( designationName(designation));
	        designationDTO.setAlias( designation.getAlias() );
	        designationDTO.setDescription( designation.getDescription() );

	        return designationDTO;
	    }

	    @Override
	    public List<DesignationDTO> designationsToDesignationDTOs(List<Designation> designations) {
	        if ( designations == null ) {
	            return null;
	        }

	        List<DesignationDTO> list = new ArrayList<DesignationDTO>();
	        for ( Designation designation : designations ) {
	            list.add( designationToDesignationDTO( designation ) );
	        }

	        return list;
	    }

	    @Override
	    public Designation designationDTOToDesignation(DesignationDTO designationDTO) {
	        if ( designationDTO == null ) {
	            return null;
	        }

	        Designation designation = new Designation();

	        designation.setActivated( designationDTO.getActivated() );
	        designation.setPid( designationDTO.getPid() );
	        designation.setName( designationDTO.getName() );
	        designation.setAlias( designationDTO.getAlias() );
	        designation.setDescription( designationDTO.getDescription() );

	        return designation;
	    }

	    @Override
	    public List<Designation> designationDTOsToDesignations(List<DesignationDTO> designationDTOs) {
	        if ( designationDTOs == null ) {
	            return null;
	        }

	        List<Designation> list = new ArrayList<Designation>();
	        for ( DesignationDTO designationDTO : designationDTOs ) {
	            list.add( designationDTOToDesignation( designationDTO ) );
	        }

	        return list;
	    }
	    private String designationName(Designation designation) {
	        if(designation.getDescription()!=null && getCompanyCofig() && !designation.getDescription().equals("common")) {
	        return designation.getDescription();
	        }
	       
	    return designation.getName();
	    }
}
