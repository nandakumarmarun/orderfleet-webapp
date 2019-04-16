package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DashboardItem;
import com.orderfleet.webapp.domain.DashboardItemUser;
import com.orderfleet.webapp.domain.User;

public interface DashboardItemUserRepository extends JpaRepository<DashboardItemUser, Long>{

	@Query("select dashboardItemUser.user from DashboardItemUser dashboardItemUser where dashboardItemUser.dashboardItem.pid = ?1 ")
	List<User> findUsersByDashboardItemPid(String dashboardItemPid);
	
	@Query("select dashboardItemUser.dashboardItem from DashboardItemUser dashboardItemUser where dashboardItemUser.user.login = ?1 order by dashboardItemUser.dashboardItem.sortOrder")
	List<DashboardItem> findDashboardItemByUserLogin(String login);
	
	void deleteByDashboardItemPid(String dashboardItemPid);
}
