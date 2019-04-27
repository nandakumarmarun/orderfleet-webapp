package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountNameTextSettings;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductNameTextSettings;
import com.orderfleet.webapp.repository.AccountNameTextSettingsRepository;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

/**
 * Service Implementation for managing LocationAccountProfile.
 * 
 * @author Muhammed Riyas T
 * @since August 30, 2016
 */
@Service
@Transactional
public class LocationAccountProfileServiceImpl implements LocationAccountProfileService {
	private final Logger log = LoggerFactory.getLogger(LocationAccountProfileServiceImpl.class);
 
	@Inject
	private LocationRepository locationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private AccountNameTextSettingsRepository accountNameTextSettingsRepository;

	@Override
	public void save(String locationPid, String assignedAccountProfile) {
		log.debug("Request to save Location AccountProfile");
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Location location = locationRepository.findOneByPid(locationPid).get();
		String[] accountProfiles = assignedAccountProfile.split(",");
		List<LocationAccountProfile> locationAccountProfile = new ArrayList<>();
		for (String accountProfilePid : accountProfiles) {
			AccountProfile accountProfile = accountProfileRepository.findOneByPid(accountProfilePid).get();
			locationAccountProfile.add(new LocationAccountProfile(location, accountProfile, company));
		}
		locationAccountProfileRepository.deleteByLocationPid(locationPid);
		locationAccountProfileRepository.save(locationAccountProfile);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountProfileDTO> findAccountProfileByLocationPid(String locationPid) {
		log.debug("Request to get all AccountProfile under in a locations");
		List<AccountProfile> accountProfileList = locationAccountProfileRepository
				.findAccountProfileByLocationPid(locationPid);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findAccountProfileByLocationPidIn(List<String> locationPids) {
		List<AccountProfile> accountProfileList = locationAccountProfileRepository
				.findAccountProfileByLocationPidIn(locationPids);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	/**
	 * Get all the accountProfiles.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<LocationAccountProfile> findAllByCompany() {
		log.debug("Request to get all AccountProfile");
		List<LocationAccountProfile> locationAccountProfile = locationAccountProfileRepository.findAllByCompanyId();
		return locationAccountProfile;
	}

	@Override
	public Page<AccountProfileDTO> findAccountProfilesByCurrentUserLocations(Pageable pageable) {
		// current user employee locations
		List<Location> locations = employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();

		// get accounts in employee locations
		List<AccountProfile> result = new ArrayList<>();
		if (locations.size() > 0) {
			Page<AccountProfile> accountProfiles = locationAccountProfileRepository
					.findAccountProfilesByUserLocations(locations, pageable);
			// remove duplicates
			result = accountProfiles.getContent().parallelStream().distinct().collect(Collectors.toList());
		}
		return new PageImpl<>(accountProfileMapper.accountProfilesToAccountProfileDTOs(result));
	}

	@Override
	public List<AccountProfileDTO> findAccountProfilesByCurrentUserLocations() {
		// current user employee locations
		List<Location> locations = employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();

		// get accounts in employee locations
		List<AccountProfile> result = new ArrayList<>();
		if (!locations.isEmpty()) {
			List<AccountProfile> accountProfiles = locationAccountProfileRepository
					.findAccountProfilesByUserLocationsOrderByAccountProfilesName(locations);
			// remove duplicates
			result = accountProfiles.parallelStream().distinct().collect(Collectors.toList());
		}
		return accountProfileMapper.accountProfilesToAccountProfileDTOs(result);
	}

	@Override
	public List<LocationAccountProfile> findAllByAccountProfilePidAndLocationPid(String accountProfilePid,
			String locationPid) {
		log.debug("Request to get all AccountProfile");
		return locationAccountProfileRepository.findAllByAccountProfilePidAndLocationPid(accountProfilePid,
				locationPid);

	}

	@Override
	public void deleteByCompanyId(Long companyId) {
		locationAccountProfileRepository.deleteByCompanyId(companyId);
	}

	@Override
	public List<LocationAccountProfile> findAllByCompanyAndAccountProfilePid(Long companyId, String accountProfilePid) {
		log.debug("Request to get all LocationAccountProfile by AccountProfile");
		return locationAccountProfileRepository.findAllByCompanyAccountProfilePid(accountProfilePid, companyId);
	}

	/**
	 * get all accountProfiles activated true
	 * 
	 * @author Sarath T
	 * 
	 * @since Jan 20, 2017
	 */
	@Override
	public Page<AccountProfileDTO> findAccountProfilesByUserLocationsAndAccountProfileActivated(int page, int count) {
		// current user employee locations
		List<Location> locations = employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();
		// get accounts in employee locations
		List<AccountProfile> result = new ArrayList<>();
		log.info("===============================================================");
		log.info("get accountProfiles..........");
		log.info("Page : {}" + page);
		log.info("Count : {}" + count);
		if (locations.size() > 0) {
			Page<LocationAccountProfile> accountProfiles = locationAccountProfileRepository
					.findDistinctAccountProfileByAccountProfileActivatedTrueAndLocationInOrderByIdAsc(locations,
							new PageRequest(page, count));
			result = accountProfiles.getContent().stream().map(la -> la.getAccountProfile())
					.collect(Collectors.toList());
			
		}
//appending alias configurable
		List<AccountNameTextSettings> accountNameTextSettings = accountNameTextSettingsRepository
				.findAllByCompanyIdAndEnabledTrue(SecurityUtils.getCurrentUsersCompanyId());
		if (accountNameTextSettings.size() > 0) {
			for (AccountProfile  accountProfile : result) {
				String name = " (";
				for (AccountNameTextSettings accountNameText : accountNameTextSettings) {
					
					if (accountNameText.getName().equals("ALIAS")) {
						if (accountProfile.getAlias() != null && !accountProfile.getAlias().isEmpty()) {
							name += accountProfile.getAlias() + ",";
						}
							
					} /*else if (accountNameText.getName().equals("MRP")) {
						name += accountProfile.getMrp() + ",";
					} */
				}
				name = name.substring(0, name.length() - 1);
				if (name.length() > 1) {
					name += ")";
				}
				accountProfile.setName(accountProfile.getName() + name);
			}
		}
		System.out.println("===============================================================");
		return new PageImpl<>(accountProfileMapper.accountProfilesToAccountProfileDTOs(result));
	}

	/**
	 * get location wise accountProfiles activated true
	 * 
	 * @author Riyas
	 * @since Jan 28, 2017
	 * 
	 * @author Shaheer
	 * @since July 26, 2018
	 */
	@Override
	public List<LocationAccountProfileDTO> findByUserLocationsAndAccountProfileActivated(int limit, int offset) {
		// current user employee locations
		Set<Long> locationIds = employeeProfileLocationRepository.findLocationIdsByEmployeeProfileIsCurrentUser();
		// get accounts in employee locations
		List<LocationAccountProfileDTO> result = new ArrayList<>();
		if (!locationIds.isEmpty()) {
			List<Object[]> locAccProfiles = locationAccountProfileRepository.findByLocationInAndAccountProfileActivatedPaginated(locationIds, limit, offset);
			for (Object[] objects : locAccProfiles) {
				LocationAccountProfileDTO locAPDto = new LocationAccountProfileDTO(objects[0].toString(), objects[1].toString() ,objects[2].toString(),objects[3].toString(),((java.sql.Timestamp)objects[4]).toLocalDateTime());
				result.add(locAPDto);
			}
		}
		return result;
	}

	/**
	 * Get all the accountProfiles.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<LocationAccountProfile> findAllByCompanyId(Long companyId) {
		log.debug("Request to get all AccountProfile");
		List<LocationAccountProfile> locationAccountProfile = locationAccountProfileRepository
				.findAllByCompanyId(companyId);
		return locationAccountProfile;
	}

	/**
	 * get all accountProfiles activated true And modifiedDateCheck.
	 * 
	 * @author Sarath T
	 * 
	 * @since Mar 30, 2017
	 */
	@Override
	public Page<AccountProfileDTO> findAllByAccountProfileActivatedTrueAndLocationInAndLastModifiedDate(int page,
			int count, LocalDateTime lastSyncdate) {
		// current user employee locations
		List<Location> locations = employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();

		// get accounts in employee locations
		List<AccountProfile> result = new ArrayList<>();
		if (locations.size() > 0) {
			Page<LocationAccountProfile> accountProfiles = locationAccountProfileRepository
					.findAllByAccountProfileActivatedTrueAndLocationInAndLastModifiedDate(locations, true, lastSyncdate,
							new PageRequest(page, count));
			result = accountProfiles.getContent().stream().map(la -> la.getAccountProfile())
					.collect(Collectors.toList());
		}
		return new PageImpl<>(accountProfileMapper.accountProfilesToAccountProfileDTOs(result));
	}

	@Override
	public void saveLocationAccountProfile(Location location, String accountProfilePid) {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		AccountProfile accountProfile = accountProfileRepository.findOneByPid(accountProfilePid).get();
		LocationAccountProfile locationAccountProfile = new LocationAccountProfile(location, accountProfile, company);
		locationAccountProfileRepository.save(locationAccountProfile);
	}

	/**
	 * get location wise accountProfiles activated true
	 * 
	 * @author Sarath T
	 * @since April 7, 2017
	 * 
	 * @author Shaheer
	 * @since July 26, 2018
	 */
	@Override
	public List<LocationAccountProfileDTO> findByUserLocationsAndAccountProfileActivatedAndLastModified(int limit, int offset,
			LocalDateTime lastModified) {
		// current user employee locations
		Set<Long> locationIds = employeeProfileLocationRepository.findLocationIdsByEmployeeProfileIsCurrentUser();
		List<LocationAccountProfileDTO> result = new ArrayList<>();
		if (!locationIds.isEmpty()) {
			List<Object[]> locAccProfiles = locationAccountProfileRepository
					.findByLocationInAndAccountProfileActivatedPaginated(locationIds, lastModified, limit, offset);
			for (Object[] objects : locAccProfiles) {
				LocationAccountProfileDTO locAPDto = new LocationAccountProfileDTO(objects[0].toString(), objects[1].toString() ,objects[2].toString(),objects[3].toString(),((java.sql.Timestamp)objects[4]).toLocalDateTime());
				result.add(locAPDto);
			}
		}
		return result;
	}

	@Override
	public List<AccountProfileDTO> findAccountProfileByLocationPidInAndActivated(List<String> locationPids,
			boolean activated) {
		List<AccountProfile> accountProfileList = locationAccountProfileRepository
				.findAccountProfileByLocationPidIn(locationPids);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public List<LocationAccountProfileDTO> findLocationByAccountProfilePid(String accountProfilePid) {
		List<LocationAccountProfile> locationAccountProfiles = locationAccountProfileRepository
				.findAllByAccountProfilePid(accountProfilePid);
		List<LocationAccountProfileDTO> locationAccountProfileDTOs = new ArrayList<>();
		for (LocationAccountProfile locationAccountProfile : locationAccountProfiles) {
			LocationAccountProfileDTO locationAccountProfileDTO = new LocationAccountProfileDTO(locationAccountProfile);
			locationAccountProfileDTOs.add(locationAccountProfileDTO);
		}
		return locationAccountProfileDTOs;
	}

	@Override
	public List<AccountProfileDTO> findAllAccountProfileByLocationPidInAndActivated(List<String> locationPids,
			boolean activated) {
		List<AccountProfile> accountProfileList = locationAccountProfileRepository
				.findAccountProfileByLocationPidInAndAccountProfileActivated(locationPids, activated);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findAccountProfileByLocationPidAndActivated(String locationPid, boolean activated) {
		List<AccountProfile> accountProfileList = locationAccountProfileRepository
				.findAccountProfileByLocationPidAndAccountProfileActivated(locationPid, activated);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findAccountProfilesByCurrentUserLocationsAndAccountTypePidIn(
			List<String> accountTypePids, boolean importStatus) {
		List<Location> locations = employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();

		// get accounts in employee locations
		List<AccountProfile> result = new ArrayList<>();
		if (locations.size() > 0) {
			List<AccountProfile> accountProfiles = locationAccountProfileRepository
					.findAccountProfilesByUserLocationsAndAccountTypePidInOrderByAccountProfilesName(locations,
							accountTypePids, importStatus);
			// remove duplicates
			result = accountProfiles.parallelStream().distinct().collect(Collectors.toList());
		}
		return accountProfileMapper.accountProfilesToAccountProfileDTOs(result);
	}

	@Override
	public List<AccountProfileDTO> findAccountByCurrentUserLocationsAndAccountTypePidIn(List<String> accountTypePids) {
		List<Location> locations = employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();

		// get accounts in employee locations
		List<AccountProfile> result = new ArrayList<>();
		if (locations.size() > 0) {
			List<AccountProfile> accountProfiles = locationAccountProfileRepository
					.findAccountByUserLocationsAndAccountTypePidInOrderByAccountProfilesName(locations,
							accountTypePids);
			// remove duplicates
			result = accountProfiles.parallelStream().distinct().collect(Collectors.toList());
		}
		return accountProfileMapper.accountProfilesToAccountProfileDTOs(result);
	}

	@Override
	public List<AccountProfileDTO> findAccountProfilesByCurrentUserLocationsAndImpotedStatus(boolean importStatus) {
		List<Location> locations = employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();

		// get accounts in employee locations
		List<AccountProfile> result = new ArrayList<>();
		if (locations.size() > 0) {
			List<AccountProfile> accountProfiles = locationAccountProfileRepository
					.findAccountByUserLocationsAndImportedStatusOrderByAccountProfilesName(locations, importStatus);
			// remove duplicates
			result = accountProfiles.parallelStream().distinct().collect(Collectors.toList());
		}
		return accountProfileMapper.accountProfilesToAccountProfileDTOs(result);
	}

	@Override
	public List<AccountProfileDTO> findAccountByCurrentUserLocationsAndAllImpotedStatus() {
		List<Location> locations = employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();

		// get accounts in employee locations
		List<AccountProfile> result = new ArrayList<>();
		if (locations.size() > 0) {
			List<AccountProfile> accountProfiles = locationAccountProfileRepository
					.findAccountByUserLocationsAndAllImportedStatusOrderByAccountProfilesName(locations);
			// remove duplicates
			result = accountProfiles.parallelStream().distinct().collect(Collectors.toList());
		}
		return accountProfileMapper.accountProfilesToAccountProfileDTOs(result);
	}

	@Override
	public void saveLocationAccountProfileSingle(String locationPid, String accountProfilePid) {
		log.debug("Request to save Location AccountProfile");
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Optional<AccountProfile> accountProfile = accountProfileRepository.findOneByPid(accountProfilePid);
		Optional<Location> location = locationRepository.findOneByPid(locationPid);
		if (accountProfile.isPresent() && location.isPresent()) {
			LocationAccountProfile locationAccountProfile = new LocationAccountProfile(location.get(),
					accountProfile.get(), company);
			locationAccountProfileRepository.save(locationAccountProfile);
		}
	}

	@Override
	public List<AccountProfileDTO> findAccountProfilesByUsers(List<Long> userIds) {
		// current user's subordinates employee locations
		List<Location> locations = employeeProfileLocationRepository.findLocationsByUserIdIn(userIds);

		// get accounts in employee locations
		List<AccountProfile> result = new ArrayList<>();
		if (!locations.isEmpty()) {
			List<AccountProfile> accountProfiles = locationAccountProfileRepository
					.findAccountProfilesByUserLocationsOrderByAccountProfilesName(locations);
			// remove duplicates
			result = accountProfiles.parallelStream().distinct().collect(Collectors.toList());
		}
		return accountProfileMapper.accountProfilesToAccountProfileDTOs(result);
	}

	@Override
	public Set<String> findAccountProfilePidsByUsers(List<Long> userIds) {
		// current user's subordinates employee locations
		List<Location> locations = employeeProfileLocationRepository.findLocationsByUserIdIn(userIds);
		if (!locations.isEmpty()) {
			return locationAccountProfileRepository.findAccountProfilePidsByUserLocationsOrderByAccountProfilesName(locations);
		}
		return Collections.emptySet();
	}

	@Override
	public List<LocationAccountProfile> findAllLocationAccountProfiles(Long companyId) {
		List<LocationAccountProfile> locationAccountProfiles = new ArrayList<>();
		for (Object[] object : locationAccountProfileRepository.findAllLocationAccountProfilesByCompanyId(companyId)) {
			if(object[1] == null || object[2] == null){
				log.error("Either one of AccountProfilePid or LocationPid is null!");
				continue;
			}
			LocationAccountProfile locationAccountProfile = new LocationAccountProfile();
			AccountProfile accountProfile = new AccountProfile();
			Location location = new Location();
			locationAccountProfile.setId((Long) object[0]);
			accountProfile.setPid(object[1].toString());
			location.setPid(object[2].toString());
			locationAccountProfile.setAccountProfile(accountProfile);
			locationAccountProfile.setLocation(location);
			locationAccountProfiles.add(locationAccountProfile);
		}
		return locationAccountProfiles;
	}
}
