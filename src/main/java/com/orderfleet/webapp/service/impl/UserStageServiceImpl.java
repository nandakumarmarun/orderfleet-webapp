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
import com.orderfleet.webapp.domain.UserStage;
import com.orderfleet.webapp.repository.AccountGroupAccountProfileRepository;
import com.orderfleet.webapp.repository.AccountGroupRepository;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.StageRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserStageRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserStageService;
import com.orderfleet.webapp.web.rest.dto.StageDTO;
import com.orderfleet.webapp.web.rest.dto.UserStageDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

/**
 * Service Interface for managing UserStage.
 * 
 * @author Muhammed Riyas T
 * @since August 29, 2016
 */

@Service
public class UserStageServiceImpl implements UserStageService {

	private final Logger log = LoggerFactory.getLogger(AccountGroupAccountProfileServiceImpl.class);

	@Inject
	private UserRepository userRepository;

	@Inject
	private StageRepository stageRepository;

	@Inject
	private UserStageRepository userStageRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void save(String userPid, String assignedStages) {

		log.debug("Request to save User Stages");
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		User user = userRepository.findOneByPid(userPid).get();
		String[] stages = assignedStages.split(",");
		List<UserStage> userStages = new ArrayList<>();

		for (String stagePid : stages) {
			Stage stage = stageRepository.findOneByPid(stagePid).get();
			userStages.add(new UserStage(user, stage, company));
		}

		List<UserStage> existingUserStages = userStageRepository.findUserStagesByUserPid(userPid);
		if (existingUserStages.size() > 0) {

			List<Long> userStageIds = new ArrayList<>();

			for (UserStage userStage : existingUserStages) {
				userStageIds.add(userStage.getId());
			}

			userStageRepository.deleteByIdIn(userStageIds);
		}
		userStageRepository.save(userStages);

	}

	@Override
	public List<UserStageDTO> findUserStagesByUserPid(String userPid) {

		List<UserStage> userStages = userStageRepository.findUserStagesByUserPid(userPid);

		List<UserStageDTO> userStageDTOs = new ArrayList<>();

		for (UserStage userStage : userStages) {

			userStageDTOs.add(new UserStageDTO(userStage.getStage().getPid(), userStage.getStage().getName(), userPid,
					userStage.getUser().getFirstName() + " " + userStage.getUser().getLastName()));

		}
		return userStageDTOs;
	}

}
