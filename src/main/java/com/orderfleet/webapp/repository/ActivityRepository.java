package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Activity;

/**
 * Spring Data JPA repository for the Activity entity.
 * 
 * @author Muhammed Riyas T
 * @since May 19, 2016
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {

	Optional<Activity> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<Activity> findOneByPid(String pid);

	@Query("select activity from Activity activity where activity.company.id = ?#{principal.companyId}")
	List<Activity> findAllByCompanyId();

	@Query("select activity from Activity activity where activity.company.id = ?#{principal.companyId}")
	Page<Activity> findAllByCompanyId(Pageable pageable);

	@Query("select activity.activityAccountTypes from Activity activity where activity.pid = ?1 ")
	List<AccountType> findActivityAccountTypesByPid(String pid);

	@Query("select activity from Activity activity where activity.company.id = ?#{principal.companyId} and activity.activated = ?1 Order By activity.name asc")
	Page<Activity> findAllByCompanyIdAndActivatedActivityOrderByName(Pageable pageable, boolean active);

	@Query("select activity from Activity activity where activity.company.id = ?#{principal.companyId} and activity.activated = ?1 Order By activity.name asc")
	List<Activity> findAllByCompanyIdAndActivatedOrDeactivatedActivity(boolean deactive);
	
	@Query("select activity from Activity activity where activity.company.id = ?#{principal.companyId} and activity.activated = 'TRUE' Order By activity.name asc")
	List<Activity> findAllByCompanyIdOrderByName();
	
	@Query("select activity from Activity activity where activity.company.pid = ?1")
	List<Activity> findAllByCompanyPid(String companyPid);
	
	Optional<Activity> findByCompanyPidAndNameIgnoreCase(String pid, String name);
}
