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
import com.orderfleet.webapp.domain.AccountGroup;
import com.orderfleet.webapp.domain.AccountGroupAccountProfile;
import com.orderfleet.webapp.domain.enums.DataSourceType;

/**
 * repository for AccountGroupAccountProfile
 * 
 * @author Prashob Sasidharan
 * @since April 11, 2019
 */
public interface AccountGroupAccountProfileRepository extends JpaRepository<AccountGroupAccountProfile, Long> {

	@Query("select accountGroupAccountProfile.accountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup.pid = ?1 ")
	List<AccountProfile> findAccountProfileByAccountGroupPid(String accountGroupPid);
	
	void deleteByAccountGroupPid(String accountGroupPid);

	void deleteByCompanyId(Long companyId);

	@Query("select accountGroupAccountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.company.id = ?#{principal.companyId}")
	List<AccountGroupAccountProfile> findAllByCompanyId();

	List<AccountGroupAccountProfile> findByAccountGroupIn(List<AccountGroup> accountGroups);

	@Query("select accountGroupAccountProfile.accountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup in  ?1 ")
	Page<AccountProfile> findAccountProfilesByUserAccountGroups(List<AccountGroup> accountGroups, Pageable pageable);

	@Query("select accountGroupAccountProfile.accountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup in  ?1 ")
	List<AccountProfile> findAccountProfilesByUserAccountGroups(List<AccountGroup> accountGroups);

	@Query("select accountGroupAccountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountProfile.pid = ?1 and accountGroupAccountProfile.accountGroup.pid=?2")
	List<AccountGroupAccountProfile> findAllByAccountProfilePidAndAccountGroupPid(String accountProfilePid, String accountGroupPid);

	@Query("select accountGroupAccountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountProfile.pid = ?1 and accountGroupAccountProfile.company.id = ?2")
	List<AccountGroupAccountProfile> findAllByCompanyAccountProfilePid(String accountProfilePid, Long companyId);

	@Query("select distinct accountGroupAccountProfile.accountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup in ?1 and accountGroupAccountProfile.accountProfile.activated=true")
	Page<AccountProfile> findAccountProfilesByUserAccountGroupsAndAccountProfileActivated(List<AccountGroup> accountGroups,
			Pageable pageable);

	Page<AccountGroupAccountProfile> findDistinctAccountProfileByAccountProfileActivatedTrueAndAccountGroupInOrderByIdAsc(
			List<AccountGroup> accountGroups, Pageable pageable);

	List<AccountGroupAccountProfile> findDistinctAccountProfileByAccountProfileActivatedTrueAndAccountGroupInOrderByIdAsc(
			List<AccountGroup> accountGroups);

	@Query("select DISTINCT(accountGroupAccountProfile.accountProfile) from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup.id = ?1 and accountGroupAccountProfile.accountGroup.activated=true")
	List<AccountProfile> findAccountProfileByAccountGroupId(Long accountGroupId);
	
	@Query("select DISTINCT(accountGroupAccountProfile.accountProfile.id) from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup.pid = ?1 and accountGroupAccountProfile.accountGroup.activated=true")
	Set<Long> findAccountProfileIdByAccountGroupPid(String accountGroupPid);

	@Query("select DISTINCT(accountGroupAccountProfile.accountProfile) from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup.id in ?1 and accountGroupAccountProfile.accountGroup.activated=true")
	List<AccountProfile> findAccountProfileByAccountGroupIdIn(List<Long> accountGroupIds);

	@Query("select accountGroupAccountProfile.accountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup.pid in ?1 and accountGroupAccountProfile.accountGroup.activated=true order by accountGroupAccountProfile.accountProfile.name asc")
	List<AccountProfile> findAccountProfileByAccountGroupPidIn(List<String> accountGroupPids);

	@Query("select accountGroupAccountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup in ?1 and accountGroupAccountProfile.accountProfile.activated=true")
	Page<AccountGroupAccountProfile> findByUserAccountGroupsAndAccountProfileActivated(List<AccountGroup> accountGroups,
			Pageable pageable);

