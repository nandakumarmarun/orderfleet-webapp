package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserTaskListAssignment;
import com.orderfleet.webapp.web.rest.dto.UserTaskListAssignmentDTO;
import com.orderfleet.webapp.web.rest.mapper.UserTaskListAssignmentMapper;
@Component
public class UserTaskListAssignmentMapperImpl extends UserTaskListAssignmentMapper{

   @Override
    public UserTaskListAssignmentDTO userTaskListAssignmentToUserTaskListAssignmentDTO(UserTaskListAssignment userTaskListAssignment) {
        if ( userTaskListAssignment == null ) {
            return null;
        }

        UserTaskListAssignmentDTO userTaskListAssignmentDTO = new UserTaskListAssignmentDTO();

        userTaskListAssignmentDTO.setExecutiveUserPid( userTaskListAssignmentExecutiveUserPid( userTaskListAssignment ) );
        userTaskListAssignmentDTO.setTaskListName( userTaskListAssignmentTaskListName( userTaskListAssignment ) );
        userTaskListAssignmentDTO.setExecutiveUserName( userTaskListAssignmentExecutiveUserFirstName( userTaskListAssignment ) );
        userTaskListAssignmentDTO.setTaskListPid( userTaskListAssignmentTaskListPid( userTaskListAssignment ) );
        userTaskListAssignmentDTO.setUserPid( userTaskListAssignmentUserPid( userTaskListAssignment ) );
        userTaskListAssignmentDTO.setUserName( userTaskListAssignmentUserFirstName( userTaskListAssignment ) );
        userTaskListAssignmentDTO.setPid( userTaskListAssignment.getPid() );
        userTaskListAssignmentDTO.setPriorityStatus( userTaskListAssignment.getPriorityStatus() );
        userTaskListAssignmentDTO.setStartDate( userTaskListAssignment.getStartDate() );
        userTaskListAssignmentDTO.setRemarks( userTaskListAssignment.getRemarks() );

        return userTaskListAssignmentDTO;
    }

   public UserTaskListAssignmentDTO userTaskListAssignmentToUserTaskListAssignmentDTODescription(UserTaskListAssignment userTaskListAssignment) {
       if ( userTaskListAssignment == null ) {
           return null;
       }

       UserTaskListAssignmentDTO userTaskListAssignmentDTO = new UserTaskListAssignmentDTO();

       userTaskListAssignmentDTO.setExecutiveUserPid( userTaskListAssignmentExecutiveUserPid( userTaskListAssignment ) );
       userTaskListAssignmentDTO.setTaskListName( userTaskListAssignmentTaskListDescription( userTaskListAssignment ) );
       userTaskListAssignmentDTO.setExecutiveUserName( userTaskListAssignmentExecutiveUserFirstName( userTaskListAssignment ) );
       userTaskListAssignmentDTO.setTaskListPid( userTaskListAssignmentTaskListPid( userTaskListAssignment ) );
       userTaskListAssignmentDTO.setUserPid( userTaskListAssignmentUserPid( userTaskListAssignment ) );
       userTaskListAssignmentDTO.setUserName( userTaskListAssignmentUserFirstName( userTaskListAssignment ) );
       userTaskListAssignmentDTO.setPid( userTaskListAssignment.getPid() );
       userTaskListAssignmentDTO.setPriorityStatus( userTaskListAssignment.getPriorityStatus() );
       userTaskListAssignmentDTO.setStartDate( userTaskListAssignment.getStartDate() );
       userTaskListAssignmentDTO.setRemarks( userTaskListAssignment.getRemarks() );

       return userTaskListAssignmentDTO;
   }

    @Override
    public List<UserTaskListAssignmentDTO> userTaskListAssignmentsToUserTaskListAssignmentDTOs(List<UserTaskListAssignment> userTaskListAssignments) {
        if ( userTaskListAssignments == null ) {
            return null;
        }

        List<UserTaskListAssignmentDTO> list = new ArrayList<UserTaskListAssignmentDTO>();
        if(getCompanyCofig())
        {
        for ( UserTaskListAssignment userTaskListAssignment : userTaskListAssignments ) {
            list.add( userTaskListAssignmentToUserTaskListAssignmentDTODescription( userTaskListAssignment ) );
        }}
        {
        	for ( UserTaskListAssignment userTaskListAssignment : userTaskListAssignments ) {
                list.add( userTaskListAssignmentToUserTaskListAssignmentDTO( userTaskListAssignment ) );
            }
        }

        return list;
    }

