package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.domain.MenuItem;
import com.orderfleet.webapp.web.rest.dto.UserMenuItemDTO;

/**
 * Service Interface for managing UserMenuItem.
 * 
 * @author Shaheer
 * @since December 28, 2016
 */
public interface UserMenuItemService {

	void save(String userPid, String assignedMenuItems);
	
	void updateSortOrder(String sortOrders);
	
	List<MenuItem> findMenuItemsByUserPid(String userPid);
	
	List<UserMenuItemDTO> findUserMenuItemsByUserPid(String userPid);
	
	List<MenuItem> findMenuItemsByCurrentUser();
	
	void copyMenuItems(String fromUserPid, List<String> toUserPids);
	
}
