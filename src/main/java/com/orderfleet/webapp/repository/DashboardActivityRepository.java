package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DashboardActivity;
import com.orderfleet.webapp.domain.Activity;

/**
 * Spring Data JPA repository for the DashboardActivity entity.
 *
 * @author Sarath
 * @since Oct 27, 2016
 */
public interface DashboardActivityRepository extends JpaRepository<DashboardActivity, Long> {

	@Query("select dashboardActivity.activity from DashboardActivity dashboardActivity where dashboardActivity.company.id = ?#{principal.companyId}")
	List<Activity> findActivitiesByCompanyId();

	@Query("select dashboardActivity.activity from DashboardActivity dashboardActivity where dashboardActivity.company.id = ?#{principal.companyId}")
	Page<Activity> findActivitiesByCompanyId(Pageable pageable);

	@Modifying
	@Query("delete from DashboardActivity dashboardActivity where dashboardActivity.company.id = ?#{principal.companyId}")
	void deleteDashboardActivities();

	@Query("select count(dashboardActivity) from DashboardActivity dashboardActivity where dashboardActivity.company.id = ?#{principal.companyId}")
	Long countByCompanyId();
}
