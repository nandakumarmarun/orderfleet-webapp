package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.LeadToCashReportConfig;

public interface LeadToCashReportConfigRepository extends JpaRepository<LeadToCashReportConfig, Long>{

	@Query("select leadReport from LeadToCashReportConfig leadReport where leadReport.company.id =?#{principal.companyId} order by leadReport.sortOrder")
	List<LeadToCashReportConfig> findAllByCompany();
	
	Optional<LeadToCashReportConfig> findByNameAndCompanyId(String name,long companyId);
	
	Optional<LeadToCashReportConfig> findOneByPid(String pid);
	
	@Query("select leadReport.name from LeadToCashReportConfig leadReport where leadReport.company.id =?#{principal.companyId} order by leadReport.sortOrder")
	List<String> findNamesByCompany();
	
	@Query("select leadReport.stage.pid from LeadToCashReportConfig leadReport where leadReport.company.id =?#{principal.companyId} order by leadReport.sortOrder")
	List<String> findStagePidByCompany();
	
	@Query("select leadReport.stage.id from LeadToCashReportConfig leadReport where leadReport.company.id =?#{principal.companyId} order by leadReport.sortOrder")
	List<Long> findStageIdOrderBySortOrderAsc();
	
}
