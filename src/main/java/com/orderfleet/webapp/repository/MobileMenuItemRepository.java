package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.MobileMenuItem;

/**
 * Spring Data JPA repository for the MobileMenuItem entity.
 * 
 * @author Muhammed Riyas T
 * @since Feb 01, 2017
 */
public interface MobileMenuItemRepository extends JpaRepository<MobileMenuItem, Long> {

	Optional<MobileMenuItem> findByNameIgnoreCase(String name);
	
	Optional<MobileMenuItem> findByLabelIgnoreCase(String name);
}
