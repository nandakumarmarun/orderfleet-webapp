package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.Designation;
import com.orderfleet.webapp.domain.EmployeeHierarchy;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.web.rest.dto.EmployeeHierarchyDTO;
import com.orderfleet.webapp.web.rest.mapper.EmployeeHierarchyMapper;
@Component
public class EmployeeHierarchyMapperImpl extends EmployeeHierarchyMapper {

	 @Override
	    public EmployeeHierarchyDTO employeeHierarchyToEmployeeHierarchyDTO(EmployeeHierarchy employeeHierarchy) {
	        if ( employeeHierarchy == null ) {
	            return null;
	        }

	        EmployeeHierarchyDTO employeeHierarchyDTO = new EmployeeHierarchyDTO();

	        employeeHierarchyDTO.setDesignationName( employeeHierarchyEmployeeDesignationName( employeeHierarchy ) );
	        employeeHierarchyDTO.setEmployeeName( employeeHierarchyEmployeeName( employeeHierarchy ) );
	        employeeHierarchyDTO.setParentName( employeeHierarchyParentName( employeeHierarchy ) );
	        employeeHierarchyDTO.setEmployeePid( employeeHierarchyEmployeePid( employeeHierarchy ) );
	        employeeHierarchyDTO.setParentPid( employeeHierarchyParentPid( employeeHierarchy ) );
	        employeeHierarchyDTO.setEmployeeId( employeeHierarchyEmployeeId( employeeHierarchy ) );
	        employeeHierarchyDTO.setParentId( employeeHierarchyParentId( employeeHierarchy ) );

	        return employeeHierarchyDTO;
	    }
	 
	 
	    public EmployeeHierarchyDTO employeeHierarchyToEmployeeHierarchyDTOCustomName(EmployeeHierarchy employeeHierarchy) {
	        if ( employeeHierarchy == null ) {
	            return null;
	        }

	        EmployeeHierarchyDTO employeeHierarchyDTO = new EmployeeHierarchyDTO();

	        employeeHierarchyDTO.setDesignationName( employeeHierarchyEmployeeDesignationName( employeeHierarchy ));
	        employeeHierarchyDTO.setEmployeeName( employeeHierarchyEmployeCustomeName( employeeHierarchy ) );
	        employeeHierarchyDTO.setParentName( employeeHierarchyParentCustomeName( employeeHierarchy ) );
	        employeeHierarchyDTO.setEmployeePid( employeeHierarchyEmployeePid( employeeHierarchy ) );
	        employeeHierarchyDTO.setParentPid( employeeHierarchyParentPid( employeeHierarchy ) );
	        employeeHierarchyDTO.setEmployeeId( employeeHierarchyEmployeeId( employeeHierarchy ) );
	        employeeHierarchyDTO.setParentId( employeeHierarchyParentId( employeeHierarchy ) );

	        return employeeHierarchyDTO;
	    }

	    @Override
	    public List<EmployeeHierarchyDTO> employeeHierarchiesToEmployeeHierarchyDTOs(List<EmployeeHierarchy> employeeHierarchies) {
	        if ( employeeHierarchies == null ) {
	            return null;
	        }

	        List<EmployeeHierarchyDTO> list = new ArrayList<EmployeeHierarchyDTO>();
	        for ( EmployeeHierarchy employeeHierarchy : employeeHierarchies ) {
	            list.add( employeeHierarchyToEmployeeHierarchyDTO( employeeHierarchy ) );
	        }

	        return list;
	    }

	    @Override
	    public EmployeeHierarchy employeeHierarchyDTOToEmployeeHierarchy(EmployeeHierarchyDTO employeeHierarchyDTO) {
	        if ( employeeHierarchyDTO == null ) {
	            return null;
	        }

	        EmployeeHierarchy employeeHierarchy = new EmployeeHierarchy();

	        employeeHierarchy.setParent( employeeProfileFromId( employeeHierarchyDTO.getParentId() ) );
	        employeeHierarchy.setEmployee( employeeProfileFromId( employeeHierarchyDTO.getEmployeeId() ) );

	        return employeeHierarchy;
	    }

	    @Override
	    public List<EmployeeHierarchy> employeeHierarchyDTOsToEmployeeHierarchies(List<EmployeeHierarchyDTO> employeeHierarchyDTOs) {
	        if ( employeeHierarchyDTOs == null ) {
	            return null;
	        }

	        List<EmployeeHierarchy> list = new ArrayList<EmployeeHierarchy>();
	        for ( EmployeeHierarchyDTO employeeHierarchyDTO : employeeHierarchyDTOs ) {
	            list.add( employeeHierarchyDTOToEmployeeHierarchy( employeeHierarchyDTO ) );
	        }

	        return list;
	    }

