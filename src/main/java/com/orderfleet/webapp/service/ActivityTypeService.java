package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityTypeDTO;

public interface ActivityTypeService {

	String PID_PREFIX = "ACTYPE-";

	/**
	 * Save a activityType.
	 * 
	 * @param activityTypeDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	ActivityTypeDTO save(ActivityTypeDTO activityTypeDTO);

	/**
	 * Update a activityType.
	 * 
	 * @param activityTypeDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	ActivityTypeDTO update(ActivityTypeDTO activityTypeDTO);

	/**
	 * Get all the activityTypes.
	 * 
	 * @return the list of entities
	 */
	List<ActivityTypeDTO> findAllByCompany();

	/**
	 * Get the "id" activityType.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	ActivityTypeDTO findOne(Long id);

	/**
	 * Get the activityType by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<ActivityTypeDTO> findOneByPid(String pid);

	/**
	 * Get the activityTypeDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<ActivityTypeDTO> findByName(String name);

	/**
	 * Delete the "id" activityType.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        Update the ActivityType status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	ActivityTypeDTO updateActivityTypeStatus(String pid, boolean activate);

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
	List<ActivityTypeDTO> findAllByCompanyAndDeactivatedActivityType(boolean deactive);
	
	List<ActivityDTO> findAllActivityByActivityTypePid(String activityTypePid);
	
	void saveActivityTypeActivity(String activityTypePid,String activityPids);
	
	void deleteActivityTypeActivityByActivityTypePid(String activityTypePid);
}
