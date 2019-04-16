package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.TaskSetting;
import com.orderfleet.webapp.domain.enums.ActivityEvent;

/**
 * Spring Data JPA repository for the TaskSetting entity.
 * 
 * @author Muhammed Riyas T
 * @since October 04, 2016
 */
public interface TaskSettingRepository extends JpaRepository<TaskSetting, Long> {

	Optional<TaskSetting> findOneByPid(String pid);
	
	Optional<TaskSetting>  findByActivityPidAndDocumentPidAndStartDateColumnAndActivityEventAndTaskActivityPid(String activityPid, String documentPid,
			String startDateColumn, ActivityEvent activityEvent, String taskActivityPid);

	@Query("select taskSetting from TaskSetting taskSetting where taskSetting.company.id = ?#{principal.companyId}")
	List<TaskSetting> findAllByCompanyId();

	@Query("select taskSetting from TaskSetting taskSetting where taskSetting.company.id = ?#{principal.companyId}")
	Page<TaskSetting> findAllByCompanyId(Pageable pageable);

	List<TaskSetting> findByActivityPidAndDocumentPidAndActivityEvent(String activityPid, String documentPid,
			ActivityEvent activityEvent);

	List<TaskSetting> findByActivityPidAndDocumentPid(String activityPid, String documentPid);
}
