package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.MenuItem;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserMenuItem;
import com.orderfleet.webapp.repository.MenuItemRepository;
import com.orderfleet.webapp.repository.UserMenuItemRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.service.UserMenuItemService;
import com.orderfleet.webapp.web.rest.dto.UserMenuItemDTO;

/**
 * Service Implementation for managing UserMenuItem.
 * 
 * @author Shaheer
 * @since December 28, 2016
 */
@Service
@Transactional
public class UserMenuItemServiceImpl implements UserMenuItemService {

	private final Logger log = LoggerFactory.getLogger(UserMenuItemServiceImpl.class);
	
	@Inject
	private MenuItemRepository menuItemRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private UserMenuItemRepository userMenuItemRepository;

	/**
	 * Save a UserMenuItem.
	 * 
	 * @param userPid
	 * @param assignedMenuItems
	 */
	@Override
	public void save(String userPid, String assignedMenuItems) {
		log.debug("Request to save UserMenuItem");
		
		userRepository.findOneByPid(userPid).ifPresent(user -> {
			String[] menuItems = assignedMenuItems.split(",");
			
			List<UserMenuItem> userMenuItems = new ArrayList<>();
			for (String menuItemId : menuItems) {
				MenuItem menuItem = menuItemRepository.findOne(Long.valueOf(menuItemId));
				userMenuItems.add(new UserMenuItem(user, menuItem));
			}
			userMenuItemRepository.deleteByUserPid(userPid);
			userMenuItemRepository.save(userMenuItems);
		});
	}
	
	/**
	 * Update a UserMenuItem sortorder.
	 * 
	 * @param userPid
	 * @param sortOrders
	 */
	@Override
	public void updateSortOrder(String sortOrders) {
		//split by comma
		String[] arrSortOrders = sortOrders.split(",");
		for(String value : arrSortOrders) {
			String[] sort = value.split(":");
			if(sort.length == 2) {
				userMenuItemRepository.updateSortOrderById(Integer.valueOf(sort[0]), Long.valueOf(sort[1]));
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<MenuItem> findMenuItemsByUserPid(String userPid) {
		log.debug("Request to get all menuitems of a user");
		List<UserMenuItem> userMenuItems = userMenuItemRepository.findByUserPid(userPid);
		List<MenuItem> menuItems = new ArrayList<>();
		for(UserMenuItem userMenuItem : userMenuItems) {
			menuItems.add(userMenuItem.getMenuItem());
		}
		return menuItems;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<UserMenuItemDTO> findUserMenuItemsByUserPid(String userPid) {
		List<UserMenuItem> userMenuItems = userMenuItemRepository.findByUserPid(userPid);
		return userMenuItems.stream().map(UserMenuItemDTO::new).collect(Collectors.toList());
	}

	@Override
	public List<MenuItem> findMenuItemsByCurrentUser() {
		return userMenuItemRepository.findByUserIsCurrentUser();
	}

	@Override
	public void copyMenuItems(String fromUserPid, List<String> toUserPids) {
		//delete association first
		userMenuItemRepository.deleteByUserPidIn(toUserPids);
		List<UserMenuItem> userMenuItems = userMenuItemRepository.findByUserPid(fromUserPid);
		if(userMenuItems != null && !userMenuItems.isEmpty()) {
			List<User> users = userRepository.findByUserPidIn(toUserPids);
			for(User user : users){
				List<UserMenuItem> newUserMenuItems = userMenuItems.stream().map(umi -> {
					UserMenuItem newUserMenuItem = new UserMenuItem();
					newUserMenuItem.setUser(user);
					newUserMenuItem.setMenuItem(umi.getMenuItem());
					newUserMenuItem.setSortOrder(umi.getSortOrder());
					newUserMenuItem.setCompany(umi.getCompany());
					return newUserMenuItem;
				}).collect(Collectors.toList());
				userMenuItemRepository.save(newUserMenuItems);
			}
		}
	}

	@Override
	public String findMenuItemLabelView(String link) {
		List<MenuItem> menuItems = userMenuItemRepository.findByUserIsCurrentUser().stream()
				.filter(menu -> menu.getLink().equals(link) && menu.getMenuItemLabelView() != null 
						&& !menu.getMenuItemLabelView().isEmpty()).collect(Collectors.toList());
		String menuItemLabelView = null;
		if(menuItems != null && !menuItems.isEmpty()) {
			menuItemLabelView = menuItems.get(0).getMenuItemLabelView();
		}
		return menuItemLabelView;
	}
	
	
}
