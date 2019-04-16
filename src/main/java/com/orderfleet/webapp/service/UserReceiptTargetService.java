package com.orderfleet.webapp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.UserReceiptTarget;
import com.orderfleet.webapp.web.rest.dto.UserReceiptTargetDTO;

/**
 * Service Interface for managing UserReceiptTarget.
 * 
 * @author Sarath
 * @since Aug 12, 2016
 */
public interface UserReceiptTargetService {

	String PID_PREFIX = "URETGT-";

	/**
	 * Save a userReceiptTarget.
	 * 
	 * @param userReceiptTargetDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	UserReceiptTargetDTO save(UserReceiptTargetDTO userReceiptTargetDTO);

	/**
	 * Update a userReceiptTarget.
	 * 
	 * @param userReceiptTargetDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	UserReceiptTargetDTO update(UserReceiptTargetDTO userReceiptTargetDTO);

	/**
	 * Get all the userReceiptTargets.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<UserReceiptTarget> findAll(Pageable pageable);

	/**
	 * Get all the userReceiptTargets.
	 * 
	 * @return the list of entities
	 */
	List<UserReceiptTargetDTO> findAllByCompany();

	/**
	 * Get all the userReceiptTargets of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<UserReceiptTargetDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "date" userReceiptTarget.
	 * 
	 * @param date
	 *            the date of the entity
	 * @return the entity
	 */
	UserReceiptTargetDTO findOneByCurrentUserAndDate(LocalDate date);

	/**
	 * Get the "id" userReceiptTarget.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	UserReceiptTargetDTO findOne(Long id);

	/**
	 * Get the userReceiptTarget by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<UserReceiptTargetDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" userReceiptTarget.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	List<UserReceiptTarget> findUserAndDateWiseDuplicate(String userPid, LocalDate startDate, LocalDate endDate);

}
