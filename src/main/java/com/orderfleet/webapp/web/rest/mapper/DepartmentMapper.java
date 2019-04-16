package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.Department;
import com.orderfleet.webapp.web.rest.dto.DepartmentDTO;

/**
 * Mapper for the entity Department and its DTO DepartmentDTO.
 * 
 * @author Muhammed Riyas T
 * @since May 24, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface DepartmentMapper {

	DepartmentDTO departmentToDepartmentDTO(Department department);

	List<DepartmentDTO> departmentsToDepartmentDTOs(List<Department> departments);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	Department departmentDTOToDepartment(DepartmentDTO departmentDTO);

	List<Department> departmentDTOsToDepartments(List<DepartmentDTO> departmentDTOs);

}
