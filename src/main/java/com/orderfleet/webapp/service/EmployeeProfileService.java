package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;

/**
 * Service Interface for managing EmployeeProfile.
 * 
 * @author Shaheer
 * @since June 06, 2016
 */
public interface EmployeeProfileService {

	String PID_PREFIX = "EMPP-";

	/**
	 * Save a employeeProfile.
	 * 
	 * @param employeeProfileDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	EmployeeProfileDTO save(EmployeeProfileDTO employeeProfileDTO);

	/**
	 * Update a employeeProfile.
	 * 
	 * @param employeeProfileDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	EmployeeProfileDTO update(EmployeeProfileDTO employeeProfileDTO);

	
	EmployeeProfile updateEmployee(EmployeeProfile employeeProfile);
	/**
	 * Get all the employeeProfiles.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<EmployeeProfileDTO> findAll(Pageable pageable);

	/**
	 * Get all the employeeProfiles of a company.
	 * 
	 * @return the list of entities
	 */
	List<EmployeeProfileDTO> findAll();

	Page<EmployeeProfileDTO> findAllByCompany(Pageable pageable);

	List<EmployeeProfileDTO> findAllByCompany();

	/**
	 * Get the "id" employeeProfile.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	EmployeeProfileDTO findOne(Long id);

	/**
	 * Get the "pid" employeeProfile.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<EmployeeProfileDTO> findOneByPid(String pid);

	/**
	 * Get the "name" employeeProfileDTO.
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<EmployeeProfileDTO> findByName(String name);

	/**
	 * Delete the "pid" employeeProfile.
	 * 
	 * @param pid
	 *            the pid of the entity
	 */
	void delete(String pid);

	/**
	 * Delete the "id" employeeProfile.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(Long id);

	Optional<EmployeeProfileDTO> saveEmployeeUser(String employeePid, String userPid);

	/**
	 * Get the "userPid" employeeProfileDTO.
	 * 
	 * @param userPid
	 *            the userPid of the entity
	 * @return the entity
	 */
	Optional<EmployeeProfile> findByUserPid(String userPid);
	
	EmployeeProfileDTO findDtoByUserPid(String userPid);

	EmployeeProfileDTO findtCurrentUserEmployeeProfileImage();

	EmployeeProfileDTO findEmployeeProfileByUserLogin(String userLogin);

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        Update the EmployeeProfile status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	EmployeeProfileDTO updateEmployeeProfileStatus(String pid, boolean activate);

	/**
	 * get the EmployeeProfile order by name.
	 * 
	 * @param pageable
	 *            the pageable of the entity
	 * @return the Page of entity
	 */
	public Page<EmployeeProfileDTO> findAllByCompanyOrderByEmployeeProfileName(Pageable pageable);

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
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
	Page<EmployeeProfileDTO> findAllByCompanyAndActivatedEmployeeProfileOrderByName(Pageable pageable, boolean active);

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
	List<EmployeeProfileDTO> findAllByCompanyAndDeactivatedEmployeeProfile(boolean deactive);

	void copyEmployeeUser(String fromUserPid, List<String> toUserPids);
	
	List<EmployeeProfileDTO> findAllEmployeeByUserIdsIn(List<Long> userPids);
	
	List<EmployeeProfileDTO> findAllEmployeeByUserIdsInAndActivated(List<Long> userPids);

	List<EmployeeProfileDTO> findAllByCompanyIdNotInUserIds(boolean activate,List<Long> toUserIds);
	
	List<EmployeeProfileDTO> findByEmployeeByUserPidIn(List<String> userPids);
	
}
