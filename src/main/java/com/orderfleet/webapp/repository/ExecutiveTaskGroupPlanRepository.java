package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.ExecutiveTaskGroupPlan;

/**
 * Spring Data JPA repository for the ExecutiveTaskGroupPlan entity.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
public interface ExecutiveTaskGroupPlanRepository extends JpaRepository<ExecutiveTaskGroupPlan, Long> {

	Optional<ExecutiveTaskGroupPlan> findOneByPid(String pid);

	@Query("select executiveTaskGroupPlan from ExecutiveTaskGroupPlan executiveTaskGroupPlan where executiveTaskGroupPlan.company.id = ?#{principal.companyId}")
	List<ExecutiveTaskGroupPlan> findAllByCompanyId();

	@Query("select executiveTaskGroupPlan from ExecutiveTaskGroupPlan executiveTaskGroupPlan where executiveTaskGroupPlan.company.id = ?#{principal.companyId}")
	Page<ExecutiveTaskGroupPlan> findAllByCompanyId(Pageable pageable);

	Long countByUserPidAndTaskGroupActivityGroupPidAndPlannedDateBetween(String userPid, String activityGroupPid,
			LocalDateTime startDate, LocalDateTime endDate);

}
