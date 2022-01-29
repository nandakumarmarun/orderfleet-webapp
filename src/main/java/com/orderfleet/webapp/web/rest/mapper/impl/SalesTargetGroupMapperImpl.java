package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.SalesTargetGroupMapper;
@Component
public class SalesTargetGroupMapperImpl extends SalesTargetGroupMapper {

  @Override
    public SalesTargetGroupDTO salesTargetGroupToSalesTargetGroupDTO(SalesTargetGroup salesTargetGroup) {
        if ( salesTargetGroup == null ) {
            return null;
        }

        SalesTargetGroupDTO salesTargetGroupDTO = new SalesTargetGroupDTO();

        salesTargetGroupDTO.setPid( salesTargetGroup.getPid() );
        salesTargetGroupDTO.setName( salesTargetGroupName(salesTargetGroup) );
        salesTargetGroupDTO.setAlias( salesTargetGroup.getAlias() );
        salesTargetGroupDTO.setDescription( salesTargetGroup.getDescription() );
        salesTargetGroupDTO.setTargetUnit( salesTargetGroup.getTargetUnit() );
        salesTargetGroupDTO.setTargetSettingType( salesTargetGroup.getTargetSettingType() );

        return salesTargetGroupDTO;
    }

    @Override
    public List<SalesTargetGroupDTO> salesTargetGroupsToSalesTargetGroupDTOs(List<SalesTargetGroup> salesTargetGroups) {
        if ( salesTargetGroups == null ) {
            return null;
        }

        List<SalesTargetGroupDTO> list = new ArrayList<SalesTargetGroupDTO>();
        for ( SalesTargetGroup salesTargetGroup : salesTargetGroups ) {
            list.add( salesTargetGroupToSalesTargetGroupDTO( salesTargetGroup ) );
        }

        return list;
    }

    @Override
    public SalesTargetGroup salesTargetGroupDTOToSalesTargetGroup(SalesTargetGroupDTO salesTargetGroupDTO) {
        if ( salesTargetGroupDTO == null ) {
            return null;
        }

        SalesTargetGroup salesTargetGroup = new SalesTargetGroup();

        salesTargetGroup.setPid( salesTargetGroupDTO.getPid() );
        salesTargetGroup.setName( salesTargetGroupDTO.getName() );
        salesTargetGroup.setAlias( salesTargetGroupDTO.getAlias() );
        salesTargetGroup.setDescription( salesTargetGroupDTO.getDescription() );
        salesTargetGroup.setTargetUnit( salesTargetGroupDTO.getTargetUnit() );
        salesTargetGroup.setTargetSettingType( salesTargetGroupDTO.getTargetSettingType() );

        return salesTargetGroup;
    }

    @Override
    public List<SalesTargetGroup> salesTargetGroupDTOsToSalesTargetGroups(List<SalesTargetGroupDTO> salesTargetGroupDTOs) {
        if ( salesTargetGroupDTOs == null ) {
            return null;
        }

        List<SalesTargetGroup> list = new ArrayList<SalesTargetGroup>();
        for ( SalesTargetGroupDTO salesTargetGroupDTO : salesTargetGroupDTOs ) {
            list.add( salesTargetGroupDTOToSalesTargetGroup( salesTargetGroupDTO ) );
        }

        return list;
    }
    private String salesTargetGroupName(SalesTargetGroup salesTargetGroup) {
        if(salesTargetGroup.getDescription()!=null && getCompanyCofig() && !salesTargetGroup.getDescription().equals("common")) {
        return salesTargetGroup.getDescription();
        }
       
    return salesTargetGroup.getName();
    }
}
