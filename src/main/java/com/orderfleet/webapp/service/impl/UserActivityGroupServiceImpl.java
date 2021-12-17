package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.ActivityGroup;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserActivityGroup;
import com.orderfleet.webapp.repository.ActivityGroupRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.UserActivityGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserActivityGroupService;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.ActivityGroupMapper;

/**
 * Service Implementation for managing UserActivityGroup.
 * 
 * @author Muhammed Riyas T
 * @since June 30, 2016
 */
@Service
@Transactional
public class UserActivityGroupServiceImpl implements UserActivityGroupService {

	private final Logger log = LoggerFactory.getLogger(UserActivityGroupServiceImpl.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private UserActivityGroupRepository userActivityGroupRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private ActivityGroupRepository activityGroupRepository;

	@Inject
	private ActivityGroupMapper activityGroupMapper;

	@Inject
	private CompanyRepository companyRepository;

	/**
	 * Save a UserActivityGroup.
	 * 
	 * @param userPid
	 * @param assignedActivities
	 */
	@Override
	public void save(String userPid, String assignedActivityGroups) {
		log.debug("Request to save User Activity Groups");

		User user = userRepository.findOneByPid(userPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] activityGroups = assignedActivityGroups.split(",");

		List<UserActivityGroup> userActivities = new ArrayList<>();

		for (String activityGroupPid : activityGroups) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AG_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			ActivityGroup activityGroup = activityGroupRepository.findOneByPid(activityGroupPid).get();
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);
			userActivities.add(new UserActivityGroup(user, activityGroup, company));
		}
		userActivityGroupRepository.deleteByUserPid(userPid);
		userActivityGroupRepository.save(userActivities);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityGroupDTO> findActivityGroupsByUserIsCurrentUser() {
		log.debug("Request to get current user all ActivityGroups");
		List<ActivityGroup> activityGroupList = userActivityGroupRepository.findActivityGroupsByUserIsCurrentUser();
		List<ActivityGroupDTO> result = activityGroupMapper.activityGroupsToActivityGroupDTOs(activityGroupList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityGroupDTO> findActivityGroupsByUserPid(String userPid) {
		log.debug("Request to get all ActivityGroups under in a user");
		List<ActivityGroup> activityGroupList = userActivityGroupRepository.findActivityGroupsByUserPid(userPid);
		List<ActivityGroupDTO> result = activityGroupMapper.activityGroupsToActivityGroupDTOs(activityGroupList);
		return result;
	}

}
