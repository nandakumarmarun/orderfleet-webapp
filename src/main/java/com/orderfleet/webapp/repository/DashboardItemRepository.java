package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DashboardItem;
import com.orderfleet.webapp.domain.enums.DashboardItemConfigType;

/**
 * Spring Data JPA repository for the DashboardItem entity.
 * 
 * @author Muhammed Riyas T
 * @since Jan 18, 2017
 */
public interface DashboardItemRepository extends JpaRepository<DashboardItem, Long> {

	Optional<DashboardItem> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<DashboardItem> findOneByPid(String pid);
	
	List<DashboardItem> findByPidIn(List<String> pids);
	
	DashboardItem findTopByCompanyIdOrderBySortOrderAsc(Long id);

	@Query("select dashboardItem from DashboardItem dashboardItem where dashboardItem.company.id = ?#{principal.companyId} order by dashboardItem.sortOrder asc")
	List<DashboardItem> findAllByCompanyId();
	
	@Query("select dashboardItem from DashboardItem dashboardItem where dashboardItem.dashboardItemConfigType = ?1 OR dashboardItem.dashboardItemConfigType = 'BOTH' and dashboardItem.company.id = ?#{principal.companyId} order by id asc")
	List<DashboardItem> findByCompanyIdAndDashboardItemConfigType(DashboardItemConfigType dashboardItemConfigType);
	
	List<DashboardItem> findByCompanyId(Long companyId);

	@Query("select dashboardItem from DashboardItem dashboardItem where dashboardItem.company.id = ?#{principal.companyId}")
	Page<DashboardItem> findAllByCompanyId(Pageable pageable);

	List<DashboardItem> findAllByCompanyPid(String companyPid);
}
