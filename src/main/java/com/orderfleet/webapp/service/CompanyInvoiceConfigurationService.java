package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.CompanyInvoiceConfigurationDTO;

/**
 * Service Interface for managing CompanyInvoiceConfiguration.
 *
 * @author Sarath
 * @since Apr 13, 2018
 *
 */
public interface CompanyInvoiceConfigurationService {

	Optional<CompanyInvoiceConfigurationDTO> findOneByCompanyPid(String pid);

	CompanyInvoiceConfigurationDTO save(CompanyInvoiceConfigurationDTO companyInvoiceConfigurationDTO);

	List<CompanyInvoiceConfigurationDTO> findAll();

	Optional<CompanyInvoiceConfigurationDTO> findOneById(Long id);

	void delete(Long id);

	CompanyInvoiceConfigurationDTO update(CompanyInvoiceConfigurationDTO companyInvoiceConfigurationDTO);
	
	
}
