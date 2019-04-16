package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityGroup;

/**
 * Spring Data JPA repository for the ActivityGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since June 09, 2016
 */
public interface ActivityGroupRepository extends JpaRepository<ActivityGroup, Long> {

	Optional<ActivityGroup> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<ActivityGroup> findOneByPid(String pid);

	@Query("select activityGroup from ActivityGroup activityGroup where activityGroup.company.id = ?#{principal.companyId}")
	List<ActivityGroup> findAllByCompanyId();

	@Query("select activityGroup from ActivityGroup activityGroup where activityGroup.company.id = ?#{principal.companyId}")
	Page<ActivityGroup> findAllByCompanyId(Pageable pageable);

	@Query("select activityGroup.activities from ActivityGroup activityGroup where activityGroup.pid = ?1 ")
	List<Activity> findActivityGroupActivitiesByPid(String pid);
	
	@Query("select activityGroup from ActivityGroup activityGroup where activityGroup.company.id = ?#{principal.companyId} and activityGroup.activated = ?1 order by activityGroup.name asc")
	Page<ActivityGroup> findAllByCompanyIdAndActivatedActivityGroupOrderByName(Pageable pageable,boolean active);

	@Query("select activityGroup from ActivityGroup activityGroup where activityGroup.company.id = ?#{principal.companyId} and  activityGroup.activated = ?1 ")
	List<ActivityGroup> findAllByCompanyIdAndDeactivatedActivityGroup(boolean deactive);
}
