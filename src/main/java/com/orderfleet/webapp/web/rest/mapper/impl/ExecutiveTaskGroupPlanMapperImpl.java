package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.ExecutiveTaskGroupPlan;
import com.orderfleet.webapp.domain.TaskGroup;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskGroupPlanDTO;
import com.orderfleet.webapp.web.rest.mapper.ExecutiveTaskGroupPlanMapper;
@Component
public class ExecutiveTaskGroupPlanMapperImpl extends ExecutiveTaskGroupPlanMapper {


    @Override
    public ExecutiveTaskGroupPlanDTO executiveTaskGroupPlanToExecutiveTaskGroupPlanDTO(ExecutiveTaskGroupPlan executiveTaskGroupPlan) {
        if ( executiveTaskGroupPlan == null ) {
            return null;
        }

        ExecutiveTaskGroupPlanDTO executiveTaskGroupPlanDTO = new ExecutiveTaskGroupPlanDTO();

        executiveTaskGroupPlanDTO.setTaskGroupName( executiveTaskGroupPlanTaskGroupName( executiveTaskGroupPlan ) );
        executiveTaskGroupPlanDTO.setTaskGroupPid( executiveTaskGroupPlanTaskGroupPid( executiveTaskGroupPlan ) );
        executiveTaskGroupPlanDTO.setPid( executiveTaskGroupPlan.getPid() );
        executiveTaskGroupPlanDTO.setRemarks( executiveTaskGroupPlan.getRemarks() );
        executiveTaskGroupPlanDTO.setCreatedDate( executiveTaskGroupPlan.getCreatedDate() );
        executiveTaskGroupPlanDTO.setPlannedDate( executiveTaskGroupPlan.getPlannedDate() );

        return executiveTaskGroupPlanDTO;
    }
    public ExecutiveTaskGroupPlanDTO executiveTaskGroupPlanToExecutiveTaskGroupPlanDTODescription(ExecutiveTaskGroupPlan executiveTaskGroupPlan) {
        if ( executiveTaskGroupPlan == null ) {
            return null;
        }

        ExecutiveTaskGroupPlanDTO executiveTaskGroupPlanDTO = new ExecutiveTaskGroupPlanDTO();

        executiveTaskGroupPlanDTO.setTaskGroupName( executiveTaskGroupPlanTaskGroupDescription( executiveTaskGroupPlan ) );
        executiveTaskGroupPlanDTO.setTaskGroupPid( executiveTaskGroupPlanTaskGroupPid( executiveTaskGroupPlan ) );
        executiveTaskGroupPlanDTO.setPid( executiveTaskGroupPlan.getPid() );
        executiveTaskGroupPlanDTO.setRemarks( executiveTaskGroupPlan.getRemarks() );
        executiveTaskGroupPlanDTO.setCreatedDate( executiveTaskGroupPlan.getCreatedDate() );
        executiveTaskGroupPlanDTO.setPlannedDate( executiveTaskGroupPlan.getPlannedDate() );

        return executiveTaskGroupPlanDTO;
    }

    @Override
    public List<ExecutiveTaskGroupPlanDTO> executiveTaskGroupPlansToExecutiveTaskGroupPlanDTOs(List<ExecutiveTaskGroupPlan> executiveTaskGroupPlans) {
        if ( executiveTaskGroupPlans == null ) {
            return null;
        }

        List<ExecutiveTaskGroupPlanDTO> list = new ArrayList<ExecutiveTaskGroupPlanDTO>();
        if(getCompanyCofig()) {
        for ( ExecutiveTaskGroupPlan executiveTaskGroupPlan : executiveTaskGroupPlans ) {
            list.add( executiveTaskGroupPlanToExecutiveTaskGroupPlanDTODescription( executiveTaskGroupPlan ) );
        }
        }else
        {
        	 for ( ExecutiveTaskGroupPlan executiveTaskGroupPlan : executiveTaskGroupPlans ) {
                 list.add( executiveTaskGroupPlanToExecutiveTaskGroupPlanDTO( executiveTaskGroupPlan ) );
             }
        }
        return list;
    }

    @Override
    public ExecutiveTaskGroupPlan executiveTaskGroupPlanDTOToExecutiveTaskGroupPlan(ExecutiveTaskGroupPlanDTO executiveTaskGroupPlanDTO) {
        if ( executiveTaskGroupPlanDTO == null ) {
            return null;
        }

        ExecutiveTaskGroupPlan executiveTaskGroupPlan = new ExecutiveTaskGroupPlan();

        executiveTaskGroupPlan.setTaskGroup( taskGroupFromPid( executiveTaskGroupPlanDTO.getTaskGroupPid() ) );
        executiveTaskGroupPlan.setPid( executiveTaskGroupPlanDTO.getPid() );
        executiveTaskGroupPlan.setPlannedDate( executiveTaskGroupPlanDTO.getPlannedDate() );
        executiveTaskGroupPlan.setCreatedDate( executiveTaskGroupPlanDTO.getCreatedDate() );
        executiveTaskGroupPlan.setRemarks( executiveTaskGroupPlanDTO.getRemarks() );

        return executiveTaskGroupPlan;
    }

    @Override
    public List<ExecutiveTaskGroupPlan> executiveTaskGroupPlanDTOsToExecutiveTaskGroupPlans(List<ExecutiveTaskGroupPlanDTO> executiveTaskGroupPlanDTOs) {
        if ( executiveTaskGroupPlanDTOs == null ) {
            return null;
        }

        List<ExecutiveTaskGroupPlan> list = new ArrayList<ExecutiveTaskGroupPlan>();
        for ( ExecutiveTaskGroupPlanDTO executiveTaskGroupPlanDTO : executiveTaskGroupPlanDTOs ) {
            list.add( executiveTaskGroupPlanDTOToExecutiveTaskGroupPlan( executiveTaskGroupPlanDTO ) );
        }

        return list;
    }

    private String executiveTaskGroupPlanTaskGroupName(ExecutiveTaskGroupPlan executiveTaskGroupPlan) {

        if ( executiveTaskGroupPlan == null ) {
            return null;
        }
        TaskGroup taskGroup = executiveTaskGroupPlan.getTaskGroup();
        if ( taskGroup == null ) {
            return null;
        }
        String name = taskGroup.getName();
        if ( name == null ) {
            return null;
        }
//        if(taskGroup.getDescription()!=null && getCompanyCofig() && !taskGroup.getDescription().equals("common")) {
//	        return taskGroup.getDescription();
//	        }
        return name;
    }

    private String executiveTaskGroupPlanTaskGroupDescription(ExecutiveTaskGroupPlan executiveTaskGroupPlan) {

        if ( executiveTaskGroupPlan == null ) {
            return null;
        }
        TaskGroup taskGroup = executiveTaskGroupPlan.getTaskGroup();
        if ( taskGroup == null ) {
            return null;
        }
        String name = taskGroup.getName();
        if ( name == null ) {
            return null;
        }
        if(taskGroup.getDescription()!=null && !taskGroup.getDescription().equals("common")) {
	        return taskGroup.getDescription();
	        }
        return name;
    }
    private String executiveTaskGroupPlanTaskGroupPid(ExecutiveTaskGroupPlan executiveTaskGroupPlan) {

        if ( executiveTaskGroupPlan == null ) {
            return null;
        }
        TaskGroup taskGroup = executiveTaskGroupPlan.getTaskGroup();
        if ( taskGroup == null ) {
            return null;
        }
        String pid = taskGroup.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

}
