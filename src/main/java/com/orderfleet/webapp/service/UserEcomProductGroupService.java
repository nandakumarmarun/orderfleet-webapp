package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;

/**
 * Service Interface for managing UserProductGroup.
 * 
 * @author Anish
 * @since June 9 2020
 */
public interface UserEcomProductGroupService {

	/**
	 * Save a UserProductGroup.
	 * 
	 * @param userPid
	 * @param assignedProductGroup
	 */
	void save(String userPid, String assignedProductGroup);

	List<EcomProductGroupDTO> findProductGroupsByUserIsCurrentUser();

	List<EcomProductGroupDTO> findProductGroupsByUserPid(String userPid);

	/**
	 * @author Fahad
	 * @since Feb 9, 2017
	 * 
	 *        find all productGroupDTO from ProductGroup by status and user.
	 * 
	 * @param active
	 *            the active of the entity
	 * 
	 */
	List<EcomProductGroupDTO> findProductGroupsByUserIsCurrentUserAndProductGroupsActivated(boolean active);

	List<EcomProductGroupDTO> findProductGroupsByUserIsCurrentUserAndProductGroupActivatedAndProductGroupLastModifiedDate(
			boolean active, LocalDateTime lastModifiedDate);

	void copyProductGroups(String fromUserPid, List<String> toUserPids);
}
