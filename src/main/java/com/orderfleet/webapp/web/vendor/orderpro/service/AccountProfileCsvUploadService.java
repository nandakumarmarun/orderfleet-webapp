package com.orderfleet.webapp.web.vendor.orderpro.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.internal.function.numeric.Sum;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.LocationHierarchy;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PriceLevelAccountProductGroup;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.PriceLevelAccountProductGroupRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.PriceLevelAccountProductGroupService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelAccountProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;

/**
 * Service for save/update account profile related data from third party
 * softwares like tally.
 * <p>
 * Use the @Async annotation to process asynchronously.
 * </p>
 */
@Service
public class AccountProfileCsvUploadService {

	public static final String PID_PREFIX = PriceLevelAccountProductGroupService.PID_PREFIX;
	private final Logger log = LoggerFactory.getLogger(AccountProfileCsvUploadService.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	private final SyncOperationRepository syncOperationRepository;

	private final PriceLevelRepository priceLevelRepository;

	private final AccountProfileRepository accountProfileRepository;

	private final AccountProfileService accountProfileService;

	private final AccountTypeRepository accountTypeRepository;

	private final LocationRepository locationRepository;

	private final LocationHierarchyRepository locationHierarchyRepository;

	private final LocationAccountProfileRepository locationAccountProfileRepository;

	private final ReceivablePayableRepository receivablePayableRepository;

	private final LocationAccountProfileService locationAccountProfileService;

	private final UserRepository userRepository;

	private final ProductGroupRepository productGroupRepository;

	private final PriceLevelAccountProductGroupRepository priceLevelAccountProductGroupRepository;

	private final LocationService locationService;

	public AccountProfileCsvUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
			SyncOperationRepository syncOperationRepository, PriceLevelRepository priceLevelRepository,
			AccountProfileRepository accountProfileRepository, AccountTypeRepository accountTypeRepository,
			AccountProfileService accountProfileService, LocationRepository locationRepository,
			LocationHierarchyRepository locationHierarchyRepository,
			LocationAccountProfileRepository locationAccountProfileRepository,
			ReceivablePayableRepository receivablePayableRepository,
			LocationAccountProfileService locationAccountProfileService, UserRepository userRepository,
			ProductGroupRepository productGroupRepository,
			PriceLevelAccountProductGroupRepository priceLevelAccountProductGroupRepository,
			LocationService locationService) {
		super();
		this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
		this.syncOperationRepository = syncOperationRepository;
		this.priceLevelRepository = priceLevelRepository;
		this.accountProfileRepository = accountProfileRepository;
		this.accountTypeRepository = accountTypeRepository;
		this.locationRepository = locationRepository;
		this.locationHierarchyRepository = locationHierarchyRepository;
		this.locationAccountProfileRepository = locationAccountProfileRepository;
		this.receivablePayableRepository = receivablePayableRepository;
		this.accountProfileService = accountProfileService;
		this.locationAccountProfileService = locationAccountProfileService;
		this.userRepository = userRepository;
		this.productGroupRepository = productGroupRepository;
		this.priceLevelAccountProductGroupRepository = priceLevelAccountProductGroupRepository;
		this.locationService = locationService;
	}

