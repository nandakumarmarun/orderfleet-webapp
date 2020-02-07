package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.MenuItem;
import com.orderfleet.webapp.repository.MenuItemRepository;
import com.orderfleet.webapp.service.MenuItemService;
import com.orderfleet.webapp.web.rest.dto.MenuItemDTO;

/**
 * Service Implementation for managing menuItem.
 *
 * @author Sarath
 * @since Dec 27, 2016
 */

@Transactional
@Service
public class MenuItemServiceImpl implements MenuItemService {

	private final Logger log = LoggerFactory.getLogger(MenuItemServiceImpl.class);

	@Inject
	private MenuItemRepository menuItemRepository;

	@Override
	public MenuItemDTO save(MenuItemDTO menuItemDTO) {
		log.debug("Request to save MenuItem : {}", menuItemDTO);
		MenuItem menuItem = new MenuItem();
		if (menuItemDTO.getParentId() != null) {
			MenuItem parent = menuItemRepository.findOne(menuItemDTO.getParentId());
			menuItem.setParent(parent);
		}
		menuItem.setLabel(menuItemDTO.getLabel());
		menuItem.setLink(menuItemDTO.getLink());
		menuItem.setDescription(menuItemDTO.getDescription());
		menuItem.setIconClass(menuItemDTO.getIconClass());
		menuItem.setMenuItemLabelView(menuItemDTO.getMenuItemLabelView());

		menuItem = menuItemRepository.save(menuItem);
		menuItemDTO = new MenuItemDTO(menuItem);
		return menuItemDTO;
	}

	@Override
	public MenuItemDTO update(MenuItemDTO menuItemDTO) {
		log.debug("Request to update MenuItem : {}", menuItemDTO);
		MenuItem menuItem = menuItemRepository.findOne(menuItemDTO.getId());
		if (menuItemDTO.getParentId() != null) {
			MenuItem parent = menuItemRepository.findOne(menuItemDTO.getParentId());
			menuItem.setParent(parent);
		}
		menuItem.setLabel(menuItemDTO.getLabel());
		menuItem.setLink(menuItemDTO.getLink());
		menuItem.setDescription(menuItemDTO.getDescription());
		menuItem.setIconClass(menuItemDTO.getIconClass());
		menuItem.setMenuItemLabelView(menuItemDTO.getMenuItemLabelView());
		menuItem = menuItemRepository.save(menuItem);
		menuItemDTO = new MenuItemDTO(menuItem);
		return menuItemDTO;
	}

	@Override
	public MenuItemDTO findOne(Long id) {
		log.debug("Request to find One MenuItem : {}", id);
		MenuItem menuItem = menuItemRepository.findOne(id);
		MenuItemDTO menuItemDTO = new MenuItemDTO(menuItem);
		return menuItemDTO;
	}

	@Override
	public void delete(Long id) {
		log.debug("Request to delete MenuItem : {}", id);
		MenuItem menuItem = menuItemRepository.findOne(id);
		if (menuItem != null) {
			menuItemRepository.delete(menuItem.getId());
		}
	}

	@Override
	public List<MenuItemDTO> findByActivatedTrue() {
		log.debug("Request to get all activated true MenuItem : {}");
		List<MenuItemDTO> menuItemDTOs = new ArrayList<>();
		List<MenuItem> menuItems = menuItemRepository.findByActivatedTrue();
		for (MenuItem menuItem : menuItems) {
			MenuItemDTO menuItemDTO = new MenuItemDTO(menuItem);
			menuItemDTOs.add(menuItemDTO);
		}
		return menuItemDTOs;
	}

}
