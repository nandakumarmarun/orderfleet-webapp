package com.orderfleet.webapp.service.impl;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;
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
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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
		log.debug("*********************************"+locationPid,assignedAccountProfile);
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Location location = locationRepository.findOneByPid(locationPid).get();
		
		String[] accountProfiles = assignedAccountProfile.split(",");

		List<LocationAccountProfile> locationAccountProfile = new ArrayList<>();
		for (String accountProfilePid : accountProfiles) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			AccountProfile accountProfile = accountProfileRepository.findOneByPid(accountProfilePid).get();
			String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
			locationAccountProfile.add(new LocationAccountProfile(location, accountProfile, company));
		}
		
		if (accountProfiles.length > 1) {
			locationAccountProfileRepository.deleteByLocationPid(locationPid);
		}
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

		locationAccountProfile
		.sort((LocationAccountProfile s1, LocationAccountProfile s2) -> s1.getAccountProfile().getName().compareTo(s2.getAccountProfile().getName()));
		
		
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
		// List<Location> locations =
		// employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();

		Set<Long> locationIds = employeeProfileLocationRepository.findLocationIdsByEmployeeProfileIsCurrentUser();

		// List<Long> locationIds = locations.stream().map(p ->
		// p.getId()).collect((Collectors.toList()));
		// get accounts in employee locations
		List<AccountProfile> result = new ArrayList<>();
		if (!locationIds.isEmpty()) {

			Set<BigInteger> apIds = locationAccountProfileRepository
					.findAccountProfileIdsByUserLocationsOrderByAccountProfilesName(locationIds);

			Set<Long> accountProfileIds = new HashSet<>();

			for (BigInteger apId : apIds) {
				accountProfileIds.add(apId.longValue());
			}

			// List<AccountProfile> accountProfiles =
			// locationAccountProfileRepository.findAccountProfilesByUserLocationsOrderByAccountProfilesName(locations);

			if (accountProfileIds.size() > 0) {
				DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id1 = "AP_QUERY_137" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 ="get all by compId and IdsIn";
				LocalDateTime startLCTime1 = LocalDateTime.now();
				String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
				String startDate1 = startLCTime1.format(DATE_FORMAT1);
				logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
				List<AccountProfile> accountProfiles = accountProfileRepository
						.findAllByCompanyIdAndIdsIn(accountProfileIds);
				 String flag1 = "Normal";
					LocalDateTime endLCTime1 = LocalDateTime.now();
					String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
					String endDate1 = startLCTime1.format(DATE_FORMAT1);
					Duration duration1 = Duration.between(startLCTime1, endLCTime1);
					long minutes1 = duration1.toMinutes();
					if (minutes1 <= 1 && minutes1 >= 0) {
						flag1 = "Fast";
					}
					if (minutes1 > 1 && minutes1 <= 2) {
						flag1 = "Normal";
					}
					if (minutes1 > 2 && minutes1 <= 10) {
						flag1 = "Slow";
					}
					if (minutes1 > 10) {
						flag1 = "Dead Slow";
					}
			                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
							+ description1);
				// remove duplicates
				result = accountProfiles.parallelStream().distinct().collect(Collectors.toList());
			}
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
		// List<Location> locations =
		// employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();
		// get accounts in employee locations
		List<AccountProfile> result = new ArrayList<>();
		log.info("===============================================================");
		log.info("get accountProfiles..........");
		log.info("Page : {}" + page);
		log.info("Count : {}" + count);

		Set<Long> locationIds = employeeProfileLocationRepository.findLocationIdsByEmployeeProfileIsCurrentUser();
		long companyId = SecurityUtils.getCurrentUsersCompanyId();

		if (locationIds.size() > 0) {
			Page<LocationAccountProfile> accountProfiles = locationAccountProfileRepository
					.findDistinctAccountProfileByAccountProfileActivatedTrueAndLocationIdInAndCompanyIdOrderByIdAsc(
							locationIds, companyId, new PageRequest(page, count));

			result = accountProfiles.getContent().stream().map(la -> la.getAccountProfile())
					.collect(Collectors.toList());

		}

//		if (locationIds.size() > 0) {
//
//			int limit = count;
//			int offset = page * count;
//			Page<LocationAccountProfile> accountProfiles = locationAccountProfileRepository
//					.findDistinctAccountProfileByAccountProfileActivatedTrueAndLocationInOrderByIdAsc(locations,
//							new PageRequest(page, count));
//
//			List<Object[]> locAccProfiles = locationAccountProfileRepository
//					.findDistinctAccountProfileByAccountProfileActivatedTrueAndLocationInOrderByIdAscPaginated(
//							locationIds, limit, offset);
//			// List<LocationAccountProfileDTO> accountProfiles = new ArrayList<>();
//
//			List<String> accountPids = new ArrayList<>();
//
//			for (Object[] objects : locAccProfiles) {
//				accountPids.add(objects[0].toString());
//			}
//
//			result = accountProfileRepository.findAllByAccountProfilePids(accountPids);
//
//		}
		  DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ANTS_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by compId and enable true";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountNameTextSettings> accountNameTextSettings = accountNameTextSettingsRepository
				.findAllByCompanyIdAndEnabledTrue(SecurityUtils.getCurrentUsersCompanyId());

        String flag = "Normal";
