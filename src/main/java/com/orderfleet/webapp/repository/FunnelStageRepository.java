package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.FunnelStage;
import com.orderfleet.webapp.domain.Stage;

public interface FunnelStageRepository extends JpaRepository<FunnelStage, Long> {

	@Query("select funnelStage from FunnelStage funnelStage where funnelStage.companyId = ?#{principal.companyId}")
	List<FunnelStage> findAllByCompanyId();
	
	@Query("select funnelStage.stage from FunnelStage funnelStage where funnelStage.companyId = ?#{principal.companyId} and funnelStage.funnel.id = ?1")
	List<Stage> findStageByFunnelId(Long funnelId);
	
	void deleteByFunnelId(Long id);
}
