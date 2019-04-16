package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Funnel;

public interface FunnelRepository extends JpaRepository<Funnel, Long> {

	Optional<Funnel> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<Funnel> findOneById(Long id);
	
	@Query("select funnel from Funnel funnel where funnel.company.id = ?#{principal.companyId} order by (CASE WHEN funnel.sortOrder = 0 THEN 2147483647 ELSE funnel.sortOrder END)")
	List<Funnel> findAllByCompanyId();
	
}
