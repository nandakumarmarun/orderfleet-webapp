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
import com.orderfleet.webapp.domain.AccountProfileAssociation;
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
public interface AccountProfileAssociationRepository extends JpaRepository<AccountProfileAssociation, Long> {

	@Query("select aap.associatedAccountProfile.pid from AccountProfileAssociation aap where aap.accountProfile.pid = ?1")
	List<String> findAssociatedAccountProfileByPid(String pid);

	@Modifying
	@Transactional
	void deleteByAccountProfilePidAndCompanyIdAndAssociatedAccountProfileAccountTypePidIn(String accountTypePid, Long companyId,
			List<String> associatedAccountTypePid);

	@Query("select aap from AccountProfileAssociation aap where aap.accountProfile.pid = ?1 and aap.associatedAccountProfile.accountType.pid in ?2")
	List<AccountProfileAssociation> findAllAssociatedAccountProfileByAccountProfilePid(String accountPid,
			List<String> accountTypePids);

	@Modifying
	@Transactional
	void deleteByAccountProfilePidAndCompanyId(String accountProfilePid, Long currentUsersCompanyId);

}
