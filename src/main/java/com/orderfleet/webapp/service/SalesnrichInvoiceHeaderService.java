package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.SalesnrichInvoiceHeaderDTO;

/**
 * Service Interface for managing SalesnrichInvoice.
 *
 * @author Sarath
 * @since Mar 15, 2018
 *
 */
public interface SalesnrichInvoiceHeaderService {

	SalesnrichInvoiceHeaderDTO save(SalesnrichInvoiceHeaderDTO SalesnrichInvoiceHeaderDTO);

	List<SalesnrichInvoiceHeaderDTO> findAllByCompanyPid(String companyPid);

	Optional<SalesnrichInvoiceHeaderDTO> getTop1SalesnrichInvoiceByInvoiceDateBetween(LocalDateTime fromDate,
			LocalDateTime toDate);

	Optional<SalesnrichInvoiceHeaderDTO> findByInvoiceNumber(Long invoiceNumber);
	
	Long getCountOfSalesnrichInvoiceHeader();
	
	List<SalesnrichInvoiceHeaderDTO> findAll();
	
	Optional<SalesnrichInvoiceHeaderDTO> findOneById(Long pid);
	
}
