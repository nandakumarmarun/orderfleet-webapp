package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.ExecutiveTaskListPlan;

/**
 * Spring Data JPA repository for the ExecutiveTaskListPlan entity.
 * 
 * @author Sarath
 * @since Jul 14, 2016
 */
public interface ExecutiveTaskListPlanRepository extends JpaRepository<ExecutiveTaskListPlan, Long> {

	Optional<ExecutiveTaskListPlan> findOneByPid(String pid);

	@Query("select executiveTaskListPlan from ExecutiveTaskListPlan executiveTaskListPlan where executiveTaskListPlan.company.id = ?#{principal.companyId}")
	List<ExecutiveTaskListPlan> findAllByCompanyId();

	@Query("select executiveTaskListPlan from ExecutiveTaskListPlan executiveTaskListPlan where executiveTaskListPlan.company.id = ?#{principal.companyId}")
	Page<ExecutiveTaskListPlan> findAllByCompanyId(Pageable pageable);

}
