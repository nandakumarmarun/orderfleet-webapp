package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.UserBalanceDTO;

public interface UserBalanceService {

	/**
	 * Save a userBalance.
	 * 
	 * @param userBalanceDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	UserBalanceDTO save(UserBalanceDTO userBalanceDTO);

	/**
	 * Update a userBalance.
	 * 
	 * @param userBalanceDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	UserBalanceDTO update(UserBalanceDTO userBalanceDTO);

	/**
	 * Get all the userBalances.
	 * 
	 * @return the list of entities
	 */
	List<UserBalanceDTO> findAllByCompany();
	
	/**
	 * Get the "id" userBalance.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	UserBalanceDTO findOne(Long id);
	
	/**
	 * Delete the "id" userBalance.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(Long id);
}
