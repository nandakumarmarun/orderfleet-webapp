package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.AccountGroup;
import com.orderfleet.webapp.domain.AccountGroupAccountProfile;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountGroupAccountProfileDTO;

/**
 * Spring Data JPA repository for the AccountGroupAccountProfile entity.
 * 
 * @author Prashob Sasidharan
 * @since April 11, 2019
 */
public interface AccountGroupAccountProfileService {

	void save(String accountGroupPid, String assignedAccountProfiles);

	List<AccountProfileDTO> findAccountProfileByAccountGroupPid(String accountGroupPid);

	List<AccountGroupAccountProfile> findAllByCompany();

	Page<AccountProfileDTO> findAccountProfilesByCurrentUserAccountGroups(Pageable pageable);

	List<AccountProfileDTO> findAccountProfilesByCurrentUserAccountGroups();

	List<AccountGroupAccountProfile> findAllByAccountProfilePidAndAccountGroupPid(String accountProfilePid, String accountGroupPid);

	void deleteByCompanyId(Long companyId);

	List<AccountGroupAccountProfile> findAllByCompanyAndAccountProfilePid(Long companyId, String accountProfilePid);

	Page<AccountProfileDTO> findAccountProfilesByUserAccountGroupsAndAccountProfileActivated(int page, int size);

	List<AccountProfileDTO> findAccountProfileByAccountGroupPidIn(List<String> accountGroupPids);

	List<AccountGroupAccountProfileDTO> findByUserAccountGroupsAndAccountProfileActivated(int limit, int offset);

	List<AccountGroupAccountProfile> findAllByCompanyId(Long companyId);

	Page<AccountProfileDTO> findAllByAccountProfileActivatedTrueAndAccountGroupInAndLastModifiedDate(int page, int size,
			LocalDateTime lastSyncdate);

	void saveAccountGroupAccountProfile(AccountGroup accountGroup, String accountProfilePid);

	List<AccountGroupAccountProfileDTO> findByUserAccountGroupsAndAccountProfileActivatedAndLastModified(int limit, int offset,
			LocalDateTime lastModified);

	List<AccountProfileDTO> findAccountProfileByAccountGroupPidInAndActivated(List<String> accountGroupPids, boolean activated);
	
	List<AccountGroupAccountProfileDTO> findAccountGroupByAccountProfilePid(String accountProfilePid);
	
	List<AccountProfileDTO> findAllAccountProfileByAccountGroupPidInAndActivated(List<String> accountGroupPids, boolean activated);
	
	List<AccountProfileDTO> findAccountProfileByAccountGroupPidAndActivated(String accountGroupPid, boolean activated);
	
	List<AccountProfileDTO> findAccountProfilesByCurrentUserAccountGroupsAndAccountTypePidIn(List<String>accountTypePids ,boolean importStatus);
	
	List<AccountProfileDTO> findAccountByCurrentUserAccountGroupsAndAccountTypePidIn(List<String>accountTypePids);
	
	List<AccountProfileDTO> findAccountProfilesByCurrentUserAccountGroupsAndImpotedStatus(boolean importStatus);
	
	List<AccountProfileDTO> findAccountByCurrentUserAccountGroupsAndAllImpotedStatus();

	void saveAccountGroupAccountProfileSingle(String accountGroupPid, String accountProfilePid);
	
	List<AccountProfileDTO> findAccountProfilesByUsers(List<Long> userIds);
	
	Set<String> findAccountProfilePidsByUsers(List<Long> userIds);
	
	List<AccountGroupAccountProfile> findAllAccountGroupAccountProfiles(Long companyId);
}
