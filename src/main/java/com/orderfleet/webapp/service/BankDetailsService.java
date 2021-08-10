package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.web.rest.dto.BankDetailsDTO;

/**
 * Service Interface for managing Bank.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
public interface BankDetailsService {

	String PID_PREFIX = "BANKDETAILS-";

	BankDetailsDTO save(BankDetailsDTO bankDTO);

	/**
	 * Update a bank.
	 * 
	 * @param bankDTO the entity to update
	 * @return the persisted entity
	 */
	BankDetailsDTO update(BankDetailsDTO bankDTO);

	/**
	 * Get all the banks.
	 * 
	 * @return the list of entities
	 */
	List<BankDetailsDTO> findAllByCompany();

	/**
	 * Get the "id" bank.
	 * 
	 * @param id the id of the entity
	 * @return the entity
	 */
	BankDetailsDTO findOne(Long id);

	/**
	 * Get the bank by "pid".
	 * 
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	Optional<BankDetailsDTO> findOneByPid(String pid);

	/**
	 * Get the bankDTO by "name".
	 * 
	 * @param name the name of the entity
	 * @return the entity
	 */
	Optional<BankDetailsDTO> findByName(String name);

	/**
	 * Delete the "id" bank.
	 * 
	 * @param id the id of the entity
	 */
	void delete(String pid);

	List<BankDetailsDTO> findAllByCompanyAndDeactivatedBank(boolean deactive);
}
