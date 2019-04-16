package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.RootPlanSubgroupApproveDTO;

/**
 * Service  for managing RootPlanSubgroupApprove.
 * 
 * @author fahad
 * @since Aug 28, 2017
 */
public interface RootPlanSubgroupApproveService {

	/**
	 * Save a rootPlanSubgroupApprove.
	 * 
	 * @param rootPlanSubgroupApproveDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	RootPlanSubgroupApproveDTO save(RootPlanSubgroupApproveDTO rootPlanSubgroupApproveDTO);
	
	/**
	 * Get all the rootPlanSubgroupApproves of a company.
	 * 
	 * @return the list of entities
	 */
	List<RootPlanSubgroupApproveDTO> findAllByCompany();
	
	/**
	 * Update a rootPlanSubgroupApprove.
	 * 
	 * @param rootPlanSubgroupApproveDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	RootPlanSubgroupApproveDTO update(RootPlanSubgroupApproveDTO rootPlanSubgroupApproveDTO);
	
	/**
	 * Get the "id" rootPlanSubgroupApprove.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	RootPlanSubgroupApproveDTO findOneId(Long id);
	
	/**
	 * Delete the "id" rootPlanSubgroupApprove.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(Long pid);
	
	Optional<RootPlanSubgroupApproveDTO> findOneByUserPidAndId(String userPid,Long id);
	
	Optional<RootPlanSubgroupApproveDTO> findOneByUserPidAndAttendanceStatusSubgroupId(String userPid,Long subgroupId);

	List<RootPlanSubgroupApproveDTO> findAllByUserLogin();
	
}
