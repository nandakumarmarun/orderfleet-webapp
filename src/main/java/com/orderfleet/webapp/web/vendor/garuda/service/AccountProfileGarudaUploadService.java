package com.orderfleet.webapp.web.vendor.garuda.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.LocationHierarchy;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.vendor.garuda.dto.AccountProfileGarudaDTO;

@Service
public class AccountProfileGarudaUploadService {
	
	private final Logger log = LoggerFactory.getLogger(AccountProfileGarudaUploadService.class);
	
	private final AccountTypeRepository accountTypeRepository;
	private final AccountProfileRepository accountProfileRepository;
	private final UserRepository userRepository;
	private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;
	private final CompanyRepository companyRepository;
	private final LocationRepository locationRepository;
	private final LocationService locationService;
	private final LocationAccountProfileService locationAccountProfileService;
	private final AccountProfileService accountProfileService;
	private final LocationAccountProfileRepository locationAccountProfileRepository;
	private final LocationHierarchyRepository locationHierarchyRepository;

	public AccountProfileGarudaUploadService(AccountTypeRepository accountTypeRepository, AccountProfileRepository accountProfileRepository,  UserRepository userRepository, 
			BulkOperationRepositoryCustom bulkOperationRepositoryCustom, CompanyRepository companyRepository, LocationRepository locationRepository,
			LocationService locationService, LocationAccountProfileService locationAccountProfileService, AccountProfileService accountProfileService,
			LocationAccountProfileRepository locationAccountProfileRepository, LocationHierarchyRepository locationHierarchyRepository) {
		super();
		this.accountTypeRepository = accountTypeRepository;
		this.accountProfileRepository = accountProfileRepository;
		this.userRepository = userRepository;
		this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
		this.companyRepository = companyRepository;
		this.locationRepository = locationRepository;
		this.locationService = locationService;
		this.locationAccountProfileService = locationAccountProfileService;
		this.accountProfileService = accountProfileService;
		this.locationAccountProfileRepository = locationAccountProfileRepository;
		this.locationHierarchyRepository = locationHierarchyRepository;
	}
	
	@Transactional
	public void saveUpdateAccountProfiles(final List<AccountProfileGarudaDTO> accountProfileDTOs) {
		long start = System.nanoTime();
		final Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		final User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();
		// All product must have a division/category, if not, set a default one
		AccountType defaultAccountType = accountTypeRepository.findFirstByCompanyIdOrderByIdAsc(company.getId());
		log.info("Default Account type 123456:" + defaultAccountType.getName());
		// List<AccountProfile> accountProfiles = accountProfileRepository.findByCompanyIdAndAliasIgnoreCaseIn(companyId,
		// apAlias);
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyId();
		List<LocationDTO> locationDtos = new ArrayList<>();
		List<LocationAccountProfileDTO> locationAccountProfileDtos = new ArrayList<>();

		for (AccountProfileGarudaDTO apDto : accountProfileDTOs) {
			AccountProfile accountProfile;
			// check exist by name, only one exist with a name
			Optional<AccountProfile> optionalAP = accountProfiles.stream()
					.filter(pc -> pc.getName().equalsIgnoreCase(apDto.getName())).findAny();
			if (optionalAP.isPresent()) {
				accountProfile = optionalAP.get();
				// if not update, skip this iteration. Not implemented now
				// if (!accountProfile.getThirdpartyUpdate()) { continue; }
			} else {
				accountProfile = new AccountProfile();
				accountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
				accountProfile.setName(apDto.getName());
				accountProfile.setUser(user);
				accountProfile.setCompany(company);
				accountProfile.setAccountStatus(AccountStatus.Unverified);
				accountProfile.setDataSourceType(DataSourceType.TALLY);
				accountProfile.setImportStatus(true);
			}
			if (isValidPhone(apDto.getPhone1())) {
				accountProfile.setPhone1(apDto.getPhone1());
			}
			if (isValidPhone(apDto.getPhone2())) {
				accountProfile.setPhone2(apDto.getPhone2());
			}
			if (isValidEmail(apDto.getEmail1())) {
				accountProfile.setEmail1(apDto.getEmail1());
			}
			accountProfile.setTinNo(apDto.getGstNo());
			accountProfile.setActivated(true);
			accountProfile.setAddress(apDto.getAddress());
			accountProfile.setCity(apDto.getCity());
			accountProfile.setClosingBalance(apDto.getClosingBalance());
			accountProfile.setCustomerId(apDto.getCode());
			// account type
			if (accountProfile.getAccountType() == null) {
				accountProfile.setAccountType(defaultAccountType);
			}

			LocationDTO locationDTO = new LocationDTO();

			LocationAccountProfileDTO locationAccountProfileDto = new LocationAccountProfileDTO();

			if (apDto.getCode() == null || apDto.getCode().equalsIgnoreCase("")) {
				locationAccountProfileDto.setAccountProfileName(apDto.getName());
				locationAccountProfileDto.setLocationName("Territory");
			} else {

				locationDTO.setAlias(apDto.getLocation());
				locationDTO.setName(apDto.getLocation());

				locationDtos.add(locationDTO);

				locationAccountProfileDto.setAccountProfileName(apDto.getName());
				locationAccountProfileDto.setLocationName(apDto.getLocation());
			}

			locationAccountProfileDtos.add(locationAccountProfileDto);
			accountProfile.setDataSourceType(DataSourceType.TALLY);
			saveUpdateAccountProfiles.add(accountProfile);
		}
		
		if (locationDtos.size() > 0) {
			saveUpdateLocations(locationDtos);
			saveUpdateLocationHierarchy(locationDtos);
		}
		bulkOperationRepositoryCustom.bulkSaveAccountProfile(saveUpdateAccountProfiles);

		saveUpdateLocationAccountProfiles(locationAccountProfileDtos);
		
		bulkOperationRepositoryCustom.bulkSaveAccountProfile(saveUpdateAccountProfiles);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		log.info("Sync completed in {} ms", elapsedTime);
	}
	
