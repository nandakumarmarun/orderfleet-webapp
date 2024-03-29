package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.ExecutiveTaskListPlan;
import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.TaskListRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskListPlanDTO;

/**
 * Mapper for the entity ExecutiveTaskListPlan and its DTO
 * ExecutiveTaskListPlanDTO.
 * 
 * @author Sarath
 * @since July 23, 2016
 */
@Component
public abstract class ExecutiveTaskListPlanMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	@Inject
	private TaskListRepository taskListRepository;

//	@Mapping(source = "taskList.pid", target = "taskListPid")
//	@Mapping(source = "taskList.name", target = "taskListName")
	public abstract ExecutiveTaskListPlanDTO executiveTaskListPlanToExecutiveTaskListPlanDTO(
			ExecutiveTaskListPlan executiveTaskListPlan);

	public abstract List<ExecutiveTaskListPlanDTO> executiveTaskListPlansToExecutiveTaskListPlanDTOs(
			List<ExecutiveTaskListPlan> executiveTaskListPlans);

//	@Mapping(source = "taskListPid", target = "taskList")
//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "user", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract ExecutiveTaskListPlan executiveTaskListPlanDTOToExecutiveTaskListPlan(
			ExecutiveTaskListPlanDTO executiveTaskListPlanDTO);

	public abstract List<ExecutiveTaskListPlan> executiveTaskListPlanDTOsToExecutiveTaskListPlans(
			List<ExecutiveTaskListPlanDTO> executiveTaskListPlanDTOs);

	public TaskList taskListFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return taskListRepository.findOneByPid(pid).map(taskList -> taskList).orElse(null);
	}
	public boolean getCompanyCofig(){
		Optional<CompanyConfiguration> optconfig = companyConfigurationRepository.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if(optconfig.isPresent()) {
		if(Boolean.valueOf(optconfig.get().getValue())) {
		return true;
		}
		}
		return false;
		}
}
