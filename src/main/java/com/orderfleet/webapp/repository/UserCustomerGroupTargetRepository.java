package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.domain.UserCustomerGroupTarget;

/**
 * Spring Data JPA repository for the UserCustomerGroupTarget entity.
 * 
 * @author Muhammed Riyas T
 * @since June 15, 2016
 */
public interface UserCustomerGroupTargetRepository extends JpaRepository<UserCustomerGroupTarget, Long> {

	Optional<UserCustomerGroupTarget> findOneByPid(String pid);

	@Query("select userCustomerGroupTarget from UserCustomerGroupTarget userCustomerGroupTarget where userCustomerGroupTarget.company.id = ?#{principal.companyId}")
	List<UserCustomerGroupTarget> findAllByCompanyId();
	
	@Query("select distinct userCustomerGroupTarget from UserCustomerGroupTarget userCustomerGroupTarget where userCustomerGroupTarget.user.login = ?#{principal.username} and startDate >= ?1 and endDate <= ?2")
	List<UserCustomerGroupTarget> findByUserIsCurrentUserAndDateBetween(LocalDate startDate, LocalDate endDate);

	@Query("select userCustomerGroupTarget from UserCustomerGroupTarget userCustomerGroupTarget where userCustomerGroupTarget.company.id = ?#{principal.companyId}")
	Page<UserCustomerGroupTarget> findAllByCompanyId(Pageable pageable);

	@Query("select auTarget from UserCustomerGroupTarget auTarget where"
			+ " auTarget.user.id = ?1 and auTarget.stage.id = ?2 and"
			+ " ?3 between auTarget.startDate and auTarget.endDate")
	UserCustomerGroupTarget findUserCustomerGroupTarget(Long userId, Long stageId, LocalDate date);

	List<UserCustomerGroupTarget> findByUserPidAndStartDateGreaterThanEqualAndEndDateLessThanEqual(String userPid,
			LocalDate startDate, LocalDate endDate);
	
	List<UserCustomerGroupTarget> findByUserPidAndStageInAndStartDateGreaterThanEqualAndEndDateLessThanEqual(String userPid,List<Stage> activities,
			LocalDate startDate, LocalDate endDate);

	List<UserCustomerGroupTarget> findByUserPidAndStagePidAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
			String userPid, String stagePid, LocalDate startDate, LocalDate endDate);

}
