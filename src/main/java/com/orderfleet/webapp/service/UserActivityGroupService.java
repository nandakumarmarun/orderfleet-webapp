package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.ActivityGroupDTO;

/**
 * Service Interface for managing UserActivityGroup.
 * 
 * @author Muhammed Riyas T
 * @since June 29, 2016
 */
public interface UserActivityGroupService {

	/**
	 * Save a UserActivityGroup.
	 * 
	 * @param userPid
	 * @param assignedActivityGroups
	 */
	void save(String userPid, String assignedActivityGroups);

	List<ActivityGroupDTO> findActivityGroupsByUserIsCurrentUser();

	List<ActivityGroupDTO> findActivityGroupsByUserPid(String userPid);

}
