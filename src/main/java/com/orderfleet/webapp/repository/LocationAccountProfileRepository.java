package com.orderfleet.webapp.repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.enums.DataSourceType;

/**
 * repository for LocationAccountProfile
 * 
 * @author Muhammed Riyas T
 * @since August 31, 2016
 */
public interface LocationAccountProfileRepository extends JpaRepository<LocationAccountProfile, Long> {
	@Modifying
	@Query(value = "DELETE FROM tbl_location_account_profile WHERE account_profile_id = ?1 AND location_id = ?2", nativeQuery = true)
	void deleteByAccountProfileIdAndLocationId(long accountProfileId, long locationId);

	@Query("SELECT lap.location FROM LocationAccountProfile lap JOIN lap.accountProfile ap JOIN ap.user u WHERE u.pid = ?1 and lap.location.activated = true")
	List<Location> findLocationByAccountProfileUser(String userPid);


	@Query("DELETE FROM LocationAccountProfile l WHERE l.accountProfile = ?1")
	void deleteByAccountProfile(AccountProfile accountProfile);
	@Query("SELECT lap " +
			"FROM LocationAccountProfile lap " +
			"JOIN lap.accountProfile ap " +
			"JOIN ap.user u " +
			"JOIN lap.location l " +
			"WHERE u.pid = ?1 " + // Argument for User PID
			"AND l.pid = ?2 " +    // Argument for Location PID
			"ORDER BY u.pid, l.pid") // Sort by User PID and Location PID
	List<LocationAccountProfile> findLocationAccountProfilesSortedByUserAndLocation(String userPid, String locationPid);
	@Query("SELECT lap " +
			"FROM LocationAccountProfile lap " +
			"JOIN lap.accountProfile ap " +
			"JOIN ap.user u " +"WHERE u.pid = ?1 ")
	List<LocationAccountProfile> findByAccountUser(String userPid);

	@Query("select locationAccountProfile.accountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location.pid = ?1 ")
	List<AccountProfile> findAccountProfileByLocationPid(String locationPid);

	void deleteByLocationPid(String locationPid);

	@Transactional
	@Modifying
	@Query(value = "delete from tbl_location_account_profile where company_id= ?1 and account_profile_id= ?2 ", nativeQuery = true)
	void deleteByAccountProfilePid(long companyId, long accountProfileId);

	void deleteByCompanyId(Long companyId);

	@Transactional
	@Modifying
	void deleteByCompanyIdAndAccountProfilePidIn(Long currentUsersCompanyId, List<String> accountProfilePids);
	
	@Query("select locationAccountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.company.id = ?#{principal.companyId}")
	List<LocationAccountProfile> findAllByCompanyId();


	List<LocationAccountProfile> findByLocationIn(List<Location> locations);

	@Query("select locationAccountProfile.accountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in  ?1 ")
	Page<AccountProfile> findAccountProfilesByUserLocations(List<Location> locations, Pageable pageable);

	@Query("select locationAccountProfile.accountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in  ?1 ")
	List<AccountProfile> findAccountProfilesByUserLocations(List<Location> locations);

	@Query("select locationAccountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.accountProfile.pid = ?1 and locationAccountProfile.location.pid=?2")
	List<LocationAccountProfile> findAllByAccountProfilePidAndLocationPid(String accountProfilePid, String locationPid);

	@Query("select locationAccountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.accountProfile.pid = ?1 and locationAccountProfile.company.id = ?2")
	List<LocationAccountProfile> findAllByCompanyAccountProfilePid(String accountProfilePid, Long companyId);

	/*
	 * @Query("select DISTINCT(locationAccountProfile.accountProfile) from LocationAccountProfile locationAccountProfile where locationAccountProfile.location.activated=true"
	 * ) List<AccountProfile> findAllAccountProfile();
	 */
	@Query("select locationAccountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.company.id = ?#{principal.companyId} ")
	List<LocationAccountProfile> findAllByAccountProfile();

