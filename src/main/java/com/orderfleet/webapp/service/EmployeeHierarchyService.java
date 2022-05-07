package com.orderfleet.webapp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.web.rest.dto.EmployeeHierarchyDTO;

/**
 * Service Interface for managing EmployeeHierarchy.
 * 
 * @author Shaheer
 * @since June 02, 2016
 */
public interface EmployeeHierarchyService {

	/**
	 * Save a employeeHierarchy.
	 * 
	 * @param employeeHierarchyDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	EmployeeHierarchyDTO save(EmployeeHierarchyDTO employeeHierarchyDTO);

	/**
	 * Save a employeeHierarchy.
	 * 
	 * @param employeePid
	 * @param parentPid
	 * @return the persisted entity
	 */
	public EmployeeHierarchyDTO save(String employeePid, String parentPid);
	
	/**
	 * Save a employeeHierarchyRoot.
	 * 
	 * @param employeePid
	 * @param parentPid
	 * @return the persisted entity
	 */
	public EmployeeHierarchyDTO saveRoot(String employeePid, String parentPid);

	/**
	 * update a employeeHierarchy.
	 * 
	 * @param employeeHierarchyDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	EmployeeHierarchyDTO update(String employeePid, String parentPid);

	/**
	 * Get all the employeeHierarchies.
	 * 
	 * @return the list of entities
	 */
	List<EmployeeHierarchyDTO> findAllByCompanyAndActivatedTrue();

	/**
	 * Get all the employeeHierarchies.
	 * 
	 * @return the list of entities
	 */
	Page<EmployeeHierarchyDTO> findAllByCompanyAndActivatedTrue(Pageable pageable);

	/**
	 * Get the "id" employeeHierarchy.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	EmployeeHierarchyDTO findOne(Long id);

	Long countByParentIdAndActivatedTrue(String parentId);

	EmployeeHierarchyDTO findOneByEmployeeIdAndActivatedTrue(String employeePid);

	void inactivateEmployeeHierarchy(String employeePid);
	
	List<Long> getCurrentUsersSubordinateIds();
	
	List<Long> getEmployeeSubordinateIds(String employeePid);


	/**
	 * Delete the "id" employeeHierarchy.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(Long id);
	
	EmployeeHierarchyDTO findEmployeeHierarchyRootByCompany();
	
	void deleteByEmployeePid(String employeePid);
	
	
	List<Long> getEmployeeSubordinateEmployeeIds(String employeePid);

	List<Long> getCurrentUsersSubordinateIdsForLiveTracking();
}