	    private String employeeHierarchyEmployeeDesignationName(EmployeeHierarchy employeeHierarchy) {

	        if ( employeeHierarchy == null ) {
	            return null;
	        }
	        EmployeeProfile employee = employeeHierarchy.getEmployee();
	        if ( employee == null ) {
	            return null;
	        }
	        Designation designation = employee.getDesignation();
	        if ( designation == null ) {
	            return null;
	        }
	        String name = designation.getName();
	        if ( name == null ) {
	            return null;
	        }
	        if(designation.getDescription()!=null && getCompanyCofig() && !designation.getDescription().equals("common")) {
		        return designation.getDescription();
		        }
		       
	        return name;
	    }

	    private String employeeHierarchyEmployeeName(EmployeeHierarchy employeeHierarchy) {

	        if ( employeeHierarchy == null ) {
	            return null;
	        }
	        EmployeeProfile employee = employeeHierarchy.getEmployee();
	        if ( employee == null ) {
	            return null;
	        }
	        String name = employee.getName();
	        if ( name == null ) {
	            return null;
	        }
	       
		       
	        return name;
	    }
	    
	    private String employeeHierarchyEmployeCustomeName(EmployeeHierarchy employeeHierarchy) {

	        if ( employeeHierarchy == null ) {
	            return null;
	        }
	        EmployeeProfile employee = employeeHierarchy.getEmployee();
	        if ( employee == null ) {
	            return null;
	        }
	        
	        String name = employee.getName()+"\n"+"("+employee.getUser().getLogin()+")";
	        if ( name == null ) {
	            return null;
	        }
	       
		       
	        return name;
	    }


	    private String employeeHierarchyParentName(EmployeeHierarchy employeeHierarchy) {

	        if ( employeeHierarchy == null ) {
	            return null;
	        }
	        EmployeeProfile parent = employeeHierarchy.getParent();
	        if ( parent == null ) {
	            return null;
	        }
	        String name = parent.getName();
	        if ( name == null ) {
	            return null;
	        }
	       
		       
	        return name;
	    }
	    
	    private String employeeHierarchyParentCustomeName(EmployeeHierarchy employeeHierarchy) {

	        if ( employeeHierarchy == null ) {
	            return null;
	        }
	        EmployeeProfile parent = employeeHierarchy.getParent();
	        if ( parent == null ) {
	            return null;
	        }
	        String name = parent.getName()+"\n"+"("+parent.getUser().getLogin()+")";
	        if ( name == null ) {
	            return null;
	        }
	        
	       
		       
	        return name;
	    }


	    private String employeeHierarchyEmployeePid(EmployeeHierarchy employeeHierarchy) {

	        if ( employeeHierarchy == null ) {
	            return null;
	        }
	        EmployeeProfile employee = employeeHierarchy.getEmployee();
	        if ( employee == null ) {
	            return null;
	        }
	        String pid = employee.getPid();
	        if ( pid == null ) {
	            return null;
	        }
	        return pid;
	    }

	    private String employeeHierarchyParentPid(EmployeeHierarchy employeeHierarchy) {

	        if ( employeeHierarchy == null ) {
	            return null;
	        }
	        EmployeeProfile parent = employeeHierarchy.getParent();
	        if ( parent == null ) {
	            return null;
	        }
	        String pid = parent.getPid();
	        if ( pid == null ) {
	            return null;
	        }
	        return pid;
	    }

	    private Long employeeHierarchyEmployeeId(EmployeeHierarchy employeeHierarchy) {

	        if ( employeeHierarchy == null ) {
	            return null;
	        }
	        EmployeeProfile employee = employeeHierarchy.getEmployee();
	        if ( employee == null ) {
	            return null;
	        }
	        Long id = employee.getId();
	        if ( id == null ) {
	            return null;
	        }
	        return id;
	    }

	    private Long employeeHierarchyParentId(EmployeeHierarchy employeeHierarchy) {

	        if ( employeeHierarchy == null ) {
	            return null;
	        }
	        EmployeeProfile parent = employeeHierarchy.getParent();
	        if ( parent == null ) {
	            return null;
	        }
	        Long id = parent.getId();
	        if ( id == null ) {
	            return null;
	        }
	        return id;
	    }

		@Override
		public List<EmployeeHierarchyDTO> employeeHierarchiesToEmployeeHierarchyDTOsCustumName(
				List<EmployeeHierarchy> employeeHierarchies) {
			  if ( employeeHierarchies == null ) {
		            return null;
		        }

		        List<EmployeeHierarchyDTO> list = new ArrayList<EmployeeHierarchyDTO>();
		        for ( EmployeeHierarchy employeeHierarchy : employeeHierarchies ) {
		            list.add( employeeHierarchyToEmployeeHierarchyDTOCustomName( employeeHierarchy ) );
		        }

		        return list;
		}
}