	@Query("select locationAccountProfile.location.activated,locationAccountProfile.location.pid,locationAccountProfile.accountProfile.id,locationAccountProfile.location.name,locationAccountProfile.accountProfile.pid,locationAccountProfile.accountProfile.name from LocationAccountProfile locationAccountProfile where locationAccountProfile.company.id = ?#{principal.companyId}  ")
	List<Object[]> findAllByAccountProfileOptimised();

	@Query("select distinct locationAccountProfile.accountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in ?1 and locationAccountProfile.accountProfile.activated=true")
	Page<AccountProfile> findAccountProfilesByUserLocationsAndAccountProfileActivated(List<Location> locations,
			Pageable pageable);

	Page<LocationAccountProfile> findDistinctAccountProfileByAccountProfileActivatedTrueAndLocationInOrderByIdAsc(
			List<Location> locations, Pageable pageable);

	@Query(value = "select ap.pid as accountProfilePid , ap.name as accountProfileName, loc.pid as locationPid, "
			+ "loc.name as locationName, lap.last_modified_date as lastModifiedDate from tbl_location_account_profile lap "
			+ "join tbl_account_profile ap on lap.account_profile_id = ap.id join tbl_location loc on lap.location_id = loc.id  "
			+ "where lap.company_id = ?#{principal.companyId} and lap.location_id in ?1 and ap.activated=true ORDER BY lap.id LIMIT ?2 OFFSET ?3", nativeQuery = true)
	List<Object[]> findDistinctAccountProfileByAccountProfileActivatedTrueAndLocationInOrderByIdAscPaginated(
			Set<Long> locationIds, int limit, int offset);

	// @Query("select distinct locationAccountProfile from LocationAccountProfile
	// locationAccountProfile where locationAccountProfile.location in ?1 and
	// locationAccountProfile.accountProfile.activated=?2 and
	// (locationAccountProfile.lastModifiedDate > ?3) OR
	// (locationAccountProfile.accountProfile.lastModifiedDate > ?3) OR
	// (locationAccountProfile.location.lastModifiedDate > ?3) order by
	// locationAccountProfile.id asc")

	@Query(value = "select ap.pid as accountProfilePid , ap.name as accountProfileName, loc.pid as locationPid, "
			+ "loc.name as locationName, lap.last_modified_date as lastModifiedDate from tbl_location_account_profile lap "
			+ "join tbl_account_profile ap on lap.account_profile_id = ap.id join tbl_location loc on lap.location_id = loc.id  "
			+ "where lap.company_id = ?#{principal.companyId} and lap.location_id in ?1 and ap.activated=true and (lap.last_modified_date > ?2) OR (ap.last_modified_date > ?2) OR (loc.last_modified_date > ?2) ORDER BY lap.id LIMIT ?3 OFFSET ?4", nativeQuery = true)
	List<Object[]> findAllByAccountProfileActivatedTrueAndLocationInAndLastModifiedDatePaginated(Set<Long> locationIds,
			LocalDateTime lastModifiedDate, int limit, int offset);

	List<LocationAccountProfile> findDistinctAccountProfileByAccountProfileActivatedTrueAndLocationInOrderByIdAsc(
			List<Location> locations);

	@Query("select DISTINCT(locationAccountProfile.accountProfile) from LocationAccountProfile locationAccountProfile where locationAccountProfile.location.id = ?1 and locationAccountProfile.location.activated=true")
	List<AccountProfile> findAccountProfileByLocationId(Long locationId);

	@Query("select DISTINCT(locationAccountProfile.accountProfile.id) from LocationAccountProfile locationAccountProfile where locationAccountProfile.location.pid = ?1 and locationAccountProfile.location.activated=true")
	Set<Long> findAccountProfileIdByLocationPid(String locationPid);

	@Query("select DISTINCT(locationAccountProfile.accountProfile) from LocationAccountProfile locationAccountProfile where locationAccountProfile.location.id in ?1 and locationAccountProfile.location.activated=true")
	List<AccountProfile> findAccountProfileByLocationIdIn(List<Long> locationIds);

