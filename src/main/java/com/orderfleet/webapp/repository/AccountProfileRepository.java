package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.enums.AccountNameType;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.projections.CustomAccountProfiles;

/**
 * Spring Data JPA repository for the AccountProfile entity.
 * 
 * @author Muhammed Riyas T
 * @since June 2, 2016
 */
public interface AccountProfileRepository extends JpaRepository<AccountProfile, Long> {

	Optional<AccountProfile> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<AccountProfile> findOneByPid(String pid);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId} order by accountProfile.name asc")
	List<AccountProfile> findAllByCompanyId();

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?1 order by accountProfile.name asc")
	List<AccountProfile> findAllByCompanyId(Long companyId);

	List<AccountProfile> findByNameStartingWith(String alphabet);

	List<AccountProfile> findByCompanyId(Long companyId);

	List<AccountProfile> findByCompanyIdAndActivatedTrue(Long companyId);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId}")
	Page<AccountProfile> findAllByCompanyId(Pageable pageable);

	List<AccountProfile> findAllByAccountTypePid(String pid);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId} and  accountProfile.accountType.name = ?1")
	List<AccountProfile> findAllByCompanyIdAndAccountType(String accountType);

	@Query("select accountProfile.name from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId}")
	List<String> findAccountNamesByCompanyId();

	@Query("select accountProfile.pid from AccountProfile accountProfile where accountProfile.activated = true and accountProfile.company.id = ?#{principal.companyId}")
	List<String> findAccountPidsByActivatedAndCompany();

	@Query("select accountProfile.pid, accountProfile.alias, accountProfile.name from AccountProfile accountProfile where accountProfile.alias in ?1 and accountProfile.activated = true and accountProfile.company.id = ?#{principal.companyId}")
	List<Object[]> findAccountPidsByAliasInAndActivatedAndCompany(Set<String> aliases);

	Optional<AccountProfile> findByCompanyIdAndAliasIgnoreCase(Long id, String alias);

	List<AccountProfile> findAllByCompanyIdAndAccountTypeName(Long id, String accountTypeName);

	List<AccountProfile> findAllByCompanyPid(String companyPid);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId} and  accountProfile.accountType in ?1")
	List<AccountProfile> findByCompanyIdAndAccountTypesIn(List<AccountType> accountTypes);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId} and  accountProfile.importStatus in ?1")
	List<AccountProfile> findAllByCompanyAndAccountImportStatus(boolean importStatus);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId} and accountProfile.activated =?1 order by accountProfile.name asc")
	List<AccountProfile> findAllByCompanyAndActivateOrDeactivateAccountProfileOrderByName(boolean active);

	List<AccountProfile> findByPidIn(List<String> accountProfilePids);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId} and UPPER(accountProfile.name) in ?1")
	List<AccountProfile> findByCompanyIdAndNameIgnoreCaseIn(List<String> names);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId} and UPPER(accountProfile.description) = ?1")
	List<AccountProfile> findByCompanyIdAndDescriptionIgnoreCase(String description);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId} and accountProfile.pid not in ?1 and accountProfile.accountType.pid = ?2")
	List<AccountProfile> findAllByPidNotInAndAccountTypePid(List<String> accountProfilePids, String accountTypePid);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId} and  accountProfile.accountType.pid in ?1 and accountProfile.activated =?2 order by accountProfile.name asc")
	List<AccountProfile> findByCompanyIdAndAccountTypesInAndActivated(List<String> accountTypes, boolean active);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId} and  accountProfile.accountType.pid in ?1 and accountProfile.activated = ?2 and accountProfile.importStatus = ?3 order by accountProfile.name asc")
	List<AccountProfile> findByCompanyIdAndAccountTypesInAndActivatedAndAccountImportStatus(List<String> accountTypes,
			boolean active, boolean importStatus);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId} and  accountProfile.importStatus = ?1 and accountProfile.activated = ?2 order by accountProfile.name asc")
	List<AccountProfile> findAllByCompanyAndAccountImportStatusAndActivated(boolean importStatus, boolean active);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?1 and UPPER(accountProfile.name) in ?2")
	List<AccountProfile> findByCompanyIdAndNameIgnoreCaseIn(Long companyId, List<String> names);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?1 and UPPER(accountProfile.alias) in ?2")
	List<AccountProfile> findByCompanyIdAndAliasIgnoreCaseIn(Long companyId, List<String> names);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE AccountProfile lh SET  lh.closingBalance =0 WHERE  lh.company.id = ?1")
	int updateAccountProfileClosingBalanceZeroByCompanyId(Long companyPid);

