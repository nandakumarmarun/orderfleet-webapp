package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.ExecutiveTaskGroupPlan;
import com.orderfleet.webapp.domain.TaskGroup;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.TaskGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskGroupPlanDTO;

/**
 * Mapper for the entity ExecutiveTaskGroupPlan and its DTO
 * ExecutiveTaskGroupPlanDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
@Component
public abstract class ExecutiveTaskGroupPlanMapper {

	@Inject
	private TaskGroupRepository taskGroupRepository;
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

//	@Mapping(source = "taskGroup.pid", target = "taskGroupPid")
//	@Mapping(source = "taskGroup.name", target = "taskGroupName")
	public abstract ExecutiveTaskGroupPlanDTO executiveTaskGroupPlanToExecutiveTaskGroupPlanDTO(
			ExecutiveTaskGroupPlan executiveTaskGroupPlan);

	public abstract List<ExecutiveTaskGroupPlanDTO> executiveTaskGroupPlansToExecutiveTaskGroupPlanDTOs(
			List<ExecutiveTaskGroupPlan> executiveTaskGroupPlans);

//	@Mapping(source = "taskGroupPid", target = "taskGroup")
//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "user", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract ExecutiveTaskGroupPlan executiveTaskGroupPlanDTOToExecutiveTaskGroupPlan(
			ExecutiveTaskGroupPlanDTO executiveTaskGroupPlanDTO);

	public abstract List<ExecutiveTaskGroupPlan> executiveTaskGroupPlanDTOsToExecutiveTaskGroupPlans(
			List<ExecutiveTaskGroupPlanDTO> executiveTaskGroupPlanDTOs);

	public TaskGroup taskGroupFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return taskGroupRepository.findOneByPid(pid).map(taskGroup -> taskGroup).orElse(null);
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
