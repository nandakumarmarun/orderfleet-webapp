package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.TaskListDTO;

@Component
public abstract class TaskListMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	public abstract TaskListDTO taskListToTaskListDTO(TaskList taskList);

	public abstract List<TaskListDTO> taskListsToTaskListDTOs(List<TaskList> taskLists);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract TaskList taskListDTOToTaskList(TaskListDTO taskListDTO);

	public abstract List<TaskList> taskListDTOsToTaskLists(List<TaskListDTO> taskListDTOs);

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
