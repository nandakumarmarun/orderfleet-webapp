package com.orderfleet.webapp.web.rest.mapper;

import com.orderfleet.webapp.domain.EmployeeHierarchy;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.web.rest.dto.EmployeeHierarchyDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity EmployeeHierarchy and its DTO EmployeeHierarchyDTO.
 * 
 * @author Shaheer
 * @since June 02, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface EmployeeHierarchyMapper {

    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "employee.pid", target = "employeePid")
    @Mapping(source = "employee.name", target = "employeeName")
    @Mapping(source = "employee.designation.name", target = "designationName")
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "parent.pid", target = "parentPid")
    @Mapping(source = "parent.name", target = "parentName")
    EmployeeHierarchyDTO employeeHierarchyToEmployeeHierarchyDTO(EmployeeHierarchy employeeHierarchy);

    List<EmployeeHierarchyDTO> employeeHierarchiesToEmployeeHierarchyDTOs(List<EmployeeHierarchy> employeeHierarchies);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "activated", ignore = true)
    @Mapping(target = "activatedDate", ignore = true)
    @Mapping(target = "inactivatedDate", ignore = true)
    @Mapping(source = "employeeId", target = "employee")
    @Mapping(source = "parentId", target = "parent")
    EmployeeHierarchy employeeHierarchyDTOToEmployeeHierarchy(EmployeeHierarchyDTO employeeHierarchyDTO);

    List<EmployeeHierarchy> employeeHierarchyDTOsToEmployeeHierarchies(List<EmployeeHierarchyDTO> employeeHierarchyDTOs);

    default EmployeeProfile employeeProfileFromId(Long id) {
        if (id == null) {
            return null;
        }
        EmployeeProfile employee = new EmployeeProfile();
        employee.setId(id);
        return employee;
    }
}
