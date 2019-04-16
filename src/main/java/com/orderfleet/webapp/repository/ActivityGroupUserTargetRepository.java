package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.ActivityGroupUserTarget;

/**
 * Spring Data JPA repository for the ActivityGroupUserTarget entity.
 * 
 * @author Muhammed Riyas T
 * @since June 16, 2016
 */
public interface ActivityGroupUserTargetRepository extends JpaRepository<ActivityGroupUserTarget, Long> {

	Optional<ActivityGroupUserTarget> findOneByPid(String pid);

	@Query("select activityGroupUserTarget from ActivityGroupUserTarget activityGroupUserTarget where activityGroupUserTarget.company.id = ?#{principal.companyId}")
	List<ActivityGroupUserTarget> findAllByCompanyId();

	@Query("select activityGroupUserTarget from ActivityGroupUserTarget activityGroupUserTarget where activityGroupUserTarget.company.id = ?#{principal.companyId}")
	Page<ActivityGroupUserTarget> findAllByCompanyId(Pageable pageable);

	@Query("select aguTarget from ActivityGroupUserTarget aguTarget where"
			+ " aguTarget.user.id = ?1 and aguTarget.activityGroup.id = ?2 and"
			+ " ?3 between aguTarget.startDate and aguTarget.endDate")
	ActivityGroupUserTarget findActivityGroupUserTarget(Long userId, Long activityGroupId, LocalDate date);

}
