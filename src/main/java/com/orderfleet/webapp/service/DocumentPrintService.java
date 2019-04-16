package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentPrintDTO;

/**
 * Service Interface for managing DocumentPrint.
 *
 * @author Sarath
 * @since Aug 12, 2017
 *
 */
public interface DocumentPrintService {

	String PID_PREFIX = "DOCPRNT-";

	/**
	 * Save a documentPrint.
	 * 
	 * @param documentPrintDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	DocumentPrintDTO save(DocumentPrintDTO documentPrintDTO);

	/**
	 * Update a documentPrint.
	 * 
	 * @param documentPrintDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	DocumentPrintDTO update(DocumentPrintDTO documentPrintDTO);

	/**
	 * Get all the documentPrints.
	 * 
	 * @return the list of entities
	 */
	List<DocumentPrintDTO> findAllByCompany();

	/**
	 * Get the "id" documentPrint.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	DocumentPrintDTO findOne(Long id);

	/**
	 * Get the documentPrint by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<DocumentPrintDTO> findOneByPid(String pid);

	void saveDocumentPrint(String userPid, String activityPid, String printEnableDocuments);
	
	List<DocumentDTO> findAllDocumentsByActivityPidAndUserPid(String activityPid,String userPid);
	
	List<DocumentPrintDTO> findAllByUserLogin();
}