	@Query("select locationAccountProfile.accountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location.pid in ?1 and locationAccountProfile.location.activated=true order by locationAccountProfile.accountProfile.name asc")
	List<AccountProfile> findAccountProfileByLocationPidIn(List<String> locationPids);

	@Query("select locationAccountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in ?1 and locationAccountProfile.accountProfile.activated=true")
	Page<LocationAccountProfile> findByUserLocationsAndAccountProfileActivated(List<Location> locations,
			Pageable pageable);

	@Query(value = "select ap.pid as accountProfilePid , ap.name as accountProfileName, loc.pid as locationPid, "
			+ "loc.name as locationName, lap.last_modified_date as lastModifiedDate from tbl_location_account_profile lap "
			+ "join tbl_account_profile ap on lap.account_profile_id = ap.id join tbl_location loc on lap.location_id = loc.id  "
			+ "where lap.company_id = ?#{principal.companyId} and lap.location_id in ?1 and ap.activated=true ORDER BY lap.id LIMIT ?2 OFFSET ?3", nativeQuery = true)
	List<Object[]> findByLocationInAndAccountProfileActivatedPaginated(Set<Long> locationIds, int limit, int offset);

	@Query(value = "select ap.pid as accountProfilePid , ap.name as accountProfileName, loc.pid as locationPid, "
			+ "loc.name as locationName, lap.last_modified_date as lastModifiedDate from tbl_location_account_profile lap "
			+ "join tbl_account_profile ap on lap.account_profile_id = ap.id join tbl_location loc on lap.location_id = loc.id "
			+ "where lap.company_id = ?#{principal.companyId} and lap.location_id in ?1 and ap.activated=true and "
			+ "(lap.last_modified_date > ?2  OR ap.last_modified_date > ?2 OR loc.last_modified_date > ?2) ORDER BY lap.id LIMIT ?3 OFFSET ?4", nativeQuery = true)
	List<Object[]> findByLocationInAndAccountProfileActivatedPaginated(Set<Long> locationIds,
			LocalDateTime lastModifiedDate, int limit, int offset);

	List<LocationAccountProfile> findAllByCompanyId(Long companyId);

	List<LocationAccountProfile> findAllByCompanyPid(String companyPid);

	@Query("select distinct locationAccountProfile.accountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in ?1 and locationAccountProfile.accountProfile.activated=true order by locationAccountProfile.accountProfile.name asc")
	List<AccountProfile> findAccountProfilesByUserLocationsAndAccountProfileActivated(List<Location> locations);

	@Query("select distinct locationAccountProfile.accountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in ?1 and locationAccountProfile.accountProfile.accountType in ?2 and locationAccountProfile.accountProfile.activated=true order by locationAccountProfile.accountProfile.name asc")
	List<AccountProfile> findAccountProfilesByUserLocationsAccountTypesAndAccountProfileActivated(
			List<Location> locations, List<AccountType> accountTypes);

	@Modifying(clearAutomatically = true)
	void deleteByCompanyIdAndDataSourceTypeAndThirdpartyUpdateTrue(Long companyId, DataSourceType dataSourceType);

	@Modifying(clearAutomatically = true)
	void deleteByCompanyIdAndDataSourceTypeAndThirdpartyUpdateTrueAndAccountProfilePid(Long companyId,
			DataSourceType dataSourceType, String accountProfilePid);

	@Query("select distinct locationAccountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in ?1 and locationAccountProfile.accountProfile.activated=?2 and (locationAccountProfile.lastModifiedDate > ?3) OR (locationAccountProfile.accountProfile.lastModifiedDate > ?3) OR (locationAccountProfile.location.lastModifiedDate > ?3) order by locationAccountProfile.id asc")
	Page<LocationAccountProfile> findAllByAccountProfileActivatedTrueAndLocationInAndLastModifiedDate(
			List<Location> locations, boolean status, LocalDateTime lastModifiedDate, Pageable pageable);

