package com.orderfleet.webapp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.web.rest.dto.ActivityDTO;

/**
 * Service Interface for managing DashboardActivity.
 *
 * @author Sarath
 * @since Oct 27, 2016
 */
public interface DashboardActivityService {

	/**
	 * Save a dashboardActivity.
	 * 
	 * @param activitiesPids
	 *            the entity to save
	 * @return the persisted entity
	 */
	void save(List<String> activitiesPids);

	/**
	 * Get all the dashboardActivities.
	 * 
	 * @return the list of entities
	 */
	List<ActivityDTO> findActivitiesByCompanyId();

	/**
	 * Get all the dashboardActivities of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ActivityDTO> findActivitiesByCompanyId(Pageable pageable);

	Long countByCompanyId();

}
