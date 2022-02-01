package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupUserTargetDTO;
import com.orderfleet.webapp.web.rest.mapper.SalesTargetGroupUserTargetMapper;
@Component
public class SalesTargetGroupUserTargetMapperImpl extends SalesTargetGroupUserTargetMapper {

   @Override
    public SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTO(SalesTargetGroupUserTarget salesTargetGroupUserTarget) {
        if ( salesTargetGroupUserTarget == null ) {
            return null;
        }

        SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO = new SalesTargetGroupUserTargetDTO();

        salesTargetGroupUserTargetDTO.setAccountProfilePid( salesTargetGroupUserTargetAccountProfilePid( salesTargetGroupUserTarget ) );
        salesTargetGroupUserTargetDTO.setTargetUnit( salesTargetGroupUserTargetSalesTargetGroupTargetUnit( salesTargetGroupUserTarget ) );
        salesTargetGroupUserTargetDTO.setUserPid( salesTargetGroupUserTargetUserPid( salesTargetGroupUserTarget ) );
        salesTargetGroupUserTargetDTO.setUserName( salesTargetGroupUserTargetUserFirstName( salesTargetGroupUserTarget ) );
        salesTargetGroupUserTargetDTO.setAccountProfileName( salesTargetGroupUserTargetAccountProfileName( salesTargetGroupUserTarget ) );
        salesTargetGroupUserTargetDTO.setSalesTargetGroupName( salesTargetGroupUserTargetSalesTargetGroupName( salesTargetGroupUserTarget ) );
        salesTargetGroupUserTargetDTO.setSalesTargetGroupPid( salesTargetGroupUserTargetSalesTargetGroupPid( salesTargetGroupUserTarget ) );
        salesTargetGroupUserTargetDTO.setPid( salesTargetGroupUserTarget.getPid() );
        salesTargetGroupUserTargetDTO.setFromDate( salesTargetGroupUserTarget.getFromDate() );
        salesTargetGroupUserTargetDTO.setToDate( salesTargetGroupUserTarget.getToDate() );
        salesTargetGroupUserTargetDTO.setVolume( salesTargetGroupUserTarget.getVolume() );
        salesTargetGroupUserTargetDTO.setAmount( salesTargetGroupUserTarget.getAmount() );
        salesTargetGroupUserTargetDTO.setAccountWiseTarget( salesTargetGroupUserTarget.getAccountWiseTarget() );
        salesTargetGroupUserTargetDTO.setLastModifiedDate( salesTargetGroupUserTarget.getLastModifiedDate() );

        return salesTargetGroupUserTargetDTO;
    }

   public SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTODescription(SalesTargetGroupUserTarget salesTargetGroupUserTarget) {
       if ( salesTargetGroupUserTarget == null ) {
           return null;
       }

       SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO = new SalesTargetGroupUserTargetDTO();

       salesTargetGroupUserTargetDTO.setAccountProfilePid( salesTargetGroupUserTargetAccountProfilePid( salesTargetGroupUserTarget ) );
       salesTargetGroupUserTargetDTO.setTargetUnit( salesTargetGroupUserTargetSalesTargetGroupTargetUnit( salesTargetGroupUserTarget ) );
       salesTargetGroupUserTargetDTO.setUserPid( salesTargetGroupUserTargetUserPid( salesTargetGroupUserTarget ) );
       salesTargetGroupUserTargetDTO.setUserName( salesTargetGroupUserTargetUserFirstName( salesTargetGroupUserTarget ) );
       salesTargetGroupUserTargetDTO.setAccountProfileName( salesTargetGroupUserTargetAccountProfileDescription( salesTargetGroupUserTarget ) );
       salesTargetGroupUserTargetDTO.setSalesTargetGroupName( salesTargetGroupUserTargetSalesTargetGroupDescription( salesTargetGroupUserTarget ) );
       salesTargetGroupUserTargetDTO.setSalesTargetGroupPid( salesTargetGroupUserTargetSalesTargetGroupPid( salesTargetGroupUserTarget ) );
       salesTargetGroupUserTargetDTO.setPid( salesTargetGroupUserTarget.getPid() );
       salesTargetGroupUserTargetDTO.setFromDate( salesTargetGroupUserTarget.getFromDate() );
       salesTargetGroupUserTargetDTO.setToDate( salesTargetGroupUserTarget.getToDate() );
       salesTargetGroupUserTargetDTO.setVolume( salesTargetGroupUserTarget.getVolume() );
       salesTargetGroupUserTargetDTO.setAmount( salesTargetGroupUserTarget.getAmount() );
       salesTargetGroupUserTargetDTO.setAccountWiseTarget( salesTargetGroupUserTarget.getAccountWiseTarget() );
       salesTargetGroupUserTargetDTO.setLastModifiedDate( salesTargetGroupUserTarget.getLastModifiedDate() );

       return salesTargetGroupUserTargetDTO;
   }

