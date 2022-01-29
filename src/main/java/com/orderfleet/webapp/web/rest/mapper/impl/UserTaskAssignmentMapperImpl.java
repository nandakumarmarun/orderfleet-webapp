package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserTaskAssignment;
import com.orderfleet.webapp.web.rest.dto.UserTaskAssignmentDTO;
import com.orderfleet.webapp.web.rest.mapper.UserTaskAssignmentMapper;
@Component
public class UserTaskAssignmentMapperImpl extends UserTaskAssignmentMapper {



    @Override
    public UserTaskAssignmentDTO userTaskAssignmentToUserTaskAssignmentDTO(UserTaskAssignment userTaskAssignment) {
        if ( userTaskAssignment == null ) {
            return null;
        }

        UserTaskAssignmentDTO userTaskAssignmentDTO = new UserTaskAssignmentDTO();

        userTaskAssignmentDTO.setExecutiveUserPid( userTaskAssignmentExecutiveUserPid( userTaskAssignment ) );
        userTaskAssignmentDTO.setTaskActivityName( userTaskAssignmentTaskActivityName( userTaskAssignment ) );
        userTaskAssignmentDTO.setExecutiveUserName( userTaskAssignmentExecutiveUserFirstName( userTaskAssignment ) );
        userTaskAssignmentDTO.setTaskPid( userTaskAssignmentTaskPid( userTaskAssignment ) );
        userTaskAssignmentDTO.setUserPid( userTaskAssignmentUserPid( userTaskAssignment ) );
        userTaskAssignmentDTO.setTaskAccountName( userTaskAssignmentTaskAccountProfileName( userTaskAssignment ) );
        userTaskAssignmentDTO.setUserName( userTaskAssignmentUserFirstName( userTaskAssignment ) );
        userTaskAssignmentDTO.setPid( userTaskAssignment.getPid() );
        userTaskAssignmentDTO.setPriorityStatus( userTaskAssignment.getPriorityStatus() );
        userTaskAssignmentDTO.setStartDate( userTaskAssignment.getStartDate() );
        userTaskAssignmentDTO.setRemarks( userTaskAssignment.getRemarks() );
        userTaskAssignmentDTO.setTaskStatus( userTaskAssignment.getTaskStatus() );

        return userTaskAssignmentDTO;
    }

    @Override
    public List<UserTaskAssignmentDTO> userTaskAssignmentsToUserTaskAssignmentDTOs(List<UserTaskAssignment> userTaskAssignments) {
        if ( userTaskAssignments == null ) {
            return null;
        }

        List<UserTaskAssignmentDTO> list = new ArrayList<UserTaskAssignmentDTO>();
        for ( UserTaskAssignment userTaskAssignment : userTaskAssignments ) {
            list.add( userTaskAssignmentToUserTaskAssignmentDTO( userTaskAssignment ) );
        }

        return list;
    }

    @Override
    public UserTaskAssignment userTaskAssignmentDTOToUserTaskAssignment(UserTaskAssignmentDTO userTaskAssignmentDTO) {
        if ( userTaskAssignmentDTO == null ) {
            return null;
        }

        UserTaskAssignment userTaskAssignment = new UserTaskAssignment();

        userTaskAssignment.setExecutiveUser( userFromPid( userTaskAssignmentDTO.getExecutiveUserPid() ) );
        userTaskAssignment.setTask( taskFromPid( userTaskAssignmentDTO.getTaskPid() ) );
        userTaskAssignment.setPid( userTaskAssignmentDTO.getPid() );
        userTaskAssignment.setPriorityStatus( userTaskAssignmentDTO.getPriorityStatus() );
        userTaskAssignment.setStartDate( userTaskAssignmentDTO.getStartDate() );
        userTaskAssignment.setRemarks( userTaskAssignmentDTO.getRemarks() );
        userTaskAssignment.setTaskStatus( userTaskAssignmentDTO.getTaskStatus() );

        return userTaskAssignment;
    }

    @Override
    public List<UserTaskAssignment> userTaskAssignmentDTOsToUserTaskAssignments(List<UserTaskAssignmentDTO> userTaskAssignmentDTOs) {
        if ( userTaskAssignmentDTOs == null ) {
            return null;
        }

        List<UserTaskAssignment> list = new ArrayList<UserTaskAssignment>();
        for ( UserTaskAssignmentDTO userTaskAssignmentDTO : userTaskAssignmentDTOs ) {
            list.add( userTaskAssignmentDTOToUserTaskAssignment( userTaskAssignmentDTO ) );
        }

        return list;
    }

    private String userTaskAssignmentExecutiveUserPid(UserTaskAssignment userTaskAssignment) {

        if ( userTaskAssignment == null ) {
            return null;
        }
        User executiveUser = userTaskAssignment.getExecutiveUser();
        if ( executiveUser == null ) {
            return null;
        }
        String pid = executiveUser.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String userTaskAssignmentTaskActivityName(UserTaskAssignment userTaskAssignment) {

        if ( userTaskAssignment == null ) {
            return null;
        }
        Task task = userTaskAssignment.getTask();
        if ( task == null ) {
            return null;
        }
        Activity activity = task.getActivity();
        if ( activity == null ) {
            return null;
        }
        String name = activity.getName();
        if ( name == null ) {
            return null;
        }
        if(activity.getDescription()!=null && getCompanyCofig() && !activity.getDescription().equals("common")) {
	        return activity.getDescription();
	        }
        return name;
    }

    private String userTaskAssignmentExecutiveUserFirstName(UserTaskAssignment userTaskAssignment) {

        if ( userTaskAssignment == null ) {
            return null;
        }
        User executiveUser = userTaskAssignment.getExecutiveUser();
        if ( executiveUser == null ) {
            return null;
        }
        String firstName = executiveUser.getFirstName();
        if ( firstName == null ) {
            return null;
        }
        return firstName;
    }

    private String userTaskAssignmentTaskPid(UserTaskAssignment userTaskAssignment) {

        if ( userTaskAssignment == null ) {
            return null;
        }
        Task task = userTaskAssignment.getTask();
        if ( task == null ) {
            return null;
        }
        String pid = task.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String userTaskAssignmentUserPid(UserTaskAssignment userTaskAssignment) {

        if ( userTaskAssignment == null ) {
            return null;
        }
        User user = userTaskAssignment.getUser();
        if ( user == null ) {
            return null;
        }
        String pid = user.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String userTaskAssignmentTaskAccountProfileName(UserTaskAssignment userTaskAssignment) {

        if ( userTaskAssignment == null ) {
            return null;
        }
        Task task = userTaskAssignment.getTask();
        if ( task == null ) {
            return null;
        }
        AccountProfile accountProfile = task.getAccountProfile();
        if ( accountProfile == null ) {
            return null;
        }
        String name = accountProfile.getName();
        if ( name == null ) {
            return null;
        }
        if(accountProfile.getDescription()!=null && getCompanyCofig() && !accountProfile.getDescription().equals("common")) {
	        return accountProfile.getDescription();
	        }
        return name;
    }

    private String userTaskAssignmentUserFirstName(UserTaskAssignment userTaskAssignment) {

        if ( userTaskAssignment == null ) {
            return null;
        }
        User user = userTaskAssignment.getUser();
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
