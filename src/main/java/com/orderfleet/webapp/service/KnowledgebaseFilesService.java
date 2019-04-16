package com.orderfleet.webapp.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.orderfleet.webapp.service.impl.FileManagerException;
import com.orderfleet.webapp.web.rest.dto.KnowledgebaseFileDTO;

public interface KnowledgebaseFilesService {

	String PID_PREFIX = "KWBF-";

	void saveKnowledgebaseFile(KnowledgebaseFileDTO knowledgebaseFileDTO, MultipartFile file)
			throws FileManagerException, IOException;

	/**
	 * Update a knowledgebaseFile.
	 * 
	 * @param knowledgebaseFileDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	KnowledgebaseFileDTO update(KnowledgebaseFileDTO knowledgebaseFileDTO, MultipartFile file)
			throws FileManagerException, IOException;

	void saveAssignedUsers(String pid, String assignedUsers);

	/**
	 * Get all the KnowledgebaseFilw of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<KnowledgebaseFileDTO> findAllByCompany(Pageable pageable);
	
	/**
	 * Get all the KnowledgebaseFilw of a company.
	 * 
	 * @return the list of entities
	 */
	List<KnowledgebaseFileDTO> findAllByCompanyId();

	/**
	 * Get the KnowledgebaseFileDTO by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<KnowledgebaseFileDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" foKnowledgebaseFile.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	Set<String> findSearchTagsByCompany();

}
