package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.TaskGroup;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserTaskGroupAssignment;
import com.orderfleet.webapp.web.rest.dto.UserTaskGroupAssignmentDTO;
import com.orderfleet.webapp.web.rest.mapper.UserTaskGroupAssignmentMapper;
@Component
public class UserTaskGroupAssignmentMapperImpl extends UserTaskGroupAssignmentMapper{

   @Override
    public UserTaskGroupAssignmentDTO userTaskGroupAssignmentToUserTaskGroupAssignmentDTO(UserTaskGroupAssignment userTaskGroupAssignment) {
        if ( userTaskGroupAssignment == null ) {
            return null;
        }

        UserTaskGroupAssignmentDTO userTaskGroupAssignmentDTO = new UserTaskGroupAssignmentDTO();

        userTaskGroupAssignmentDTO.setExecutiveUserPid( userTaskGroupAssignmentExecutiveUserPid( userTaskGroupAssignment ) );
        userTaskGroupAssignmentDTO.setTaskGroupName( userTaskGroupAssignmentTaskGroupName( userTaskGroupAssignment ) );
        userTaskGroupAssignmentDTO.setExecutiveUserName( userTaskGroupAssignmentExecutiveUserFirstName( userTaskGroupAssignment ) );
        userTaskGroupAssignmentDTO.setTaskGroupPid( userTaskGroupAssignmentTaskGroupPid( userTaskGroupAssignment ) );
        userTaskGroupAssignmentDTO.setUserPid( userTaskGroupAssignmentUserPid( userTaskGroupAssignment ) );
        userTaskGroupAssignmentDTO.setUserName( userTaskGroupAssignmentUserFirstName( userTaskGroupAssignment ) );
        userTaskGroupAssignmentDTO.setPid( userTaskGroupAssignment.getPid() );
        userTaskGroupAssignmentDTO.setPriorityStatus( userTaskGroupAssignment.getPriorityStatus() );
        userTaskGroupAssignmentDTO.setStartDate( userTaskGroupAssignment.getStartDate() );
        userTaskGroupAssignmentDTO.setRemarks( userTaskGroupAssignment.getRemarks() );

        return userTaskGroupAssignmentDTO;
    }

    @Override
    public List<UserTaskGroupAssignmentDTO> userTaskGroupAssignmentsToUserTaskGroupAssignmentDTOs(List<UserTaskGroupAssignment> userTaskGroupAssignments) {
        if ( userTaskGroupAssignments == null ) {
            return null;
        }

        List<UserTaskGroupAssignmentDTO> list = new ArrayList<UserTaskGroupAssignmentDTO>();
        for ( UserTaskGroupAssignment userTaskGroupAssignment : userTaskGroupAssignments ) {
            list.add( userTaskGroupAssignmentToUserTaskGroupAssignmentDTO( userTaskGroupAssignment ) );
        }

        return list;
    }

    @Override
    public UserTaskGroupAssignment userTaskGroupAssignmentDTOToUserTaskGroupAssignment(UserTaskGroupAssignmentDTO userTaskGroupAssignmentDTO) {
        if ( userTaskGroupAssignmentDTO == null ) {
            return null;
        }

        UserTaskGroupAssignment userTaskGroupAssignment = new UserTaskGroupAssignment();

        userTaskGroupAssignment.setExecutiveUser( userFromPid( userTaskGroupAssignmentDTO.getExecutiveUserPid() ) );
        userTaskGroupAssignment.setTaskGroup( taskGroupFromPid( userTaskGroupAssignmentDTO.getTaskGroupPid() ) );
        userTaskGroupAssignment.setPid( userTaskGroupAssignmentDTO.getPid() );
        userTaskGroupAssignment.setPriorityStatus( userTaskGroupAssignmentDTO.getPriorityStatus() );
        userTaskGroupAssignment.setStartDate( userTaskGroupAssignmentDTO.getStartDate() );
        userTaskGroupAssignment.setRemarks( userTaskGroupAssignmentDTO.getRemarks() );

        return userTaskGroupAssignment;
    }

    @Override
    public List<UserTaskGroupAssignment> userTaskGroupAssignmentDTOsToUserTaskGroupAssignments(List<UserTaskGroupAssignmentDTO> userTaskGroupAssignmentDTOs) {
        if ( userTaskGroupAssignmentDTOs == null ) {
            return null;
        }

        List<UserTaskGroupAssignment> list = new ArrayList<UserTaskGroupAssignment>();
        for ( UserTaskGroupAssignmentDTO userTaskGroupAssignmentDTO : userTaskGroupAssignmentDTOs ) {
            list.add( userTaskGroupAssignmentDTOToUserTaskGroupAssignment( userTaskGroupAssignmentDTO ) );
        }

        return list;
    }

    private String userTaskGroupAssignmentExecutiveUserPid(UserTaskGroupAssignment userTaskGroupAssignment) {

        if ( userTaskGroupAssignment == null ) {
            return null;
        }
        User executiveUser = userTaskGroupAssignment.getExecutiveUser();
        if ( executiveUser == null ) {
            return null;
        }
        String pid = executiveUser.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String userTaskGroupAssignmentTaskGroupName(UserTaskGroupAssignment userTaskGroupAssignment) {

        if ( userTaskGroupAssignment == null ) {
            return null;
        }
        TaskGroup taskGroup = userTaskGroupAssignment.getTaskGroup();
        if ( taskGroup == null ) {
            return null;
        }
        String name = taskGroup.getName();
        if ( name == null ) {
            return null;
        }
        if(taskGroup.getDescription()!=null && getCompanyCofig() && !taskGroup.getDescription().equals("common")) {
	        return taskGroup.getDescription();
	        }
        return name;
    }

    private String userTaskGroupAssignmentExecutiveUserFirstName(UserTaskGroupAssignment userTaskGroupAssignment) {

        if ( userTaskGroupAssignment == null ) {
            return null;
        }
        User executiveUser = userTaskGroupAssignment.getExecutiveUser();
        if ( executiveUser == null ) {
            return null;
        }
        String firstName = executiveUser.getFirstName();
        if ( firstName == null ) {
            return null;
        }
        return firstName;
    }

    private String userTaskGroupAssignmentTaskGroupPid(UserTaskGroupAssignment userTaskGroupAssignment) {

        if ( userTaskGroupAssignment == null ) {
            return null;
        }
        TaskGroup taskGroup = userTaskGroupAssignment.getTaskGroup();
        if ( taskGroup == null ) {
            return null;
        }
        String pid = taskGroup.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String userTaskGroupAssignmentUserPid(UserTaskGroupAssignment userTaskGroupAssignment) {

        if ( userTaskGroupAssignment == null ) {
            return null;
        }
        User user = userTaskGroupAssignment.getUser();
        if ( user == null ) {
            return null;
        }
        String pid = user.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String userTaskGroupAssignmentUserFirstName(UserTaskGroupAssignment userTaskGroupAssignment) {

        if ( userTaskGroupAssignment == null ) {
            return null;
        }
        User user = userTaskGroupAssignment.getUser();
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
