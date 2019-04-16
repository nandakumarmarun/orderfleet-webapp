package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.DocumentAccountingVoucherColumn;
import com.orderfleet.webapp.domain.AccountingVoucherColumn;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherColumnDTO;

/**
 * Service Interface for managing DocumentAccountingVoucherColumn.
 * 
 * @author Muhammed Riyas T
 * @since July 25, 2016
 *
 */
public interface DocumentAccountingVoucherColumnService {

	void save(String documentPid, List<AccountingVoucherColumnDTO> assignedAccountingVoucherColumns);

	List<AccountingVoucherColumn> findAccountingVoucherColumnsByDocumentPid(String documentPid);

	List<DocumentAccountingVoucherColumn> findByDocumentPid(String documentPid);

	List<DocumentAccountingVoucherColumn> findByCompanyId();

	List<DocumentAccountingVoucherColumn> findByCompanyIdAndLastModifiedDate(LocalDateTime lastModifiedDate);
	
	List<DocumentAccountingVoucherColumn> findByCompanyIdAndDocumentPid(String documentPid);
}
