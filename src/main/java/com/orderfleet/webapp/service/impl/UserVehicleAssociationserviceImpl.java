package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.EmployeeVehicleAssosiationHistory;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserVehicleAssociation;
import com.orderfleet.webapp.domain.Vehicle;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserVehicleAssociationRepository;
import com.orderfleet.webapp.repository.VehicleRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeVehicleAssociationHistoryService;
import com.orderfleet.webapp.service.UserVehicleService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.EmployeeVehicleAssosiationHistoryDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.VehicleDTO;

@Service
@Transactional
public class UserVehicleAssociationserviceImpl implements UserVehicleService {

	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private EmployeeProfileRepository employeeProfileRepository;
	
	@Inject 
	private VehicleRepository vehicleRepository;
	
	@Inject 
	private EmployeeVehicleAssociationHistoryService employeeVehicleAssociationHistoryService;
	
	@Inject 
	private UserVehicleAssociationRepository userVehicleAssociationRepository;
	
	
	@Override
	public void save(String employeePid, String assignedVehicle) {
		System.out.println("entered to userVehicle service");
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Optional<EmployeeProfile> employeeProfile = employeeProfileRepository.findOneByPid(employeePid);
		System.out.println(employeeProfile);
		System.out.println(assignedVehicle);
		System.out.println(employeePid);
		String[] vehicle = assignedVehicle.split(",");
		
		UserVehicleAssociation UserVehicleAssociation = new UserVehicleAssociation();
		
		Optional<Vehicle> optvehicle = vehicleRepository.findOneByPid(vehicle[0]);
		System.out.println(optvehicle.isPresent());
		if(optvehicle.isPresent()) {
		Vehicle getvehicle = optvehicle.get();
		System.out.println(getvehicle);
		UserVehicleAssociation.setPid(UserVehicleService.PID_PREFIX+RandomUtil.generatePid());
		UserVehicleAssociation.setCompany(company);
		UserVehicleAssociation.setEmployeeProfile(employeeProfile.get());
		UserVehicleAssociation.setVehicle(getvehicle);
		}
		System.out.println(UserVehicleAssociation);
		
		EmployeeVehicleAssosiationHistoryDTO employeeVehicleAssosiationHistoryDTO = new EmployeeVehicleAssosiationHistoryDTO();
		employeeVehicleAssosiationHistoryDTO.setCompany(UserVehicleAssociation.getCompany());
		employeeVehicleAssosiationHistoryDTO.setEmployeeProfile(UserVehicleAssociation.getEmployeeProfile());
		employeeVehicleAssosiationHistoryDTO.setVehicle(UserVehicleAssociation.getVehicle());
		employeeVehicleAssosiationHistoryDTO.setCreatedDate(LocalDateTime.now());
		employeeVehicleAssosiationHistoryDTO.setEmployeeName(UserVehicleAssociation.getEmployeeProfile().getName());
		employeeVehicleAssosiationHistoryDTO.setRegistrationNumber(UserVehicleAssociation.getVehicle().getRegistrationNumber());
		employeeVehicleAssosiationHistoryDTO.setEmployeeName(UserVehicleAssociation.getEmployeeProfile().getName());
		employeeVehicleAssosiationHistoryDTO.setRegistrationNumber(UserVehicleAssociation.getVehicle().getName());
		
		employeeVehicleAssociationHistoryService.save(employeeVehicleAssosiationHistoryDTO);
		userVehicleAssociationRepository.deleteByemployeeProfilePid(employeePid);
		userVehicleAssociationRepository.save(UserVehicleAssociation);
		
	}


	@Override
	public VehicleDTO findVehicleByUserPid(String userPid) {
		Vehicle vehicle = userVehicleAssociationRepository.findVehicleByUserPid(userPid);
		System.out.println(vehicle);
		VehicleDTO vehicleDTO =  new VehicleDTO();
		 vehicleDTO.setPid(vehicle.getPid());
		 vehicleDTO.setName(vehicle.getName());
		 vehicleDTO.setRegistrationNumber(vehicle.getRegistrationNumber());
		 vehicleDTO.setDescription(vehicle.getDescription());
		return vehicleDTO;
	}


	

	

	

}
