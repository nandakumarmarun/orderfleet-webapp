package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.FeedbackGroup;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserFeedbackGroup;

public interface UserFeedbackGroupRepository extends JpaRepository<UserFeedbackGroup, Long> {

	@Query("select userFeedbackGroup.feedbackGroup from UserFeedbackGroup userFeedbackGroup where userFeedbackGroup.user.login = ?#{principal.username} ")
	List<FeedbackGroup> findFeedbackGroupsByUserIsCurrentUser();

	@Query("select userFeedbackGroup.feedbackGroup from UserFeedbackGroup userFeedbackGroup where userFeedbackGroup.user.pid = ?1 ")
	List<FeedbackGroup> findFeedbackGroupsByUserPid(String userPid);

	@Query("select userFeedbackGroup.user from UserFeedbackGroup userFeedbackGroup where userFeedbackGroup.feedbackGroup.pid = ?1 ")
	List<User> findUsersByFeedbackGroupPid(String feedbackGroupPid);

	void deleteByFeedbackGroupPid(String feedbackGroupPid);

}