	@Transactional
	public void saveUpdateLocations(final List<LocationDTO> locationDTOs) {

		log.info("Saving Locations.........");
		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		Set<Location> saveUpdateLocations = new HashSet<>();
		// find all locations
		List<Location> locations = locationRepository.findAllByCompanyId(company.getId());
		for (LocationDTO locDto : locationDTOs) {
			// check exist by name, only one exist with a name
			Optional<Location> optionalLoc = locations.stream().filter(p -> p.getName().equals(locDto.getName()))
					.findAny();
			Location location;
			if (optionalLoc.isPresent()) {
				location = optionalLoc.get();
				// if not update, skip this iteration.
				// if (!location.getThirdpartyUpdate()) {continue;}
			} else {
				location = new Location();
				location.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid());
				location.setName(locDto.getName());
				location.setCompany(company);
			}
			location.setAlias(locDto.getAlias());
			location.setActivated(true);
			Optional<Location> opLoc = saveUpdateLocations.stream()
					.filter(so -> so.getName().equalsIgnoreCase(locDto.getName())).findAny();
			if (opLoc.isPresent()) {
				continue;
			}

			saveUpdateLocations.add(location);
		}
		bulkOperationRepositoryCustom.bulkSaveLocations(saveUpdateLocations);
		locationRepository.flush();
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	@Async
	public void saveUpdateLocationAccountProfiles(final List<LocationAccountProfileDTO> locationAccountProfileDTOs) {

		log.info("Saving Location Account Profiles.........");
		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		List<LocationAccountProfile> newLocationAccountProfiles = new ArrayList<>();
		List<LocationAccountProfile> locationAccountProfiles = locationAccountProfileService
				.findAllLocationAccountProfiles(companyId);
		// delete all assigned location account profile from tally
		// locationAccountProfileRepository.deleteByCompanyIdAndDataSourceTypeAndThirdpartyUpdateTrue(company.getId(),DataSourceType.TALLY);
		List<AccountProfile> accountProfiles = accountProfileService.findAllAccountProfileByCompanyId(companyId);
		List<Location> locations = locationService.findAllLocationByCompanyId(companyId);
		List<Long> locationAccountProfilesIds = new ArrayList<>();

		for (LocationAccountProfileDTO locAccDto : locationAccountProfileDTOs) {
			LocationAccountProfile locationAccountProfile = new LocationAccountProfile();
			// find location

			Optional<Location> loc = locations.stream().filter(pl -> locAccDto.getLocationName().equals(pl.getName()))
					.findFirst();
			// find accountprofile
			// System.out.println(loc.get()+"===Location");

			Optional<AccountProfile> acc = accountProfiles.stream()
					.filter(ap -> locAccDto.getAccountProfileName().equals(ap.getName())).findFirst();
			if (acc.isPresent()) {
				List<Long> locationAccountProfileIds = locationAccountProfiles.stream()
						.filter(lap -> acc.get().getPid().equals(lap.getAccountProfile().getPid()))
						.map(lap -> lap.getId()).collect(Collectors.toList());
				if (locationAccountProfileIds.size() != 0) {
					locationAccountProfilesIds.addAll(locationAccountProfileIds);
				}
				if (loc.isPresent()) {
					locationAccountProfile.setLocation(loc.get());
				} else if (acc.isPresent()) {
					locationAccountProfile.setLocation(locations.get(0));
				}
				locationAccountProfile.setAccountProfile(acc.get());
				locationAccountProfile.setCompany(company);
				newLocationAccountProfiles.add(locationAccountProfile);
			}
		}
		if (locationAccountProfilesIds.size() != 0) {
			locationAccountProfileRepository.deleteByIdIn(companyId, locationAccountProfilesIds);
		}

		locationAccountProfileRepository.save(newLocationAccountProfiles);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}
	