//	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?1 and accountProfile.user.id in ?2 and accountProfile.createdDate between ?3 and ?4 and accountProfile.importStatus = ?5 order by accountProfile.createdDate desc")
//	List<AccountProfile> findByCompanyIdAndUserIdInAndImportStatusAndCreatedDateBetweenOrderByCreatedDateDesc(Long companyId,
//			List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate,boolean importStatus);

	// For Test Purpose of above commented query added activated = true
	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?1 and accountProfile.user.id in ?2 and accountProfile.createdDate between ?3 and ?4 and accountProfile.importStatus = ?5 and accountProfile.activated = true order by accountProfile.createdDate desc")
	List<AccountProfile> findByCompanyIdAndUserIdInAndImportStatusAndCreatedDateBetweenOrderByCreatedDateDesc(
			Long companyId, List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate, boolean importStatus);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?1 and accountProfile.user.id in ?2 and accountProfile.lastModifiedDate between ?3 and ?4 and accountProfile.dataSourceType = ?5 order by accountProfile.lastModifiedDate desc")
	List<AccountProfile> findByCompanyIdAndUserIdInAndLastModifiedDateBetweenAndDataSourceTypeMobileOrderByLastModifiedDateDesc(
			Long companyId, List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate,
			DataSourceType dataSourceType);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId} and  accountProfile.dataSourceType = ?1")
	List<AccountProfile> findAllByCompanyAndDataSourceType(DataSourceType dataSourceType);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId} and  accountProfile.dataSourceType = ?1 order by accountProfile.createdDate desc ")
	List<AccountProfile> findAllByCompanyAndDataSourceTypeAndCreatedDate(DataSourceType dataSourceType);

	@Query("select accountProfile.pid from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId}")
	Set<String> findPidsByCompanyId();

	@Query("select accountProfile.pid, accountProfile.name from AccountProfile accountProfile where accountProfile.activated = true and accountProfile.company.id = ?#{principal.companyId} and accountProfile.name LIKE CONCAT(UPPER(:searchTerm), '%')) order by accountProfile.name")
	List<Object[]> findByCompanyAndAccountPidAndNameStartWith(@Param("searchTerm") String searchTerm);

	@Query(value = "select pid, name from tbl_account_profile where activated = true and company_id = ?#{principal.companyId} and name ~ E'^[^a-zA-Z].*' order by name", nativeQuery = true)
	List<Object[]> findByCompanyAndAccountPidAndNameStartWithNonAlphabetic();

	@Modifying
	@Query("update AccountProfile accountProfile set accountProfile.activated = false where accountProfile.company.id = ?1")
	void deactiveAllAccountProfiles(Long companyId);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?#{principal.companyId} and  accountProfile.accountType.accountNameType = ?1")
	List<AccountProfile> findAllByCompanyAndAccountTypeAccountNameType(AccountNameType accountNameType);

	@Query("select accountProfile.pid, accountProfile.alias, accountProfile.name, accountProfile.description, accountProfile.address ,accountProfile.user.firstName from AccountProfile accountProfile where accountProfile.activated = ?1 and accountProfile.company.id = ?#{principal.companyId}")
	List<Object[]> findAccountProfileAndActivated(Boolean activated);

	@Query("select accountProfile.pid, accountProfile.alias, accountProfile.name, accountProfile.description, accountProfile.address , accountProfile.user.firstName from AccountProfile accountProfile where accountProfile.activated = ?1 and accountProfile.company.id = ?#{principal.companyId} order by accountProfile.name")
	List<Object[]> findAccountProfileAndCreatedByAndActivated(Boolean activated);

	@Query("select accountProfile.id, accountProfile.pid, accountProfile.name,accountProfile.alias from AccountProfile accountProfile where accountProfile.company.id = ?1 order by accountProfile.name")
	List<Object[]> findAllAccountProfileByCompanyId(Long companyId);

	@Query("select ap.pid as pid, ap.name as name, ap.alias as alias, ap.user.firstName as userName,"
			+ "ap.description as description, ap.accountType.name as accountTypeName,"
			+ "ap.address as address, ap.city as city, ap.location as location, ap.pin as pin, ap.latitude as latitude,"
			+ "ap.longitude as longitude, ap.phone1 as phone1, ap.phone2 as phone2, ap.email1 as email1, ap.email2 as email2,"
			+ "ap.whatsAppNo as whatsAppNo, "
			+ "ap.accountStatus as accountStatus, ap.importStatus as importStatus, ap.creditDays as creditDays, ap.creditLimit as creditLimit,"
			+ "ap.contactPerson as contactPerson, ap.activated as activated, ap.lastModifiedDate as lastModifiedDate,"
			+ "ap.createdDate as createdDate, ap.leadToCashStage as leadToCashStage, ap.tinNo as tinNo, ap.closingBalance as closingBalance,"
			+ "ap.defaultDiscountPercentage as defaultDiscountPercentage, ap.trimChar as trimChar, ap.dataSourceType as dataSourceType "
			+ "from AccountProfile ap where ap.company.id = ?#{principal.companyId} and ap.activated =?1 order by ap.name asc")
	List<CustomAccountProfiles> findAllCustomAccountProfileByCompanyAndActivateOrDeactivateAccountProfileOrderByName(
			boolean active);

	@Query("select accountProfile from AccountProfile accountProfile where accountProfile.company.id = ?1 and  accountProfile.dataSourceType = ?2")
	List<AccountProfile> findAllByCompanyIdAndDataSourceType(Long companyId, DataSourceType dataSourceType);

}
