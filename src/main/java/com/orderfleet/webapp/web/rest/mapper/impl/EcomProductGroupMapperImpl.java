package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.EcomProductGroup;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.EcomProductGroupMapper;
@Component
public class EcomProductGroupMapperImpl extends EcomProductGroupMapper {

	@Override
    public EcomProductGroupDTO productGroupToProductGroupDTO(EcomProductGroup ecomProductGroup) {
        if ( ecomProductGroup == null ) {
            return null;
        }

        EcomProductGroupDTO ecomProductGroupDTO = new EcomProductGroupDTO();

        ecomProductGroupDTO.setPid( ecomProductGroup.getPid() );
        ecomProductGroupDTO.setName( ecomProductGroup.getName() );
        ecomProductGroupDTO.setAlias( ecomProductGroup.getAlias() );
        ecomProductGroupDTO.setDescription( ecomProductGroup.getDescription() );
        ecomProductGroupDTO.setImageContentType( ecomProductGroup.getImageContentType() );
        ecomProductGroupDTO.setLastModifiedDate( ecomProductGroup.getLastModifiedDate() );
        ecomProductGroupDTO.setActivated( ecomProductGroup.getActivated() );
        ecomProductGroupDTO.setThirdpartyUpdate( ecomProductGroup.getThirdpartyUpdate() );

        return ecomProductGroupDTO;
    }

	  public EcomProductGroupDTO productGroupToProductGroupDTODescription(EcomProductGroup ecomProductGroup) {
	        if ( ecomProductGroup == null ) {
	            return null;
	        }

	        EcomProductGroupDTO ecomProductGroupDTO = new EcomProductGroupDTO();

	        ecomProductGroupDTO.setPid( ecomProductGroup.getPid() );
	        ecomProductGroupDTO.setName( ecomProductGroup.getDescription() != null && !ecomProductGroup.getDescription().equalsIgnoreCase("common")
					? ecomProductGroup.getDescription()
					: ecomProductGroup.getName()); 
	        ecomProductGroupDTO.setAlias( ecomProductGroup.getAlias() );
	        ecomProductGroupDTO.setDescription( ecomProductGroup.getDescription() );
	        ecomProductGroupDTO.setImageContentType( ecomProductGroup.getImageContentType() );
	        ecomProductGroupDTO.setLastModifiedDate( ecomProductGroup.getLastModifiedDate() );
	        ecomProductGroupDTO.setActivated( ecomProductGroup.getActivated() );
	        ecomProductGroupDTO.setThirdpartyUpdate( ecomProductGroup.getThirdpartyUpdate() );

	        return ecomProductGroupDTO;
	    }

    @Override
    public List<EcomProductGroupDTO> productGroupsToProductGroupDTOs(List<EcomProductGroup> ecomProductGroups) {
        if ( ecomProductGroups == null ) {
            return null;
        }

        List<EcomProductGroupDTO> list = new ArrayList<EcomProductGroupDTO>();
        if(getCompanyCofig()) {
        for ( EcomProductGroup ecomProductGroup : ecomProductGroups ) {
            list.add( productGroupToProductGroupDTODescription( ecomProductGroup ) );
        }
        }else
        { for ( EcomProductGroup ecomProductGroup : ecomProductGroups ) {
            list.add( productGroupToProductGroupDTO( ecomProductGroup ) );
        }
        	
        }

        return list;
    }

    @Override
    public EcomProductGroup productGroupDTOToProductGroup(EcomProductGroupDTO ecomProductGroupDTO) {
        if ( ecomProductGroupDTO == null ) {
            return null;
        }

        EcomProductGroup ecomProductGroup = new EcomProductGroup();

        ecomProductGroup.setPid( ecomProductGroupDTO.getPid() );
        ecomProductGroup.setName( ecomProductGroupDTO.getName() );
        ecomProductGroup.setAlias( ecomProductGroupDTO.getAlias() );
        ecomProductGroup.setDescription( ecomProductGroupDTO.getDescription() );
        if ( ecomProductGroupDTO.getImage() != null ) {
            byte[] image = ecomProductGroupDTO.getImage();
            ecomProductGroup.setImage( Arrays.copyOf( image, image.length ) );
        }
        ecomProductGroup.setImageContentType( ecomProductGroupDTO.getImageContentType() );
        ecomProductGroup.setActivated( ecomProductGroupDTO.getActivated() );
        ecomProductGroup.setThirdpartyUpdate( ecomProductGroupDTO.getThirdpartyUpdate() );

        return ecomProductGroup;
    }

    @Override
    public List<EcomProductGroup> productGroupDTOsToProductGroups(List<EcomProductGroupDTO> ecomProductGroupDTOs) {
        if ( ecomProductGroupDTOs == null ) {
            return null;
        }

        List<EcomProductGroup> list = new ArrayList<EcomProductGroup>();
        for ( EcomProductGroupDTO ecomProductGroupDTO : ecomProductGroupDTOs ) {
            list.add( productGroupDTOToProductGroup( ecomProductGroupDTO ) );
        }

        return list;
    }
    private String ecomProductGroupName(EcomProductGroup ecomProductGroup) {
        if(ecomProductGroup.getDescription()!=null && getCompanyCofig() && !ecomProductGroup.getDescription().equals("common")) {
        return ecomProductGroup.getDescription();
        }
       
    return ecomProductGroup.getName();
    }
}
