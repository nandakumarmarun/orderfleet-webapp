package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.CompanyIntegrationModule;

public interface CompanyIntegrationModuleRepository extends JpaRepository<CompanyIntegrationModule, Long> {

	Optional<CompanyIntegrationModule> findByCompanyId(Long id);
	
	List<CompanyIntegrationModule> findByAutoUpdateTrue();
	
}
