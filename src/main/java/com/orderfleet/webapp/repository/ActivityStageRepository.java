package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityStage;

public interface ActivityStageRepository extends JpaRepository<ActivityStage, Long> {

	@Query("select activityStage from ActivityStage activityStage where activityStage.company.id = ?#{principal.companyId}")
	List<ActivityStage> findAllByCompanyId();
	
	@Query("select activityStage.activity from ActivityStage activityStage where activityStage.company.id = ?#{principal.companyId} and activityStage.stage.pid = ?1")
	List<Activity> findActivityByStagePid(String stagePid);
	
	@Query("select activityStage from ActivityStage activityStage where activityStage.company.id = ?#{principal.companyId} and activityStage.activity.pid = ?1")
	List<ActivityStage> findByActivityPid(String activityPid);
	
	void deleteByActivityPid(String pid);
}
