package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.MobileMenuItemDTO;
import com.orderfleet.webapp.web.rest.dto.MobileMenuItemGroupDTO;

/**
 * Service Interface for managing AccountType.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
public interface UserMobileMenuItemGroupService {

	void save(String userPid, String menuGroupPid);

	MobileMenuItemGroupDTO findUserMobileMenuItemGroupByUserPid(String userPid);

	/**
	 * Get user mobile MenuItems.
	 * 
	 * @return the list of entities
	 */
	List<MobileMenuItemDTO> findCurrentUserMenuItems();
	
	void copyMobileMenuItemGroup(String fromUserPid, List<String> toUserPids);

}
