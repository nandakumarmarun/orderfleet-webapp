package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.domain.UserTaskAssignment;
import com.orderfleet.webapp.domain.enums.PriorityStatus;
import com.orderfleet.webapp.domain.enums.TaskStatus;

/**
 * Spring Data JPA repository for the UserTaskAssignment entity.
 * 
 * @author Muhammed Riyas T
 * @since June 20, 2016
 */
public interface UserTaskAssignmentRepository extends JpaRepository<UserTaskAssignment, Long> {

	Optional<UserTaskAssignment> findOneByPid(String pid);

	@Query("select userTaskAssignment from UserTaskAssignment userTaskAssignment where userTaskAssignment.company.id = ?#{principal.companyId}")
	List<UserTaskAssignment> findAllByCompanyId();

	@Query("select userTaskAssignment from UserTaskAssignment userTaskAssignment where userTaskAssignment.company.id = ?#{principal.companyId}")
	Page<UserTaskAssignment> findAllByCompanyId(Pageable pageable);

	@Query("select userTaskAssignment from UserTaskAssignment userTaskAssignment where userTaskAssignment.executiveUser.login = ?#{principal.username} and userTaskAssignment.company.id = ?#{principal.companyId} and userTaskAssignment.taskStatus = 'OPENED'")
	List<UserTaskAssignment> findUserTasksByUserIsCurrentUser();

	@Query("select userTaskAssignment from UserTaskAssignment userTaskAssignment where userTaskAssignment.executiveUser.login = ?#{principal.username} and userTaskAssignment.company.id = ?#{principal.companyId} and userTaskAssignment.startDate = ?1")
	List<UserTaskAssignment> findByStartDate(LocalDate startDate);

	@Query("select userTaskAssignment from UserTaskAssignment userTaskAssignment where userTaskAssignment.executiveUser.login = ?#{principal.username} and userTaskAssignment.company.id = ?#{principal.companyId} and userTaskAssignment.startDate between ?1 and ?2")
	List<UserTaskAssignment> findByStartDateBetween(LocalDate fromDate, LocalDate toDate);

	@Query("select userTaskAssignment from UserTaskAssignment userTaskAssignment where userTaskAssignment.executiveUser.login = ?#{principal.username} and userTaskAssignment.company.id = ?#{principal.companyId} and userTaskAssignment.priorityStatus = ?1")
	List<UserTaskAssignment> findByPriorityStatus(PriorityStatus priorityStatus);

	@Query("select userTaskAssignment from UserTaskAssignment userTaskAssignment where userTaskAssignment.executiveUser.login = ?#{principal.username} and userTaskAssignment.task.pid = ?1")
	List<UserTaskAssignment> findUserTasksByCurrentUserAndTaskPid(String taskPid);

	@Query("select userTaskAssignment from UserTaskAssignment userTaskAssignment where userTaskAssignment.executiveUser.pid = ?1 and userTaskAssignment.company.id = ?#{principal.companyId}")
	List<UserTaskAssignment> findUserTasksByUserPid(String userPid);

	UserTaskAssignment findTop1ByExecutiveUserIdAndTaskActivityIdAndTaskAccountProfileIdAndTaskStatusOrderByIdDesc(
			Long userPid, Long activityPid, Long accountProfilePid, TaskStatus taskStatus);

	@Query("select userTaskAssignment.task from UserTaskAssignment userTaskAssignment where userTaskAssignment.executiveUser.pid = ?1 and userTaskAssignment.startDate = ?2")
	List<Task> findTasksByUserPidAndStartDate(String userPid, LocalDate date);

}
