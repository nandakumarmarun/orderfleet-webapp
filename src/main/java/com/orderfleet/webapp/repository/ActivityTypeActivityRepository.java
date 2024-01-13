package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityTypeActivity;

public interface ActivityTypeActivityRepository extends JpaRepository<ActivityTypeActivity, Long> {

	@Query("select activityTypeActivity.activity from ActivityTypeActivity activityTypeActivity where activityTypeActivity.company.id = ?#{principal.companyId} and activityTypeActivity.activityType.pid = ?1")
	List<Activity> findActivityByActivityTypePid(String activitytypePid);

	
	@Transactional
	void deleteByActivityTypePid(String pid);
}
