package com.orderfleet.webapp.web.vendor.focus.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import com.orderfleet.webapp.domain.RouteCode;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.RouteCodeRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.RouteCodeService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.dto.RouteCodeDTO;
import com.orderfleet.webapp.web.vendor.focus.dto.AccountProfileFocus;

@Service
public class AccountProfileFocusUploadService {

	private final Logger log = LoggerFactory.getLogger(AccountProfileFocusUploadService.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");

	private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	private final AccountProfileRepository accountProfileRepository;

	private final AccountProfileService accountProfileService;

	private final AccountTypeRepository accountTypeRepository;

	private final LocationRepository locationRepository;

	private final LocationAccountProfileRepository locationAccountProfileRepository;

	private final LocationAccountProfileService locationAccountProfileService;

	private final UserRepository userRepository;

	private final LocationService locationService;

	private final CompanyRepository companyRepository;

	private final LocationHierarchyRepository locationHierarchyRepository;

	private final RouteCodeRepository routeCodeRepository;

	public AccountProfileFocusUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
			AccountProfileRepository accountProfileRepository, AccountTypeRepository accountTypeRepository,
			AccountProfileService accountProfileService, LocationRepository locationRepository,
			LocationAccountProfileRepository locationAccountProfileRepository,
			LocationAccountProfileService locationAccountProfileService, UserRepository userRepository,
			LocationService locationService, CompanyRepository companyRepository,
			LocationHierarchyRepository locationHierarchyRepository, RouteCodeRepository routeCodeRepository) {
		super();
		this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
		this.accountProfileRepository = accountProfileRepository;
		this.accountTypeRepository = accountTypeRepository;
		this.locationRepository = locationRepository;
		this.locationAccountProfileRepository = locationAccountProfileRepository;
		this.accountProfileService = accountProfileService;
		this.locationAccountProfileService = locationAccountProfileService;
		this.userRepository = userRepository;
		this.locationService = locationService;
		this.companyRepository = companyRepository;
		this.locationHierarchyRepository = locationHierarchyRepository;
		this.routeCodeRepository = routeCodeRepository;
	}

