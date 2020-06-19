package com.orderfleet.webapp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;

/**
 * Service Interface for managing ReceivablePayable.
 * 
 * @author Sarath
 * @since Aug 16, 2016
 */
public interface ReceivablePayableService {

	String PID_PREFIX = "RP-";

	/**
	 * Save a receivablePayable.
	 * 
	 * @param receivablePayableDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	ReceivablePayableDTO save(ReceivablePayableDTO receivablePayableDTO);

	/**
	 * Update a receivablePayable.
	 * 
	 * @param receivablePayableDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	ReceivablePayableDTO update(ReceivablePayableDTO receivablePayableDTO);

	/**
	 * Get all the receivablePayables.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ReceivablePayable> findAll(Pageable pageable);

	/**
	 * Get all the receivablePayables of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ReceivablePayableDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get all the receivablePayable.
	 * 
	 * @return the list of entities
	 */
	List<ReceivablePayableDTO> findAllByCompany();

	/**
	 * Get all the receivablePayable.
	 * 
	 * @param accountPid
	 * @return the list of entities
	 */
	List<ReceivablePayableDTO> findAllByAccountProfilePid(String accountPid);

	/**
	 * Get the "id" receivablePayable.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	ReceivablePayableDTO findOne(Long id);

	/**
	 * Get the receivablePayable by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<ReceivablePayableDTO> findOneByPid(String pid);

	/**
	 * Get the receivablePayableDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<ReceivablePayableDTO> findByAccountName(String name);

	/**
	 * Delete the "id" receivablePayable.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	List<ReceivablePayableDTO> findAllByAccountProfilePidAndReceivablePayableType(String accountPid,
			ReceivablePayableType receivablePayableType);

	List<ReceivablePayableDTO> findAllByCompanyId(Long companyId);

	ReceivablePayableDTO saveReceivablePayable(ReceivablePayableDTO receivablePayableDTO, Long companyId);

	List<ReceivablePayableDTO> findAllByCompanyAndlastModifiedDate(LocalDateTime lastModifiedDate);
	
	/**
	 * Get all the receivablePayable.
	 * 
	 * @return the list of entities
	 */
	List<ReceivablePayableDTO> findAllByCompanyAndAccountProfileIn();
	
	/**
	 * Get all the receivablePayable .
	 * 
	 * @return the list of entities
	 */
	List<ReceivablePayableDTO> findAllByCompanyAndlastModifiedDateAndAccountProfileIn(LocalDateTime lastModifiedDate);
	
	ReceivablePayableDTO updateFromPage(ReceivablePayableDTO receivablePayableDTO);
	
	Integer dueUpdate();

	List<ReceivablePayableDTO> findAllByCompanyAndDateBetween(LocalDate fromDate, LocalDate toDate);

	List<ReceivablePayableDTO> findAllByAccountProfilePidAndDateBetween(String accountPid, LocalDate fromDate,
			LocalDate toDate);
}