LocalDateTime endLCTime = LocalDateTime.now();
String endTime = endLCTime.format(DATE_TIME_FORMAT);
String endDate = startLCTime.format(DATE_FORMAT);
Duration duration = Duration.between(startLCTime, endLCTime);
long minutes = duration.toMinutes();
if (minutes <= 1 && minutes >= 0) {
	flag = "Fast";
}
if (minutes > 1 && minutes <= 2) {
	flag = "Normal";
}
if (minutes > 2 && minutes <= 10) {
	flag = "Slow";
}
if (minutes > 10) {
	flag = "Dead Slow";
}
        logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
		+ description);

		Page<AccountProfileDTO> accountProfileDtoPage = new PageImpl<>(
				accountProfileMapper.accountProfilesToAccountProfileDTOs(result));
		if (accountNameTextSettings.size() > 0) {
			for (AccountProfileDTO dto : accountProfileDtoPage) {
				String name = " (";
				for (AccountNameTextSettings accountNameText : accountNameTextSettings) {
					if (accountNameText.getName().equals("ALIAS")) {
						if (dto.getAlias() != null && !dto.getAlias().isEmpty()) {
							name += dto.getAlias() + ",";
						}
					}
				}
				name = name.substring(0, name.length() - 1);
				if (name.length() > 1) {
					name += ")";
				}
				dto.setName(dto.getName() + name);
			}
		}

		System.out.println("===============================================================");
		return accountProfileDtoPage;
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
			List<Object[]> locAccProfiles = locationAccountProfileRepository
					.findByLocationInAndAccountProfileActivatedPaginated(locationIds, limit, offset);
			for (Object[] objects : locAccProfiles) {
				LocationAccountProfileDTO locAPDto = new LocationAccountProfileDTO(objects[0].toString(),
						objects[1].toString(), objects[2].toString(), objects[3].toString(),
						((java.sql.Timestamp) objects[4]).toLocalDateTime());
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
		// List<Location> locations =
		// employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();

		Set<Long> locationIds = employeeProfileLocationRepository.findLocationIdsByEmployeeProfileIsCurrentUser();
		long companyId = SecurityUtils.getCurrentUsersCompanyId();

		// get accounts in employee locations
		List<AccountProfile> result = new ArrayList<>();
		if (locationIds.size() > 0) {
//			Page<LocationAccountProfile> accountProfiles = locationAccountProfileRepository
//					.findAllByAccountProfileActivatedTrueAndLocationInAndLastModifiedDate(locations, true, lastSyncdate,
//							new PageRequest(page, count));

			Page<LocationAccountProfile> accountProfiles = locationAccountProfileRepository
					.findDistinctAccountProfileByAccountProfileActivatedTrueAndLocationIdInAndCompanyIdAndLastModifiedDateOrderByIdAsc(
							locationIds, companyId, lastSyncdate, new PageRequest(page, count));
			result = accountProfiles.getContent().stream().map(la -> la.getAccountProfile())
					.collect(Collectors.toList());
		}
		return new PageImpl<>(accountProfileMapper.accountProfilesToAccountProfileDTOs(result));
	}

	@Override
	public void saveLocationAccountProfile(Location location, String accountProfilePid) {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountProfile accountProfile = accountProfileRepository.findOneByPid(accountProfilePid).get();
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
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
	public List<LocationAccountProfileDTO> findByUserLocationsAndAccountProfileActivatedAndLastModified(int limit,
			int offset, LocalDateTime lastModified) {
		// current user employee locations
		Set<Long> locationIds = employeeProfileLocationRepository.findLocationIdsByEmployeeProfileIsCurrentUser();
		List<LocationAccountProfileDTO> result = new ArrayList<>();
		if (!locationIds.isEmpty()) {
			List<Object[]> locAccProfiles = locationAccountProfileRepository
					.findByLocationInAndAccountProfileActivatedPaginated(locationIds, lastModified, limit, offset);
			for (Object[] objects : locAccProfiles) {
				LocationAccountProfileDTO locAPDto = new LocationAccountProfileDTO(objects[0].toString(),
						objects[1].toString(), objects[2].toString(), objects[3].toString(),
						((java.sql.Timestamp) objects[4]).toLocalDateTime());
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
	public List<LocationDTO> findAllLocationByAccountProfilePid(String accountProfilePid) {
		List<Location> locationList = locationAccountProfileRepository
				.findAllLocationByAccountProfilePid(accountProfilePid);
		List<LocationDTO> result = convertLocationToLocationDTO(locationList);
		return result;
	}

	private List<LocationDTO> convertLocationToLocationDTO(List<Location> locationList) {
		List<LocationDTO> result = new ArrayList<>();

		for (Location loc : locationList) {
			result.add(new LocationDTO(loc));
		}

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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountProfile> accountProfile = accountProfileRepository.findOneByPid(accountProfilePid);
		  String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
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
			return locationAccountProfileRepository
					.findAccountProfilePidsByUserLocationsOrderByAccountProfilesName(locations);
		}
		return Collections.emptySet();
	}

	@Override
	public List<LocationAccountProfile> findAllLocationAccountProfiles(Long companyId) {
		List<LocationAccountProfile> locationAccountProfiles = new ArrayList<>();
		List<Object[]> list = locationAccountProfileRepository.findAllLocationAccountProfilesByCompanyId(companyId);
		for (Object[] object : list) {
			if (object[1] == null || object[2] == null) {
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
