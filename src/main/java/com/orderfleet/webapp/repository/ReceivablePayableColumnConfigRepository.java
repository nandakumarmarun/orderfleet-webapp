package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.ReceivablePayableColumnConfig;

public interface ReceivablePayableColumnConfigRepository extends JpaRepository<ReceivablePayableColumnConfig, Long> {
	
	@Query("select rpColumnConfig from ReceivablePayableColumnConfig rpColumnConfig where rpColumnConfig.company.id = ?#{principal.companyId}")
	List<ReceivablePayableColumnConfig> findAllByCompanyId();

	void deleteByCompanyId(Long companyId);
}
