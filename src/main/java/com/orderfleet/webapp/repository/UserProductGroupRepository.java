package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.UserProductGroup;

/**
 * Spring Data JPA repository for the UserProductGroup.
 * 
 * @author Sarath
 * @since July 9 2016
 */

public interface UserProductGroupRepository extends JpaRepository<UserProductGroup, Long> {

	@Query("select userProductGroup.productGroup from UserProductGroup userProductGroup where userProductGroup.user.login = ?#{principal.username} and userProductGroup.company.id = ?#{principal.companyId}")
	List<ProductGroup> findProductGroupsByUserIsCurrentUser();

	@Query("select userProductGroup.productGroup from UserProductGroup userProductGroup where userProductGroup.user.pid = ?1 ")
	List<ProductGroup> findProductGroupsByUserPid(String userPid);

	List<UserProductGroup> findByUserPid(String userPid);

	void deleteByUserPid(String userPid);

	@Query("select userProductGroup.productGroup from UserProductGroup userProductGroup where userProductGroup.user.login = ?#{principal.username} and userProductGroup.productGroup.activated = ?1 and userProductGroup.company.id = ?#{principal.companyId}")
	List<ProductGroup> findProductGroupsByUserIsCurrentUserAndProductGroupsActivated(boolean active);

	@Query("select userProductGroup.productGroup from UserProductGroup userProductGroup where userProductGroup.user.login = ?#{principal.username} and userProductGroup.productGroup.activated = ?1 and userProductGroup.productGroup.lastModifiedDate > ?2 and userProductGroup.company.id = ?#{principal.companyId}")
	List<ProductGroup> findProductGroupsByUserIsCurrentUserAndProductGroupActivatedAndProductGroupLastModifiedDate(
			boolean active, LocalDateTime lastModifiedDate);

	void deleteByUserPidIn(List<String> userPids);

	@Query("select userProductGroup from UserProductGroup userProductGroup where userProductGroup.productGroup.pid IN ?1")
	List<UserProductGroup> findByProductGroupPids(List<String> productGroupPids);

	List<String> findUserPidByCompanyPid(String pid);

	@Query("select userProductGroup.productGroup.pid from UserProductGroup userProductGroup where userProductGroup.user.pid = ?1 and userProductGroup.company.id = ?#{principal.companyId}")
	List<String> findProductGroupPidByUserPid(String userPid);

}
