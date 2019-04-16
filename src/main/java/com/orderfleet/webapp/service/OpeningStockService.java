package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;

/**
 * Service Interface for managing OpeningStock.
 * 
 * @author Muhammed Riyas T
 * @since July 16, 2016
 */
public interface OpeningStockService {

	String PID_PREFIX = "OPPST-";

	/**
	 * Save a openingStock.
	 * 
	 * @param openingStockDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	OpeningStockDTO save(OpeningStockDTO openingStockDTO);

	/**
	 * Update a openingStock.
	 * 
	 * @param openingStockDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	OpeningStockDTO update(OpeningStockDTO openingStockDTO);

	/**
	 * Get all the openingStocks.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<OpeningStock> findAll(Pageable pageable);

	/**
	 * Get all the openingStocks.
	 * 
	 * @return the list of entities
	 */
	List<OpeningStockDTO> findAllByCompany();

	/**
	 * Get all the openingStocks of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<OpeningStockDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" openingStock.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	OpeningStockDTO findOne(Long id);

	/**
	 * Get the openingStock by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<OpeningStockDTO> findOneByPid(String pid);

	/**
	 * Get the openingStockDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<OpeningStockDTO> findByName(String name, String pid);

	List<OpeningStockDTO> findAllOpeningStockByStockLocations();

	/**
	 * Delete the "id" openingStock.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	List<OpeningStockDTO> findAllOpeningStockByProductProfiles(List<ProductProfile> productProfiles);

	/**
	 * Update the OpeningStock status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	OpeningStockDTO updateOpeningStockStatus(String pid, boolean activate);

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all openingStockDTOs from OpeningStock by status and company
	 *        andProductProfile.
	 * 
	 * @param active
	 *            the active of the entity
	 * @param productPid
	 *            the productPid of the entity
	 * @return the list of entity
	 */
	List<OpeningStockDTO> findByCompanyIdAndProductProfilePidAndOpeningStockActivated(String productPid,
			boolean active);

	/**
	 * @author Fahad
	 * @since Feb 16, 2017
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
	Page<OpeningStockDTO> findAllByCompanyAndActivatedOpeningStockOrderByName(Pageable pageable, boolean active);

	/**
	 * @author Fahad
	 * @since Feb 16, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	List<OpeningStockDTO> findAllByCompanyAndDeactivatedOpeningStock(boolean deactive);

	OpeningStockDTO saveOpeningStock(OpeningStockDTO openingStockDTO, Long companyId);

	List<OpeningStockDTO> findByCompanyIdAndProductProfilePidAndOpeningStockActivatedAndLastModifiedDate(
			String productPid, boolean active, LocalDateTime lastModifiedDate);
}
