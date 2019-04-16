package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.TaskGroup;

/**
 * Spring Data JPA repository for the TaskGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
public interface TaskGroupRepository extends JpaRepository<TaskGroup, Long> {

	Optional<TaskGroup> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<TaskGroup> findOneByPid(String pid);

	@Query("select taskGroup from TaskGroup taskGroup where taskGroup.company.id = ?#{principal.companyId}")
	List<TaskGroup> findAllByCompanyId();

	@Query("select taskGroup from TaskGroup taskGroup where taskGroup.company.id = ?#{principal.companyId}")
	Page<TaskGroup> findAllByCompanyId(Pageable pageable);

	Optional<TaskGroup> findByNameIgnoreCaseAndPid(String name, String pid);
}
