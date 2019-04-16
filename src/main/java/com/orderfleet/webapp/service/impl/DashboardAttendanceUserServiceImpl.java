package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.DashboardAttendance;
import com.orderfleet.webapp.domain.DashboardAttendanceUser;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DashboardAttendanceRepository;
import com.orderfleet.webapp.repository.DashboardAttendanceUserRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.service.DashboardAttendanceUserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.mapper.UserMapper;

@Service
@Transactional
public class DashboardAttendanceUserServiceImpl implements DashboardAttendanceUserService{

	@Inject
	private DashboardAttendanceUserRepository dashboardAttendanceUserRepository;
	
	@Inject
	private UserMapper userMapper;
	
	@Inject
	private DashboardAttendanceRepository dashboardAttendanceRepository;
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Override
	public List<UserDTO> findUserByDashboardAttendanceId(Long id) {
		List<User> users=dashboardAttendanceUserRepository.findUsersByDashboardAttendanceId(id);
		List<UserDTO>userDTOs=userMapper.usersToUserDTOs(users);
		return userDTOs;
	}

	@Override
	public void saveAssignedUsers(Long id, String assignedUsers) {
		DashboardAttendance dashboardAttendance=dashboardAttendanceRepository.findOne(id);
		Company company =companyRepository.findOne(dashboardAttendance.getCompany().getId());
		
		String [] userArray=assignedUsers.split(",");
		List<DashboardAttendanceUser> dashboardAttendanceUsers=new ArrayList<>();
		for(String userPid:userArray){
			DashboardAttendanceUser dashboardAttendanceUser=new DashboardAttendanceUser();
			User user=userRepository.findOneByPid(userPid).get();
			dashboardAttendanceUser.setCompany(company);
			dashboardAttendanceUser.setDashboardAttendance(dashboardAttendance);
			dashboardAttendanceUser.setUser(user);
			dashboardAttendanceUsers.add(dashboardAttendanceUser);
		}
		dashboardAttendanceUserRepository.deleteByDashboardAttendanceId(id);
		dashboardAttendanceUserRepository.save(dashboardAttendanceUsers);
	}

}
