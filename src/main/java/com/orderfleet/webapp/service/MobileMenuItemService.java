package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.MobileMenuItemDTO;

/**
 * Service for managing MobileMenuItem.
 *
 * @author fahad
 * @since Jun 12, 2017
 */
public interface MobileMenuItemService {

	MobileMenuItemDTO save(MobileMenuItemDTO mobileMenuItemDTO);

	MobileMenuItemDTO update(MobileMenuItemDTO mobileMenuItemDTO);

	MobileMenuItemDTO findOne(Long id);

	void delete(Long id);
	
	List<MobileMenuItemDTO> findAllMobileMenuItem();
	
	Optional<MobileMenuItemDTO> findByName(String name);

	Optional<MobileMenuItemDTO> findByLabel(String label);
}
