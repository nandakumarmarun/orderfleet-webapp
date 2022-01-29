package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Department;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.DepartmentDTO;

/**
 * Mapper for the entity Department and its DTO DepartmentDTO.
 * 
 * @author Muhammed Riyas T
 * @since May 24, 2016
 */
@Component
public abstract class DepartmentMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	public abstract DepartmentDTO departmentToDepartmentDTO(Department department);

	public abstract List<DepartmentDTO> departmentsToDepartmentDTOs(List<Department> departments);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract Department departmentDTOToDepartment(DepartmentDTO departmentDTO);

	public abstract List<Department> departmentDTOsToDepartments(List<DepartmentDTO> departmentDTOs);

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
