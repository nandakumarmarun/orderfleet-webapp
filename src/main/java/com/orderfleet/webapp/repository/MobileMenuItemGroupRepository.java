package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.MobileMenuItemGroup;

/**
 * Spring Data JPA repository for the MobileMenuItemGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since Feb 01, 2017
 */
public interface MobileMenuItemGroupRepository extends JpaRepository<MobileMenuItemGroup, Long> {

	Optional<MobileMenuItemGroup> findByNameIgnoreCase(String name);

	Optional<MobileMenuItemGroup> findOneByPid(String pid);

	Optional<MobileMenuItemGroup> findByAlias(String alias);
}
