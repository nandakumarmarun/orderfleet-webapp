package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Department;
import com.orderfleet.webapp.domain.Designation;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.EmployeeProfileMapper;
@Component
public class EmployeeProfileMapperImpl extends EmployeeProfileMapper {

  @Override
    public EmployeeProfileDTO employeeProfileToEmployeeProfileDTO(EmployeeProfile employeeProfile) {
        if ( employeeProfile == null ) {
            return null;
        }

        EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();

        employeeProfileDTO.setDesignationPid( employeeProfileDesignationPid( employeeProfile ) );
        employeeProfileDTO.setDesignationName( employeeProfileDesignationName( employeeProfile ) );
        employeeProfileDTO.setDepartmentName( employeeProfileDepartmentName( employeeProfile ) );
        employeeProfileDTO.setDepartmentPid( employeeProfileDepartmentPid( employeeProfile ) );
        employeeProfileDTO.setUserLastName( employeeProfileUserLastName( employeeProfile ) );
        employeeProfileDTO.setUserPid( employeeProfileUserPid( employeeProfile ) );
        employeeProfileDTO.setUserFirstName( employeeProfileUserFirstName( employeeProfile ) );
        employeeProfileDTO.setActivated( employeeProfile.getActivated() );
        employeeProfileDTO.setPid( employeeProfile.getPid() );
        employeeProfileDTO.setOrgEmpId( employeeProfile.getOrgEmpId() );
        employeeProfileDTO.setName( employeeProfile.getName());
        employeeProfileDTO.setAlias( employeeProfile.getAlias() );
        employeeProfileDTO.setReferenceId( employeeProfile.getReferenceId() );
        employeeProfileDTO.setAddress( employeeProfile.getAddress() );
        employeeProfileDTO.setPhone( employeeProfile.getPhone() );
        employeeProfileDTO.setEmail( employeeProfile.getEmail() );
        if ( employeeProfile.getProfileImage() != null ) {
            byte[] profileImage = employeeProfile.getProfileImage();
            employeeProfileDTO.setProfileImage( Arrays.copyOf( profileImage, profileImage.length ) );
        }
        employeeProfileDTO.setProfileImageContentType( employeeProfile.getProfileImageContentType() );

        return employeeProfileDTO;
    }

    @Override
    public List<EmployeeProfileDTO> employeeProfilesToEmployeeProfileDTOs(List<EmployeeProfile> employeeProfiles) {
        if ( employeeProfiles == null ) {
            return null;
        }

        List<EmployeeProfileDTO> list = new ArrayList<EmployeeProfileDTO>();
        for ( EmployeeProfile employeeProfile : employeeProfiles ) {
            list.add( employeeProfileToEmployeeProfileDTO( employeeProfile ) );
        }

        return list;
    }

    @Override
    public EmployeeProfile employeeProfileDTOToEmployeeProfile(EmployeeProfileDTO employeeProfileDTO) {
        if ( employeeProfileDTO == null ) {
            return null;
        }

        EmployeeProfile employeeProfile = new EmployeeProfile();

        employeeProfile.setDesignation( designationFromPid( employeeProfileDTO.getDesignationPid() ) );
        employeeProfile.setDepartment( departmentFromPid( employeeProfileDTO.getDepartmentPid() ) );
        employeeProfile.setActivated( employeeProfileDTO.getActivated() );
        employeeProfile.setPid( employeeProfileDTO.getPid() );
        employeeProfile.setName( employeeProfileDTO.getName() );
        employeeProfile.setAlias( employeeProfileDTO.getAlias() );
        employeeProfile.setReferenceId( employeeProfileDTO.getReferenceId() );
        employeeProfile.setOrgEmpId( employeeProfileDTO.getOrgEmpId() );
        employeeProfile.setAddress( employeeProfileDTO.getAddress() );
        employeeProfile.setPhone( employeeProfileDTO.getPhone() );
        employeeProfile.setEmail( employeeProfileDTO.getEmail() );
        if ( employeeProfileDTO.getProfileImage() != null ) {
            byte[] profileImage = employeeProfileDTO.getProfileImage();
            employeeProfile.setProfileImage( Arrays.copyOf( profileImage, profileImage.length ) );
        }
        employeeProfile.setProfileImageContentType( employeeProfileDTO.getProfileImageContentType() );

        return employeeProfile;
    }

    @Override
    public List<EmployeeProfile> employeeProfileDTOsToEmployeeProfiles(List<EmployeeProfileDTO> employeeProfileDTOs) {
        if ( employeeProfileDTOs == null ) {
            return null;
        }

        List<EmployeeProfile> list = new ArrayList<EmployeeProfile>();
        for ( EmployeeProfileDTO employeeProfileDTO : employeeProfileDTOs ) {
            list.add( employeeProfileDTOToEmployeeProfile( employeeProfileDTO ) );
        }

        return list;
    }

    private String employeeProfileDesignationPid(EmployeeProfile employeeProfile) {

        if ( employeeProfile == null ) {
            return null;
        }
        Designation designation = employeeProfile.getDesignation();
        if ( designation == null ) {
            return null;
        }
        String pid = designation.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String employeeProfileDesignationName(EmployeeProfile employeeProfile) {

        if ( employeeProfile == null ) {
            return null;
        }
        Designation designation = employeeProfile.getDesignation();
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

    private String employeeProfileDepartmentName(EmployeeProfile employeeProfile) {

        if ( employeeProfile == null ) {
            return null;
        }
        Department department = employeeProfile.getDepartment();
        if ( department == null ) {
            return null;
        }
        String name = department.getName();
        if ( name == null ) {
            return null;
        }
        if(department.getDescription()!=null && getCompanyCofig() && !department.getDescription().equals("common")) {
	        return department.getDescription();
	        }
	       
        return name;
    }

    private String employeeProfileDepartmentPid(EmployeeProfile employeeProfile) {

        if ( employeeProfile == null ) {
            return null;
        }
        Department department = employeeProfile.getDepartment();
        if ( department == null ) {
            return null;
        }
        String pid = department.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String employeeProfileUserLastName(EmployeeProfile employeeProfile) {

        if ( employeeProfile == null ) {
            return null;
        }
        User user = employeeProfile.getUser();
        if ( user == null ) {
            return null;
        }
        String lastName = user.getLastName();
        if ( lastName == null ) {
            return null;
        }
        return lastName;
    }

    private String employeeProfileUserPid(EmployeeProfile employeeProfile) {

        if ( employeeProfile == null ) {
            return null;
        }
        User user = employeeProfile.getUser();
        if ( user == null ) {
            return null;
        }
        String pid = user.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String employeeProfileUserFirstName(EmployeeProfile employeeProfile) {

        if ( employeeProfile == null ) {
            return null;
        }
        User user = employeeProfile.getUser();
        if ( user == null ) {
            return null;
        }
        String firstName = user.getFirstName();
        if ( firstName == null ) {
            return null;
        }
        return firstName;
    }
   
}
