package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.UserRestrictedAttendanceSubgroup;

public interface UserRestrictedAttendanceSubgroupRepository extends JpaRepository<UserRestrictedAttendanceSubgroup, Long>{

	void deleteByUserPid(String userPid);
	
	@Query("select userRestrictedAttendanceSubgroup from UserRestrictedAttendanceSubgroup userRestrictedAttendanceSubgroup where userRestrictedAttendanceSubgroup.company.id = ?#{principal.companyId} and userRestrictedAttendanceSubgroup.user.pid = ?1")
	List<UserRestrictedAttendanceSubgroup> findByUserPid(String userPid);
	
	@Query("select userRestrictedAttendanceSubgroup from UserRestrictedAttendanceSubgroup userRestrictedAttendanceSubgroup where userRestrictedAttendanceSubgroup.company.id = ?#{principal.companyId} and userRestrictedAttendanceSubgroup.user.login=?1")
	List<UserRestrictedAttendanceSubgroup> findAllByUserLogin(String userLogin);
}
