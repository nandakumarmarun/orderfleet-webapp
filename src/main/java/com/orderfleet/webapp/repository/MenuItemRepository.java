package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.MenuItem;

/**
 * Spring Data JPA repository for the MenuItem entity.
 * 
 * @author Shaheer
 * @since December 24, 2016
 */
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
	
	@Query("select menuItem from MenuItem menuItem where menuItem.activated = true Order By menuItem.id asc")
	List<MenuItem> findByActivatedTrue();
	
	@Query("select menuItem from MenuItem menuItem where menuItem.activated = true and menuItem.id in ?1 Order By menuItem.id asc")
	List<MenuItem> findByMenuItemIdsIn(List<Long> menuIds);
	
}
