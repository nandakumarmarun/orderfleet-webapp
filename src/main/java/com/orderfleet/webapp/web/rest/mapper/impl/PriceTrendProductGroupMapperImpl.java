package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.PriceTrendProduct;
import com.orderfleet.webapp.domain.PriceTrendProductGroup;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductDTO;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.PriceTrendProductGroupMapper;
import com.orderfleet.webapp.web.rest.mapper.PriceTrendProductMapper;
@Component
public class PriceTrendProductGroupMapperImpl extends PriceTrendProductGroupMapper{

 @Autowired
    private PriceTrendProductMapper priceTrendProductMapper;

    @Override
    public PriceTrendProductGroupDTO priceTrendProductGroupToPriceTrendProductGroupDTO(PriceTrendProductGroup priceTrendProductGroup) {
        if ( priceTrendProductGroup == null ) {
            return null;
        }

        PriceTrendProductGroupDTO priceTrendProductGroupDTO = new PriceTrendProductGroupDTO();

        priceTrendProductGroupDTO.setActivated( priceTrendProductGroup.getActivated() );
        priceTrendProductGroupDTO.setPid( priceTrendProductGroup.getPid() );
        priceTrendProductGroupDTO.setName( priceTrendProductGroupName(priceTrendProductGroup));
        priceTrendProductGroupDTO.setAlias( priceTrendProductGroup.getAlias() );
        priceTrendProductGroupDTO.setDescription( priceTrendProductGroup.getDescription() );
        List<PriceTrendProductDTO> list = priceTrendProductMapper.priceTrendProductsToPriceTrendProductDTOs( priceTrendProductGroup.getPriceTrendProducts() );
        if ( list != null ) {
            priceTrendProductGroupDTO.setPriceTrendProducts( list );
        }
        priceTrendProductGroupDTO.setLastModifiedDate( priceTrendProductGroup.getLastModifiedDate() );

        return priceTrendProductGroupDTO;
    }

    @Override
    public List<PriceTrendProductGroupDTO> priceTrendProductGroupsToPriceTrendProductGroupDTOs(List<PriceTrendProductGroup> priceTrendProductGroups) {
        if ( priceTrendProductGroups == null ) {
            return null;
        }

        List<PriceTrendProductGroupDTO> list = new ArrayList<PriceTrendProductGroupDTO>();
        for ( PriceTrendProductGroup priceTrendProductGroup : priceTrendProductGroups ) {
            list.add( priceTrendProductGroupToPriceTrendProductGroupDTO( priceTrendProductGroup ) );
        }

        return list;
    }

    @Override
    public PriceTrendProductGroup priceTrendProductGroupDTOToPriceTrendProductGroup(PriceTrendProductGroupDTO priceTrendProductGroupDTO) {
        if ( priceTrendProductGroupDTO == null ) {
            return null;
        }

        PriceTrendProductGroup priceTrendProductGroup = new PriceTrendProductGroup();

        priceTrendProductGroup.setActivated( priceTrendProductGroupDTO.getActivated() );
        priceTrendProductGroup.setPid( priceTrendProductGroupDTO.getPid() );
        priceTrendProductGroup.setName( priceTrendProductGroupDTO.getName() );
        priceTrendProductGroup.setAlias( priceTrendProductGroupDTO.getAlias() );
        priceTrendProductGroup.setDescription( priceTrendProductGroupDTO.getDescription() );
        List<PriceTrendProduct> list = priceTrendProductMapper.priceTrendProductDTOsToPriceTrendProducts( priceTrendProductGroupDTO.getPriceTrendProducts() );
        if ( list != null ) {
            priceTrendProductGroup.setPriceTrendProducts( list );
        }

        return priceTrendProductGroup;
    }

    @Override
    public List<PriceTrendProductGroup> priceTrendProductGroupDTOsToPriceTrendProductGroups(List<PriceTrendProductGroupDTO> priceTrendProductGroupDTOs) {
        if ( priceTrendProductGroupDTOs == null ) {
            return null;
        }

        List<PriceTrendProductGroup> list = new ArrayList<PriceTrendProductGroup>();
        for ( PriceTrendProductGroupDTO priceTrendProductGroupDTO : priceTrendProductGroupDTOs ) {
            list.add( priceTrendProductGroupDTOToPriceTrendProductGroup( priceTrendProductGroupDTO ) );
        }

        return list;
    }
    private String  priceTrendProductGroupName(PriceTrendProductGroup priceTrendProductGroup) {
        if(priceTrendProductGroup.getDescription()!=null && getCompanyCofig() && !priceTrendProductGroup.getDescription().equals("common")) {
        return priceTrendProductGroup.getDescription();
        }
       
    return priceTrendProductGroup.getName();
    }
}
