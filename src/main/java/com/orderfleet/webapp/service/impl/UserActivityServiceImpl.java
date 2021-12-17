package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityDocument;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserActivity;
import com.orderfleet.webapp.repository.ActivityDocumentRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.UserActivityRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserActivityService;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;

/**
 * Service Implementation for managing UserActivity.
 * 
 * @author Muhammed Riyas T
 * @since June 30, 2016
 */
@Service
@Transactional
public class UserActivityServiceImpl implements UserActivityService {

	private final Logger log = LoggerFactory.getLogger(UserActivityServiceImpl.class);

	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private UserActivityRepository userActivityRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ActivityDocumentRepository activityDocumentRepository;

	/**
	 * Save a UserActivity.
	 * 
	 * @param userPid
	 * @param assignedActivities
	 */
	@Override
	public void save(String userPid, List<ActivityDTO> assignedActivities) {
		log.debug("Request to save User Activity");

		User user = userRepository.findOneByPid(userPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		List<UserActivity> userActivities = new ArrayList<>();
		for (ActivityDTO activityDTO : assignedActivities) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Activity activity = activityRepository.findOneByPid(activityDTO.getPid()).get();
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

			userActivities.add(new UserActivity(user, activity, company, activityDTO.getPlanThrouchOnly(),
					activityDTO.getExcludeAccountsInPlan(), activityDTO.getSaveActivityDuration(),
					activityDTO.getInterimSave()));
		}
		userActivityRepository.deleteByUserPid(userPid);
		userActivityRepository.save(userActivities);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityDTO> findActivitiesByUserIsCurrentUser() {
		List<UserActivity> userActivities = userActivityRepository.findByUserIsCurrentUser();
		List<ActivityDTO> result = new ArrayList<>();
		for (UserActivity userActivity : userActivities) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AD_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by activityPid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			List<ActivityDocument> activityDocuments = activityDocumentRepository
					.findByActivityPid(userActivity.getActivity().getPid());
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
			Set<DocumentDTO> documents = activityDocuments.stream()
					.map(actDoc -> new DocumentDTO(actDoc.getDocument(), actDoc.getRequired(), actDoc.getSortOrder()))
					.collect(Collectors.toSet());
			ActivityDTO activityDTO = new ActivityDTO(userActivity.getActivity(),
					userActivity.getSaveActivityDuration(), userActivity.getPlanThrouchOnly(),
					userActivity.getExcludeAccountsInPlan(), userActivity.getInterimSave());
			activityDTO.setDocuments(documents);
			result.add(activityDTO);
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityDTO> findActivitiesByUserPid(String userPid) {
		log.debug("Request to get all Activities");
		List<UserActivity> userActivities = userActivityRepository.findByUserPid(userPid);
		List<ActivityDTO> result = new ArrayList<>();
		for (UserActivity userActivity : userActivities) {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AD_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get by activityPid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			List<ActivityDocument> activityDocuments = activityDocumentRepository
					.findByActivityPid(userActivity.getActivity().getPid());
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

			Set<DocumentDTO> documents = activityDocuments.stream()
					.map(actDoc -> new DocumentDTO(actDoc.getDocument(), actDoc.getRequired(), actDoc.getSortOrder()))
					.collect(Collectors.toSet());
			ActivityDTO activityDTO = new ActivityDTO(userActivity.getActivity(),
					userActivity.getSaveActivityDuration(), userActivity.getPlanThrouchOnly(),
					userActivity.getExcludeAccountsInPlan(), userActivity.getInterimSave());
			activityDTO.setDocuments(documents);
			result.add(activityDTO);
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityDTO> findByUserIsCurrentUserAndActivityActivated(boolean activated) {
		List<UserActivity> userActivities = userActivityRepository
				.findByUserIsCurrentUserAndActivityActivated(activated);
		List<ActivityDTO> result = new ArrayList<>();
		for (UserActivity userActivity : userActivities) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AD_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by activityPid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			List<ActivityDocument> activityDocuments = activityDocumentRepository
					.findByActivityPid(userActivity.getActivity().getPid());
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

			Set<DocumentDTO> documents = activityDocuments.stream()
					.map(actDoc -> new DocumentDTO(actDoc.getDocument(), actDoc.getRequired(), actDoc.getSortOrder()))
					.collect(Collectors.toSet());
			ActivityDTO activityDTO = new ActivityDTO(userActivity.getActivity(),
					userActivity.getSaveActivityDuration(), userActivity.getPlanThrouchOnly(),
					userActivity.getExcludeAccountsInPlan(), userActivity.getInterimSave());
			activityDTO.setDocuments(documents);
			result.add(activityDTO);
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityDTO> findByUserIsCurrentUserAndActivityActivatedAndlastModifiedDate(boolean activated,
			LocalDateTime lastSyncdate) {
		List<UserActivity> userActivities = userActivityRepository
				.findByUserIsCurrentUserAndActivityActivatedAndlastModifiedDate(activated, lastSyncdate);
		List<ActivityDTO> result = new ArrayList<>();
		for (UserActivity userActivity : userActivities) {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AD_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get by activityPid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			List<ActivityDocument> activityDocuments = activityDocumentRepository
					.findByActivityPid(userActivity.getActivity().getPid());
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

			Set<DocumentDTO> documents = activityDocuments.stream()
					.map(actDoc -> new DocumentDTO(actDoc.getDocument(), actDoc.getRequired(), actDoc.getSortOrder()))
					.collect(Collectors.toSet());
			ActivityDTO activityDTO = new ActivityDTO(userActivity.getActivity(),
					userActivity.getSaveActivityDuration(), userActivity.getPlanThrouchOnly(),
					userActivity.getExcludeAccountsInPlan(), userActivity.getInterimSave());
			activityDTO.setDocuments(documents);
			result.add(activityDTO);
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Set<Activity> findActivitiesByActivatedTrueAndUserIdIn(List<Long> userIdds) {
		return userActivityRepository.findActivitiesByActivatedTrueAndUserIdIn(userIdds);
	}

	@Override
	public void copyActivities(String fromUserPid, List<String> toUserPids) {
		// delete association first
		userActivityRepository.deleteByUserPidIn(toUserPids);
		List<UserActivity> userActivities = userActivityRepository.findByUserPid(fromUserPid);
		if (userActivities != null && !userActivities.isEmpty()) {
			List<User> users = userRepository.findByUserPidIn(toUserPids);
			for (User user : users) {
				List<UserActivity> newUserActivities = userActivities.stream()
						.map(ua -> new UserActivity(user, ua.getActivity(), ua.getCompany(), ua.getPlanThrouchOnly(),
								ua.getExcludeAccountsInPlan(), ua.getSaveActivityDuration(), ua.getInterimSave()))
						.collect(Collectors.toList());
				userActivityRepository.save(newUserActivities);
			}
		}
	}

	@Override
	public List<ActivityDTO> findAllDistinctByUserActivityByCompany() {

		List<UserActivity> userActivities = userActivityRepository.findAllDistinctByUserActivityByCompany();
		Set<ActivityDTO> allActivityDTOs = new HashSet<>();
		allActivityDTOs.addAll(userActivities.stream()
				.map(usrActvity -> new ActivityDTO(usrActvity.getActivity(), usrActvity.getSaveActivityDuration(),
						usrActvity.getPlanThrouchOnly(), usrActvity.getExcludeAccountsInPlan(),
						usrActvity.getInterimSave()))
				.collect(Collectors.toSet()));
		return new ArrayList<>(allActivityDTOs);
	}
	

	@Override
	@Transactional(readOnly = true)
	public Set<UserActivity> findUserActivitiesByActivatedTrueAndUserIdIn(List<Long> userIds) {
		return userActivityRepository.findUserActivitiesByActivatedTrueAndUserIdIn(userIds);
	}
}
