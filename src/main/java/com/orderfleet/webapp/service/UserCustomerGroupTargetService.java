package com.orderfleet.webapp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.UserCustomerGroupTarget;
import com.orderfleet.webapp.web.rest.dto.UserCustomerGroupTargetDTO;
import com.orderfleet.webapp.web.rest.dto.MonthlyCustomerGroupTargetDTO;
import com.orderfleet.webapp.web.rest.dto.MonthlyTargetDTO;

/**
 * Service Interface for managing UserCustomerGroupTarget.
 * 
 * @author Muhammed Riyas T
 * @since June 15, 2016
 */
public interface UserCustomerGroupTargetService {

	String PID_PREFIX = "ACUT-";

	/**
	 * Save a userCustomerGroupTarget.
	 * 
	 * @param userCustomerGroupTargetDTO the entity to save
	 * @return the persisted entity
	 */
	UserCustomerGroupTargetDTO save(UserCustomerGroupTargetDTO userCustomerGroupTargetDTO);

	UserCustomerGroupTargetDTO saveMonthlyTarget(MonthlyCustomerGroupTargetDTO monthlyTargetDTO, LocalDate startDate,
			LocalDate endDate);

	/**
	 * Update a userCustomerGroupTarget.
	 * 
	 * @param userCustomerGroupTargetDTO the entity to update
	 * @return the persisted entity
	 */
	UserCustomerGroupTargetDTO update(UserCustomerGroupTargetDTO userCustomerGroupTargetDTO);


	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	List<UserCustomerGroupTargetDTO> findAllByCompany();

	/**
	 * Get all the activities of a company.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	Page<UserCustomerGroupTargetDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" userCustomerGroupTarget.
	 * 
	 * @param id the id of the entity
	 * @return the entity
	 */
	UserCustomerGroupTargetDTO findOne(Long id);

	/**
	 * Get the userCustomerGroupTarget by "pid".
	 * 
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	Optional<UserCustomerGroupTargetDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" userCustomerGroupTarget.
	 * 
	 * @param id the id of the entity
	 */
	void delete(String pid);

}
