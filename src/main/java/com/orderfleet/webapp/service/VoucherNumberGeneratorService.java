package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.VoucherNumberGeneratorDTO;

public interface VoucherNumberGeneratorService {
	
	/**
	 * Save a VoucherNumberGenerator.
	 * 
	 * @param voucherNumberGeneratorDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	VoucherNumberGeneratorDTO save(VoucherNumberGeneratorDTO voucherNumberGeneratorDTO);

	/**
	 * Update a VoucherNumberGenerator.
	 * 
	 * @param voucherNumberGeneratorDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	VoucherNumberGeneratorDTO update(VoucherNumberGeneratorDTO voucherNumberGeneratorDTO);
	
	/**
	 * Get the VoucherNumberGenerator by "prefix".
	 * 
	 * @param prefix
	 *            the prefix of the entity
	 * @return the entity
	 */
	Optional<VoucherNumberGeneratorDTO> findByPrefix(String prefix);
	
	/**
	 * Get all the VoucherNumberGenerator of a company.
	 * 
	 * @return the list of entities
	 */
	List<VoucherNumberGeneratorDTO> findAllByCompany();
	
	
	/**
	 * Get the VoucherNumberGenerator by "id".
	 * 
	 * @param prefix
	 *            the prefix of the entity
	 * @return the entity
	 */
	Optional<VoucherNumberGeneratorDTO> findById(Long id);
	
	/**
	 * Delete the "id" VoucherNumberGenerator.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(Long id);
}
