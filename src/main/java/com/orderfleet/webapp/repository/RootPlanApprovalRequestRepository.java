package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.RootPlanApprovalRequest;
import com.orderfleet.webapp.domain.RootPlanDetail;

public interface RootPlanApprovalRequestRepository extends JpaRepository<RootPlanApprovalRequest, Long>{
	
	Optional<RootPlanApprovalRequest> findFirstByRootPlanDetailOrderByRootPlanDetailIdDesc(RootPlanDetail rootPlanDetail);
	
	@Query("select rootPlanApprovalRequest from RootPlanApprovalRequest rootPlanApprovalRequest where rootPlanApprovalRequest.company.id =?#{principal.companyId}")
	List<RootPlanApprovalRequest>findAllByCompanyId();
	
	@Query("select rootPlanApprovalRequest from RootPlanApprovalRequest rootPlanApprovalRequest where rootPlanApprovalRequest.company.id =?#{principal.companyId} and rootPlanApprovalRequest.requestUser.pid = ?1 order by rootPlanApprovalRequest.requestedDate desc")
	List<RootPlanApprovalRequest>findAllByUserPid(String userPid);
	
	@Query("select rootPlanApprovalRequest from RootPlanApprovalRequest rootPlanApprovalRequest where rootPlanApprovalRequest.company.id =?#{principal.companyId} and rootPlanApprovalRequest.rootPlanDetail in ?1")
	List<RootPlanApprovalRequest>findAllByDetailPidIn(List<RootPlanDetail> rootPlanDetails);
	
	@Query("select rootPlanApprovalRequest from RootPlanApprovalRequest rootPlanApprovalRequest where rootPlanApprovalRequest.company.id =?#{principal.companyId} and rootPlanApprovalRequest.requestedDate between ?1 and ?2 order by rootPlanApprovalRequest.requestedDate desc")
	List<RootPlanApprovalRequest>findAllByDateBetween(LocalDateTime fromDate,LocalDateTime toDate);
	
}
