package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DashboardItemGroup;

public interface DashboardItemGroupRepository extends JpaRepository<DashboardItemGroup, Long> {

	Optional<DashboardItemGroup> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<DashboardItemGroup> findOneById(Long id);
	
	@Query("select dig from DashboardItemGroup dig where dig.company.id = ?#{principal.companyId} order by dig.sortOrder ASC")
	List<DashboardItemGroup> findAllByCompanyId();
	
	@Query("select dig.id from DashboardItemGroup dig where dig.company.id = ?#{principal.companyId} order by dig.sortOrder ASC")
	List<Long> findIdByCompanyId();

}
