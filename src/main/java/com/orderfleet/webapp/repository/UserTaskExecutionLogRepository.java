package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.UserTaskExecutionLog;

/**
 * Spring Data JPA repository for the UserTaskExecutionLog entity.
 * 
 * @author Muhammed Riyas T
 * @since 11 Novembor, 2016
 */
public interface UserTaskExecutionLogRepository extends JpaRepository<UserTaskExecutionLog, Long> {

	@Query("select userTaskExecutionLog.createdDate from UserTaskExecutionLog userTaskExecutionLog where userTaskExecutionLog.userTaskAssignment.pid = ?1")
	List<LocalDateTime> findUserTaskExecutionLogByUserTaskAssignmentPid(String userTaskAssignmentPid);

}