	public void saveUpdateAccountProfiles(List<AccountProfileFocus> accountProfileDTos) {

		log.info("Saving Account Profiles.........");

		long start = System.nanoTime();

		final User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		log.info("Company ID" + companyId);
		Company company = companyRepository.findOne(companyId);
		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();

		// All product must have a division/category, if not, set a default one
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AT_QUERY_109" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get first by compId";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountType defaultAccountType = accountTypeRepository.findFirstByCompanyIdOrderByIdAsc(companyId);
		List<AccountType> accountTypes = accountTypeRepository.findAllByCompanyIdAndActivated(true);
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

		// find all exist account profiles

//		List<String> apNames = list.stream().map(apDto -> apDto.getName()).collect(Collectors.toList());
//		List<AccountProfile> accountProfiles = accountProfileRepository.findByCompanyIdAndNameIgnoreCaseIn(companyId,
//				apNames);
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "AP_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 = "get all by compId";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyId();
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

		List<LocationDTO> locationDtos = new ArrayList<>();
		List<LocationAccountProfileDTO> locationAccountProfileDtos = new ArrayList<>();
		List<RouteCode> routecodeList = new ArrayList<>();
		List<RouteCode> RouteCodeList = routeCodeRepository.findAll();
		
		if (accountProfileDTos != null) {
			for (AccountProfileFocus apDto : accountProfileDTos) {
				// check exist by name, only one exist with a name

				Optional<AccountProfile> optionalAP = accountProfiles.stream()
						.filter(pc -> pc.getName().equalsIgnoreCase(apDto.getName())).findAny();
				AccountProfile accountProfile;
				if (optionalAP.isPresent()) {
					accountProfile = optionalAP.get();
					// if not update, skip this iteration. Not implemented now
					// if (!accountProfile.getThirdpartyUpdate()) { continue; }
				} else {
					accountProfile = new AccountProfile();
					accountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
					accountProfile.setName(apDto.getName());
					accountProfile.setAddress(apDto.getAddress());
					accountProfile.setCustomerId(apDto.getCode());
					accountProfile.setCreditDays(Long.valueOf(apDto.getCreditDays()));
					accountProfile.setClosingBalance(Double.parseDouble(apDto.getClosing_Balance()));
					accountProfile.setTinNo(apDto.getGstin());
					accountProfile.setUser(user);
					accountProfile.setCompany(company);
					accountProfile.setAccountStatus(AccountStatus.Unverified);
					accountProfile.setDataSourceType(DataSourceType.TALLY);
					accountProfile.setImportStatus(true);

				}

				accountProfile.setClosingBalance(Double.parseDouble(apDto.getClosing_Balance()));

				if (apDto.getCreditLimit() != null && !apDto.getCreditLimit().equals("")) {
					Float floatnum = Float.parseFloat(apDto.getCreditLimit());
					accountProfile.setCreditLimit((Long) floatnum.longValue());
				}

				accountProfile.setActivated(true);
				if (apDto.getAddress() != null && !apDto.getAddress().equals("")) {
					accountProfile.setAddress(apDto.getAddress());
				} else {
					accountProfile.setAddress("No Address");
				}
				if (apDto.getCity() != null && !apDto.getCity().equals("")) {
					accountProfile.setCity(apDto.getCity());
				} else {
					accountProfile.setCity("No City");
				}
				if (apDto.getPhone() != null && !apDto.getPhone().equals("")) {
					if (isValidPhone(apDto.getPhone())) {
						accountProfile.setPhone1(apDto.getPhone());
					}
				} else {
					accountProfile.setPhone1("");
				}

				// account type

//				if (accountProfile.getAccountType() == null) {
//					accountProfile.setAccountType(defaultAccountType);
//				}

				Optional<AccountType> opAccountType = accountTypes.stream()
						.filter(acc -> acc.getName().equalsIgnoreCase(apDto.getAccountType())).findFirst();
				if (opAccountType.isPresent()) {
					accountProfile.setAccountType(opAccountType.get());
				} else {
					accountProfile.setAccountType(defaultAccountType);
				}

				Optional<AccountProfile> opAccP = saveUpdateAccountProfiles.stream()
						.filter(so -> so.getName().equalsIgnoreCase(apDto.getName())).findAny();

				if (opAccP.isPresent()) {
					continue;
				}

				LocationDTO locationDTO = new LocationDTO();

				LocationAccountProfileDTO locationAccountProfileDto = new LocationAccountProfileDTO();

				if (apDto.getRouteCode().equalsIgnoreCase("null") && apDto.getRouteName().equalsIgnoreCase("null")) {
					locationDTO.setLocationId("null");
					locationDTO.setName("No Location");

					locationDtos.add(locationDTO);
					locationAccountProfileDto.setAccountProfileName(apDto.getName());
					locationAccountProfileDto.setLocationName("No Location");

				} else {
					System.out.println("routecode"+apDto.getRouteCode());
					locationDTO.setLocationId(apDto.getRouteCode());
					locationDTO.setName(apDto.getRouteName());
					locationDtos.add(locationDTO);
					locationAccountProfileDto.setAccountProfileName(apDto.getName());
					locationAccountProfileDto.setLocationName(apDto.getRouteName());
				}
   
				RouteCode routecode = new RouteCode();
				if (!apDto.getRouteCode().equalsIgnoreCase("null") && !apDto.getRouteName().equalsIgnoreCase("null")) {
					
					Optional<RouteCode> routeCodeop =  RouteCodeList.stream().filter(pc -> pc.getMasterCode().equals(apDto.getRouteCode())).findAny();	
					if(routeCodeop.isPresent()) {
						routecode = routeCodeop.get();
						routecode.setId(routeCodeop.get().getId());
						routecode.setMasterCode(apDto.getRouteCode());
						routecode.setMasterName(apDto.getRouteName());
						routecode.setAccountProfile(accountProfile);
						routecode.setCompany(company);
						System.out.println("present");
						System.out.println(apDto.getRouteCode());
						System.out.println(apDto.getRouteName());
					}else {
						routecode.setPid(RouteCodeService.PID_PREFIX + RandomUtil.generatePid() );
						routecode.setMasterCode(apDto.getRouteCode());
						routecode.setMasterName(apDto.getRouteName());
						routecode.setAccountProfile(accountProfile);
						routecode.setCompany(company);
						System.out.println("presentelse");
						System.out.println(apDto.getRouteCode());
						System.out.println(apDto.getRouteName());
					}
				}     
				locationAccountProfileDtos.add(locationAccountProfileDto);
				accountProfile.setDataSourceType(DataSourceType.TALLY);
				saveUpdateAccountProfiles.add(accountProfile);
				routecodeList.add(routecode);
			}

			if (locationDtos.size() > 0)

			{
				saveUpdateLocations(locationDtos);
				saveUpdateLocationHierarchy(locationDtos);
			}

			bulkOperationRepositoryCustom.bulkSaveAccountProfile(saveUpdateAccountProfiles);
			routeCodeRepository.save(routecodeList);
			saveUpdateLocationAccountProfiles(locationAccountProfileDtos);
		}
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	private boolean isValidPhone(String phone) {
		// TODO Auto-generated method stub
		return false;
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
		locationRepository.save(saveUpdateLocations);
		// bulkOperationRepositoryCustom.bulkSaveLocations(saveUpdateLocations);
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

}
