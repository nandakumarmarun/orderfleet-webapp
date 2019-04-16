package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.DocumentInventoryVoucherColumn;
import com.orderfleet.webapp.domain.InventoryVoucherColumn;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherColumnDTO;

/**
 * Service Interface for managing DocumentInventoryVoucherColumn.
 * 
 * @author Muhammed Riyas T
 * @since July 25, 2016
 *
 */
public interface DocumentInventoryVoucherColumnService {

	void save(String documentPid, List<InventoryVoucherColumnDTO> assignedInventoryVoucherColumns);

	List<InventoryVoucherColumn> findInventoryVoucherColumnsByDocumentPid(String documentPid);

	List<DocumentInventoryVoucherColumn> findByDocumentPid(String documentPid);

	List<DocumentInventoryVoucherColumn> findByCompanyId();

	List<DocumentInventoryVoucherColumn> findAllByCompanyIdAndLastModifiedDate(LocalDateTime lastModifiedDate);
	
	List<DocumentInventoryVoucherColumn> findAllByCompanyIdAndDocumentPid(String document);
}
