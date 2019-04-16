package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.MenuItemDTO;

/**
 * Spring Data JPA repository for the MenuItem entity.
 *
 * @author Sarath
 * @since Dec 27, 2016
 */
public interface MenuItemService {

	MenuItemDTO save(MenuItemDTO bankDTO);

	MenuItemDTO update(MenuItemDTO bankDTO);

	MenuItemDTO findOne(Long id);

	void delete(Long id);

	List<MenuItemDTO> findByActivatedTrue();

}
