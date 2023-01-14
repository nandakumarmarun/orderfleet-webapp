package com.orderfleet.webapp.web.rest.mapper;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.EmployeeHierarchy;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.EmployeeHierarchyDTO;

import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

/**
 * Mapper for the entity EmployeeHierarchy and its DTO EmployeeHierarchyDTO.
 * 
 * @author Shaheer
 * @since June 02, 2016
 */
@Component
public abstract class EmployeeHierarchyMapper {
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;


//    @Mapping(source = "employee.id", target = "employeeId")
//    @Mapping(source = "employee.pid", target = "employeePid")
//    @Mapping(source = "employee.name", target = "employeeName")
//    @Mapping(source = "employee.designation.name", target = "designationName")
//    @Mapping(source = "parent.id", target = "parentId")
//    @Mapping(source = "parent.pid", target = "parentPid")
//    @Mapping(source = "parent.name", target = "parentName")
	public abstract  EmployeeHierarchyDTO employeeHierarchyToEmployeeHierarchyDTO(EmployeeHierarchy employeeHierarchy);

	public abstract  List<EmployeeHierarchyDTO> employeeHierarchiesToEmployeeHierarchyDTOs(List<EmployeeHierarchy> employeeHierarchies);
	
	public abstract  List<EmployeeHierarchyDTO> employeeHierarchiesToEmployeeHierarchyDTOsCustumName(List<EmployeeHierarchy> employeeHierarchies);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "company", ignore = true)
//    @Mapping(target = "activated", ignore = true)
//    @Mapping(target = "activatedDate", ignore = true)
//    @Mapping(target = "inactivatedDate", ignore = true)
//    @Mapping(source = "employeeId", target = "employee")
//    @Mapping(source = "parentId", target = "parent")
	public abstract  EmployeeHierarchy employeeHierarchyDTOToEmployeeHierarchy(EmployeeHierarchyDTO employeeHierarchyDTO);

	public abstract  List<EmployeeHierarchy> employeeHierarchyDTOsToEmployeeHierarchies(List<EmployeeHierarchyDTO> employeeHierarchyDTOs);

    public EmployeeProfile employeeProfileFromId(Long id) {
        if (id == null) {
            return null;
        }
        EmployeeProfile employee = new EmployeeProfile();
        employee.setId(id);
        return employee;
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
