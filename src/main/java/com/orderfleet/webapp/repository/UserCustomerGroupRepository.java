package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.domain.UserCustomerGroup;

/**
 * Spring Data JPA repository for the UserCustomerGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since July 01, 2016
 */
public interface UserCustomerGroupRepository extends JpaRepository<UserCustomerGroup, Long> {

	@Transactional
	@Modifying
	@Query("delete from UserCustomerGroup u where u.user.pid = ?1")
	void deleteUserCustomerGroupsByUserPid(String userPid);

	@Query("select userCustomerGroup from UserCustomerGroup userCustomerGroup where userCustomerGroup.user.pid = ?1 ")
	List<UserCustomerGroup> findUserCustomerGroupsByUserPid(String userPid);

	@Transactional
	@Modifying
	@Query(value = "delete from tbl_user_customer_group userCustomerGroup where userCustomerGroup.user.pid = ?1", nativeQuery = true)
	void deleteByUserPid(String userPid);
	
	@Transactional
	@Modifying
	@Query("delete from UserCustomerGroup userCustomerGroup where userCustomerGroup.id in ?1")
	void deleteByIdIn(List<Long> ids);

}
