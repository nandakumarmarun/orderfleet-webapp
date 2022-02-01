package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.ExecutiveTaskListPlan;
import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskListPlanDTO;
import com.orderfleet.webapp.web.rest.mapper.ExecutiveTaskListPlanMapper;

@Component
public class ExecutiveTaskListPlanMapperImpl extends ExecutiveTaskListPlanMapper {



    @Override
    public ExecutiveTaskListPlanDTO executiveTaskListPlanToExecutiveTaskListPlanDTO(ExecutiveTaskListPlan executiveTaskListPlan) {
        if ( executiveTaskListPlan == null ) {
            return null;
        }

        ExecutiveTaskListPlanDTO executiveTaskListPlanDTO = new ExecutiveTaskListPlanDTO();

        executiveTaskListPlanDTO.setTaskListName( executiveTaskListPlanTaskListName( executiveTaskListPlan ) );
        executiveTaskListPlanDTO.setTaskListPid( executiveTaskListPlanTaskListPid( executiveTaskListPlan ) );
        executiveTaskListPlanDTO.setPid( executiveTaskListPlan.getPid() );
        executiveTaskListPlanDTO.setCreatedDate( executiveTaskListPlan.getCreatedDate() );
        executiveTaskListPlanDTO.setRemarks( executiveTaskListPlan.getRemarks() );
        executiveTaskListPlanDTO.setPlannedDate( executiveTaskListPlan.getPlannedDate() );

        return executiveTaskListPlanDTO;
    }
    public ExecutiveTaskListPlanDTO executiveTaskListPlanToExecutiveTaskListPlanDTODescription(ExecutiveTaskListPlan executiveTaskListPlan) {
        if ( executiveTaskListPlan == null ) {
            return null;
        }

        ExecutiveTaskListPlanDTO executiveTaskListPlanDTO = new ExecutiveTaskListPlanDTO();

        executiveTaskListPlanDTO.setTaskListName( executiveTaskListPlanTaskListDescription( executiveTaskListPlan ) );
        executiveTaskListPlanDTO.setTaskListPid( executiveTaskListPlanTaskListPid( executiveTaskListPlan ) );
        executiveTaskListPlanDTO.setPid( executiveTaskListPlan.getPid() );
        executiveTaskListPlanDTO.setCreatedDate( executiveTaskListPlan.getCreatedDate() );
        executiveTaskListPlanDTO.setRemarks( executiveTaskListPlan.getRemarks() );
        executiveTaskListPlanDTO.setPlannedDate( executiveTaskListPlan.getPlannedDate() );

        return executiveTaskListPlanDTO;
    }
    @Override
    public List<ExecutiveTaskListPlanDTO> executiveTaskListPlansToExecutiveTaskListPlanDTOs(List<ExecutiveTaskListPlan> executiveTaskListPlans) {
        if ( executiveTaskListPlans == null ) {
            return null;
        }

        List<ExecutiveTaskListPlanDTO> list = new ArrayList<ExecutiveTaskListPlanDTO>();
        if(getCompanyCofig())
        {
        for ( ExecutiveTaskListPlan executiveTaskListPlan : executiveTaskListPlans ) {
            list.add( executiveTaskListPlanToExecutiveTaskListPlanDTODescription( executiveTaskListPlan ) );
        }}
        else
        {
        	for ( ExecutiveTaskListPlan executiveTaskListPlan : executiveTaskListPlans ) {
                list.add( executiveTaskListPlanToExecutiveTaskListPlanDTO( executiveTaskListPlan ) );
            }
        }

        return list;
    }

    @Override
    public ExecutiveTaskListPlan executiveTaskListPlanDTOToExecutiveTaskListPlan(ExecutiveTaskListPlanDTO executiveTaskListPlanDTO) {
        if ( executiveTaskListPlanDTO == null ) {
            return null;
        }

        ExecutiveTaskListPlan executiveTaskListPlan = new ExecutiveTaskListPlan();

        executiveTaskListPlan.setTaskList( taskListFromPid( executiveTaskListPlanDTO.getTaskListPid() ) );
        executiveTaskListPlan.setPid( executiveTaskListPlanDTO.getPid() );
        executiveTaskListPlan.setPlannedDate( executiveTaskListPlanDTO.getPlannedDate() );
        executiveTaskListPlan.setCreatedDate( executiveTaskListPlanDTO.getCreatedDate() );
        executiveTaskListPlan.setRemarks( executiveTaskListPlanDTO.getRemarks() );

        return executiveTaskListPlan;
    }

    @Override
    public List<ExecutiveTaskListPlan> executiveTaskListPlanDTOsToExecutiveTaskListPlans(List<ExecutiveTaskListPlanDTO> executiveTaskListPlanDTOs) {
        if ( executiveTaskListPlanDTOs == null ) {
            return null;
        }

        List<ExecutiveTaskListPlan> list = new ArrayList<ExecutiveTaskListPlan>();
        for ( ExecutiveTaskListPlanDTO executiveTaskListPlanDTO : executiveTaskListPlanDTOs ) {
            list.add( executiveTaskListPlanDTOToExecutiveTaskListPlan( executiveTaskListPlanDTO ) );
        }

        return list;
    }

    private String executiveTaskListPlanTaskListName(ExecutiveTaskListPlan executiveTaskListPlan) {

        if ( executiveTaskListPlan == null ) {
            return null;
        }
        TaskList taskList = executiveTaskListPlan.getTaskList();
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
    private String executiveTaskListPlanTaskListDescription(ExecutiveTaskListPlan executiveTaskListPlan) {

        if ( executiveTaskListPlan == null ) {
            return null;
        }
        TaskList taskList = executiveTaskListPlan.getTaskList();
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
    private String executiveTaskListPlanTaskListPid(ExecutiveTaskListPlan executiveTaskListPlan) {

        if ( executiveTaskListPlan == null ) {
            return null;
        }
        TaskList taskList = executiveTaskListPlan.getTaskList();
        if ( taskList == null ) {
            return null;
        }
        String pid = taskList.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

}
