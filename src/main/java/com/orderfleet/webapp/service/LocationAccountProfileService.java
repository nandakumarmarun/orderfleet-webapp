package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;

/**
 * Spring Data JPA repository for the LocationAccountProfile entity.
 * 
 * @author Muhammed Riyas T
 * @since August 31, 2016
 */
public interface LocationAccountProfileService {

	void save(String locationPid, String assignedAccountProfiles);

	List<AccountProfileDTO> findAccountProfileByLocationPid(String locationPid);

	List<LocationAccountProfile> findAllByCompany();

	Page<AccountProfileDTO> findAccountProfilesByCurrentUserLocations(Pageable pageable);

	List<AccountProfileDTO> findAccountProfilesByCurrentUserLocations();

	List<LocationAccountProfile> findAllByAccountProfilePidAndLocationPid(String accountProfilePid, String locationPid);

	void deleteByCompanyId(Long companyId);

	List<LocationAccountProfile> findAllByCompanyAndAccountProfilePid(Long companyId, String accountProfilePid);

	Page<AccountProfileDTO> findAccountProfilesByUserLocationsAndAccountProfileActivated(int page, int size);

	List<AccountProfileDTO> findAccountProfileByLocationPidIn(List<String> locationPids);

	List<LocationAccountProfileDTO> findByUserLocationsAndAccountProfileActivated(int limit, int offset);

	List<LocationAccountProfile> findAllByCompanyId(Long companyId);

	Page<AccountProfileDTO> findAllByAccountProfileActivatedTrueAndLocationInAndLastModifiedDate(int page, int size,
			LocalDateTime lastSyncdate);

	void saveLocationAccountProfile(Location location, String accountProfilePid);

	List<LocationAccountProfileDTO> findByUserLocationsAndAccountProfileActivatedAndLastModified(int limit, int offset,
			LocalDateTime lastModified);

	List<AccountProfileDTO> findAccountProfileByLocationPidInAndActivated(List<String> locationPids, boolean activated);
	
	List<LocationAccountProfileDTO> findLocationByAccountProfilePid(String accountProfilePid);
	
	List<AccountProfileDTO> findAllAccountProfileByLocationPidInAndActivated(List<String> locationPids, boolean activated);
	
	List<AccountProfileDTO> findAccountProfileByLocationPidAndActivated(String locationPid, boolean activated);
	
	List<AccountProfileDTO> findAccountProfilesByCurrentUserLocationsAndAccountTypePidIn(List<String>accountTypePids ,boolean importStatus);
	
	List<AccountProfileDTO> findAccountByCurrentUserLocationsAndAccountTypePidIn(List<String>accountTypePids);
	
	List<AccountProfileDTO> findAccountProfilesByCurrentUserLocationsAndImpotedStatus(boolean importStatus);
	
	List<AccountProfileDTO> findAccountByCurrentUserLocationsAndAllImpotedStatus();

	void saveLocationAccountProfileSingle(String locationPid, String accountProfilePid);
	
	List<AccountProfileDTO> findAccountProfilesByUsers(List<Long> userIds);
	
	Set<String> findAccountProfilePidsByUsers(List<Long> userIds);
	
	List<LocationAccountProfile> findAllLocationAccountProfiles(Long companyId);
}
