package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.MenuItem;
import com.orderfleet.webapp.domain.UserMenuItem;

/**
 * Spring Data JPA repository for the UserMenuItem entity.
 * 
 * @author Shaheer
 * @since December 27, 2016
 */
public interface UserMenuItemRepository extends JpaRepository<UserMenuItem, Long> {
	
	void deleteByUserPid(String userPid);
	
	void deleteByUserPidIn(List<String> userPids);
	
	//sort integer except for value 0
	@Query("select userMenuItem from UserMenuItem userMenuItem where userMenuItem.user.pid = ?1 Order By userMenuItem.sortOrder asc NULLS LAST")
	List<UserMenuItem> findByUserPid(String userPid);
	
	@Query("select userMenuItem.menuItem from UserMenuItem userMenuItem where userMenuItem.user.login = ?#{principal.username} Order By userMenuItem.sortOrder asc NULLS LAST")
	List<MenuItem> findByUserIsCurrentUser();
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE UserMenuItem umi SET umi.sortOrder = ?1 WHERE umi.id = ?2")
	int updateSortOrderById(Integer sortOrder, Long id);

}
