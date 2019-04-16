package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupDTO;

public interface SalesTargetGroupService {

	String PID_PREFIX = "STGRP-";

	/**
	 * Save a salesTargetGroup.
	 * 
	 * @param salesTargetGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	SalesTargetGroupDTO save(SalesTargetGroupDTO salesTargetGroupDTO);

	/**
	 * Update a salesTargetGroup.
	 * 
	 * @param salesTargetGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	SalesTargetGroupDTO update(SalesTargetGroupDTO salesTargetGroupDTO);

	/**
	 * Get all the salesTargetGroups.
	 * 
	 * @return the list of entities
	 */
	List<SalesTargetGroupDTO> findAllByCompany();

	/**
	 * Get all the salesTargetGroups of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<SalesTargetGroupDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" salesTargetGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	SalesTargetGroupDTO findOne(Long id);

	/**
	 * Get the salesTargetGroup by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<SalesTargetGroupDTO> findOneByPid(String pid);

	/**
	 * Get the salesTargetGroupDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<SalesTargetGroupDTO> findByName(String name);

	/**
	 * Delete the "id" salesTargetGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	List<SalesTargetGroupDTO> findAllByCompanyAndTargetSettingType(BestPerformanceType targetSettingType);
}
