package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.domain.TaskList;

/**
 * Spring Data JPA repository for the TaskList entity.
 * 
 * @author Sarath
 * @since July 13, 2016
 */

public interface TaskListRepository extends JpaRepository<TaskList, Long> {

	Optional<TaskList> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<TaskList> findOneByPid(String pid);

	@Query("select taskList from TaskList taskList where taskList.company.id = ?#{principal.companyId}")
	List<TaskList> findAllByCompanyId();

	@Query("select taskList from TaskList taskList where taskList.company.id = ?#{principal.companyId}")
	Page<TaskList> findAllByCompanyId(Pageable pageable);

	Optional<TaskList> findByNameIgnoreCaseAndPid(String name, String pid);
	
	@Query("select taskList.tasks from TaskList taskList where taskList.company.id = ?#{principal.companyId} and taskList.pid = ?1")
	List<Task> findAllByTaskListPid(String pid);

	@Query("select taskList.tasks from TaskList taskList where taskList.company.id = ?#{principal.companyId}")
	List<Task> findAllByNotInTaskListPid();

}
