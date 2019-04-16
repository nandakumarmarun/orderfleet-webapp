package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentStockCalculationDTO;

/**
 * Service Interface for managing Document.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
public interface DocumentService {

	String PID_PREFIX = "DOC-";

	/**
	 * Save a document.
	 * 
	 * @param documentDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	DocumentDTO save(DocumentDTO documentDTO);

	DocumentStockCalculationDTO saveDocumentStockCalculation(DocumentStockCalculationDTO documentStockCalculationDTO);

	/**
	 * Update a document.
	 * 
	 * @param documentDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	DocumentDTO update(DocumentDTO documentDTO);

	/**
	 * Get all the documents.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<Document> findAll(Pageable pageable);

	/**
	 * Get all the documents of a company.
	 * 
	 * @return the list of entities
	 */
	List<DocumentDTO> findAllByCompany();

	List<DocumentDTO> findAllByDocumentType(DocumentType documentType);

	/**
	 * Get all the documents of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<DocumentDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get all the documents of a company and DocumentType.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<DocumentDTO> findAllByCompanyAndDocumentType(Pageable pageable, DocumentType documentType);

	/**
	 * Get the "id" document.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	DocumentDTO findOne(Long id);

	/**
	 * Get the document by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<DocumentDTO> findOneByPid(String pid);

	/**
	 * Get the document by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<DocumentDTO> findByName(String name);

	/**
	 * Get the document by "documentPrefix".
	 * 
	 * @param documentPrefix
	 *            the documentPrefix of the entity
	 * @return the entity
	 */
	Optional<DocumentDTO> findByDocumentPrefix(String documentPrefix);

	/**
	 * Delete the "id" document.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 *
	 */
	List<DocumentDTO> findAllUnderInventoryAndAccountingVoucher();

	/**
	 * Get the document by "pids".
	 * 
	 * @param pids
	 *            the pid of the entity
	 * @return the entity
	 */
	List<DocumentDTO> findOneByPidIn(List<String> pids);

	/**
	 * Get all the documents of a company.
	 * 
	 * @return the list of entities
	 */
	List<DocumentDTO> findAllByCompanyPidAndDocumentType(String companyPid,DocumentType documentType);
	
	/**
	 * Get all the documents of a activity.
	 * 
	 * @return the list of entities
	 */
	List<DocumentDTO> findAllByActivityPid(String activityPid);

	List<DocumentDTO> findAllByCompanyPid(String companyPid);

	Optional<DocumentDTO> findByNameAndCompanyPid(String name, String companyPid);

	DocumentDTO saveFormSAdmin(DocumentDTO documentDTO);

}
