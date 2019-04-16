package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.AttendanceSubgroupApprovalRequest;

public interface AttendanceSubgroupApprovalRequestRepository extends JpaRepository<AttendanceSubgroupApprovalRequest, Long>{
	
	@Query("select attendanceSubgroupApprovalRequest from AttendanceSubgroupApprovalRequest attendanceSubgroupApprovalRequest where attendanceSubgroupApprovalRequest.company.id = ?#{principal.companyId}")
	List<AttendanceSubgroupApprovalRequest> findAllByCompanyId();
	
	@Query("select attendanceSubgroupApprovalRequest from AttendanceSubgroupApprovalRequest attendanceSubgroupApprovalRequest where attendanceSubgroupApprovalRequest.company.id = ?#{principal.companyId} and attendanceSubgroupApprovalRequest.requestedUser.login=?1 and attendanceSubgroupApprovalRequest.attendanceStatusSubgroup.id=?2 and attendanceSubgroupApprovalRequest.requestedDate >= ?3 and attendanceSubgroupApprovalRequest.requestedDate <= ?4")
	List<AttendanceSubgroupApprovalRequest> findByUserLoginAndAttendanceStatusSubgroupIdAndRequestedDateIsCurrentDate(String login,Long subgroupId, LocalDateTime startDate, LocalDateTime endDate);

	@Query("select attendanceSubgroupApprovalRequest from AttendanceSubgroupApprovalRequest attendanceSubgroupApprovalRequest where attendanceSubgroupApprovalRequest.company.id =?#{principal.companyId} and attendanceSubgroupApprovalRequest.requestedUser.pid = ?1")
	List<AttendanceSubgroupApprovalRequest>findAllByUserPid(String userPid);
	
	@Query("select attendanceSubgroupApprovalRequest from AttendanceSubgroupApprovalRequest attendanceSubgroupApprovalRequest where attendanceSubgroupApprovalRequest.company.id =?#{principal.companyId} and attendanceSubgroupApprovalRequest.requestedDate between ?1 and ?2 order by attendanceSubgroupApprovalRequest.requestedDate desc")
	List<AttendanceSubgroupApprovalRequest>findAllByDateBetween(LocalDateTime fromDate,LocalDateTime toDate);
}
