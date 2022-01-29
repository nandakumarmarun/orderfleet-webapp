package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Department;
import com.orderfleet.webapp.domain.Designation;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.DepartmentRepository;
import com.orderfleet.webapp.repository.DesignationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;

/**
 * Mapper for the entity EmployeeProfile and its DTO EmployeeProfileDTO.
 * 
 * @author Shaheer
 * @since June 06, 2016
 */
@Component
public abstract class EmployeeProfileMapper {

	@Inject
	private DesignationRepository designationRepository;

	@Inject
	private DepartmentRepository departmentRepository;

	@Inject
	private UserRepository userRepository;
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;


//	@Mapping(source = "designation.pid", target = "designationPid")
//	@Mapping(source = "designation.name", target = "designationName")
//	@Mapping(source = "department.pid", target = "departmentPid")
//	@Mapping(source = "department.name", target = "departmentName")
//	@Mapping(source = "user.firstName", target = "userFirstName")
//	@Mapping(source = "user.lastName", target = "userLastName")
//	@Mapping(source = "user.pid", target = "userPid")
//	@Mapping(target = "encodedBase64Image", ignore = true)
	public abstract EmployeeProfileDTO employeeProfileToEmployeeProfileDTO(EmployeeProfile employeeProfile);

	public abstract List<EmployeeProfileDTO> employeeProfilesToEmployeeProfileDTOs(
			List<EmployeeProfile> employeeProfiles);

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "createdDate", ignore = true)
//	@Mapping(target = "lastUpdatedDate", ignore = true)
//	@Mapping(target = "user", ignore = true)
//	@Mapping(source = "designationPid", target = "designation")
//	@Mapping(source = "departmentPid", target = "department")
	public abstract EmployeeProfile employeeProfileDTOToEmployeeProfile(EmployeeProfileDTO employeeProfileDTO);

	public abstract List<EmployeeProfile> employeeProfileDTOsToEmployeeProfiles(
			List<EmployeeProfileDTO> employeeProfileDTOs);

	public Designation designationFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}

		return designationRepository.findOneByPid(pid).map(designation -> designation).orElse(null);
	}

	public Department departmentFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return departmentRepository.findOneByPid(pid).map(department -> department).orElse(null);
	}

	public User userFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return userRepository.findOneByPid(pid).map(department -> department).orElse(null);
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
