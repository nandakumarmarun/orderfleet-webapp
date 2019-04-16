package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.ActivityType;

public interface ActivityTypeRepository extends JpaRepository<ActivityType, Long> {
	Optional<ActivityType> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<ActivityType> findOneByPid(String pid);

	@Query("select activityType from ActivityType activityType where activityType.company.id = ?#{principal.companyId}")
	List<ActivityType> findAllByCompanyId();

	@Query("select activityType from ActivityType activityType where activityType.company.id = ?#{principal.companyId} and activityType.activated = ?1")
	List<ActivityType> findAllByCompanyAndDeactivatedActivityType(boolean deactive);
}
