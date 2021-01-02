package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.TaskGroup;
import com.orderfleet.webapp.domain.UserTaskGroupAssignment;
import com.orderfleet.webapp.domain.enums.PriorityStatus;

/**
 * Spring Data JPA repository for the UserTaskGroupAssignment entity.
 * 
 * @author Muhammed Riyas T
 * @since June 20, 2016
 */
public interface UserTaskGroupAssignmentRepository extends JpaRepository<UserTaskGroupAssignment, Long> {

	Optional<UserTaskGroupAssignment> findOneByPid(String pid);

	@Query("select userTaskGroupAssignment from UserTaskGroupAssignment userTaskGroupAssignment where userTaskGroupAssignment.company.id = ?#{principal.companyId}")
	List<UserTaskGroupAssignment> findAllByCompanyId();

	@Query("select userTaskGroupAssignment from UserTaskGroupAssignment userTaskGroupAssignment where userTaskGroupAssignment.company.id = ?#{principal.companyId}")
	Page<UserTaskGroupAssignment> findAllByCompanyId(Pageable pageable);

	@Query("select userTaskGroupAssignment from UserTaskGroupAssignment userTaskGroupAssignment where userTaskGroupAssignment.executiveUser.login = ?#{principal.username} and userTaskGroupAssignment.company.id = ?#{principal.companyId}")
	List<UserTaskGroupAssignment> findByUserIsCurrentUser();

	@Query("select userTaskGroupAssignment from UserTaskGroupAssignment userTaskGroupAssignment where userTaskGroupAssignment.executiveUser.login = ?#{principal.username} and userTaskGroupAssignment.company.id = ?#{principal.companyId} and userTaskGroupAssignment.startDate =?1")
	List<UserTaskGroupAssignment> findByStartDate(LocalDate startDate);

	@Query("select userTaskGroupAssignment from UserTaskGroupAssignment userTaskGroupAssignment where userTaskGroupAssignment.executiveUser.login = ?#{principal.username} and userTaskGroupAssignment.company.id = ?#{principal.companyId} and userTaskGroupAssignment.startDate between ?1 and ?2")
	List<UserTaskGroupAssignment> findByStartDateBetween(LocalDate fromDate, LocalDate toDate);

	@Query("select userTaskGroupAssignment from UserTaskGroupAssignment userTaskGroupAssignment where userTaskGroupAssignment.executiveUser.login = ?#{principal.username} and userTaskGroupAssignment.company.id = ?#{principal.companyId} and userTaskGroupAssignment.priorityStatus = ?1")
	List<UserTaskGroupAssignment> findByPriorityStatus(PriorityStatus priorityStatus);

	UserTaskGroupAssignment findAllByTaskGroupPidAndStartDate(String taskGroupPid, LocalDate startDate);

	@Query("select userTaskGroupAssignment from UserTaskGroupAssignment userTaskGroupAssignment where userTaskGroupAssignment.executiveUser.pid = ?1 and userTaskGroupAssignment.company.id = ?#{principal.companyId}")
	List<UserTaskGroupAssignment> findUserTaskGroupsByUserPid(String userPid);

	@Query("select userTaskGroupAssignment.taskGroup from UserTaskGroupAssignment userTaskGroupAssignment where userTaskGroupAssignment.executiveUser.pid = ?1 and userTaskGroupAssignment.startDate = ?2")
	List<TaskGroup> findTaskGroupsByUserPidAndStartDate(String userPid, LocalDate startDate);

	@Query("select userTaskGroupAssignment.taskGroup from UserTaskGroupAssignment userTaskGroupAssignment where userTaskGroupAssignment.executiveUser.pid in ?1 and userTaskGroupAssignment.startDate = ?2")
	List<TaskGroup> findTaskGroupsByUserPidInAndStartDate(List<String> userPids, LocalDate date);

}
