package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.ActivityGroup;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupDTO;

/**
 * Service Interface for managing ActivityGroup.
 * 
 * @author Muhammed Riyas T
 * @since June 09, 2016
 */
public interface ActivityGroupService {

	String PID_PREFIX = "ACTGP-";

	/**
	 * Save a activityGroup.
	 * 
	 * @param activityGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	ActivityGroupDTO save(ActivityGroupDTO activityGroupDTO);

	void saveAssignedActivities(String pid, String assignedActivities);

	/**
	 * Update a activityGroup.
	 * 
	 * @param activityGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	ActivityGroupDTO update(ActivityGroupDTO activityGroupDTO);

	/**
	 * Get all the activityGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ActivityGroup> findAll(Pageable pageable);

	/**
	 * Get all the activityGroups of a company.
	 * 
	 * @return the list of entities
	 */
	List<ActivityGroupDTO> findAllByCompany();

	/**
	 * Get all the activityGroups of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ActivityGroupDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" activityGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	ActivityGroupDTO findOne(Long id);

	/**
	 * Get the activityGroup by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<ActivityGroupDTO> findOneByPid(String pid);

	/**
	 * Get the activityGroupDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<ActivityGroupDTO> findByName(String name);

	/**
	 * Delete the "id" activityGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	List<ActivityDTO> findActivityGroupActivitiesByPid(String pid);

	/**
	 * @author Fahad
	 * 
	 * @since Feb 6, 2017
	 * 
	 *        Update Status of activityGroup by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	ActivityGroupDTO updateActivityGroupActive(String pid, boolean active);

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all activityGroupDTOs from ActivityGroup by status and
	 *        company.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	List<ActivityGroupDTO> findActivityGroupsByUserIsCurrentUserAndActivityGroupActivated(boolean active);

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
	Page<ActivityGroupDTO> findAllByCompanyIdAndActivatedActivityGroupOrderByName(Pageable pageable, boolean active);

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
	List<ActivityGroupDTO> findAllByCompanyIdAndDeactivatedActivityGroup(boolean deactive);

	List<ActivityGroupDTO> findActivityGroupsByUserIsCurrentUserAndActivityGroupActivatedAndLastModifiedDate(
			boolean active, LocalDateTime lastModifiedDate);
}
