package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DynamicReportName;


public interface DynamicReportUploadRepository extends JpaRepository<DynamicReportName, Long>{

	Optional<DynamicReportName> findByCompanyIdAndNameIgnoreCase(Long id, String name);
	
	@Query("select dynamicReportName from DynamicReportName dynamicReportName where dynamicReportName.company.id = ?#{principal.companyId} order by dynamicReportName.id desc")
	List<DynamicReportName> findAllByCompanyId();
	
	Optional<DynamicReportName> findOneById(long id);                
}
       