package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.orderfleet.webapp.domain.UserMobileMenuItemGroup;

/**
 * Spring Data JPA repository for the UserMobileMenuItemGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since Feb 01, 2017
 */
public interface UserMobileMenuItemGroupRepository extends JpaRepository<UserMobileMenuItemGroup, Long> {

	UserMobileMenuItemGroup findByUserLogin(String login);

	UserMobileMenuItemGroup findByUserPid(String login);

	void deleteByUserPidIn(List<String> userPids);

}