	// not used
	@Query("select locationAccountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.accountProfile in  ?1 ")
	List<LocationAccountProfile> findLocationAccountProfileByAccountProfileIn(List<AccountProfile> accountProfiles);

	@Query("select locationAccountProfile.accountProfile.pid,locationAccountProfile.location.pid from LocationAccountProfile locationAccountProfile where locationAccountProfile.accountProfile in  ?1 ")
	List<Object[]> findLocationAccountProfileByAccountProfilesIn(List<AccountProfile> accountProfiles);

	@Query("select locationAccountProfile.location from LocationAccountProfile locationAccountProfile where locationAccountProfile.accountProfile.pid =  ?1 ")
	List<Location> findLocationAccountProfileByAccountProfileIn(String accountPid);

	@Query("select locationAccountProfile.accountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in  ?1 order by locationAccountProfile.accountProfile.name asc")
	List<AccountProfile> findAccountProfilesByUserLocationsOrderByAccountProfilesName(List<Location> locations);

	@Query(value = "select account_profile_id from tbl_location_account_profile where location_id in  ?1", nativeQuery = true)
	Set<BigInteger> findAccountProfileIdsByUserLocationsOrderByAccountProfilesName(Set<Long> locationIds);

	@Query("select locationAccountProfile.accountProfile.pid from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in  ?1 order by locationAccountProfile.accountProfile.name asc")
	Set<String> findAccountProfilePidsByUserLocationsOrderByAccountProfilesName(List<Location> locations);

	@Query("select locationAccountProfile.accountProfile.pid from LocationAccountProfile locationAccountProfile where locationAccountProfile.location.pid in ?1")
	Set<String> findAccountProfilePidsByLocationPidIn(List<String> locationPids);

	@Query("select locationAccountProfile.accountProfile.pid from LocationAccountProfile locationAccountProfile where locationAccountProfile.location.id in ?1")
	Set<String> findAccountProfilePidsByLocationIdIn(Set<Long> locationIds);

	@Query("select la.accountProfile.pid, la.accountProfile.name from LocationAccountProfile la where la.location.id in ?1 order by la.accountProfile.name ASC")
	List<Object[]> findAccountProfilesByLocationIdIn(Set<Long> locationIds);

	@Query("select locationAccountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.company.id = ?#{principal.companyId} and locationAccountProfile.accountProfile.pid = ?1 ")
	List<LocationAccountProfile> findAllByAccountProfilePid(String accountProfilePid);

	@Query("select locationAccountProfile.location from LocationAccountProfile locationAccountProfile where locationAccountProfile.company.id = ?#{principal.companyId} and locationAccountProfile.accountProfile.pid = ?1 ")
	List<Location> findAllLocationByAccountProfilePid(String accountProfilePid);

	@Query("select locationAccountProfile.location from LocationAccountProfile locationAccountProfile where locationAccountProfile.company.id = ?#{principal.companyId} and locationAccountProfile.accountProfile.pid in ?1 ")
	List<Location> findAllLocationByAccountProfilePidIn(List<String> accountProfilePid);

	@Query("select locationAccountProfile.accountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location.pid in ?1 and locationAccountProfile.accountProfile.activated = ?2")
	List<AccountProfile> findAccountProfileByLocationPidInAndAccountProfileActivated(List<String> locationPids,
			boolean active);

	@Query("select locationAccountProfile.accountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location.pid = ?1 and locationAccountProfile.accountProfile.activated = ?2")
	List<AccountProfile> findAccountProfileByLocationPidAndAccountProfileActivated(String locationPid, boolean active);

	@Query("select locationAccountProfile.accountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in  ?1 and locationAccountProfile.accountProfile.accountType.pid in ?2 and locationAccountProfile.accountProfile.importStatus= ?3 order by locationAccountProfile.accountProfile.name asc")
	List<AccountProfile> findAccountProfilesByUserLocationsAndAccountTypePidInOrderByAccountProfilesName(
			List<Location> locations, List<String> accountTypePids, boolean status);

