package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.RootPlanDetail;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;

public interface RootPlanDetailRepository extends JpaRepository<RootPlanDetail, Long>{

	@Query("select rootPlanDetail from RootPlanDetail rootPlanDetail where rootPlanDetail.company.id =?#{principal.companyId}")
	List<RootPlanDetail>findAllByCompanyId();
	
	@Query("select rootPlanDetail from RootPlanDetail rootPlanDetail where rootPlanDetail.company.id =?#{principal.companyId} and rootPlanDetail.rootPlanHeader.pid = ?1")
	List<RootPlanDetail>findAllByHeaderPid(String pid);
	
	void deleteByRootPlanHeaderPid(String rootPlanHeaderPid);
	
	@Query("select rootPlanDetail from RootPlanDetail rootPlanDetail where rootPlanDetail.company.id =?#{principal.companyId} and rootPlanDetail.rootPlanHeader.pid in ?1")
	List<RootPlanDetail>findAllByHeaderPidIn(List<String> rootPlanHeaderPids);
	
	@Query("select rootPlanDetail from RootPlanDetail rootPlanDetail where rootPlanDetail.company.id =?#{principal.companyId} and rootPlanDetail.id = ?1")
	RootPlanDetail findOneById(Long id);
	
	@Query("select rootPlanDetail from RootPlanDetail rootPlanDetail where rootPlanDetail.company.id =?#{principal.companyId} and rootPlanDetail.pid = ?1")
	Optional<RootPlanDetail> findOneByPid(String pid);
	
	@Query("select rootPlanDetail from RootPlanDetail rootPlanDetail where rootPlanDetail.company.id =?#{principal.companyId} and rootPlanDetail.taskList.id = ?1")
	RootPlanDetail findOneByTaskListId(Long id);
	
	Optional<RootPlanDetail> findFirstByApprovalStatusEqualsAndCompanyIdAndRootPlanHeaderIdOrderByRootPlanOrderDesc(ApprovalStatus approvalStatus, Long companyId,Long headerId);
	
	@Query("select rootPlanDetail from RootPlanDetail rootPlanDetail where rootPlanDetail.company.id =?#{principal.companyId} and rootPlanDetail.approvalStatus = ?1 and rootPlanDetail.rootPlanHeader.id in ?2 order by rootPlanDetail.rootPlanOrder asc")
	List<RootPlanDetail> findAllByApprovalStatusAndHeaderPidInOrderByRootOrder(ApprovalStatus approvalStatus,List<Long> rootPlanHeaderIds);
	
	@Query("select rootPlanDetail from RootPlanDetail rootPlanDetail where rootPlanDetail.company.id =?#{principal.companyId} and rootPlanDetail.approvalStatus != ?1 and rootPlanDetail.rootPlanHeader.id = ?2 order by rootPlanDetail.rootPlanOrder asc")
	List<RootPlanDetail> findByApprovalStatusNotEqualAndHeaderIdOrderByRootOrder(ApprovalStatus approvalStatus,Long rootPlanHeaderId);
	
	@Query("select rootPlanDetail from RootPlanDetail rootPlanDetail where rootPlanDetail.company.id =?#{principal.companyId} and rootPlanDetail.rootPlanHeader.user.login = ?1 and rootPlanDetail.approvalStatus != ?2 order by rootPlanDetail.rootPlanOrder asc")
	List<RootPlanDetail> findAllByUserLoginAndApprovalStatusNotEqualOrderByRootOrder(String login, ApprovalStatus approvalStatus);
	
	@Query("select rootPlanDetail from RootPlanDetail rootPlanDetail where rootPlanDetail.company.id =?#{principal.companyId} and rootPlanDetail.approvalStatus = ?1 and rootPlanDetail.downloadDate between ?2 and ?3 and rootPlanDetail.rootPlanHeader.pid = ?4")
	RootPlanDetail findByApprovalStatusAndDownloadDateAndHeaderPid(ApprovalStatus approvalStatus,LocalDateTime fromdate,LocalDateTime todate,String rootPlanHeaderPid);
	
	@Query("select rootPlanDetail from RootPlanDetail rootPlanDetail where rootPlanDetail.company.id =?#{principal.companyId} and rootPlanDetail.approvalStatus = ?1 and rootPlanDetail.rootPlanHeader.pid = ?2")
	List<RootPlanDetail>findByApprovalStatusAndHeaderPid(ApprovalStatus approvalStatus,String rootPlanHeaderPid);
	
	@Query("select rootPlanDetail from RootPlanDetail rootPlanDetail where rootPlanDetail.company.id =?#{principal.companyId} and rootPlanDetail.rootPlanHeader.user.pid = ?1 and rootPlanDetail.downloadDate between ?2 and ?3")
	List<RootPlanDetail> findAllByUserPidAndDownloadDateBetween(String userpid, LocalDateTime startDownloadDate, LocalDateTime endDownloadDate);
}
