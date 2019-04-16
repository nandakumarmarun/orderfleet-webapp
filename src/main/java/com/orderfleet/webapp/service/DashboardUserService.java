package com.orderfleet.webapp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.web.rest.api.dto.UserDTO;

/**
 * Service Interface for managing DashboardUser.
 * 
 * @author Muhammed Riyas T
 * @since August 23, 2016
 */
public interface DashboardUserService {

	/**
	 * Save a dashboardUser.
	 * 
	 * @param usersPids
	 *            the entity to save
	 * @return the persisted entity
	 */
	void save(List<String> usersPids);

	/**
	 * Get all the dashboardUsers.
	 * 
	 * @return the list of entities
	 */
	List<UserDTO> findUsersByCompanyId();
	
	List<UserDTO> findUsersByUserIdIn(List<Long> userIds);
	
	/**
	 * Get all the dashboardUsers of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<UserDTO> findUsersByCompanyId(Pageable pageable);

	Long countByCompanyId();

}
