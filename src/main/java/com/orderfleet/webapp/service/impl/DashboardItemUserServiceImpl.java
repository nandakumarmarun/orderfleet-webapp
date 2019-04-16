package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.DashboardItem;
import com.orderfleet.webapp.domain.DashboardItemUser;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DashboardItemRepository;
import com.orderfleet.webapp.repository.DashboardItemUserRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.service.DashboardItemUserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.mapper.UserMapper;

@Service
@Transactional
public class DashboardItemUserServiceImpl implements DashboardItemUserService{

	@Inject
	private DashboardItemUserRepository dashboardItemUserRepository;	
	
	@Inject
	private UserMapper userMapper;
	
	@Inject
	private DashboardItemRepository dashboardItemRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Override
	public List<UserDTO> findUserByDashboardItemPid(String pid) {
		List<User> users=dashboardItemUserRepository.findUsersByDashboardItemPid(pid);
		List<UserDTO>userDTOs=userMapper.usersToUserDTOs(users);
		return userDTOs;
	}

	@Override
	public void saveAssignedUsers(String pid, String assignedUsers) {
		
		DashboardItem dashboardItem=dashboardItemRepository.findOneByPid(pid).get();
		Company company =companyRepository.findOne(dashboardItem.getCompany().getId());
		
		String [] userArray=assignedUsers.split(",");
		List<DashboardItemUser> dashboardItemUsers=new ArrayList<>();
		for(String userPid:userArray){
			DashboardItemUser dashboardItemUser=new DashboardItemUser();
			User user=userRepository.findOneByPid(userPid).get();
			dashboardItemUser.setCompany(company);
			dashboardItemUser.setDashboardItem(dashboardItem);
			dashboardItemUser.setUser(user);
			dashboardItemUsers.add(dashboardItemUser);
		}
		dashboardItemUserRepository.deleteByDashboardItemPid(pid);
		dashboardItemUserRepository.save(dashboardItemUsers);
	}

}