    @Override
    public List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetsToSalesTargetGroupUserTargetDTOs(List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets) {
        if ( salesTargetGroupUserTargets == null ) {
            return null;
        }

        List<SalesTargetGroupUserTargetDTO> list = new ArrayList<SalesTargetGroupUserTargetDTO>();
       if(getCompanyCofig())
       {
        for ( SalesTargetGroupUserTarget salesTargetGroupUserTarget : salesTargetGroupUserTargets ) {
            list.add( salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTODescription( salesTargetGroupUserTarget ) );
        }}
       else
       {
    	   for ( SalesTargetGroupUserTarget salesTargetGroupUserTarget : salesTargetGroupUserTargets ) {
               list.add( salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTO( salesTargetGroupUserTarget ) );
           }
       }

        return list;
    }

    @Override
    public SalesTargetGroupUserTarget salesTargetGroupUserTargetDTOToSalesTargetGroupUserTarget(SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO) {
        if ( salesTargetGroupUserTargetDTO == null ) {
            return null;
        }

        SalesTargetGroupUserTarget salesTargetGroupUserTarget = new SalesTargetGroupUserTarget();

        salesTargetGroupUserTarget.setSalesTargetGroup( salesTargetGroupFromPid( salesTargetGroupUserTargetDTO.getSalesTargetGroupPid() ) );
        salesTargetGroupUserTarget.setUser( userFromPid( salesTargetGroupUserTargetDTO.getUserPid() ) );
        salesTargetGroupUserTarget.setPid( salesTargetGroupUserTargetDTO.getPid() );
        salesTargetGroupUserTarget.setFromDate( salesTargetGroupUserTargetDTO.getFromDate() );
        salesTargetGroupUserTarget.setToDate( salesTargetGroupUserTargetDTO.getToDate() );
        salesTargetGroupUserTarget.setVolume( salesTargetGroupUserTargetDTO.getVolume() );
        salesTargetGroupUserTarget.setAmount( salesTargetGroupUserTargetDTO.getAmount() );
        salesTargetGroupUserTarget.setAccountWiseTarget( salesTargetGroupUserTargetDTO.getAccountWiseTarget() );

        return salesTargetGroupUserTarget;
    }

    @Override
    public List<SalesTargetGroupUserTarget> salesTargetGroupUserTargetDTOsToSalesTargetGroupUserTargets(List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs) {
        if ( salesTargetGroupUserTargetDTOs == null ) {
            return null;
        }

        List<SalesTargetGroupUserTarget> list = new ArrayList<SalesTargetGroupUserTarget>();
        for ( SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO : salesTargetGroupUserTargetDTOs ) {
            list.add( salesTargetGroupUserTargetDTOToSalesTargetGroupUserTarget( salesTargetGroupUserTargetDTO ) );
        }

        return list;
    }