	@Transactional
	private void saveUpdateLocationHierarchy(List<LocationDTO> locationDtos) {
		List<LocationHierarchyDTO> locationHierarchyDTOs = locationDtosToLocationHierarchyDtos(locationDtos);

		log.info("Saving Location Hierarchies.........");

		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();

		Long version;
		// Only one version of a company hierarchy is active at a time
		Optional<LocationHierarchy> locationHierarchy = locationHierarchyRepository
				.findFirstByCompanyIdAndActivatedTrueOrderByIdDesc(companyId);
		if (locationHierarchy.isPresent()) {
			locationHierarchyRepository.updateLocationHierarchyInactivatedFor(ZonedDateTime.now(),
					locationHierarchy.get().getVersion());
			version = locationHierarchy.get().getVersion() + 1;
		} else {
			version = 1L;
		}
		// find all locations
		List<Location> locations = locationRepository.findByCompanyIdAndActivatedTrue(companyId);
		// create hierarchy
		for (LocationHierarchyDTO locationDTO : locationHierarchyDTOs) {

			// check location exist
			Optional<Location> optionalLoc = locations.stream()
					.filter(p -> p.getName().equals(locationDTO.getLocationName())).findAny();
			if (optionalLoc.isPresent()) {
				if (locationDTO.getParentName() != null && locationDTO.getParentName().length() > 0) {
					// check parent location exist
					Optional<Location> optionalParentLoc = locations.stream()
							.filter(p -> p.getName().equals(locationDTO.getParentName())).findAny();
					if (optionalParentLoc.isPresent()) {
						locationHierarchyRepository.insertLocationHierarchyWithParent(version,
								optionalLoc.get().getId(), optionalParentLoc.get().getId());
					}
				} else {
					locationHierarchyRepository.insertLocationHierarchyWithNoParent(version, optionalLoc.get().getId());
				}
			}
		}
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}
	
	private List<LocationHierarchyDTO> locationDtosToLocationHierarchyDtos(List<LocationDTO> locationDtos) {

		Set<String> locations = new HashSet<>();

		for (LocationDTO locationDTO : locationDtos) {

			locations.add(locationDTO.getName());
		}

		List<LocationHierarchyDTO> locationHierarchyDTOs = new ArrayList<>();

		LocationHierarchyDTO defaultLocationHierarchyDTO = new LocationHierarchyDTO();
		defaultLocationHierarchyDTO.setLocationName("Territory");
		defaultLocationHierarchyDTO.setParentName(null);
		locationHierarchyDTOs.add(defaultLocationHierarchyDTO);

		for (String location : locations) {

			LocationHierarchyDTO locationHierarchyDTO = new LocationHierarchyDTO();

			locationHierarchyDTO.setLocationName(location);
			locationHierarchyDTO.setParentName("Territory");

			locationHierarchyDTOs.add(locationHierarchyDTO);

		}

		return locationHierarchyDTOs;
	}
	
	private static boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{2,7}$";
		Pattern pat = Pattern.compile(emailRegex);
		if (email == null)
			return false;
		return pat.matcher(email).matches();
	}

	private static boolean isValidPhone(String phone) {
		if (phone == null || phone.isEmpty()) {
			return false;
		}
		if (phone.length() > 20) {
			return false;
		}
		return true;
	}
	

}
