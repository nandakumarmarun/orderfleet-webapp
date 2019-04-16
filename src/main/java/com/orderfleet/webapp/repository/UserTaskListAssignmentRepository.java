package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.domain.UserTaskListAssignment;
import com.orderfleet.webapp.domain.enums.PriorityStatus;

/**
 * Spring Data JPA repository for the UserTaskList.
 * 
 * @author Sarath
 * @since July 13, 2016
 */
public interface UserTaskListAssignmentRepository extends JpaRepository<UserTaskListAssignment, Long> {

	Optional<UserTaskListAssignment> findOneByPid(String pid);

	@Query("select userTaskListAssignment from UserTaskListAssignment userTaskListAssignment where userTaskListAssignment.company.id = ?#{principal.companyId}")
	List<UserTaskListAssignment> findAllByCompanyId();

	@Query("select userTaskListAssignment from UserTaskListAssignment userTaskListAssignment where userTaskListAssignment.company.id = ?#{principal.companyId}")
	Page<UserTaskListAssignment> findAllByCompanyId(Pageable pageable);

	@Query("select userTaskListAssignment from UserTaskListAssignment userTaskListAssignment where userTaskListAssignment.executiveUser.login = ?#{principal.username} and userTaskListAssignment.company.id = ?#{principal.companyId}")
	List<UserTaskListAssignment> findByUserIsCurrentUser();

	@Query("select userTaskListAssignment from UserTaskListAssignment userTaskListAssignment where userTaskListAssignment.executiveUser.login = ?#{principal.username} and userTaskListAssignment.company.id = ?#{principal.companyId} and userTaskListAssignment.startDate =?1")
	List<UserTaskListAssignment> findByStartDate(LocalDate startDate);

	@Query("select userTaskListAssignment from UserTaskListAssignment userTaskListAssignment where userTaskListAssignment.executiveUser.login = ?#{principal.username} and userTaskListAssignment.company.id = ?#{principal.companyId} and userTaskListAssignment.startDate between ?1 and ?2")
	List<UserTaskListAssignment> findByStartDateBetween(LocalDate fromDate, LocalDate toDate);

	@Query("select userTaskListAssignment from UserTaskListAssignment userTaskListAssignment where userTaskListAssignment.executiveUser.login = ?#{principal.username} and userTaskListAssignment.company.id = ?#{principal.companyId} and userTaskListAssignment.priorityStatus = ?1")
	List<UserTaskListAssignment> findByPriorityStatus(PriorityStatus priorityStatus);

	@Query("select userTaskListAssignment from UserTaskListAssignment userTaskListAssignment where userTaskListAssignment.executiveUser.pid = ?1 and userTaskListAssignment.company.id = ?#{principal.companyId}")
	List<UserTaskListAssignment> findUserTaskListByUserPid(String userPid);

	@Query("select userTaskListAssignment.taskList from UserTaskListAssignment userTaskListAssignment where userTaskListAssignment.executiveUser.pid = ?1 and userTaskListAssignment.startDate = ?2")
	List<TaskList> findTaskListsByUserPidAndStartDate(String userPid, LocalDate startDate);

}
