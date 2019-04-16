package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.FormElementUserAccount;

public interface FormElementUserAccountRepository extends JpaRepository<FormElementUserAccount, Long> {
	
	Optional<FormElementUserAccount> findByFormElementPidAndUserPidAndAccountProfilePidAndCompanyId(String formElementPid, String userPid, String accProfilePid, Long companyId);
	
	List<FormElementUserAccount> findByFormElementPidAndUserPidAndCompanyId(String formElementPid, String userPid, Long companyId);
	
	void deleteByFormElementPidAndUserPid(String formElementPid, String userPid);
}
