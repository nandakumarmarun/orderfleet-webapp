package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.Department;
import com.orderfleet.webapp.web.rest.dto.DepartmentDTO;

/**
 * Service Interface for managing Department.
 * 
 * @author Muhammed Riyas T
 * @since May 24, 2016
 */
public interface DepartmentService {

	String PID_PREFIX = "DEP-";

	/**
	 * Save a department.
	 * 
	 * @param departmentDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	DepartmentDTO save(DepartmentDTO departmentDTO);

	/**
	 * Update a department.
	 * 
	 * @param departmentDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	DepartmentDTO update(DepartmentDTO departmentDTO);

	/**
	 * Get all the departments.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<Department> findAll(Pageable pageable);

	/**
	 * Get all the departments.
	 * 
	 * @return the list of entities
	 */
	List<DepartmentDTO> findAllByCompany();

	/**
	 * Get all the departments of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<DepartmentDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" department.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	DepartmentDTO findOne(Long id);

	/**
	 * Get the department by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<DepartmentDTO> findOneByPid(String pid);

	/**
	 * Get the departmentDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<DepartmentDTO> findByName(String name);

	/**
	 * Delete the "id" department.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);
	
	/**
	 * Update  the Department status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 *@param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	DepartmentDTO updateDepartmentStatus(String pid,boolean activate);
	
	/**
	 *  @author Fahad
	 * @since Feb 14, 2017
	 * 
	 * find all active company 
	 * 
	 *@param active
	 *            the active of the entity
	 * @return the entity
	 */
	Page<DepartmentDTO> findAllByCompanyAndActivatedDepartmentOrderByName(Pageable pageable,boolean active);
	
	 /**
		 *  @author Fahad
		 * @since Feb 14, 2017
		 * 
		 * find all deactive company 
		 * 
		 *@param deactive
		 *            the deactive of the entity
		 * @return the list
		 */
	List<DepartmentDTO> findAllByCompanyAndDeactivatedDepartment(boolean deactive);
}
