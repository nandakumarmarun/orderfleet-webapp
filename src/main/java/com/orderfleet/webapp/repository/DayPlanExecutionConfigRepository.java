package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DayPlanExecutionConfig;
import com.orderfleet.webapp.domain.enums.DayPlanPages;

/**
 * Spring Data JPA repository for the DayPlanExecutionConfig entity.
 * 
 * @author Muhammed Riyas T
 * @since Jan 03, 2017
 */
public interface DayPlanExecutionConfigRepository extends JpaRepository<DayPlanExecutionConfig, Long> {

	Optional<DayPlanExecutionConfig> findByCompanyIdAndName(Long id, DayPlanPages name);

	@Query("select dayPlanExecutionConfig from DayPlanExecutionConfig dayPlanExecutionConfig where dayPlanExecutionConfig.company.id = ?#{principal.companyId}")
	List<DayPlanExecutionConfig> findAllByCompanyId();

	List<DayPlanExecutionConfig> findAllByCompanyIdAndEnabledTrue(Long companyId);

}
