package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.DynamicReportHeader;

public interface DynamicReportHeaderRepository extends JpaRepository<DynamicReportHeader, Long>{

	Optional<DynamicReportHeader> findOneByDynamicReportNameId(long id);
	
	Optional<DynamicReportHeader> findOneById(long id);
	
	Optional<DynamicReportHeader> findOneByDynamicReportNameName(String reportName);
}
