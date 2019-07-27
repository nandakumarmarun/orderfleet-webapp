package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;

/**
 * Service Interface for managing Location.
 * 
 * @author Shaheer
 * @since May 26, 2016
 */
public interface LocationService {

	String PID_PREFIX = "LOC-";

	/**
	 * Save a Location.
	 * 
	 * @param LocationDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	LocationDTO save(LocationDTO LocationDTO);

	/**
	 * Update a Location.
	 * 
	 * @param LocationDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	LocationDTO update(LocationDTO LocationDTO);

	/**
	 * Get all the Locations.
	 * 
	 * @return the list of entities
	 */
	List<LocationDTO> findAllByCompany();

	/**
	 * Get all the Locations of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<LocationDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" Location.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	LocationDTO findOne(Long id);

	/**
	 * Get the Location by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<LocationDTO> findOneByPid(String pid);

	/**
	 * Get the LocationDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<LocationDTO> findByName(String name);

	/**
	 * Delete the "id" Location.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 * Get all locations by not in location hierarchy.
	 * 
	 * @return the list of entities
	 */
	List<LocationDTO> findAllByCompanyAndIdNotInLocationHierarchy();

	/**
	 * @author Fahad
	 * 
	 * @since Feb 6, 2017
	 * 
	 *        Update the Location status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	LocationDTO updateLocationStatus(String pid, boolean active);

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all locationDTOs from Location by status and company.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the list of entity
	 */
	List<LocationDTO> findAllByCompanyAndLocationActivated(boolean active);

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
	Page<LocationDTO> findAllByCompanyAndActivatedLocationOrderByName(Pageable pageable, boolean active);

	Optional<LocationDTO> findByCompanyIdAndName(Long companyId, String name);

	LocationDTO saveLocation(Long companyId, LocationDTO locationDTO);

	List<LocationDTO> findAllByCompanyIdAndLocationActivatedLastModified(boolean active,
			LocalDateTime lastModifiedDate);
	
	List<LocationDTO> findAllByCompanyAndLocationIdIn(List<Long> locationIds);

	List<Location> findAllLocationByCompanyId(Long companyId);

	List<LocationDTO> findAllLocationsByCompanyAndActivatedLocations();

	void saveActivatedLocations(String assignedLocations);
}