	@Query("select locationAccountProfile.accountProfile.pid from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in  ?1 and locationAccountProfile.accountProfile.accountType.pid in ?2 and locationAccountProfile.accountProfile.importStatus= ?3 order by locationAccountProfile.accountProfile.name asc")
	List<String> findAccountProfilePidsByUserLocationsAndAccountTypePidInOrderByAccountProfilesName(
			List<Location> locations, List<String> accountTypePids, boolean status);

	@Query("select locationAccountProfile.accountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in  ?1 and locationAccountProfile.accountProfile.accountType.pid in ?2 order by locationAccountProfile.accountProfile.name asc")
	List<AccountProfile> findAccountByUserLocationsAndAccountTypePidInOrderByAccountProfilesName(
			List<Location> locations, List<String> accountTypePids);

	@Query("select locationAccountProfile.accountProfile.pid from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in  ?1 and locationAccountProfile.accountProfile.accountType.pid in ?2 order by locationAccountProfile.accountProfile.name asc")
	List<String> findAccountPidByUserLocationsAndAccountTypePidInOrderByAccountProfilesName(List<Location> locations,
			List<String> accountTypePids);

	@Query("select locationAccountProfile.accountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in  ?1 and locationAccountProfile.accountProfile.importStatus = ?2 order by locationAccountProfile.accountProfile.name asc")
	List<AccountProfile> findAccountByUserLocationsAndImportedStatusOrderByAccountProfilesName(List<Location> locations,
			boolean status);

	@Query("select locationAccountProfile.accountProfile.pid from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in  ?1 and locationAccountProfile.accountProfile.importStatus = ?2 order by locationAccountProfile.accountProfile.name asc")
	List<String> findAccountPidByUserLocationsAndImportedStatusOrderByAccountProfilesName(List<Location> locations,
			boolean status);

	@Query("select locationAccountProfile.accountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in  ?1 order by locationAccountProfile.accountProfile.name asc")
	List<AccountProfile> findAccountByUserLocationsAndAllImportedStatusOrderByAccountProfilesName(
			List<Location> locations);

	@Query("select locationAccountProfile.accountProfile.pid from LocationAccountProfile locationAccountProfile where locationAccountProfile.location in  ?1 order by locationAccountProfile.accountProfile.name asc")
	List<String> findAccountPidByUserLocationsAndAllImportedStatusOrderByAccountProfilesName(List<Location> locations);

	LocationAccountProfile findByAccountProfile(AccountProfile accountProfile);

	@Query("select locationAccountProfile.accountProfile.id from LocationAccountProfile locationAccountProfile where locationAccountProfile.location.id in ?1")
	Set<Long> findAccountProfileIdsByUserLocationIdsIn(Set<Long> locationIds);

	@Query("select lap.id, lap.accountProfile.pid, lap.location.pid from LocationAccountProfile lap where lap.company.id in ?1")
	List<Object[]> findAllLocationAccountProfilesByCompanyId(Long companyId);

	@Transactional
	@Modifying
	@Query("delete from LocationAccountProfile locationAccountProfile where locationAccountProfile.company.id = ?1 and locationAccountProfile.id in ?2")
	void deleteByIdIn(Long companyId, List<Long> ids);

	@Transactional
	@Modifying
	@Query("delete from LocationAccountProfile locationAccountProfile where locationAccountProfile.company.id = ?1")
	void deleteByCompany(Long companyId);

	@Transactional
	@Modifying
	@Query("delete from LocationAccountProfile locationAccountProfile where locationAccountProfile.company.id = ?1 and locationAccountProfile.accountProfile.id NOT IN (?2)")
	void deleteByCompanyExcludeDefault(Long companyId, List<Long> accountIds);

	@Query("select locationAccountProfile from LocationAccountProfile locationAccountProfile  where locationAccountProfile.accountProfile.pid in ?1")
	List<LocationAccountProfile> findAllLocationByAccountProfilePids(List<String> accountProfilePids);

