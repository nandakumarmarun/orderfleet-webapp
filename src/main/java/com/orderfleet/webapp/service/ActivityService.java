package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDocumentDTO;

/**
 * Service Interface for managing Activity.
 * 
 * @author Muhammed Riyas T
 * @since May 19, 2016
 */
public interface ActivityService {

	String PID_PREFIX = "ACTV-";

	/**
	 * Save a activity.
	 * 
	 * @param activityDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	ActivityDTO save(ActivityDTO activityDTO);

	void saveAssignedAccountTypes(String pid, String assignedAccountTypes);

	void saveAssignedDocuments(String pid, List<ActivityDocumentDTO> assignedDocuments);
	
	void saveAssignedDocumentsWithCompany(String pid, List<ActivityDocumentDTO> assignedDocuments);
	
	void saveAssignedStages(String pid, String[] stagePids);

	/**
	 * Update a activity.
	 * 
	 * @param activityDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	ActivityDTO update(ActivityDTO activityDTO);

	/**
	 * Get all the activities.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<Activity> findAll(Pageable pageable);

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	List<ActivityDTO> findAllByCompany();
	
	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	List<ActivityDTO> findAllByCompanyPid(String companyPid);

	/**
	 * Get all the activities of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ActivityDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" activity.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	ActivityDTO findOne(Long id);

	/**
	 * Get the activity by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<ActivityDTO> findOneByPid(String pid);

	/**
	 * Get the activityDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<ActivityDTO> findByName(String name);

	/**
	 * Delete the "id" activity.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	List<AccountTypeDTO> findActivityAccountTypesByPid(String pid);

	/**
	 * Update the activity status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	ActivityDTO updateActivityStatus(String pid, boolean active);

	/**
	 * @author Fahad
	 * @since Feb 17, 2017
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
	Page<ActivityDTO> findAllByCompanyAndActivatedActivityOrderByName(Pageable pageable, boolean active);

	/**
	 * @author Fahad
	 * @since Feb 17, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	List<ActivityDTO> findAllByCompanyAndDeactivatedActivity(boolean deactive);

	ActivityDTO saveFormSAdmin(ActivityDTO activityDTO);

	Optional<ActivityDTO> findByNameAndCompanyPid(String name, String companyPid);
}
