package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DashboardChartItem;

public interface DashboardChartItemRepository extends JpaRepository<DashboardChartItem, Long> {

	Optional<DashboardChartItem> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<DashboardChartItem> findOneByPid(String pid);
	
	Optional<DashboardChartItem> findOneByCompanyId(Long companyId);

	List<DashboardChartItem> findByPidIn(List<String> pids);
	
	@Query("select dci from DashboardChartItem dci where dci.company.id = ?1 and dci.name = ?2 order by dci.name ASC")
	List<DashboardChartItem> findByCompanyIdAndName(Long companyId, String name);

	@Query(value = "select dci from DashboardChartItem dci where dci.company.id = ?#{principal.companyId} order by dci.name ASC")
	List<DashboardChartItem> findAllByCompanyId();
}
