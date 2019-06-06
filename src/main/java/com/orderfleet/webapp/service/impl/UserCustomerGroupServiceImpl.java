package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.AccountGroup;
import com.orderfleet.webapp.domain.AccountGroupAccountProfile;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserCustomerGroup;
import com.orderfleet.webapp.repository.AccountGroupAccountProfileRepository;
import com.orderfleet.webapp.repository.AccountGroupRepository;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.StageRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserCustomerGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserCustomerGroupService;
import com.orderfleet.webapp.web.rest.dto.StageDTO;
import com.orderfleet.webapp.web.rest.dto.UserCustomerGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

/**
 * Service Interface for managing UserCustomerGroup.
 * 
 * @author Muhammed Riyas T
 * @since August 29, 2016
 */

@Service
public class UserCustomerGroupServiceImpl implements UserCustomerGroupService {

	private final Logger log = LoggerFactory.getLogger(AccountGroupAccountProfileServiceImpl.class);

	@Inject
	private UserRepository userRepository;

	@Inject
	private StageRepository stageRepository;

	@Inject
	private UserCustomerGroupRepository userCustomerGroupRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void save(String userPid, String assignedStages) {

		log.debug("Request to save User Stages");
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		User user = userRepository.findOneByPid(userPid).get();
		String[] stages = assignedStages.split(",");
		List<UserCustomerGroup> userCustomerGroups = new ArrayList<>();

		for (String stagePid : stages) {
			Stage stage = stageRepository.findOneByPid(stagePid).get();
			userCustomerGroups.add(new UserCustomerGroup(user, stage, company));
		}

		List<UserCustomerGroup> existingUserCustomerGroups = userCustomerGroupRepository.findUserCustomerGroupsByUserPid(userPid);
		if (existingUserCustomerGroups.size() > 0) {

			List<Long> userCustomerGroupIds = new ArrayList<>();

			for (UserCustomerGroup userCustomerGroup : existingUserCustomerGroups) {
				userCustomerGroupIds.add(userCustomerGroup.getId());
			}

			userCustomerGroupRepository.deleteByIdIn(userCustomerGroupIds);
		}
		userCustomerGroupRepository.save(userCustomerGroups);

	}

	@Override
	public List<UserCustomerGroupDTO> findUserCustomerGroupsByUserPid(String userPid) {

		List<UserCustomerGroup> userCustomerGroups = userCustomerGroupRepository.findUserCustomerGroupsByUserPid(userPid);

		List<UserCustomerGroupDTO> userCustomerGroupDTOs = new ArrayList<>();

		for (UserCustomerGroup userCustomerGroup : userCustomerGroups) {

			userCustomerGroupDTOs.add(new UserCustomerGroupDTO(userCustomerGroup.getStage().getPid(), userCustomerGroup.getStage().getName(), userPid,
					userCustomerGroup.getUser().getFirstName() + " " + userCustomerGroup.getUser().getLastName()));

		}
		return userCustomerGroupDTOs;
	}

}