    private String salesTargetGroupUserTargetAccountProfilePid(SalesTargetGroupUserTarget salesTargetGroupUserTarget) {

        if ( salesTargetGroupUserTarget == null ) {
            return null;
        }
        AccountProfile accountProfile = salesTargetGroupUserTarget.getAccountProfile();
        if ( accountProfile == null ) {
            return null;
        }
        String pid = accountProfile.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String salesTargetGroupUserTargetSalesTargetGroupTargetUnit(SalesTargetGroupUserTarget salesTargetGroupUserTarget) {

        if ( salesTargetGroupUserTarget == null ) {
            return null;
        }
        SalesTargetGroup salesTargetGroup = salesTargetGroupUserTarget.getSalesTargetGroup();
        if ( salesTargetGroup == null ) {
            return null;
        }
        String targetUnit = salesTargetGroup.getTargetUnit();
        if ( targetUnit == null ) {
            return null;
        }
        return targetUnit;
    }

    private String salesTargetGroupUserTargetUserPid(SalesTargetGroupUserTarget salesTargetGroupUserTarget) {

        if ( salesTargetGroupUserTarget == null ) {
            return null;
        }
        User user = salesTargetGroupUserTarget.getUser();
        if ( user == null ) {
            return null;
        }
        String pid = user.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String salesTargetGroupUserTargetUserFirstName(SalesTargetGroupUserTarget salesTargetGroupUserTarget) {

        if ( salesTargetGroupUserTarget == null ) {
            return null;
        }
        User user = salesTargetGroupUserTarget.getUser();
        if ( user == null ) {
            return null;
        }
        String firstName = user.getFirstName();
        if ( firstName == null ) {
            return null;
        }
        return firstName;
    }

    private String salesTargetGroupUserTargetAccountProfileName(SalesTargetGroupUserTarget salesTargetGroupUserTarget) {

        if ( salesTargetGroupUserTarget == null ) {
            return null;
        }
        AccountProfile accountProfile = salesTargetGroupUserTarget.getAccountProfile();
        if ( accountProfile == null ) {
            return null;
        }
        String name = accountProfile.getName();
        if ( name == null ) {
            return null;
        }
//        if(accountProfile.getDescription()!=null && getCompanyCofig() && !accountProfile.getDescription().equals("common")) {
//	        return accountProfile.getDescription();
//	        }
        return name;
    }
    private String salesTargetGroupUserTargetAccountProfileDescription(SalesTargetGroupUserTarget salesTargetGroupUserTarget) {

        if ( salesTargetGroupUserTarget == null ) {
            return null;
        }
        AccountProfile accountProfile = salesTargetGroupUserTarget.getAccountProfile();
        if ( accountProfile == null ) {
            return null;
        }
        String name = accountProfile.getName();
        if ( name == null ) {
            return null;
        }
        if(accountProfile.getDescription()!=null && !accountProfile.getDescription().equals("common")) {
	        return accountProfile.getDescription();
	        }
        return name;
    }

    private String salesTargetGroupUserTargetSalesTargetGroupName(SalesTargetGroupUserTarget salesTargetGroupUserTarget) {

        if ( salesTargetGroupUserTarget == null ) {
            return null;
        }
        SalesTargetGroup salesTargetGroup = salesTargetGroupUserTarget.getSalesTargetGroup();
        if ( salesTargetGroup == null ) {
            return null;
        }
        String name = salesTargetGroup.getName();
        if ( name == null ) {
            return null;
        }
//        if(salesTargetGroup.getDescription()!=null && getCompanyCofig() && !salesTargetGroup.getDescription().equals("common")) {
//	        return salesTargetGroup.getDescription();
//	        }
        return name;
    }
    private String salesTargetGroupUserTargetSalesTargetGroupDescription(SalesTargetGroupUserTarget salesTargetGroupUserTarget) {

        if ( salesTargetGroupUserTarget == null ) {
            return null;
        }
        SalesTargetGroup salesTargetGroup = salesTargetGroupUserTarget.getSalesTargetGroup();
        if ( salesTargetGroup == null ) {
            return null;
        }
        String name = salesTargetGroup.getName();
        if ( name == null ) {
            return null;
        }
        if(salesTargetGroup.getDescription()!=null  && !salesTargetGroup.getDescription().equals("common")) {
	        return salesTargetGroup.getDescription();
	        }
        return name;
    }

    private String salesTargetGroupUserTargetSalesTargetGroupPid(SalesTargetGroupUserTarget salesTargetGroupUserTarget) {

        if ( salesTargetGroupUserTarget == null ) {
            return null;
        }
        SalesTargetGroup salesTargetGroup = salesTargetGroupUserTarget.getSalesTargetGroup();
        if ( salesTargetGroup == null ) {
            return null;
        }
        String pid = salesTargetGroup.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

}
