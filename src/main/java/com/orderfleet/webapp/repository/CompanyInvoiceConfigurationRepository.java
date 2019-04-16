package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.CompanyInvoiceConfiguration;

/**
 * Spring Data JPA repository for the CompanyInvoiceConfiguration entity.
 *
 * @author Sarath
 * @since Apr 13, 2018
 *
 */
public interface CompanyInvoiceConfigurationRepository extends JpaRepository<CompanyInvoiceConfiguration, Long> {

	@Query("select companyInvoiceConfiguration from CompanyInvoiceConfiguration companyInvoiceConfiguration where companyInvoiceConfiguration.company.pid = ?1")
	Optional<CompanyInvoiceConfiguration> findOneByCompanyPid(String pid);

	Optional<CompanyInvoiceConfiguration> findOneById(Long id);
}