    @Override
    public UserTaskListAssignment userTaskListAssignmentDTOToUserTaskListAssignment(UserTaskListAssignmentDTO userTaskListAssignmentDTO) {
        if ( userTaskListAssignmentDTO == null ) {
            return null;
        }

        UserTaskListAssignment userTaskListAssignment = new UserTaskListAssignment();

        userTaskListAssignment.setTaskList( taskListFromPid( userTaskListAssignmentDTO.getTaskListPid() ) );
        userTaskListAssignment.setExecutiveUser( userFromPid( userTaskListAssignmentDTO.getExecutiveUserPid() ) );
        userTaskListAssignment.setPid( userTaskListAssignmentDTO.getPid() );
        userTaskListAssignment.setPriorityStatus( userTaskListAssignmentDTO.getPriorityStatus() );
        userTaskListAssignment.setStartDate( userTaskListAssignmentDTO.getStartDate() );
        userTaskListAssignment.setRemarks( userTaskListAssignmentDTO.getRemarks() );

        return userTaskListAssignment;
    }

    @Override
    public List<UserTaskListAssignment> userTaskListAssignmentDTOsToUserTaskListAssignments(List<UserTaskListAssignmentDTO> userTaskListAssignmentDTOs) {
        if ( userTaskListAssignmentDTOs == null ) {
            return null;
        }

        List<UserTaskListAssignment> list = new ArrayList<UserTaskListAssignment>();
        for ( UserTaskListAssignmentDTO userTaskListAssignmentDTO : userTaskListAssignmentDTOs ) {
            list.add( userTaskListAssignmentDTOToUserTaskListAssignment( userTaskListAssignmentDTO ) );
        }

        return list;
    }

    private String userTaskListAssignmentExecutiveUserPid(UserTaskListAssignment userTaskListAssignment) {

        if ( userTaskListAssignment == null ) {
            return null;
        }
        User executiveUser = userTaskListAssignment.getExecutiveUser();
        if ( executiveUser == null ) {
            return null;
        }
        String pid = executiveUser.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String userTaskListAssignmentTaskListName(UserTaskListAssignment userTaskListAssignment) {

        if ( userTaskListAssignment == null ) {
            return null;
        }
        TaskList taskList = userTaskListAssignment.getTaskList();
        if ( taskList == null ) {
            return null;
        }
        String name = taskList.getName();
        if ( name == null ) {
            return null;
        }
//        if(taskList.getDescription()!=null && getCompanyCofig() && !taskList.getDescription().equals("common")) {
//	        return taskList.getDescription();
//	        }
        return name;
    }
    private String userTaskListAssignmentTaskListDescription(UserTaskListAssignment userTaskListAssignment) {

        if ( userTaskListAssignment == null ) {
            return null;
        }
        TaskList taskList = userTaskListAssignment.getTaskList();
        if ( taskList == null ) {
            return null;
        }
        String name = taskList.getName();
        if ( name == null ) {
            return null;
        }
        if(taskList.getDescription()!=null && !taskList.getDescription().equals("common")) {
	        return taskList.getDescription();
	        }
        return name;
    }

    private String userTaskListAssignmentExecutiveUserFirstName(UserTaskListAssignment userTaskListAssignment) {

        if ( userTaskListAssignment == null ) {
            return null;
        }
        User executiveUser = userTaskListAssignment.getExecutiveUser();
        if ( executiveUser == null ) {
            return null;
        }
        String firstName = executiveUser.getFirstName();
        if ( firstName == null ) {
            return null;
        }
        return firstName;
    }

    private String userTaskListAssignmentTaskListPid(UserTaskListAssignment userTaskListAssignment) {

        if ( userTaskListAssignment == null ) {
            return null;
        }
        TaskList taskList = userTaskListAssignment.getTaskList();
        if ( taskList == null ) {
            return null;
        }
        String pid = taskList.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String userTaskListAssignmentUserPid(UserTaskListAssignment userTaskListAssignment) {

        if ( userTaskListAssignment == null ) {
            return null;
        }
        User user = userTaskListAssignment.getUser();
        if ( user == null ) {
            return null;
        }
        String pid = user.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String userTaskListAssignmentUserFirstName(UserTaskListAssignment userTaskListAssignment) {

        if ( userTaskListAssignment == null ) {
            return null;
        }
        User user = userTaskListAssignment.getUser();
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
