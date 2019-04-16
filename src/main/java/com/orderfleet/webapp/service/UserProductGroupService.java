package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;

/**
 * Service Interface for managing UserProductGroup.
 * 
 * @author Sarath
 * @since July 9 2016
 */
public interface UserProductGroupService {

	/**
	 * Save a UserProductGroup.
	 * 
	 * @param userPid
	 * @param assignedProductGroup
	 */
	void save(String userPid, String assignedProductGroup);

	List<ProductGroupDTO> findProductGroupsByUserIsCurrentUser();

	List<ProductGroupDTO> findProductGroupsByUserPid(String userPid);

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
	List<ProductGroupDTO> findProductGroupsByUserIsCurrentUserAndProductGroupsActivated(boolean active);

	List<ProductGroupDTO> findProductGroupsByUserIsCurrentUserAndProductGroupActivatedAndProductGroupLastModifiedDate(
			boolean active, LocalDateTime lastModifiedDate);

	void copyProductGroups(String fromUserPid, List<String> toUserPids);
}
