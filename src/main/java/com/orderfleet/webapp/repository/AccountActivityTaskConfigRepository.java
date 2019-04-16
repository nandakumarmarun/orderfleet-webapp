package com.orderfleet.webapp.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountActivityTaskConfig;

public interface AccountActivityTaskConfigRepository extends JpaRepository<AccountActivityTaskConfig,Long>{

	
	@Query("select activityAccount from AccountActivityTaskConfig activityAccount where activityAccount.accountType.company.id = ?#{principal.companyId} and activityAccount.accountType.pid = ?1 ")
	List<AccountActivityTaskConfig> findActivityByAccountTypePid(String accountTypePid);
	
	@Query("select activityAccount.activity.pid,activityAccount.assignNotification from AccountActivityTaskConfig activityAccount where activityAccount.accountType.company.id = ?#{principal.companyId} and activityAccount.accountType.pid = ?1 ")
	List<Object[]> findActivityPidByAccountTypePid(String accountTypePid);
	
//	@Transactional
//	@Modifying
//	@Query("delete from AccountActivityTaskConfig activityAccount where activityAccount.company.id = ?#{principal.companyId} and activityAccount.accountType.pid = ?1")
//	void deleteActivityConfigByAccountTypeAndCompany(String accountTypePid);
	@Modifying
	@Transactional
	void deleteByAccountTypePidAndCompanyId(String accountTypePid,Long companyId);

}
