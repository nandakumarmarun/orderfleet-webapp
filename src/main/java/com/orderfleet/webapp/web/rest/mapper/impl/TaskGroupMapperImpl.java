package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.ActivityGroup;
import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.domain.TaskGroup;
import com.orderfleet.webapp.web.rest.dto.TaskDTO;
import com.orderfleet.webapp.web.rest.dto.TaskGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.TaskGroupMapper;
import com.orderfleet.webapp.web.rest.mapper.TaskMapper;
@Component
public class TaskGroupMapperImpl extends TaskGroupMapper{



    @Autowired
    private TaskMapper taskMapper;

    @Override
    public TaskGroupDTO taskGroupToTaskGroupDTO(TaskGroup taskGroup) {
        if ( taskGroup == null ) {
            return null;
        }

        TaskGroupDTO taskGroupDTO = new TaskGroupDTO();

        taskGroupDTO.setActivityGroupName( taskGroupActivityGroupName( taskGroup ) );
        taskGroupDTO.setActivityGroupPid( taskGroupActivityGroupPid( taskGroup ) );
        taskGroupDTO.setPid( taskGroup.getPid() );
        taskGroupDTO.setName( taskGroupName(taskGroup));
        taskGroupDTO.setAlias( taskGroup.getAlias() );
        taskGroupDTO.setDescription( taskGroup.getDescription() );
        Set<TaskDTO> set = taskSetToTaskDTOSet( taskGroup.getTasks() );
        if ( set != null ) {
            taskGroupDTO.setTasks( set );
        }

        return taskGroupDTO;
    }

    @Override
    public List<TaskGroupDTO> taskGroupsToTaskGroupDTOs(List<TaskGroup> taskGroups) {
        if ( taskGroups == null ) {
            return null;
        }

        List<TaskGroupDTO> list = new ArrayList<TaskGroupDTO>();
        for ( TaskGroup taskGroup : taskGroups ) {
            list.add( taskGroupToTaskGroupDTO( taskGroup ) );
        }

        return list;
    }

    @Override
    public TaskGroup taskGroupDTOToTaskGroup(TaskGroupDTO taskGroupDTO) {
        if ( taskGroupDTO == null ) {
            return null;
        }

        TaskGroup taskGroup = new TaskGroup();

        taskGroup.setActivityGroup( activityGroupFromPid( taskGroupDTO.getActivityGroupPid() ) );
        taskGroup.setPid( taskGroupDTO.getPid() );
        taskGroup.setName( taskGroupDTO.getName() );
        taskGroup.setAlias( taskGroupDTO.getAlias() );
        taskGroup.setDescription( taskGroupDTO.getDescription() );
        Set<Task> set = taskDTOSetToTaskSet( taskGroupDTO.getTasks() );
        if ( set != null ) {
            taskGroup.setTasks( set );
        }

        return taskGroup;
    }

    @Override
    public List<TaskGroup> taskGroupDTOsToTaskGroups(List<TaskGroupDTO> taskGroupDTOs) {
        if ( taskGroupDTOs == null ) {
            return null;
        }

        List<TaskGroup> list = new ArrayList<TaskGroup>();
        for ( TaskGroupDTO taskGroupDTO : taskGroupDTOs ) {
            list.add( taskGroupDTOToTaskGroup( taskGroupDTO ) );
        }

        return list;
    }

    private String taskGroupActivityGroupName(TaskGroup taskGroup) {

        if ( taskGroup == null ) {
            return null;
        }
        ActivityGroup activityGroup = taskGroup.getActivityGroup();
        if ( activityGroup == null ) {
            return null;
        }
        String name = activityGroup.getName();
        if ( name == null ) {
            return null;
        }
        if(activityGroup.getDescription()!=null && getCompanyCofig() && !activityGroup.getDescription().equals("common")) {
            return activityGroup.getDescription();
            }
        return name;
    }

    private String taskGroupActivityGroupPid(TaskGroup taskGroup) {

        if ( taskGroup == null ) {
            return null;
        }
        ActivityGroup activityGroup = taskGroup.getActivityGroup();
        if ( activityGroup == null ) {
            return null;
        }
        String pid = activityGroup.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    protected Set<TaskDTO> taskSetToTaskDTOSet(Set<Task> set) {
        if ( set == null ) {
            return null;
        }

        Set<TaskDTO> set_ = new HashSet<TaskDTO>();
        for ( Task task : set ) {
            set_.add( taskMapper.taskToTaskDTO( task ) );
        }

        return set_;
    }

    protected Set<Task> taskDTOSetToTaskSet(Set<TaskDTO> set) {
        if ( set == null ) {
            return null;
        }

        Set<Task> set_ = new HashSet<Task>();
        for ( TaskDTO taskDTO : set ) {
            set_.add( taskMapper.taskDTOToTask( taskDTO ) );
        }

        return set_;
    }
    private String  taskGroupName(TaskGroup taskGroup) {
        if(taskGroup.getDescription()!=null && getCompanyCofig() && !taskGroup.getDescription().equals("common")) {
        return taskGroup.getDescription();
        }
       
    return taskGroup.getName();
    }
}