	@Query("select locationAccountProfile.location.pid,locationAccountProfile.location.name,locationAccountProfile.accountProfile.pid,locationAccountProfile.accountProfile.name from LocationAccountProfile locationAccountProfile  where locationAccountProfile.accountProfile.pid in ?1 and locationAccountProfile.accountProfile.activated = true")
	List<Object[]> findAllLocationObjectsByAccountProfilePids(List<String> accountProfilePids);

	Page<LocationAccountProfile> findDistinctAccountProfileByAccountProfileActivatedTrueAndLocationIdInAndCompanyIdOrderByIdAsc(
			Set<Long> locationIds, long companyId, Pageable pageable);

	List<LocationAccountProfile> findDistinctAccountProfileByAccountProfileActivatedTrueAndLocationIdInAndCompanyIdOrderByIdAsc(
			Set<Long> locationIds, long companyId);

   @Query(" SELECT DISTINCT lap FROM LocationAccountProfile lap WHERE lap.location.id IN ?1 AND lap.accountProfile.activated = true AND lap.company.id = ?2 AND (lap.lastModifiedDate > ?3 OR lap.accountProfile.lastModifiedDate > ?3 OR lap.location.lastModifiedDate > ?3)ORDER BY lap.id ASC")
	Page<LocationAccountProfile> findDistinctAccountProfileByAccountProfileActivatedTrueAndLocationIdInAndCompanyIdAndLastModifiedDateOrderByIdAsc(
			Set<Long> locationIds, long companyId, LocalDateTime lastSyncdate, Pageable pageable);

	@Query("select lap.id, lap.accountProfile.id, lap.location.id,lap.accountProfile.pid, lap.location.pid from LocationAccountProfile lap where lap.accountProfile.pid in ?1")
	List<Object[]> findAccontProfileIdAndLocationIdByAccountProfileIds(List<String> accountProfilePids);

	@Query("select locationAccountProfile.accountProfile.pid from LocationAccountProfile locationAccountProfile where locationAccountProfile.location.pid in ?1 and locationAccountProfile.location.activated=true order by locationAccountProfile.accountProfile.name asc")
	List<String> findAccountProfilePidByLocationPidIn(List<String> locationPids);

	@Query("select locationAccountProfile.accountProfile.pid,locationAccountProfile.accountProfile.name from LocationAccountProfile locationAccountProfile where locationAccountProfile.location.pid in ?1 and locationAccountProfile.location.activated=true order by locationAccountProfile.accountProfile.name asc")
	List<Object[]> findAccountProfilePidByLocationPids(List<String> locationPids);

	@Query("select locationAccountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.location.pid in ?1 and locationAccountProfile.location.activated=true order by locationAccountProfile.accountProfile.name asc")
	List<LocationAccountProfile> findByLocationPids(List<String> locationPids);

	@Query("select locationAccountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.company.id in ?1 and locationAccountProfile.location.activated=true order by locationAccountProfile.accountProfile.name asc")
	List<LocationAccountProfile> findByCompanyID(long companyId);

	@Query("select locationAccountProfile from LocationAccountProfile locationAccountProfile where locationAccountProfile.accountProfile.pid in ?1 and locationAccountProfile.location.activated=true order by locationAccountProfile.accountProfile.name asc")
	List<LocationAccountProfile> findByAccountPids(List<String> accountpids);

	@Query("select locationAccountProfile.accountProfile.id from LocationAccountProfile locationAccountProfile where locationAccountProfile.location.id in  ?1 and locationAccountProfile.accountProfile.activated = 'true'")
	Set<Long> findAccountProfileIdsByUserLocationsOrderByAccountProfilesNameAndActivated(Set<Long> locationIds);

	@Query("select locationAccountProfile from LocationAccountProfile locationAccountProfile  where locationAccountProfile.accountProfile.pid = ?1")
	List<LocationAccountProfile> findAllLocationByAccountProfilePids(String accountProfilePid );

}
