package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserReceiptTarget;
import com.orderfleet.webapp.web.rest.dto.UserReceiptTargetDTO;
import com.orderfleet.webapp.web.rest.mapper.UserReceiptTargetMapper;
@Component
public class UserReceiptTargetMapperImpl extends UserReceiptTargetMapper {
	


    @Override
    public UserReceiptTargetDTO userReceiptTargetToUserReceiptTargetDTO(UserReceiptTarget userReceiptTarget) {
        if ( userReceiptTarget == null ) {
            return null;
        }

        UserReceiptTargetDTO userReceiptTargetDTO = new UserReceiptTargetDTO();

        userReceiptTargetDTO.setUserPid( userReceiptTargetUserPid( userReceiptTarget ) );
        userReceiptTargetDTO.setUserName( userReceiptTargetUserFirstName( userReceiptTarget ) );
        userReceiptTargetDTO.setPid( userReceiptTarget.getPid() );
        userReceiptTargetDTO.setStartDate( userReceiptTarget.getStartDate() );
        userReceiptTargetDTO.setEndDate( userReceiptTarget.getEndDate() );
        userReceiptTargetDTO.setTargetAmount( userReceiptTarget.getTargetAmount() );
        userReceiptTargetDTO.setTargetPercentage( userReceiptTarget.getTargetPercentage() );
        userReceiptTargetDTO.setLastModifiedDate( userReceiptTarget.getLastModifiedDate() );

        return userReceiptTargetDTO;
    }

    @Override
    public List<UserReceiptTargetDTO> userReceiptTargetsToUserReceiptTargetDTOs(List<UserReceiptTarget> userReceiptTargets) {
        if ( userReceiptTargets == null ) {
            return null;
        }

        List<UserReceiptTargetDTO> list = new ArrayList<UserReceiptTargetDTO>();
        for ( UserReceiptTarget userReceiptTarget : userReceiptTargets ) {
            list.add( userReceiptTargetToUserReceiptTargetDTO( userReceiptTarget ) );
        }

        return list;
    }

    @Override
    public UserReceiptTarget userReceiptTargetDTOToUserReceiptTarget(UserReceiptTargetDTO userReceiptTargetDTO) {
        if ( userReceiptTargetDTO == null ) {
            return null;
        }

        UserReceiptTarget userReceiptTarget = new UserReceiptTarget();

        userReceiptTarget.setUser( userFromPid( userReceiptTargetDTO.getUserPid() ) );
        userReceiptTarget.setPid( userReceiptTargetDTO.getPid() );
        userReceiptTarget.setStartDate( userReceiptTargetDTO.getStartDate() );
        userReceiptTarget.setEndDate( userReceiptTargetDTO.getEndDate() );
        userReceiptTarget.setTargetAmount( userReceiptTargetDTO.getTargetAmount() );
        userReceiptTarget.setTargetPercentage( userReceiptTargetDTO.getTargetPercentage() );

        return userReceiptTarget;
    }

    @Override
    public List<UserReceiptTarget> userReceiptTargetDTOsToUserReceiptTargets(List<UserReceiptTargetDTO> userReceiptTargetDTOs) {
        if ( userReceiptTargetDTOs == null ) {
            return null;
        }

        List<UserReceiptTarget> list = new ArrayList<UserReceiptTarget>();
        for ( UserReceiptTargetDTO userReceiptTargetDTO : userReceiptTargetDTOs ) {
            list.add( userReceiptTargetDTOToUserReceiptTarget( userReceiptTargetDTO ) );
        }

        return list;
    }

    private String userReceiptTargetUserPid(UserReceiptTarget userReceiptTarget) {

        if ( userReceiptTarget == null ) {
            return null;
        }
        User user = userReceiptTarget.getUser();
        if ( user == null ) {
            return null;
        }
        String pid = user.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String userReceiptTargetUserFirstName(UserReceiptTarget userReceiptTarget) {

        if ( userReceiptTarget == null ) {
            return null;
        }
        User user = userReceiptTarget.getUser();
        if ( user == null ) {
            return null;
        }
        String firstName = user.getFirstName();
        if ( firstName == null ) {
            return null;
        }
        return firstName;
    }

}
