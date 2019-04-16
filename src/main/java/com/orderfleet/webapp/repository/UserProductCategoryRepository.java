package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.UserProductCategory;

/**
 * Spring Data JPA repository for the UserProductCategory.
 * 
 * @author Sarath
 * @since July 8 2016
 */

public interface UserProductCategoryRepository extends JpaRepository<UserProductCategory, Long> {

	@Query("select userProductCategory.productCategory from UserProductCategory userProductCategory where userProductCategory.user.login = ?#{principal.username}")
	List<ProductCategory> findProductCategorysByUserIsCurrentUser();

	@Query("select userProductCategory.productCategory from UserProductCategory userProductCategory where userProductCategory.user.pid = ?1 ")
	List<ProductCategory> findProductCategorysByUserPid(String userPid);

	void deleteByUserPid(String userPid);
	
	@Query("select userProductCategory.productCategory from UserProductCategory userProductCategory where userProductCategory.user.login = ?#{principal.username} and userProductCategory.productCategory.activated = ?1 ")
	List<ProductCategory> findProductCategorysByUserIsCurrentUserAndProductCategoryActivated(boolean active);

	@Query("select userProductCategory.productCategory from UserProductCategory userProductCategory where userProductCategory.user.login = ?#{principal.username} and userProductCategory.productCategory.activated = ?1 and userProductCategory.productCategory.lastModifiedDate > ?2")
	List<ProductCategory> findByUserIsCurrentUserAndProductCategoryActivatedAndLastModifiedDate(boolean active,LocalDateTime lastModifiedDate);

	void deleteByUserPidIn(List<String> toUserPids);

	List<UserProductCategory> findByUserPid(String fromUserPid);
	
	List<String> findUserPidByCompanyPid(String pid);
}
