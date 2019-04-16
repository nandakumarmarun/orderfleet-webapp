package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;

/**
 * Service Interface for managing StockLocation.
 *
 * @author Muhammed Riyas T
 * @since July 15, 2016
 */
public interface StockLocationService {

	String PID_PREFIX = "STKLOC-";

	/**
	 * Save a stockLocation.
	 *
	 * @param stockLocationDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	StockLocationDTO save(StockLocationDTO stockLocationDTO);
	
	StockLocationDTO save(StockLocationDTO stockLocationDTO, Company company);

	/**
	 * Update a stockLocation.
	 *
	 * @param stockLocationDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	StockLocationDTO update(StockLocationDTO stockLocationDTO);

	/**
	 * Get all the stockLocations.
	 *
	 * @return the list of entities
	 */
	List<StockLocationDTO> findAllByCompany();

	/**
	 * Get all Actual stockLocations.
	 *
	 * @return the list of entities
	 */
	List<StockLocationDTO> findAllActualByCompanyId();

	/**
	 * Get all the stockLocations of a company.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<StockLocationDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" stockLocation.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	StockLocationDTO findOne(Long id);

	/**
	 * Get the stockLocation by "pid".
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<StockLocationDTO> findOneByPid(String pid);

	/**
	 * Get the stockLocationDTO by "name".
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<StockLocationDTO> findByName(String name);

	/**
	 * Delete the "id" stockLocation.
	 *
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 *
	 *        Update the StockLocation status by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	StockLocationDTO updateStockLocationStatus(String pid, boolean activate);

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
	Page<StockLocationDTO> findAllByCompanyAndActivatedStockLocationOrderByName(Pageable pageable, boolean active);

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
	List<StockLocationDTO> findAllByCompanyAndDeactivatedStockLocation(boolean deactive);

	/**
	 * Get all the stockLocations.
	 *
	 * @return the list of entities
	 */
	List<StockLocationDTO> findAllByCompanyId(Long companyId);

	/**
	 * Get the stockLocationDTO by "name".
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<StockLocationDTO> findByCompanyIdAndName(Long companyId, String name);

	StockLocationDTO findFirstByCompanyId(Long companyId);

	List<StockLocation> findAllStockLocationByCompanyId(Long companyId);
}
