package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.UserActivity;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;

/**
 * Service Interface for managing UserActivity.
 * 
 * @author Muhammed Riyas T
 * @since June 29, 2016
 */
public interface UserActivityService {

	/**
	 * Save a UserActivity.
	 * 
	 * @param userPid
	 * @param assignedActivities
	 */
	void save(String userPid, List<ActivityDTO> assignedActivities);

	List<ActivityDTO> findActivitiesByUserIsCurrentUser();

	List<ActivityDTO> findActivitiesByUserPid(String userPid);

	List<ActivityDTO> findByUserIsCurrentUserAndActivityActivated(boolean activated);

	List<ActivityDTO> findByUserIsCurrentUserAndActivityActivatedAndlastModifiedDate(boolean activated,
			LocalDateTime lastSyncdate);
	
	Set<Activity> findActivitiesByActivatedTrueAndUserIdIn(List<Long> userIds);
	
	void copyActivities(String fromUserPid, List<String> toUserPids);

	List<ActivityDTO> findAllDistinctByUserActivityByCompany();

	Set<UserActivity> findUserActivitiesByActivatedTrueAndUserIdIn(List<Long> userIds);
}
