package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.UserActivity;

/**
 * Spring Data JPA repository for the UserActivity entity.
 * 
 * @author Muhammed Riyas T
 * @since June 29, 2016
 */
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

	@Query("select userActivity from UserActivity userActivity where userActivity.user.login = ?#{principal.username}")
	List<UserActivity> findByUserIsCurrentUser();

	@Query("select userActivity.activity from UserActivity userActivity where userActivity.user.pid = ?1 ")
	List<Activity> findActivitiesByUserPid(String userPid);
	
	List<UserActivity> findByUserPid(String userPid);

	void deleteByUserPid(String userPid);

	@Query("select userActivity from UserActivity userActivity where userActivity.user.login = ?#{principal.username} and userActivity.activity.activated = ?1")
	List<UserActivity> findByUserIsCurrentUserAndActivityActivated(boolean activated);

	@Query("select userActivity from UserActivity userActivity where userActivity.user.login = ?#{principal.username} and userActivity.activity.activated = ?1 and userActivity.activity.lastModifiedDate > ?2")
	List<UserActivity> findByUserIsCurrentUserAndActivityActivatedAndlastModifiedDate(boolean activated,
			LocalDateTime lastSyncdate);
	
	@Query("select userActivity.activity from UserActivity userActivity where userActivity.activity.activated='TRUE' and userActivity.user.id in ?1)")
	Set<Activity> findActivitiesByActivatedTrueAndUserIdIn(List<Long> userIds);

	void deleteByUserPidIn(List<String> userPids);
	
	@Query("select userActivity from UserActivity userActivity where userActivity.activity.activated='TRUE' and  userActivity.user.pid in ?1 and userActivity.activity in ?2)")
	List<UserActivity> findByUserPidAndActivitiesInAndActivatedTrue(String userPid, List<Activity> activities);

	@Query("select distinct userActivity from UserActivity userActivity where userActivity.company.id = ?#{principal.companyId} and userActivity.activity.activated='TRUE'")
	List<UserActivity> findAllDistinctByUserActivityByCompany();

	@Query("select userActivity from UserActivity userActivity where userActivity.activity.activated='TRUE' and userActivity.user.id in ?1)")
	Set<UserActivity> findUserActivitiesByActivatedTrueAndUserIdIn(List<Long> userIds);
	
	List<String> findUserPidByCompanyPid(String pid);
}