	@Transactional
	public void saveUpdateAccountProfiles(
			final List<AccountProfileDTO> accountProfileDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = company.getId();
		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();

		final User user = userRepository
				.findOneByLogin(
						SecurityUtils.getCurrentUserLogin()).get();

		AccountType defaultAccountType =
				accountTypeRepository
						.findFirstByCompanyIdOrderByIdAsc(company.getId());

		List<String> apAlias =
				accountProfileDTOs
						.stream()
						.map(apDto -> apDto.getAlias()
								.toUpperCase())
				.collect(Collectors.toList());

		List<AccountProfile> accountProfiles =
				accountProfileRepository
						.findByCompanyIdAndAliasIgnoreCaseIn(
								companyId,
								apAlias);

		List<PriceLevel> tempPriceLevel =
				priceLevelRepository
						.findByCompanyId(companyId);

		for (AccountProfileDTO apDto : accountProfileDTOs) {

			Optional<AccountProfile> optionalAP =
					accountProfiles
							.stream()
							.filter(pc -> pc.getAlias()
									.equalsIgnoreCase(apDto.getAlias()))
							.findAny();

			AccountProfile accountProfile;

			if (optionalAP.isPresent()) {
				accountProfile = optionalAP.get();
			} else {
				accountProfile = new AccountProfile();
				String pid = AccountProfileService.PID_PREFIX + RandomUtil.generatePid();
				accountProfile.setPid(pid);
				accountProfile.setAlias(apDto.getAlias());
				accountProfile.setUser(user);
				accountProfile.setCompany(company);
				accountProfile.setCustomerId(apDto.getAlias());
				accountProfile.setAccountStatus(AccountStatus.Unverified);
				accountProfile.setDataSourceType(DataSourceType.TALLY);
				accountProfile.setImportStatus(true);
			}

			accountProfile.setTrimChar(apDto.getTrimChar());
			accountProfile.setName(apDto.getName());
			accountProfile.setCustomerId(apDto.getAlias());
			accountProfile.setTinNo(apDto.getTinNo());
			accountProfile.setPin(apDto.getPin());
			accountProfile.setDescription(apDto.getDescription());
			accountProfile.setActivated(apDto.getActivated());
			accountProfile.setAddress(apDto.getAddress());
			accountProfile.setCity(apDto.getCity());
			accountProfile.setContactPerson(apDto.getContactPerson());
			accountProfile.setDataSourceType(DataSourceType.TALLY);
			accountProfile.setDefaultDiscountPercentage(apDto.getDefaultDiscountPercentage());

			if (isValidPhone(apDto.getPhone1())) {
				accountProfile.setPhone1(apDto.getPhone1());
			}
			if (isValidPhone(apDto.getPhone2())) {
				accountProfile.setPhone2(apDto.getPhone2());
			}
			if (isValidEmail(apDto.getEmail1())) {
				accountProfile.setEmail1(apDto.getEmail1());
			}

			if (apDto.getDefaultPriceLevelName() != null
					&& !apDto.getDefaultPriceLevelName()
					.equalsIgnoreCase("")) {

				Optional<PriceLevel> optionalPriceLevel =
						tempPriceLevel
								.stream()
								.filter(pl -> apDto.getDefaultPriceLevelName()
										.equals(pl.getName()))
								.findAny();

				if (optionalPriceLevel.isPresent()) {
					accountProfile.setDefaultPriceLevel(optionalPriceLevel.get());
				} else {
					if (apDto.getDefaultPriceLevelName().length() > 0) {
						PriceLevel priceLevel = new PriceLevel();
						String pid = PriceLevelService.PID_PREFIX + RandomUtil.generatePid();
						priceLevel.setPid(pid);
						priceLevel.setName(apDto.getDefaultPriceLevelName());
						priceLevel.setActivated(true);
						priceLevel.setCompany(company);
						priceLevel = priceLevelRepository.save(priceLevel);
						tempPriceLevel.add(priceLevel);
						accountProfile.setDefaultPriceLevel(priceLevel);
					}
				}
			}

			if (accountProfile.getAccountType() == null) {
				accountProfile.setAccountType(defaultAccountType);
			}
			saveUpdateAccountProfiles.add(accountProfile);
		}

		bulkOperationRepositoryCustom
				.bulkSaveAccountProfile(saveUpdateAccountProfiles);
				
		accountProfileService
				.autoTaskCreationForAccountProfiles(company);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;

		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	public void saveUpdateLocations(final List<LocationDTO> locationDTOs, final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
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
			location.setActivated(locDto.getActivated());
			saveUpdateLocations.add(location);
		}
		bulkOperationRepositoryCustom.bulkSaveLocations(saveUpdateLocations);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	public void saveUpdateLocationHierarchy(final List<LocationHierarchyDTO> locationHierarchyDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		Long companyId = syncOperation.getCompany().getId();
		Long version;
		// Only one version of a company hierarchy is active at a time
		Optional<LocationHierarchy> locationHierarchy = locationHierarchyRepository
				.findFirstByCompanyIdAndActivatedTrueOrderByIdDesc(companyId);
		if (locationHierarchy.isPresent()) {
			locationHierarchyRepository.updateLocationHierarchyInactivatedExceptTerritory(ZonedDateTime.now(),
					locationHierarchy.get().getVersion(), companyId);
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
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	@Async
	public void saveUpdateLocationAccountProfiles(final List<LocationAccountProfileDTO> locationAccountProfileDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = syncOperation.getCompany().getId();
		List<LocationAccountProfile> newLocationAccountProfiles = new ArrayList<>();
		List<LocationAccountProfile> locationAccountProfiles = locationAccountProfileService
				.findAllLocationAccountProfiles(companyId);
		// delete all assigned location account profile from tally
		// locationAccountProfileRepository.deleteByCompanyIdAndDataSourceTypeAndThirdpartyUpdateTrue(company.getId(),DataSourceType.TALLY);
		List<AccountProfile> accountProfiles = accountProfileService.findAllAccountProfileByCompanyId(companyId);
		List<Location> locations = locationService.findAllLocationByCompanyId(companyId);

		List<Long> locationAccountProfilesIds = new ArrayList<>();

		for (LocationAccountProfileDTO locAccDto : locationAccountProfileDTOs) {

			LocationAccountProfile profile = new LocationAccountProfile();
			// find location

			// find accountprofile
			// System.out.println(loc.get()+"===Location");

			Optional<AccountProfile> acc = accountProfiles.stream()
					.filter(ap -> locAccDto.getAccountProfileName().equals(ap.getAlias())).findFirst();

			if (acc.isPresent()) {

				profile.setLocation(locations.get(0));
				profile.setAccountProfile(acc.get());
				profile.setCompany(company);
				newLocationAccountProfiles.add(profile);
			}
		}
		if (locationAccountProfilesIds.size() != 0) {
			locationAccountProfileRepository.deleteByIdIn(companyId, locationAccountProfilesIds);
		}

		locationAccountProfileRepository.save(newLocationAccountProfiles);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	@Async
	public void saveUpdateReceivablePayables(final List<ReceivablePayableDTO> receivablePayableDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<ReceivablePayable> saveReceivablePayable = new HashSet<>();
		// find all exist account profiles
		List<String> apAliases = receivablePayableDTOs.stream().map(rp -> rp.getAccountName())
				.collect(Collectors.toList());
		System.out.println(apAliases.size() + "---------------Alias Size");
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_124" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by compId and alias ignore case";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfiles = accountProfileRepository
				.findByCompanyIdAndAliasIgnoreCaseIn(company.getId(), apAliases);
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

		System.out.println(accountProfiles.size() + "---------------Acc Alias Sizes");
		// delete all receivable payable
		receivablePayableRepository.deleteByCompanyId(company.getId());
		for (ReceivablePayableDTO rpDto : receivablePayableDTOs) {
			// only save if account profile exist
			accountProfiles.stream().filter(a -> a.getAlias().equals(rpDto.getAccountName())).findAny()
					.ifPresent(ap -> {
						ReceivablePayable receivablePayable = new ReceivablePayable();

						receivablePayable.setReceivablePayableType(ReceivablePayableType.Receivable);
						receivablePayable.setPid(ReceivablePayableService.PID_PREFIX + RandomUtil.generatePid());
						receivablePayable.setAccountProfile(ap);
						receivablePayable.setCompany(company);
						receivablePayable.setBillOverDue(Long.valueOf(rpDto.getBillOverDue()));
						receivablePayable.setReferenceDocumentAmount(rpDto.getReferenceDocumentAmount());
						receivablePayable.setReferenceDocumentBalanceAmount(rpDto.getReferenceDocumentBalanceAmount());
						receivablePayable.setReferenceDocumentDate(rpDto.getReferenceDocumentDate());
						receivablePayable.setReferenceDocumentNumber(rpDto.getReferenceDocumentNumber());
						receivablePayable.setReferenceDocumentType(rpDto.getReferenceDocumentType());
						receivablePayable.setRemarks(rpDto.getRemarks());
						saveReceivablePayable.add(receivablePayable);
					});
		}
		System.out.println(saveReceivablePayable.size() + "----Before Saving");
		bulkOperationRepositoryCustom.bulkSaveReceivablePayables(saveReceivablePayable);

		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();

		for (AccountProfile ap : accountProfiles) {
			double closingBalance = saveReceivablePayable.stream()
					.filter(rp -> rp.getAccountProfile().getAlias().equals(ap.getAlias()))
					.mapToDouble(ReceivablePayable::getReferenceDocumentBalanceAmount).sum();

			ap.setClosingBalance(closingBalance);

			saveUpdateAccountProfiles.add(ap);

		}

		bulkOperationRepositoryCustom.bulkSaveAccountProfile(saveUpdateAccountProfiles);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	@Async
	public void saveUpdateAccountProfileClosingBalances(
			final List<AccountProfileDTO> accountProfileDTOs,
			final SyncOperation syncOperation) {

		long start = System.nanoTime();
		Long companyId = syncOperation.getCompany().getId();
		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();

		List<AccountProfile> accountProfiles =
				accountProfileRepository
						.findByCompanyId(companyId);

		for (AccountProfileDTO apDto : accountProfileDTOs) {
			Optional<AccountProfile> optionalAP = accountProfiles.stream()
					.filter(pc -> pc.getName().equals(apDto.getName())).findAny();
			AccountProfile accountProfile;
			if (optionalAP.isPresent()) {
				accountProfile = optionalAP.get();
				accountProfile.setClosingBalance(apDto.getClosingBalance());
				saveUpdateAccountProfiles.add(accountProfile);
			}
		}

		accountProfileRepository.updateAccountProfileClosingBalanceZeroByCompanyId(companyId);
		bulkOperationRepositoryCustom.bulkSaveAccountProfile(saveUpdateAccountProfiles);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	@Async
	public void saveUpdatePriceLevelAccountProductGroups(
			List<PriceLevelAccountProductGroupDTO> priceLevelAccountProductGroupDTOs,
			SyncOperation syncOperation) {

		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		List<PriceLevelAccountProductGroup> savePriceLevelAccountProductGroup = new ArrayList<>();

		List<AccountProfile> accountProfiles =
				accountProfileRepository
						.findByCompanyId(company.getId());

		// find all exist product groups
		List<String> pgNames =
				priceLevelAccountProductGroupDTOs
						.stream()
						.map(rp -> rp.getProductGroupName()
								.toUpperCase())
						.collect(Collectors.toList());

		List<ProductGroup> productGroups =
				productGroupRepository
						.findByCompanyIdAndNameIgnoreCaseIn(
								company.getId(), pgNames);

		List<String> plNames =
				priceLevelAccountProductGroupDTOs
						.stream()
						.map(rp -> rp.getPriceLevelName()
								.toUpperCase()).collect(Collectors.toList());

		List<PriceLevel> priceLevels =
				priceLevelRepository
						.findByCompanyIdAndNameIgnoreCaseIn(
								company.getId(), plNames);


		receivablePayableRepository.deleteByCompanyId(company.getId());

		for (PriceLevelAccountProductGroupDTO papg : priceLevelAccountProductGroupDTOs) {

			Optional<AccountProfile> accpro =
					accountProfiles
							.stream()
							.filter(a -> a.getName()
									.equals(papg.getAccountName()))
							.findAny();

			Optional<ProductGroup> prodgrp =
					productGroups
							.stream()
							.filter(a -> a.getName()
									.equals(papg.getProductGroupName()))
							.findAny();

			Optional<PriceLevel> prilvl =
					priceLevels
							.stream()
							.filter(a -> a.getName()
									.equals(papg.getPriceLevelName()))
					.findAny();

			if (accpro.isPresent() && prodgrp.isPresent() && prilvl.isPresent()) {

				Optional<PriceLevelAccountProductGroup> pLevelAccountPGroup =
						priceLevelAccountProductGroupRepository
								.findOneByAccountProfilePidAndProductGroupPidAndPriceLevelPid(
										company.getId(),
										accpro.get().getPid(),
										prodgrp.get().getPid(),
										prilvl.get().getPid());

				if (!pLevelAccountPGroup.isPresent()) {
					PriceLevelAccountProductGroup priceLevelAccountProductGroup = new PriceLevelAccountProductGroup();
					priceLevelAccountProductGroup.setAccountProfile(accpro.get());
					priceLevelAccountProductGroup.setCompany(company);
					String pid = PID_PREFIX + RandomUtil.generatePid();
					priceLevelAccountProductGroup.setPid(pid);
					priceLevelAccountProductGroup.setPriceLevel(prilvl.get());
					priceLevelAccountProductGroup.setProductGroup(prodgrp.get());
					savePriceLevelAccountProductGroup.add(priceLevelAccountProductGroup);
				}
			}
		}

		bulkOperationRepositoryCustom
				.bulkPriceLevelAccountProductGroups(
						savePriceLevelAccountProductGroup);


		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;


		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
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

	public void locationAccountProfileAssociation(List<String> newAccountProfileAliasList, Company company) {
		List<LocationAccountProfile> locationAccountProfiles = new ArrayList<>();
		List<AccountProfile> newAccountProfiles = new ArrayList<>();
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all by compId";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> existingAccountProfiles = accountProfileRepository.findAllByCompanyId(company.getId());
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

		for (String newAccountProfileAlias : newAccountProfileAliasList) {
			Optional<AccountProfile> accountProfileExist = existingAccountProfiles.stream()
					.filter(aProfile -> aProfile.getAlias() != null ? aProfile.getAlias().equals(newAccountProfileAlias)
							: false)
					.findAny();
			if (accountProfileExist.isPresent()) {
				newAccountProfiles.add(accountProfileExist.get());
			}
		}
		List<Location> locations = locationRepository.findAllByCompanyId(company.getId());
		for (Location location : locations) {

			if (location.getName().equalsIgnoreCase("Territory")) {
				for (AccountProfile accountProfile : newAccountProfiles) {
					locationAccountProfiles.add(new LocationAccountProfile(location, accountProfile, company));
				}
			}
		}
		System.out.println(locationAccountProfiles.size() + "----------");
		locationAccountProfileRepository.save(locationAccountProfiles);
		System.out.println("----------Success");

	}
}
