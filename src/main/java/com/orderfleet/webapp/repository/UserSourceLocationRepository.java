package com.orderfleet.webapp.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.UserSourceLocation;

public interface UserSourceLocationRepository extends JpaRepository<UserSourceLocation, Long>{

	@Query("select userSourceLocation from UserSourceLocation userSourceLocation where userSourceLocation.user.pid = ?1 ")
	UserSourceLocation findSourceLocationsByUserPid(String userPid);
	
	void deleteByUserPid(String userPid);
}
