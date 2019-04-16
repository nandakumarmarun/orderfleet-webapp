package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DashboardAttendanceUser;
import com.orderfleet.webapp.domain.DashboardAttendance;
import com.orderfleet.webapp.domain.User;

public interface DashboardAttendanceUserRepository extends JpaRepository<DashboardAttendanceUser, Long>{
	
	@Query("select dashboardAttendanceUser.user from DashboardAttendanceUser dashboardAttendanceUser where dashboardAttendanceUser.dashboardAttendance.id = ?1 ")
	List<User> findUsersByDashboardAttendanceId(Long dashboardAttendanceId);
	
	@Query("select dashboardAttendanceUser.dashboardAttendance from DashboardAttendanceUser dashboardAttendanceUser where dashboardAttendanceUser.user.login = ?1 ")
	List<DashboardAttendance> findDashboardAttendanceByUserLogin(String login);
	
	void deleteByDashboardAttendanceId(Long dashboardAttendanceId);
}
