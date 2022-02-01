package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.Department;
import com.orderfleet.webapp.web.rest.dto.DepartmentDTO;
import com.orderfleet.webapp.web.rest.mapper.DepartmentMapper;
@Component
public class DepartmentMapperImpl extends DepartmentMapper {

	 @Override
	    public DepartmentDTO departmentToDepartmentDTO(Department department) {
	        if ( department == null ) {
	            return null;
	        }

	        DepartmentDTO departmentDTO = new DepartmentDTO();

	        departmentDTO.setActivated( department.getActivated() );
	        departmentDTO.setPid( department.getPid() );
	        departmentDTO.setName( department.getName());
	        departmentDTO.setAlias( department.getAlias() );
	        departmentDTO.setDescription( department.getDescription() );

	        return departmentDTO;
	    }
	 public DepartmentDTO departmentToDepartmentDTODescription(Department department) {
	        if ( department == null ) {
	            return null;
	        }

	        DepartmentDTO departmentDTO = new DepartmentDTO();

	        departmentDTO.setActivated( department.getActivated() );
	        departmentDTO.setPid( department.getPid() );
	        departmentDTO.setName( department.getDescription() != null && !department.getDescription().equalsIgnoreCase("common")
					? department.getDescription()
					: department.getName());
	        departmentDTO.setAlias( department.getAlias() );
	        departmentDTO.setDescription( department.getDescription() );

	        return departmentDTO;
	    }
	    @Override
	    public List<DepartmentDTO> departmentsToDepartmentDTOs(List<Department> departments) {
	        if ( departments == null ) {
	            return null;
	        }

	        List<DepartmentDTO> list = new ArrayList<DepartmentDTO>();
	        if(getCompanyCofig())
	        {
	        for ( Department department : departments ) {
	            list.add( departmentToDepartmentDTODescription( department ) );
	        }
	        }
	        else
	        {for ( Department department : departments ) {
	            list.add( departmentToDepartmentDTO( department ) );
	        }
	        	
	        }
	        return list;
	    }

	    @Override
	    public Department departmentDTOToDepartment(DepartmentDTO departmentDTO) {
	        if ( departmentDTO == null ) {
	            return null;
	        }

	        Department department = new Department();

	        department.setActivated( departmentDTO.getActivated() );
	        department.setPid( departmentDTO.getPid() );
	        department.setName( departmentDTO.getName() );
	        department.setAlias( departmentDTO.getAlias() );
	        department.setDescription( departmentDTO.getDescription() );

	        return department;
	    }

	    @Override
	    public List<Department> departmentDTOsToDepartments(List<DepartmentDTO> departmentDTOs) {
	        if ( departmentDTOs == null ) {
	            return null;
	        }

	        List<Department> list = new ArrayList<Department>();
	        for ( DepartmentDTO departmentDTO : departmentDTOs ) {
	            list.add( departmentDTOToDepartment( departmentDTO ) );
	        }

	        return list;
	    }
	    private String departmentName(Department department) {
	        if(department.getDescription()!=null && getCompanyCofig() && !department.getDescription().equals("common")) {
	        return department.getDescription();
	        }
	       
	    return department.getName();
	    }
}
