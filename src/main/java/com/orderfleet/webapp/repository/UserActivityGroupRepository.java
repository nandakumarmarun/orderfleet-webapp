package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.ActivityGroup;
import com.orderfleet.webapp.domain.UserActivityGroup;

/**
 * Spring Data JPA repository for the UserActivityGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since July 01, 2016
 */
public interface UserActivityGroupRepository extends JpaRepository<UserActivityGroup, Long> {

	@Query("select userActivityGroup.activityGroup from UserActivityGroup userActivityGroup where userActivityGroup.user.login = ?#{principal.username}")
	List<ActivityGroup> findActivityGroupsByUserIsCurrentUser();

	@Query("select userActivityGroup.activityGroup from UserActivityGroup userActivityGroup where userActivityGroup.user.pid = ?1 ")
	List<ActivityGroup> findActivityGroupsByUserPid(String userPid);

	void deleteByUserPid(String userPid);
	
	@Query("select userActivityGroup.activityGroup from UserActivityGroup userActivityGroup where userActivityGroup.user.login = ?#{principal.username} and userActivityGroup.activityGroup.activated = ?1")
	List<ActivityGroup> findActivityGroupsByUserIsCurrentUserAndActivityGroupActivated(boolean active);

	@Query("select userActivityGroup.activityGroup from UserActivityGroup userActivityGroup where userActivityGroup.user.login = ?#{principal.username} and userActivityGroup.activityGroup.activated = ?1 and userActivityGroup.activityGroup.lastModifiedDate > ?2")
	List<ActivityGroup> findActivityGroupsByUserIsCurrentUserAndActivityGroupActivatedAndLastModifiedDate(boolean active, LocalDateTime lastModifiedDate);

}
