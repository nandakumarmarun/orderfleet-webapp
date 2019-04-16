package com.orderfleet.webapp.service.impl;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.TaskReferenceDocument;
import com.orderfleet.webapp.repository.TaskReferenceDocumentRepository;
import com.orderfleet.webapp.service.TaskReferenceDocumentService;
import com.orderfleet.webapp.web.rest.dto.TaskReferenceDocumentDTO;

/**
 * Service Implementation for managing TaskReferenceDocument.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
@Service
@Transactional
public class TaskReferenceDocumentServiceServiceImpl implements TaskReferenceDocumentService {

	private final Logger log = LoggerFactory.getLogger(TaskReferenceDocumentServiceServiceImpl.class);

	@Inject
	private TaskReferenceDocumentRepository taskReferenceDocumentRepository;

	/**
	 * Get one taskReferenceDocument by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public TaskReferenceDocumentDTO findOne(Long id) {
		log.debug("Request to get TaskReferenceDocument : {}", id);
		TaskReferenceDocument taskReferenceDocument = taskReferenceDocumentRepository.findOne(id);
		TaskReferenceDocumentDTO taskReferenceDocumentDTO = new TaskReferenceDocumentDTO(taskReferenceDocument);
		return taskReferenceDocumentDTO;
	}

}
