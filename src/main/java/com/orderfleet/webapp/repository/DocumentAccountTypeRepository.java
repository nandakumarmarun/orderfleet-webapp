package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.DocumentAccountType;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;

/**
 * repository for DocumentAccountType
 * 
 * @author Muhammed Riyas T
 * @since August 11, 2016
 */
public interface DocumentAccountTypeRepository extends JpaRepository<DocumentAccountType, Long> {

	@Query("select documentAccountType.accountType from DocumentAccountType documentAccountType where documentAccountType.document.pid = ?1 and documentAccountType.accountTypeColumn = ?2 ")
	List<AccountType> findAccountTypesByDocumentPidAndAccountTypeColumn(String documentPid,
			AccountTypeColumn accountTypeColumn);

	void deleteByDocumentPidAndAccountTypeColumn(String documentPid, AccountTypeColumn accountTypeColumn);

	@Query("select documentAccountType from DocumentAccountType documentAccountType where documentAccountType.company.id = ?#{principal.companyId}")
	List<DocumentAccountType> findAllByCompanyId();

	@Query("select documentAccountType from DocumentAccountType documentAccountType where documentAccountType.company.id = ?#{principal.companyId} and documentAccountType.lastModifiedDate > ?1")
	List<DocumentAccountType> findAllByCompanyIdLastModifiedDate(LocalDateTime lastModifiedDate);
}
