package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.web.rest.dto.BankDTO;

/**
 * Service Interface for managing Bank.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
public interface BankService {

	String PID_PREFIX = "BANK-";

	/**
	 * Save a bank.
	 * 
	 * @param bankDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	BankDTO save(BankDTO bankDTO);

	/**
	 * Update a bank.
	 * 
	 * @param bankDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	BankDTO update(BankDTO bankDTO);

	/**
	 * Get all the banks.
	 * 
	 * @return the list of entities
	 */
	List<BankDTO> findAllByCompany();

	/**
	 * Get all the banks of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<BankDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" bank.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	BankDTO findOne(Long id);

	/**
	 * Get the bank by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<BankDTO> findOneByPid(String pid);

	/**
	 * Get the bankDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<BankDTO> findByName(String name);

	/**
	 * Delete the "id" bank.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 * Update the Bank status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	BankDTO updateBankStatus(String pid, boolean activate);

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
	Page<BankDTO> findAllByCompanyAndActivatedBankOrderByName(Pageable pageable, boolean active);

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
	List<BankDTO> findAllByCompanyAndDeactivatedBank(boolean deactive);
}
