package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.DashboardUser;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DashboardUserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.mapper.UserMapper;

/**
 * Service Implementation for managing DashboardUser.
 * 
 * @author Muhammed Riyas T
 * @since May 24, 2016
 */
@Service
@Transactional
public class DashboardUserServiceImpl implements DashboardUserService {

	private final Logger log = LoggerFactory.getLogger(DashboardUserServiceImpl.class);

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private UserMapper userMapper;

	/**
	 * Save a dashboardUser.
	 * 
	 * @param usersPids
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public void save(List<String> usersPids) {
		log.debug("Request to save DashboardUser : {}", usersPids);
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		List<DashboardUser> dashboardUsers = new ArrayList<>();
		for (String userPid : usersPids) {
			DashboardUser dashboardUser = new DashboardUser();
			dashboardUser.setUser(userRepository.findOneByPid(userPid).get());
			dashboardUser.setCompany(company);
			dashboardUsers.add(dashboardUser);
		}
		dashboardUserRepository.deleteDashboardUsers();
		dashboardUserRepository.save(dashboardUsers);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDTO> findUsersByCompanyId() {
		log.debug("Request to get all DashboardUsers");
		List<User> dashboardUserList = dashboardUserRepository.findUsersByCompanyId();
		return userMapper.usersToUserDTOs(dashboardUserList);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<UserDTO> findUsersByUserIdIn(List<Long> userIds) {
		log.debug("Request to get all DashboardUsers by user id in");
		List<User> userList = dashboardUserRepository.findUserByUserIdIn(userIds);
		return userMapper.usersToUserDTOs(userList);
	}

	/**
	 * Get all the dashboardUsers.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<UserDTO> findUsersByCompanyId(Pageable pageable) {
		log.debug("Request to get all DashboardUsers");
		Page<User> dashboardUsers = dashboardUserRepository.findUsersByCompanyId(pageable);
		return new PageImpl<>(userMapper.usersToUserDTOs(dashboardUsers.getContent()), pageable,
				dashboardUsers.getTotalElements());
	}

	@Override
	@Transactional(readOnly = true)
	public Long countByCompanyId() {
		return dashboardUserRepository.countByCompanyId();
	}

}
