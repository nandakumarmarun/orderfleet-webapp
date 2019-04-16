package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;

/**
 * Service Interface for managing PriceLevel.
 *
 * @author Muhammed Riyas T
 * @since August 22, 2016
 */
public interface PriceLevelService {

	String PID_PREFIX = "PRCL-";

	/**
	 * Save a priceLevel.
	 *
	 * @param priceLevelDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	PriceLevelDTO save(PriceLevelDTO priceLevelDTO);

	/**
	 * Update a priceLevel.
	 *
	 * @param priceLevelDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	PriceLevelDTO update(PriceLevelDTO priceLevelDTO);

	/**
	 * Get all the priceLevels.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<PriceLevel> findAll(Pageable pageable);

	/**
	 * Get all the priceLevels.
	 *
	 * @return the list of entities
	 */
	List<PriceLevelDTO> findAllByCompany();

	/**
	 * Get all the priceLevels of a company.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<PriceLevelDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" priceLevel.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	PriceLevelDTO findOne(Long id);

	/**
	 * Get the priceLevel by "pid".
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<PriceLevelDTO> findOneByPid(String pid);

	/**
	 * Get the priceLevelDTO by "name".
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<PriceLevelDTO> findByName(String name);

	/**
	 * Delete the "id" priceLevel.
	 *
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 * Update the PriceLevel status by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	PriceLevelDTO updatePriceLevelStatus(String pid, boolean active);

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
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
	Page<PriceLevelDTO> findAllByCompanyIdAndActivatedPriceLevelOrderByName(Pageable pageable, boolean active);

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 *
	 *        find all deactive company
	 *
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	List<PriceLevelDTO> findAllByCompanyIdAndDeactivatedPriceLevel(boolean deactive);

	Optional<PriceLevelDTO> findByCompanyIdAndName(Long companyId, String name);

	Optional<PriceLevelDTO> findByCompanyIdName(Long companyId, String name);

	PriceLevelDTO savePriceLevel(Long companyId, PriceLevelDTO priceLevelDTO);

	Optional<PriceLevel> findPriceLevelByCompanyIdName(Long companyId, String name);
}
