package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.MobileMenuItemGroupMenuItem;

/**
 * Spring Data JPA repository for the MobileMenuItemGroupMenuItem entity.
 * 
 * @author Muhammed Riyas T
 * @since Feb 01, 2017
 */
public interface MobileMenuItemGroupMenuItemRepository extends JpaRepository<MobileMenuItemGroupMenuItem, Long> {

	List<MobileMenuItemGroupMenuItem> findAllByMobileMenuItemGroupPid(String menuItemGroupPid);

	@Transactional
	void deleteByMobileMenuItemGroupPid(String mobileMenuItemGroupPid);
}
