package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.AccountTypeAssociation;
import com.orderfleet.webapp.domain.AccountGroup;
import com.orderfleet.webapp.domain.AccountGroupAccountProfile;
import com.orderfleet.webapp.domain.enums.DataSourceType;

/**
 * repository for AccountGroupAccountProfile
 * 
 * @author Prashob Sasidharan
 * @since April 11, 2019
 */
public interface AccountTypeAssociationRepository extends JpaRepository<AccountTypeAssociation, Long> {

	@Query("select aat.associatedAccountType.pid from AccountTypeAssociation aat where aat.accountType.pid = ?1")
	List<String> findAssociatedAccountTypeByPid(String pid);

	@Query("select aat from AccountTypeAssociation aat where aat.accountType.pid = ?1")
	List<AccountTypeAssociation> findAllAssociatedAccountTypeByAccountTypePid(String pid);

	@Modifying
	@Transactional
	void deleteByAccountTypePidAndCompanyId(String accountTypePid, Long companyId);

}
