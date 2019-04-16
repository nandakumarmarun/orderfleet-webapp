package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import com.orderfleet.webapp.web.rest.dto.IncomeExpenseHeadDTO;



/**
 * Service Interface for managing IncomeExpenseHead
 *
 * @author fahad
 * @since Feb 15, 2017
 */

public interface IncomeExpenseHeadService {

	String PID_PREFIX = "IEHEAD-";

	/**
	 * Save a incomeExpenseHead.
	 * 
	 * @param incomeExpenseHeadDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	IncomeExpenseHeadDTO save (IncomeExpenseHeadDTO incomeExpenseHeadDTO);

	/**
	 * Update a incomeExpenseHead.
	 * 
	 * @param incomeExpenseHeadDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	IncomeExpenseHeadDTO update(IncomeExpenseHeadDTO incomeExpenseHeadDTO);

	/**
	 * Get all the incomeExpenseHeads.
	 * 
	 * @return the list of entities
	 */
	List<IncomeExpenseHeadDTO> findAllByCompany();

	/**
	 * Get all the incomeExpenseHeads of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<IncomeExpenseHeadDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" incomeExpenseHead.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	IncomeExpenseHeadDTO findOne(Long id);

	/**
	 * Get the incomeExpenseHead by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<IncomeExpenseHeadDTO> findOneByPid(String pid);

	/**
	 * Get the incomeExpenseHeadDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<IncomeExpenseHeadDTO> findByName(String name);

	/**
	 * Delete the "id" incomeExpenseHead.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 * Update the IncomeExpenseHead status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	IncomeExpenseHeadDTO updateIncomeExpenseHeadStatus(String pid, boolean activate);

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        find all active company
	 * 
	 * @param active
	 *            the active of the entity
	 *
	 * @param pageable
	 *            the pageable of the entity
	 * @return the entity
	 */
	Page<IncomeExpenseHeadDTO> findAllByCompanyAndActivatedIncomeExpenseHeadOrderByName(Pageable pageable, boolean active);

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	List<IncomeExpenseHeadDTO> findAllByCompanyAndDeactivatedIncomeExpenseHead(boolean deactive);
}
