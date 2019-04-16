package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.RootPlanSubgroupApprove;

/**
 * Repository for managing RootPlanSubgroupApprove.
 * 
 * @author fahad
 * @since Aug 28, 2017
 */
public interface RootPlanSubgroupApproveRepository extends JpaRepository<RootPlanSubgroupApprove, Long>{
	
	@Query("select rootPlanSubgroupApprove from RootPlanSubgroupApprove rootPlanSubgroupApprove where rootPlanSubgroupApprove.company.id = ?#{principal.companyId}")
	List<RootPlanSubgroupApprove> findAllByCompanyId();
	
	Optional<RootPlanSubgroupApprove> findByUserPidAndId(String userPid,Long id);
	
	Optional<RootPlanSubgroupApprove> findByUserPidAndAttendanceStatusSubgroupId(String userPid,Long subgroupId);
	
	List<RootPlanSubgroupApprove> findByUserLoginAndAttendanceStatusSubgroupId(String login,Long subgroupId);

	@Query("select rootPlanSubgroupApprove from RootPlanSubgroupApprove rootPlanSubgroupApprove where rootPlanSubgroupApprove.company.id = ?#{principal.companyId} and rootPlanSubgroupApprove.user.login=?1")
	List<RootPlanSubgroupApprove> findAllByUserLogin(String userLogin);
	
}
