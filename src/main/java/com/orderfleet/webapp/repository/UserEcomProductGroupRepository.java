package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.EcomProductGroup;
import com.orderfleet.webapp.domain.UserEcomProductGroup;

/**
 * Spring Data JPA repository for the UserEcomProductGroup.
 * 
 * @author Anish
 * @since June 9 2020
 */

public interface UserEcomProductGroupRepository extends JpaRepository<UserEcomProductGroup, Long> {

	@Query("select userProductGroup.productGroup from UserEcomProductGroup userProductGroup where userProductGroup.user.login = ?#{principal.username} and userProductGroup.company.id = ?#{principal.companyId}")
	List<EcomProductGroup> findProductGroupsByUserIsCurrentUser();

	@Query("select userProductGroup.productGroup from UserEcomProductGroup userProductGroup where userProductGroup.user.pid = ?1 ")
	List<EcomProductGroup> findProductGroupsByUserPid(String userPid);
	
	List<UserEcomProductGroup> findByUserPid(String userPid);

	void deleteByUserPid(String userPid);
	
	@Query("select userProductGroup.productGroup from UserEcomProductGroup userProductGroup where userProductGroup.user.login = ?#{principal.username} and userProductGroup.productGroup.activated = ?1 and userProductGroup.company.id = ?#{principal.companyId}")
	List<EcomProductGroup> findProductGroupsByUserIsCurrentUserAndProductGroupsActivated(boolean active);

	@Query("select userProductGroup.productGroup from UserEcomProductGroup userProductGroup where userProductGroup.user.login = ?#{principal.username} and userProductGroup.productGroup.activated = ?1 and userProductGroup.productGroup.lastModifiedDate > ?2 and userProductGroup.company.id = ?#{principal.companyId}")
	List<EcomProductGroup> findProductGroupsByUserIsCurrentUserAndProductGroupActivatedAndProductGroupLastModifiedDate(boolean active,LocalDateTime lastModifiedDate);

	void deleteByUserPidIn(List<String> userPids);
	
	@Query("select userProductGroup from UserEcomProductGroup userProductGroup where userProductGroup.productGroup.pid IN ?1")
	List<UserEcomProductGroup> findByProductGroupPids(List<String> productGroupPids);
	
	List<String> findUserPidByCompanyPid(String pid);

}
