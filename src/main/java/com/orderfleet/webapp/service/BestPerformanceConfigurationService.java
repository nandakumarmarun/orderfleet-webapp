package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.web.rest.dto.BestPerformanceConfigurationDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;

/**
 * Service Interface for managing BestPerformanceConfiguration.
 *
 * @author Sarath
 * @since Mar 27, 2018
 *
 */
public interface BestPerformanceConfigurationService {

	List<BestPerformanceConfigurationDTO> findByCompanyIdAndVoucherType(DocumentType documentType);

	void save(String assignedDocuments, DocumentType documentType, BestPerformanceType bestPerformanceType);

	List<BestPerformanceConfigurationDTO> findByCompanyId();

	List<DocumentDTO> findAllDocumentsByBestPerformanceType(BestPerformanceType bestPerformanceType);

	void deleteByBestPerformanceType(BestPerformanceType bestPerformanceType);

}
