package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.DocumentAccountType;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;

/**
 * Spring Data JPA repository for the DocumentAccountType entity.
 * 
 * @author Muhammed Riyas T
 * @since August 11, 2016
 */
public interface DocumentAccountTypeService {

	void save(String documentPid, String assignedAccountTypes, AccountTypeColumn accountTypeColumn);

	List<AccountTypeDTO> findAccountTypesByDocumentPidAndAccountTypeColumn(String documentPid,
			AccountTypeColumn accountTypeColumn);

	List<DocumentAccountType> findAllByCompany();

	List<DocumentAccountType> findAllByCompanyLastModifiedDate(LocalDateTime lastModifiedDate);
}
