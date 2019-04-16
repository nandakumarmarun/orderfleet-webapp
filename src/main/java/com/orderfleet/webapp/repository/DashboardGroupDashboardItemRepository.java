package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DashboardGroupDashboardItem;
import com.orderfleet.webapp.domain.DashboardItem;

public interface DashboardGroupDashboardItemRepository extends JpaRepository<DashboardGroupDashboardItem, Long> {

	@Query("select dgdi from DashboardGroupDashboardItem dgdi where dgdi.companyId = ?#{principal.companyId} order by dgdi.dashboardItemGroup ASC")
	List<DashboardGroupDashboardItem> findAllByCompanyId();
	
	@Query("select dgdi.dashboardItem from DashboardGroupDashboardItem dgdi where dgdi.companyId = ?#{principal.companyId} and dgdi.dashboardItemGroup.id = ?1 order by dgdi.dashboardItem.sortOrder ASC")
	List<DashboardItem> findDashboardItemByDashboardGroupId(Long dashboardGroupId);
	
	void deleteByDashboardItemGroupId(Long id);
}
