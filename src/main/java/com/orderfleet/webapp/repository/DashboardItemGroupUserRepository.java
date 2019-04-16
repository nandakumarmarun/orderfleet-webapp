package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DashboardItemGroup;
import com.orderfleet.webapp.domain.DashboardItemGroupUser;

public interface DashboardItemGroupUserRepository extends JpaRepository<DashboardItemGroupUser, Long>{
	
	@Query("select digUser from DashboardItemGroupUser digUser where digUser.company.id = ?#{principal.companyId}")
	List<DashboardItemGroupUser> findAllByCompanyId();

	@Query("select digUser.user.pid from DashboardItemGroupUser digUser where digUser.dashboardItemGroup.id = ?1 ")
	List<String> findUserPidsByDashboardItemGroupId(Long dashboardItemGroupId);
	
	@Query("select digUser.user.pid, digUser.user.login from DashboardItemGroupUser digUser where digUser.dashboardItemGroup.id = ?1")
	List<Object[]> findUserByDashboardItemGroupIdAndUserIdIn(Long dashboardItemGroupId);
	
	@Query("select digUser.user.pid, digUser.user.login from DashboardItemGroupUser digUser where digUser.dashboardItemGroup.id = ?1 and digUser.user.id in ?2")
	List<Object[]> findUserByDashboardItemGroupIdAndUserIdIn(Long dashboardItemGroupId, List<Long> userIds);
	
	@Query("select digUser.dashboardItemGroup from DashboardItemGroupUser digUser where digUser.user.login = ?1 order by digUser.dashboardItemGroup.sortOrder")
	List<DashboardItemGroup> findDashboardItemByUserLogin(String login);
	
	void deleteByDashboardItemGroupId(Long dashboardItemGroupId);
}