	@Query(value = "select ap.pid as accountProfilePid , ap.name as accountProfileName, loc.pid as accountGroupPid, "
			+ "loc.name as accountGroupName, lap.last_modified_date as lastModifiedDate from tbl_accountGroup_account_profile lap "
			+ "join tbl_account_profile ap on lap.account_profile_id = ap.id join tbl_accountGroup loc on lap.accountGroup_id = loc.id  "
			+ "where lap.company_id = ?#{principal.companyId} and lap.accountGroup_id in ?1 and ap.activated=true ORDER BY lap.id LIMIT ?2 OFFSET ?3", nativeQuery = true)
	List<Object[]> findByAccountGroupInAndAccountProfileActivatedPaginated(Set<Long> accountGroupIds,
			int limit, int offset);

	@Query(value = "select ap.pid as accountProfilePid , ap.name as accountProfileName, loc.pid as accountGroupPid, "
			+ "loc.name as accountGroupName, lap.last_modified_date as lastModifiedDate from tbl_accountGroup_account_profile lap "
			+ "join tbl_account_profile ap on lap.account_profile_id = ap.id join tbl_accountGroup loc on lap.accountGroup_id = loc.id "
			+ "where lap.company_id = ?#{principal.companyId} and lap.accountGroup_id in ?1 and ap.activated=true and "
			+ "(lap.last_modified_date > ?2) OR (ap.last_modified_date > ?2) OR (loc.last_modified_date > ?2) ORDER BY lap.id LIMIT ?3 OFFSET ?4", nativeQuery = true)
	List<Object[]> findByAccountGroupInAndAccountProfileActivatedPaginated(Set<Long> accountGroupIds, LocalDateTime lastModifiedDate, int limit, int offset);
	
	List<AccountGroupAccountProfile> findAllByCompanyId(Long companyId);
	
	List<AccountGroupAccountProfile> findAllByCompanyPid(String companyPid);

	@Query("select distinct accountGroupAccountProfile.accountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup in ?1 and accountGroupAccountProfile.accountProfile.activated=true order by accountGroupAccountProfile.accountProfile.name asc")
	List<AccountProfile> findAccountProfilesByUserAccountGroupsAndAccountProfileActivated(List<AccountGroup> accountGroups);

	@Query("select distinct accountGroupAccountProfile.accountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup in ?1 and accountGroupAccountProfile.accountProfile.accountType in ?2 and accountGroupAccountProfile.accountProfile.activated=true order by accountGroupAccountProfile.accountProfile.name asc")
	List<AccountProfile> findAccountProfilesByUserAccountGroupsAccountTypesAndAccountProfileActivated(
			List<AccountGroup> accountGroups, List<AccountType> accountTypes);

	@Modifying(clearAutomatically = true)
	void deleteByCompanyIdAndDataSourceTypeAndThirdpartyUpdateTrue(Long companyId, DataSourceType dataSourceType);

	@Modifying(clearAutomatically = true)
	void deleteByCompanyIdAndDataSourceTypeAndThirdpartyUpdateTrueAndAccountProfilePid(Long companyId,
			DataSourceType dataSourceType, String accountProfilePid);

	@Query("select distinct accountGroupAccountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup in ?1 and accountGroupAccountProfile.accountProfile.activated=?2 and (accountGroupAccountProfile.lastModifiedDate > ?3) OR (accountGroupAccountProfile.accountProfile.lastModifiedDate > ?3) OR (accountGroupAccountProfile.accountGroup.lastModifiedDate > ?3) order by accountGroupAccountProfile.id asc")
	Page<AccountGroupAccountProfile> findAllByAccountProfileActivatedTrueAndAccountGroupInAndLastModifiedDate(
			List<AccountGroup> accountGroups, boolean status, LocalDateTime lastModifiedDate, Pageable pageable);

	// not used
	@Query("select accountGroupAccountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountProfile in  ?1 ")
	List<AccountGroupAccountProfile> findAccountGroupAccountProfileByAccountProfileIn(List<AccountProfile> accountProfiles);

	@Query("select accountGroupAccountProfile.accountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup in  ?1 order by accountGroupAccountProfile.accountProfile.name asc")
	List<AccountProfile> findAccountProfilesByUserAccountGroupsOrderByAccountProfilesName(List<AccountGroup> accountGroups);
	
	@Query("select accountGroupAccountProfile.accountProfile.pid from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup in  ?1 order by accountGroupAccountProfile.accountProfile.name asc")
	Set<String> findAccountProfilePidsByUserAccountGroupsOrderByAccountProfilesName(List<AccountGroup> accountGroups);
	
