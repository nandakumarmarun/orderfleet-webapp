package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.TaskUserSetting;

/**
 * Spring Data JPA repository for the TaskUserSetting entity.
 * 
 * @author Muhammed Riyas T
 * @since October 04, 2016
 */
public interface TaskUserSettingRepository extends JpaRepository<TaskUserSetting, Long> {

	Optional<TaskUserSetting> findOneByPid(String pid);

	@Query("select taskUserSetting from TaskUserSetting taskUserSetting where taskUserSetting.company.id = ?#{principal.companyId}")
	List<TaskUserSetting> findAllByCompanyId();

	@Query("select taskUserSetting from TaskUserSetting taskUserSetting where taskUserSetting.company.id = ?#{principal.companyId}")
	Page<TaskUserSetting> findAllByCompanyId(Pageable pageable);

	TaskUserSetting findByExecutorPidAndTaskSettingPid(String executorPid, String taskSettingPid);

}
