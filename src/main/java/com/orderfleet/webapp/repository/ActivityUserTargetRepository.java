package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityUserTarget;

/**
 * Spring Data JPA repository for the ActivityUserTarget entity.
 * 
 * @author Muhammed Riyas T
 * @since June 15, 2016
 */
public interface ActivityUserTargetRepository extends JpaRepository<ActivityUserTarget, Long> {

	Optional<ActivityUserTarget> findOneByPid(String pid);

	@Query("select activityUserTarget from ActivityUserTarget activityUserTarget where activityUserTarget.company.id = ?#{principal.companyId}")
	List<ActivityUserTarget> findAllByCompanyId();
	
	@Query("select distinct activityUserTarget from ActivityUserTarget activityUserTarget where activityUserTarget.user.login = ?#{principal.username} and startDate >= ?1 and endDate <= ?2")
	List<ActivityUserTarget> findByUserIsCurrentUserAndDateBetween(LocalDate startDate, LocalDate endDate);

	@Query("select activityUserTarget from ActivityUserTarget activityUserTarget where activityUserTarget.company.id = ?#{principal.companyId}")
	Page<ActivityUserTarget> findAllByCompanyId(Pageable pageable);

	@Query("select auTarget from ActivityUserTarget auTarget where"
			+ " auTarget.user.id = ?1 and auTarget.activity.id = ?2 and"
			+ " ?3 between auTarget.startDate and auTarget.endDate")
	ActivityUserTarget findActivityUserTarget(Long userId, Long activityId, LocalDate date);

	List<ActivityUserTarget> findByUserPidAndStartDateGreaterThanEqualAndEndDateLessThanEqual(String userPid,
			LocalDate startDate, LocalDate endDate);
	
	List<ActivityUserTarget> findByUserPidAndActivityInAndStartDateGreaterThanEqualAndEndDateLessThanEqual(String userPid,List<Activity> activities,
			LocalDate startDate, LocalDate endDate);

	List<ActivityUserTarget> findByUserPidAndActivityPidAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
			String userPid, String activityPid, LocalDate startDate, LocalDate endDate);

}