	@Query("select accountGroupAccountProfile.accountProfile.pid from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup.pid in ?1")
	Set<String> findAccountProfilePidsByAccountGroupPidIn(List<String> accountGroupPids);
	
	@Query("select accountGroupAccountProfile.accountProfile.pid from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup.id in ?1")
	Set<String> findAccountProfilePidsByAccountGroupIdIn(Set<Long> accountGroupIds);
	
	@Query("select la.accountProfile.pid, la.accountProfile.name from AccountGroupAccountProfile la where la.accountGroup.id in ?1 order by la.accountProfile.name ASC")
	List<Object[]> findAccountProfilesByAccountGroupIdIn(Set<Long> accountGroupIds);
	
	@Query("select accountGroupAccountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.company.id = ?#{principal.companyId} and accountGroupAccountProfile.accountProfile.pid = ?1 ")
	List<AccountGroupAccountProfile> findAllByAccountProfilePid(String accountProfilePid);
	
	@Query("select accountGroupAccountProfile.accountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup.pid in ?1 and accountGroupAccountProfile.accountProfile.activated = ?2")
	List<AccountProfile> findAccountProfileByAccountGroupPidInAndAccountProfileActivated(List<String> accountGroupPids,boolean active);
	
	@Query("select accountGroupAccountProfile.accountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup.pid = ?1 and accountGroupAccountProfile.accountProfile.activated = ?2")
	List<AccountProfile> findAccountProfileByAccountGroupPidAndAccountProfileActivated(String accountGroupPid,boolean active);
	
	@Query("select accountGroupAccountProfile.accountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup in  ?1 and accountGroupAccountProfile.accountProfile.accountType.pid in ?2 and accountGroupAccountProfile.accountProfile.importStatus= ?3 order by accountGroupAccountProfile.accountProfile.name asc")
	List<AccountProfile> findAccountProfilesByUserAccountGroupsAndAccountTypePidInOrderByAccountProfilesName(List<AccountGroup> accountGroups,List<String> accountTypePids,boolean status);
	
	@Query("select accountGroupAccountProfile.accountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup in  ?1 and accountGroupAccountProfile.accountProfile.accountType.pid in ?2 order by accountGroupAccountProfile.accountProfile.name asc")
	List<AccountProfile> findAccountByUserAccountGroupsAndAccountTypePidInOrderByAccountProfilesName(List<AccountGroup> accountGroups,List<String> accountTypePids);
	
	@Query("select accountGroupAccountProfile.accountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup in  ?1 and accountGroupAccountProfile.accountProfile.importStatus = ?2 order by accountGroupAccountProfile.accountProfile.name asc")
	List<AccountProfile> findAccountByUserAccountGroupsAndImportedStatusOrderByAccountProfilesName(List<AccountGroup> accountGroups,boolean status);
	
	@Query("select accountGroupAccountProfile.accountProfile from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup in  ?1 order by accountGroupAccountProfile.accountProfile.name asc")
	List<AccountProfile> findAccountByUserAccountGroupsAndAllImportedStatusOrderByAccountProfilesName(List<AccountGroup> accountGroups);
	
	AccountGroupAccountProfile findByAccountProfile(AccountProfile accountProfile);
	
	@Query("select accountGroupAccountProfile.accountProfile.id from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.accountGroup.id in ?1")
	Set<Long> findAccountProfileIdsByUserAccountGroupIdsIn(Set<Long> accountGroupIds);
	
	@Query("select lap.id, lap.accountProfile.pid, lap.accountGroup.pid from AccountGroupAccountProfile lap where lap.company.id in ?1")
	List<Object[]> findAllAccountGroupAccountProfilesByCompanyId(Long companyId);
	
	@Transactional
	@Modifying
	@Query("delete from AccountGroupAccountProfile accountGroupAccountProfile where accountGroupAccountProfile.company.id = ?1 and accountGroupAccountProfile.id in ?2")
	void deleteByIdIn(Long companyId, List<Long> ids);
	
	@Query("select accountGroupAccountProfile from AccountGroupAccountProfile accountGroupAccountProfile  where accountGroupAccountProfile.accountProfile.pid in ?1 and accountGroupAccountProfile.accountProfile.activated = true")
	List<AccountGroupAccountProfile> findAllAccountGroupByAccountProfilePids(List<String> accountProfilePids);
	
}
