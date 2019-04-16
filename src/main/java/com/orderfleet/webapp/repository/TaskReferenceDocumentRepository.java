package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.TaskReferenceDocument;

/**
 * Spring Data JPA repository for the TaskReferenceDocument entity.
 * 
 * @author Muhammed Riyas T
 * @since October 17, 2016
 */
public interface TaskReferenceDocumentRepository extends JpaRepository<TaskReferenceDocument, Long> {

	TaskReferenceDocument findByPid(String pid);
	
	TaskReferenceDocument findByTaskId(Long taskId);

}
