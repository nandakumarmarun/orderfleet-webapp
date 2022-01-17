package com.orderfleet.webapp.service.async;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.GstLedger;
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
import com.orderfleet.webapp.domain.enums.GstAccountType;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.GstLedgerRepository;
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
import com.orderfleet.webapp.service.LocationHierarchyService;
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
import com.orderfleet.webapp.web.tally.dto.GstLedgerDTO;

/**
 * Service for save/update account profile related data from third party
 * softwares like tally.
 * <p>
 * Use the @Async annotation to process asynchronously.
 * </p>
 */
@Service
public class TPAccountProfileManagementService {

	private final Logger log = LoggerFactory.getLogger(TPAccountProfileManagementService.class);
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

	private final LocationHierarchyService locationHierarchyService;

	private final UserRepository userRepository;

	private final ProductGroupRepository productGroupRepository;

	private final PriceLevelAccountProductGroupRepository priceLevelAccountProductGroupRepository;

	private final LocationService locationService;

	private final GstLedgerRepository gstLedgerRepository;
	
	boolean flagac = false;

	public TPAccountProfileManagementService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
			SyncOperationRepository syncOperationRepository, PriceLevelRepository priceLevelRepository,
			AccountProfileRepository accountProfileRepository, AccountTypeRepository accountTypeRepository,
			AccountProfileService accountProfileService, LocationRepository locationRepository,
			LocationHierarchyRepository locationHierarchyRepository,
			LocationAccountProfileRepository locationAccountProfileRepository,
			ReceivablePayableRepository receivablePayableRepository,
			LocationAccountProfileService locationAccountProfileService, UserRepository userRepository,
			ProductGroupRepository productGroupRepository,
			PriceLevelAccountProductGroupRepository priceLevelAccountProductGroupRepository,
			LocationService locationService, GstLedgerRepository gstLedgerRepository,
			LocationHierarchyService locationHierarchyService) {
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
		this.gstLedgerRepository = gstLedgerRepository;
		this.locationHierarchyService = locationHierarchyService;
	}

	@Transactional
	public void saveUpdateAccountProfiles(final List<AccountProfileDTO> accountProfileDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();

		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		User userObject = new User();
		if (opUser.isPresent()) {
			userObject = opUser.get();
		} else {
			userObject = userRepository.findOneByLogin("siteadmin").get();
		}
		final User user = userObject;
		final Long companyId = company.getId();
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
		AccountType defaultAccountType = accountTypeRepository.findFirstByCompanyIdOrderByIdAsc(company.getId());
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
		log.info("Default Account Type TPAPMS:" + defaultAccountType.getName());
		// find all exist account profiles
		List<String> apNames = accountProfileDTOs.stream().map(apDto -> apDto.getName().toUpperCase())
				.collect(Collectors.toList());
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "AP_QUERY_123" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 = "get by compId and name ingore case";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		List<AccountProfile> accountProfiles = accountProfileRepository.findByCompanyIdAndNameIgnoreCaseIn(companyId,
				apNames);
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

		log.info("Db accounts: " + accountProfiles.size());
		log.info("Tally  accounts: " + accountProfileDTOs.size());
		// all pricelevels
		List<PriceLevel> tempPriceLevel = priceLevelRepository.findByCompanyId(companyId);

		// account type
		// List<AccountType> accountTypes =
		// accountTypeRepository.findAllByCompanyId(company.getId());

		for (AccountProfileDTO apDto : accountProfileDTOs) {
			// check exist by name, only one exist with a name
			Optional<AccountProfile> optionalAP = accountProfiles.stream()
					.filter(pc -> pc.getName().equalsIgnoreCase(apDto.getName())).findFirst();
			AccountProfile accountProfile;
			if (optionalAP.isPresent()) {
				accountProfile = optionalAP.get();
				accountProfile.setName(apDto.getName());
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
			accountProfile.setCustomerId(apDto.getCustomerId());
			accountProfile.setTrimChar(apDto.getTrimChar());
			accountProfile.setTinNo(apDto.getTinNo());
			accountProfile.setAlias(apDto.getAlias());
			if (isValidPhone(apDto.getPhone1())) {
				accountProfile.setPhone1(apDto.getPhone1());
			} else {
				accountProfile.setPhone1("");
			}
			if (isValidPhone(apDto.getPhone2())) {
				accountProfile.setPhone2(apDto.getPhone2());
			} else {
				accountProfile.setPhone2("");
			}
			if (isValidEmail(apDto.getEmail1())) {
				accountProfile.setEmail1(apDto.getEmail1());
			}
			accountProfile.setMailingName(apDto.getMailingName());
			accountProfile.setPin(apDto.getPin());
			accountProfile.setDescription(apDto.getDescription());
			accountProfile.setActivated(apDto.getActivated());
			accountProfile.setAddress(apDto.getAddress());
			accountProfile.setCity(apDto.getCity());
			accountProfile.setContactPerson(apDto.getContactPerson());
			accountProfile.setStateName(apDto.getStateName());
			accountProfile.setCountryName(apDto.getCountryName());
			accountProfile.setGstRegistrationType(
					apDto.getGstRegistrationType() == null ? "Regular" : apDto.getGstRegistrationType());
			if (apDto.getDefaultPriceLevelName() != null && !apDto.getDefaultPriceLevelName().equalsIgnoreCase("")) {
				// price level
				Optional<PriceLevel> optionalPriceLevel = tempPriceLevel.stream()
						.filter(pl -> apDto.getDefaultPriceLevelName().equals(pl.getName())).findAny();

				if (optionalPriceLevel.isPresent()) {
					accountProfile.setDefaultPriceLevel(optionalPriceLevel.get());
				} else {
					// create new price level
					if (apDto.getDefaultPriceLevelName().length() > 0) {
						PriceLevel priceLevel = new PriceLevel();
						priceLevel.setPid(PriceLevelService.PID_PREFIX + RandomUtil.generatePid());
						priceLevel.setName(apDto.getDefaultPriceLevelName());
						priceLevel.setActivated(true);
						priceLevel.setCompany(company);
						priceLevel = priceLevelRepository.save(priceLevel);
						tempPriceLevel.add(priceLevel);
						accountProfile.setDefaultPriceLevel(priceLevel);
					}
				}
			}
			// account type

			if (accountProfile.getAccountType() == null) {
				accountProfile.setAccountType(defaultAccountType);
			}

			// Optional<AccountType> optionalAccountType = accountTypes.stream()
			// .filter(atn ->
			// apDto.getAccountTypeName().equals(atn.getName())).findAny();
			// if (optionalAccountType.isPresent()) {
			// accountProfile.setAccountType(optionalAccountType.get());
			// } else {
			// accountProfile.setAccountType(defaultAccountType);
			// }
			accountProfile.setDataSourceType(DataSourceType.TALLY);
			Optional<AccountProfile> opAccP = saveUpdateAccountProfiles.stream()
					.filter(so -> so.getName().equalsIgnoreCase(apDto.getName())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}
			saveUpdateAccountProfiles.add(accountProfile);
		}
		log.info("Saving...accountProfileDTOs.Account Profiles" + saveUpdateAccountProfiles.size());
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
	public void saveUpdateAccountProfilesId(final List<AccountProfileDTO> accountProfileDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();

		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		User userObject = new User();
		if (opUser.isPresent()) {
			userObject = opUser.get();
		} else {
			userObject = userRepository.findOneByLogin("siteadmin").get();
		}
		final User user = userObject;
		final Long companyId = company.getId();
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
		AccountType defaultAccountType = accountTypeRepository.findFirstByCompanyIdOrderByIdAsc(company.getId());
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

		log.info("Default Account Type TPAPMS:" + defaultAccountType.getName());
		// find all exist account profiles
		List<String> apNames = accountProfileDTOs.stream().map(apDto -> apDto.getName().toUpperCase())
				.collect(Collectors.toList());
//		List<AccountProfile> accountProfiles = accountProfileRepository.findByCompanyIdAndNameIgnoreCaseIn(companyId,
//				apNames);
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "AP_QUERY_106" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 = "get by compId";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		List<AccountProfile> accountProfiles = accountProfileRepository.findByCompanyId(companyId);
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

		log.info("Db accounts: " + accountProfiles.size());
		log.info("Tally  accounts: " + accountProfileDTOs.size());
		// all pricelevels
		List<PriceLevel> tempPriceLevel = priceLevelRepository.findByCompanyId(companyId);

		// account type
		// List<AccountType> accountTypes =
		// accountTypeRepository.findAllByCompanyId(company.getId());

		for (AccountProfileDTO apDto : accountProfileDTOs) {
			Optional<AccountProfile> optionalAP = null;
			// check exist by name, only one exist with a name
			if (apDto.getCustomerId() == null) {
				optionalAP = accountProfiles.stream().filter(pc -> pc.getName().equalsIgnoreCase(apDto.getName()))
						.findFirst();
			} else {
				optionalAP = accountProfiles.stream()
						.filter(pc -> pc.getCustomerId() != null
								? pc.getCustomerId().equalsIgnoreCase(apDto.getCustomerId())
								: false)
						.findFirst();
			}

			AccountProfile accountProfile;
			if (optionalAP.isPresent()) {
				accountProfile = optionalAP.get();
				accountProfile.setName(apDto.getName());
				// if not update, skip this iteration. Not implemented now
				// if (!accountProfile.getThirdpartyUpdate()) { continue; }
			} else {
				accountProfile = new AccountProfile();
				accountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
				accountProfile.setCustomerId(apDto.getCustomerId());
				accountProfile.setUser(user);
				accountProfile.setCompany(company);
				accountProfile.setAccountStatus(AccountStatus.Unverified);
				accountProfile.setDataSourceType(DataSourceType.TALLY);
				accountProfile.setImportStatus(true);
			}
			accountProfile.setName(apDto.getName());
			accountProfile.setTrimChar(apDto.getTrimChar());
			accountProfile.setTinNo(apDto.getTinNo());
			accountProfile.setAlias(apDto.getAlias());
			if (isValidPhone(apDto.getPhone1())) {
				accountProfile.setPhone1(apDto.getPhone1());
			} else {
				accountProfile.setPhone1("");
			}
			if (isValidPhone(apDto.getPhone2())) {
				accountProfile.setPhone2(apDto.getPhone2());
			} else {
				accountProfile.setPhone2("");
			}
			if (isValidEmail(apDto.getEmail1())) {
				accountProfile.setEmail1(apDto.getEmail1());
			}
			accountProfile.setPin(apDto.getPin());
			accountProfile.setDescription(apDto.getDescription());
			accountProfile.setActivated(apDto.getActivated());
			accountProfile.setAddress(apDto.getAddress());
			accountProfile.setCity(apDto.getCity());
			accountProfile.setContactPerson(apDto.getContactPerson());
			accountProfile.setStateName(apDto.getStateName());
			accountProfile.setCountryName(apDto.getCountryName());
			accountProfile.setGstRegistrationType(
					apDto.getGstRegistrationType() == null ? "Regular" : apDto.getGstRegistrationType());
			if (apDto.getDefaultPriceLevelName() != null && !apDto.getDefaultPriceLevelName().equalsIgnoreCase("")) {
				// price level
				Optional<PriceLevel> optionalPriceLevel = tempPriceLevel.stream()
						.filter(pl -> apDto.getDefaultPriceLevelName().equals(pl.getName())).findAny();

				if (optionalPriceLevel.isPresent()) {
					accountProfile.setDefaultPriceLevel(optionalPriceLevel.get());
				} else {
					// create new price level
					if (apDto.getDefaultPriceLevelName().length() > 0) {
						PriceLevel priceLevel = new PriceLevel();
						priceLevel.setPid(PriceLevelService.PID_PREFIX + RandomUtil.generatePid());
						priceLevel.setName(apDto.getDefaultPriceLevelName());
						priceLevel.setActivated(true);
						priceLevel.setCompany(company);
						priceLevel = priceLevelRepository.save(priceLevel);
						tempPriceLevel.add(priceLevel);
						accountProfile.setDefaultPriceLevel(priceLevel);
					}
				}
			}
			// account type

			if (accountProfile.getAccountType() == null) {
				accountProfile.setAccountType(defaultAccountType);
			}

			// Optional<AccountType> optionalAccountType = accountTypes.stream()
			// .filter(atn ->
			// apDto.getAccountTypeName().equals(atn.getName())).findAny();
			// if (optionalAccountType.isPresent()) {
			// accountProfile.setAccountType(optionalAccountType.get());
			// } else {
			// accountProfile.setAccountType(defaultAccountType);
			// }
			accountProfile.setDataSourceType(DataSourceType.TALLY);
			Optional<AccountProfile> opAccP = saveUpdateAccountProfiles.stream()
					.filter(so -> so.getName().equalsIgnoreCase(apDto.getName())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}
			saveUpdateAccountProfiles.add(accountProfile);
		}
		log.info("Saving...accountProfileDTOs.Account Profiles" + saveUpdateAccountProfiles.size());
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
	public void saveUpdateAccountProfilesUpdateId(final List<AccountProfileDTO> accountProfileDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		log.info("Saving acound Profiles:----" + accountProfileDTOs.size());
		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		User userObject = new User();
		if (opUser.isPresent()) {
			userObject = opUser.get();
		} else {
			userObject = userRepository.findOneByLogin("siteadmin").get();
		}
		final User user = userObject;
		final Long companyId = company.getId();
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
		AccountType defaultAccountType = accountTypeRepository.findFirstByCompanyIdOrderByIdAsc(company.getId());
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

		log.info("Default Account Type TPAPMS:" + defaultAccountType.getName());
		// find all exist account profiles
		List<String> apNames = accountProfileDTOs.stream().map(apDto -> apDto.getName().toUpperCase())
				.collect(Collectors.toList());
//		List<AccountProfile> accountProfiles = accountProfileRepository.findByCompanyIdAndNameIgnoreCaseIn(companyId,
//				apNames);
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "AP_QUERY_106" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 = "get by compId";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyIdOrderbyid();
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

		log.info("Db accounts: " + accountProfiles.size());
		log.info("Tally  accounts: " + accountProfileDTOs.size());
		// all pricelevels
		List<PriceLevel> tempPriceLevel = priceLevelRepository.findByCompanyId(companyId);
		Set<Long> dectivatedac = new HashSet<>();
		// account type
		// List<AccountType> accountTypes =
		// accountTypeRepository.findAllByCompanyId(company.getId());
		if(!accountProfiles.isEmpty() && accountProfiles.size() > 1 && !accountProfileDTOs.isEmpty()) {
			accountProfiles.remove(0);
			for(AccountProfile ac :accountProfiles) {
			flagac = false;
			 accountProfileDTOs.forEach(data ->{
				if(ac.getCustomerId().equals(data.getCustomerId())) {
					flagac = true;
				}
			});
			if(!flagac) {
				dectivatedac.add(ac.getId());
			}
		}}
			if(!dectivatedac.isEmpty()) {
				accountProfileRepository.deactivateAcoundProfileUsingInId(dectivatedac);
			}
		for (AccountProfileDTO apDto : accountProfileDTOs) {
			Optional<AccountProfile> optionalAP = null;
			// check exist by name, only one exist with a name

			optionalAP = accountProfiles.stream()
					.filter(p -> p.getCustomerId() != null && !p.getCustomerId().equals("")
							? p.getCustomerId().equals(apDto.getCustomerId())
							: false)
					.findAny();

			AccountProfile accountProfile;
			if (optionalAP.isPresent()) {
				accountProfile = optionalAP.get();
				accountProfile.setName(apDto.getName() + "~" + apDto.getCustomerId());
				// if not update, skip this iteration. Not implemented now
				// if (!accountProfile.getThirdpartyUpdate()) { continue; }
			} else {
				accountProfile = new AccountProfile();
				accountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
				accountProfile.setCustomerId(apDto.getCustomerId());
				accountProfile.setUser(user);
				accountProfile.setCompany(company);
				accountProfile.setAccountStatus(AccountStatus.Unverified);
				accountProfile.setDataSourceType(DataSourceType.TALLY);
				accountProfile.setDescription(apDto.getName());
				accountProfile.setImportStatus(true);
			}
			accountProfile.setName(apDto.getName() + "~" + apDto.getCustomerId());
			accountProfile.setTrimChar(apDto.getTrimChar());
			accountProfile.setTinNo(apDto.getTinNo());
			accountProfile.setDescription(apDto.getName());
			accountProfile.setAlias(apDto.getAlias());
			if (isValidPhone(apDto.getPhone1())) {
				accountProfile.setPhone1(apDto.getPhone1());
			} else {
				accountProfile.setPhone1("");
			}
			if (isValidPhone(apDto.getPhone2())) {
				accountProfile.setPhone2(apDto.getPhone2());
			} else {
				accountProfile.setPhone2("");
			}
			if (isValidEmail(apDto.getEmail1())) {
				accountProfile.setEmail1(apDto.getEmail1());
			}
			accountProfile.setPin(apDto.getPin());
			accountProfile.setActivated(apDto.getActivated());
			accountProfile.setAddress(apDto.getAddress());
			accountProfile.setCity(apDto.getCity());
			accountProfile.setContactPerson(apDto.getContactPerson());
			accountProfile.setStateName(apDto.getStateName());
			accountProfile.setCountryName(apDto.getCountryName());
			accountProfile.setGstRegistrationType(
					apDto.getGstRegistrationType() == null ? "Regular" : apDto.getGstRegistrationType());
			if (apDto.getDefaultPriceLevelName() != null && !apDto.getDefaultPriceLevelName().equalsIgnoreCase("")) {
				// price level
				Optional<PriceLevel> optionalPriceLevel = tempPriceLevel.stream()
						.filter(pl -> apDto.getDefaultPriceLevelName().equals(pl.getName())).findAny();

				if (optionalPriceLevel.isPresent()) {
					accountProfile.setDefaultPriceLevel(optionalPriceLevel.get());
				} else {
					// create new price level
					if (apDto.getDefaultPriceLevelName().length() > 0) {
						PriceLevel priceLevel = new PriceLevel();
						priceLevel.setPid(PriceLevelService.PID_PREFIX + RandomUtil.generatePid());
						priceLevel.setName(apDto.getDefaultPriceLevelName());
						priceLevel.setActivated(true);
						priceLevel.setCompany(company);
						priceLevel = priceLevelRepository.save(priceLevel);
						tempPriceLevel.add(priceLevel);
						accountProfile.setDefaultPriceLevel(priceLevel);
					}
				}
			}
			// account type

			if (accountProfile.getAccountType() == null) {
				accountProfile.setAccountType(defaultAccountType);
			}

			// Optional<AccountType> optionalAccountType = accountTypes.stream()
			// .filter(atn ->
			// apDto.getAccountTypeName().equals(atn.getName())).findAny();
			// if (optionalAccountType.isPresent()) {
			// accountProfile.setAccountType(optionalAccountType.get());
			// } else {
			// accountProfile.setAccountType(defaultAccountType);
			// }
			accountProfile.setDataSourceType(DataSourceType.TALLY);
			Optional<AccountProfile> opAccP = saveUpdateAccountProfiles.stream()
					.filter(so -> so.getName().equalsIgnoreCase(apDto.getName())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}
			saveUpdateAccountProfiles.add(accountProfile);
		}
		log.info("Saving...accountProfileDTOs.Account Profiles" + saveUpdateAccountProfiles.size());
		bulkOperationRepositoryCustom.bulkSaveAccountProfile(saveUpdateAccountProfiles);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
		dectivatedac.forEach(data -> log.info("deactivated id " + dectivatedac.size() +" +"+ data));
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
			location.setActivated(locDto.getActivated());
			location.setLocationId(locDto.getLocationId());
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
	public void saveUpdateLocationsId(final List<LocationDTO> locationDTOs, final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<Location> saveUpdateLocations = new HashSet<>();
		// find all locations
		List<Location> locations = locationRepository.findAllByCompanyId(company.getId());
		for (LocationDTO locDto : locationDTOs) {
			Optional<Location> optionalLoc = null;
			// check exist by name, only one exist with a name
			if (locDto.getLocationId() == null) {
				optionalLoc = locations.stream().filter(p -> p.getName().equals(locDto.getName())).findAny();
			} else {
				optionalLoc = locations.stream().filter(
						p -> p.getLocationId() != null ? p.getLocationId().equals(locDto.getLocationId()) : false)
						.findAny();
			}
			Location location;
			if (optionalLoc.isPresent()) {
				location = optionalLoc.get();
				// if not update, skip this iteration.
				// if (!location.getThirdpartyUpdate()) {continue;}
			} else {
				location = new Location();
				location.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid());
				location.setLocationId(locDto.getLocationId());
				location.setCompany(company);
			}
			location.setActivated(locDto.getActivated());
			location.setName(locDto.getName());
			Optional<Location> opAccP = saveUpdateLocations.stream()
					.filter(so -> so.getName().equalsIgnoreCase(locDto.getName())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}
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
	public void saveUpdateLocationsUpdationId(List<LocationDTO> locationDTOs, final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<Location> saveUpdateLocations = new HashSet<>();
		locationDTOs=locationDTOs.stream().filter(data -> !data.getName().equals("Territory")).collect(Collectors.toList());
		// find all locations
		List<Location> locations = locationRepository.findAllByCompanyId(company.getId());
		for (LocationDTO locDto : locationDTOs) {
			Optional<Location> optionalLoc = null;
			// check exist by name, only one exist with a name

			optionalLoc = locations.stream()
					.filter(p -> p.getLocationId() != null && !p.getLocationId().equals("")
							? p.getLocationId().equals(locDto.getLocationId())
							: false)
					.findAny();
			Location location;
			if (optionalLoc.isPresent()) {
				location = optionalLoc.get();
				// if not update, skip this iteration.
				// if (!location.getThirdpartyUpdate()) {continue;}
			} else {
				location = new Location();
				location.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid());
				location.setLocationId(locDto.getLocationId());
				location.setDescription(locDto.getName());
				location.setCompany(company);
			}
			location.setActivated(locDto.getActivated());
			location.setDescription(locDto.getName());
			location.setName(locDto.getName() + "~" + locDto.getLocationId());
			Optional<Location> opAccP = saveUpdateLocations.stream()
					.filter(so -> so.getName().equalsIgnoreCase(locDto.getName())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}
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
		Set<Long> locationIds = new HashSet<>();
		// Only one version of a company hierarchy is active at a time
		Optional<LocationHierarchy> locationHierarchy = locationHierarchyRepository
				.findFirstByCompanyIdAndActivatedTrueOrderByIdDesc(companyId);
//		List<LocationHierarchyDTO> locationHierarchyServiceCustomisTrue = locationHierarchyService.findLocationHierarchyIscustomIstrue();
//		locationHierarchyServiceCustomisTrue.forEach(data -> locationHierarchyDTOs.forEach(data1->{
//			if(data.getParentName().equals(data1.getLocationName()) && !data.getParentName().equals("Territory")) {
//				locationIds.add(data.getLocationId());
//			}
//		}));
//		if (!locationIds.isEmpty()) {
//			locationHierarchyRepository.updateLocationHierarchyInactivatedForListOflocationIds(ZonedDateTime.now(),
//					locationIds);
//		}

		if (locationHierarchy.isPresent()) {
			locationHierarchyRepository.updateLocationHierarchyInactivatedForTally(ZonedDateTime.now(),
					locationHierarchy.get().getVersion(), false);
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
						locationHierarchyRepository.insertLocationHierarchyWithParentInTally(version,
								optionalLoc.get().getId(), optionalParentLoc.get().getId());
					}
				} else {

					locationHierarchyRepository.insertLocationHierarchyWithNoParentInTally(version,
							optionalLoc.get().getId());
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
	public void saveUpdateLocationHierarchyUpdateId(final List<LocationHierarchyDTO> locationHierarchyDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		Long companyId = syncOperation.getCompany().getId();
		Long version;
		Set<Long> locationIds = new HashSet<>();
		// Only one version of a company hierarchy is active at a time
		Optional<LocationHierarchy> locationHierarchy = locationHierarchyRepository
				.findFirstByCompanyIdAndActivatedTrueOrderByIdDesc(companyId);
//		List<LocationHierarchyDTO> locationHierarchyServiceCustomisTrue = locationHierarchyService
//				.findLocationHierarchyIscustomIstrue();
//		if (!locationHierarchyServiceCustomisTrue.isEmpty()) {
//			locationHierarchyServiceCustomisTrue.forEach(data -> locationHierarchyDTOs.forEach(data1 -> {
//				if (data.getParentName().equals(data1.getLocationName()) && !data.getParentName().equals("Territory")) {
//					locationIds.add(data.getLocationId());
//				}
//			}));
//		}
//		if (!locationIds.isEmpty()) {
//			locationHierarchyRepository.updateLocationHierarchyInactivatedForListOflocationIds(ZonedDateTime.now(),
//					locationIds);
//		}

		if (locationHierarchy.isPresent()) {
			locationHierarchyRepository.updateLocationHierarchyInactivatedForTally(ZonedDateTime.now(),
					locationHierarchy.get().getVersion(), false);
			version = locationHierarchy.get().getVersion() + 1;
		} else {
			version = 1L;
		}
		// find all locations
		List<Location> locations = locationRepository.findByCompanyIdAndActivatedTrue(companyId);
		// create hierarchy
		locations.forEach(data -> {
			String[] name = data.getName().split("~");
			data.setName(name[0]);
		});
		
		for (LocationHierarchyDTO locationDTO : locationHierarchyDTOs) {
			// check location exist
			Optional<Location> optionalLoc = locations.stream()
					.filter(p -> p.getName().equals(locationDTO.getLocationName())).findAny();
			if (optionalLoc.isPresent()) {
				if(!optionalLoc.get().getName().equals("Territory") && !optionalLoc.get().getName().equals("Primary")) {
				optionalLoc.get().setName(optionalLoc.get().getName() + "~" + optionalLoc.get().getLocationId());
				}
				if (locationDTO.getParentName() != null && locationDTO.getParentName().length() > 0) {
					// check parent location exist
					Optional<Location> optionalParentLoc = locations.stream()
							.filter(p -> p.getName().equals(locationDTO.getParentName())).findAny();
//					if(!optionalParentLoc.get().getName().equals("Territory") && !optionalParentLoc.get().getName().equals("Primary")) {
//						optionalParentLoc.get().setName(optionalParentLoc.get().getName() + "~" + optionalParentLoc.get().getLocationId());
//					}
				
					if (optionalParentLoc.isPresent()) {
						locationHierarchyRepository.insertLocationHierarchyWithParentInTally(version,
								optionalLoc.get().getId(), optionalParentLoc.get().getId());
					}
				} else {

					locationHierarchyRepository.insertLocationHierarchyWithNoParentInTally(version,
							optionalLoc.get().getId());
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
	public void saveUpdateLocationHierarchyExcel(Company company, Set<String> accountNames,
			List<Location> existingLocationList, Location defaultLocation) {
		Long companyId = company.getId();
		Long version;
		// Only one version of a company hierarchy is active at a time
		Optional<LocationHierarchy> locationHierarchy = locationHierarchyRepository
				.findFirstByCompanyIdAndActivatedTrueOrderByIdDesc(companyId);
		if (locationHierarchy.isPresent()) {
			locationHierarchyRepository.updateLocationHierarchyInactivatedFor(ZonedDateTime.now(),
					locationHierarchy.get().getVersion());
			version = locationHierarchy.get().getVersion() + 1;
			locationHierarchyRepository.insertLocationHierarchyWithNoParent(version, defaultLocation.getId());
		} else {
			version = 1L;
		}

		// create hierarchy
		for (String accounts : accountNames) {
			Optional<Location> opLocation = existingLocationList.stream().filter(ell -> ell.getName().equals(accounts))
					.findAny();
			if (opLocation.isPresent()) {
				locationHierarchyRepository.insertLocationHierarchyWithParent(version, opLocation.get().getId(),
						defaultLocation.getId());
			}
		}

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
			Optional<Location> loc = locations.stream().filter(pl -> locAccDto.getLocationName().equals(pl.getName()))
					.findAny();
			// find accountprofile
			Optional<AccountProfile> acc = accountProfiles.stream()
					.filter(ap -> locAccDto.getAccountProfileName().equals(ap.getName())).findAny();

			if (loc.isPresent() && acc.isPresent()) {

				List<Long> locationAccountProfileIds = locationAccountProfiles.stream()
						.filter(lap -> acc.get().getPid().equals(lap.getAccountProfile().getPid()))
						.map(lap -> lap.getId()).collect(Collectors.toList());
				if (locationAccountProfileIds.size() != 0) {
					locationAccountProfilesIds.addAll(locationAccountProfileIds);
				}

				profile.setLocation(loc.get());
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
	public void saveUpdateLocationAccountProfilesUpdateId(final List<LocationAccountProfileDTO> locationAccountProfileDTOs,
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
		locations.forEach(data -> {
			String[] name = data.getName().split("~");
			data.setName(name[0]);
		});
		accountProfiles.forEach(data -> {
			String[] name = data.getName().split("~");
			data.setName(name[0]);
		});
		for (LocationAccountProfileDTO locAccDto : locationAccountProfileDTOs) {

			LocationAccountProfile profile = new LocationAccountProfile();
			// find location
			Optional<Location> loc = locations.stream().filter(pl -> locAccDto.getLocationName().equals(pl.getName()))
					.findAny();
			// find accountprofile
			Optional<AccountProfile> acc = accountProfiles.stream()
					.filter(ap -> locAccDto.getAccountProfileName().equals(ap.getName())).findAny();
			
			if (loc.isPresent() && acc.isPresent()) {

				List<Long> locationAccountProfileIds = locationAccountProfiles.stream()
						.filter(lap -> acc.get().getPid().equals(lap.getAccountProfile().getPid()))
						.map(lap -> lap.getId()).collect(Collectors.toList());
				if (locationAccountProfileIds.size() != 0) {
					locationAccountProfilesIds.addAll(locationAccountProfileIds);
				}
				
				profile.setLocation(loc.get());
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
		List<String> apNames = receivablePayableDTOs.stream().map(rp -> rp.getAccountName().toUpperCase())
				.collect(Collectors.toList());
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_123" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by compId and name ingore case";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfiles = accountProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), apNames);
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
		// delete all receivable payable
		receivablePayableRepository.deleteByCompanyId(company.getId());
		for (ReceivablePayableDTO rpDto : receivablePayableDTOs) {
			// only save if account profile exist
			accountProfiles.stream().filter(a -> a.getName().equalsIgnoreCase(rpDto.getAccountName())).findAny()
					.ifPresent(ap -> {
						ReceivablePayable receivablePayable = new ReceivablePayable();
						if (receivablePayable.getReceivablePayableType() == null) {
							receivablePayable.setReceivablePayableType(ReceivablePayableType.Receivable);
						}
						receivablePayable.setReceivablePayableType(rpDto.getReceivablePayableType());
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
		bulkOperationRepositoryCustom.bulkSaveReceivablePayables(saveReceivablePayable);
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
	public void saveUpdateReceivablePayablesUpdateId(final List<ReceivablePayableDTO> receivablePayableDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<ReceivablePayable> saveReceivablePayable = new HashSet<>();
		// find all exist account profiles
		List<String> apNames = receivablePayableDTOs.stream().map(rp -> rp.getAccountName().toUpperCase())
				.collect(Collectors.toList());
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_123" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by compId and name ingore case";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfiles = accountProfileRepository
				.findByCompanyIdAndActivatedTrue(company.getId());
		accountProfiles.forEach(data -> {
			String[] name = data.getName().split("~");
			data.setName(name[0]);
		});
		
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
		// delete all receivable payable
		receivablePayableRepository.deleteByCompanyId(company.getId());
		for (ReceivablePayableDTO rpDto : receivablePayableDTOs) {
			// only save if account profile exist
			accountProfiles.stream().filter(a -> a.getName().equalsIgnoreCase(rpDto.getAccountName())).findAny()
					.ifPresent(ap -> {
						ReceivablePayable receivablePayable = new ReceivablePayable();
						if (receivablePayable.getReceivablePayableType() == null) {
							receivablePayable.setReceivablePayableType(ReceivablePayableType.Receivable);
						}
						receivablePayable.setReceivablePayableType(rpDto.getReceivablePayableType());
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
		bulkOperationRepositoryCustom.bulkSaveReceivablePayables(saveReceivablePayable);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		//update name
		List<AccountProfile> accountProfilescopy = accountProfileRepository
				.findByCompanyIdAndActivatedTrue(company.getId());
		if(!accountProfilescopy.isEmpty()) {
			accountProfilescopy.forEach(data -> {
				if(!data.getName().contains("~")) {
				data.setName(data.getName() + "~" + data.getCustomerId());
				}
			});}
		accountProfileRepository.save(accountProfilescopy);
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	@Async
	public void saveUpdateAccountProfileClosingBalances(final List<AccountProfileDTO> accountProfileDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		Long companyId = syncOperation.getCompany().getId();
		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();
		// find all exist account profiles
		List<String> apNames = accountProfileDTOs.stream().map(a -> a.getName().toUpperCase())
				.collect(Collectors.toList());
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_123" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by compId and name ingore case";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfiles = accountProfileRepository.findByCompanyIdAndNameIgnoreCaseIn(companyId,
				apNames);
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

		for (AccountProfileDTO apDto : accountProfileDTOs) {
			// check exist by name, only one exist with a name
			Optional<AccountProfile> optionalAP = accountProfiles.stream()
					.filter(pc -> pc.getName().equalsIgnoreCase(apDto.getName())).findAny();
			AccountProfile accountProfile;
			if (optionalAP.isPresent()) {
				accountProfile = optionalAP.get();
				accountProfile.setClosingBalance(apDto.getClosingBalance());
				saveUpdateAccountProfiles.add(accountProfile);
			}
		}
		// updateing closing balance to zero
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "AP_QUERY_125" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 = "Updating the account closing balance zero by compId";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		accountProfileRepository.updateAccountProfileClosingBalanceZeroByCompanyId(companyId);
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
	public void saveUpdateAccountProfileClosingBalancesUpdateId(final List<AccountProfileDTO> accountProfileDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		Long companyId = syncOperation.getCompany().getId();
		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();
		// find all exist account profiles
		List<String> apNames = accountProfileDTOs.stream().map(a -> a.getName().toUpperCase())
				.collect(Collectors.toList());
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_123" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by compId and name ingore case";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfiles = accountProfileRepository
				.findByCompanyIdAndActivatedTrue(companyId);
		accountProfiles.forEach(data -> {
			String[] name = data.getName().split("~");
			data.setName(name[0]);
		});
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

		
		for (AccountProfileDTO apDto : accountProfileDTOs) {
			// check exist by name, only one exist with a name
			Optional<AccountProfile> optionalAP = accountProfiles.stream()
					.filter(pc -> pc.getName().equalsIgnoreCase(apDto.getName())).findAny();
			AccountProfile accountProfile;
			if (optionalAP.isPresent()) {
				accountProfile = optionalAP.get();
				accountProfile.setClosingBalance(apDto.getClosingBalance());
				saveUpdateAccountProfiles.add(accountProfile);
			}
		}
		// updateing closing balance to zero
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "AP_QUERY_125" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 = "Updating the account closing balance zero by compId";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		accountProfileRepository.updateAccountProfileClosingBalanceZeroByCompanyId(companyId);
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
		bulkOperationRepositoryCustom.bulkSaveAccountProfile(saveUpdateAccountProfiles);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		//update name  
		List<AccountProfile> accountProfilescopy = accountProfileRepository
				.findByCompanyIdAndActivatedTrue(companyId);
		if(!accountProfilescopy.isEmpty()) {
			accountProfilescopy.forEach(data -> {
				if(!data.getName().contains("~")) {
				data.setName(data.getName() + "~" + data.getCustomerId());
				}
			});}
		accountProfileRepository.save(accountProfilescopy);
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	@Async
	public void saveUpdatePriceLevelAccountProductGroups(
			List<PriceLevelAccountProductGroupDTO> priceLevelAccountProductGroupDTOs, SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		List<PriceLevelAccountProductGroup> savePriceLevelAccountProductGroup = new ArrayList<>();
		// find all exist account profiles
		List<String> apNames = priceLevelAccountProductGroupDTOs.stream().map(rp -> rp.getAccountName().toUpperCase())
				.collect(Collectors.toList());
		List<AccountProfile> accountProfiles = accountProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), apNames);

		// find all exist product groups
		List<String> pgNames = priceLevelAccountProductGroupDTOs.stream()
				.map(rp -> rp.getProductGroupName().toUpperCase()).collect(Collectors.toList());
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyIdAndNameIgnoreCaseIn(company.getId(),
				pgNames);

		// find all exist price levels
		List<String> plNames = priceLevelAccountProductGroupDTOs.stream()
				.map(rp -> rp.getPriceLevelName().toUpperCase()).collect(Collectors.toList());
		List<PriceLevel> priceLevels = priceLevelRepository.findByCompanyIdAndNameIgnoreCaseIn(company.getId(),
				plNames);

		// delete all receivable payable
		receivablePayableRepository.deleteByCompanyId(company.getId());
		for (PriceLevelAccountProductGroupDTO papg : priceLevelAccountProductGroupDTOs) {
			// only save if account profile exist

			Optional<AccountProfile> accpro = accountProfiles.stream()
					.filter(a -> a.getName().equals(papg.getAccountName())).findAny();

			Optional<ProductGroup> prodgrp = productGroups.stream()
					.filter(a -> a.getName().equals(papg.getProductGroupName())).findAny();

			Optional<PriceLevel> prilvl = priceLevels.stream().filter(a -> a.getName().equals(papg.getPriceLevelName()))
					.findAny();

			if (accpro.isPresent() && prodgrp.isPresent() && prilvl.isPresent()) {

				Optional<PriceLevelAccountProductGroup> pLevelAccountPGroup = priceLevelAccountProductGroupRepository
						.findOneByAccountProfilePidAndProductGroupPidAndPriceLevelPid(company.getId(),
								accpro.get().getPid(), prodgrp.get().getPid(), prilvl.get().getPid());
				if (!pLevelAccountPGroup.isPresent()) {
					PriceLevelAccountProductGroup priceLevelAccountProductGroup = new PriceLevelAccountProductGroup();
					priceLevelAccountProductGroup.setAccountProfile(accpro.get());
					priceLevelAccountProductGroup.setCompany(company);
					priceLevelAccountProductGroup
							.setPid(PriceLevelAccountProductGroupService.PID_PREFIX + RandomUtil.generatePid());
					priceLevelAccountProductGroup.setPriceLevel(prilvl.get());
					priceLevelAccountProductGroup.setProductGroup(prodgrp.get());
					savePriceLevelAccountProductGroup.add(priceLevelAccountProductGroup);
				}
			}
		}
		bulkOperationRepositoryCustom.bulkPriceLevelAccountProductGroups(savePriceLevelAccountProductGroup);
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
	public void saveOrUpdateGstLedgers(List<GstLedgerDTO> gstLedgers, SyncOperation syncOperation) {
		List<GstLedger> saveGstLedgers = new ArrayList<>();
		final Company company = syncOperation.getCompany();
		List<GstLedger> existingGstLedgers = gstLedgerRepository.findAllByCompanyId(company.getId());
		for (GstLedgerDTO gstLedgerDTO : gstLedgers) {
			Optional<GstLedger> opGstLedger = existingGstLedgers.stream()
					.filter(eGst -> eGst.getName().equals(gstLedgerDTO.getName())).findAny();
			GstLedger gstLedger = null;
			if (opGstLedger.isPresent()) {
				gstLedger = opGstLedger.get();
				gstLedger.setTaxRate(gstLedgerDTO.getTaxRate());
			} else {
				gstLedger = new GstLedger();
				gstLedger.setName(gstLedgerDTO.getName());
				gstLedger.setTaxRate(gstLedgerDTO.getTaxRate());
				gstLedger.setCompany(company);
				gstLedger.setTaxType(gstLedgerDTO.getTaxType());
				gstLedger.setAccountType(GstAccountType.DUTIES_AND_TAXES);
				gstLedger.setActivated(false);
			}
			saveGstLedgers.add(gstLedger);
		}
		gstLedgerRepository.save(saveGstLedgers);
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
