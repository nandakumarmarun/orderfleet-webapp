package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.domain.StageStageGroup;

public interface StageStageGroupRepository extends JpaRepository<StageStageGroup, Long> {

	@Query("select ssg from StageStageGroup ssg where ssg.companyId = ?#{principal.companyId}")
	List<StageStageGroup> findAllByCompanyId();
	
	@Query("select ssg.stage from StageStageGroup ssg where ssg.companyId = ?#{principal.companyId} and ssg.stageGroup.pid = ?1")
	List<Stage> findStageByStageGroupPid(String stageGroupPid);
	
	void deleteByStageGroupPid(String pid);
}
