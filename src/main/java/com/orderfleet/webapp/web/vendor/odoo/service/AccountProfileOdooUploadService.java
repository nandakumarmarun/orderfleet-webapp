package com.orderfleet.webapp.web.vendor.odoo.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooAccountProfile;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooAccountProfileCreditTerms;

/**
 * Service for save/update account profile related data from third party
 * softwares like tally.
 * <p>
 * Use the @Async annotation to process asynchronously.
 * </p>
 */
@Service
public class AccountProfileOdooUploadService {

	private final Logger log = LoggerFactory.getLogger(AccountProfileOdooUploadService.class);

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

	private final PriceLevelRepository priceLevelRepository;

	public AccountProfileOdooUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
			AccountProfileRepository accountProfileRepository, AccountTypeRepository accountTypeRepository,
			AccountProfileService accountProfileService, LocationRepository locationRepository,
			LocationAccountProfileRepository locationAccountProfileRepository,
			LocationAccountProfileService locationAccountProfileService, UserRepository userRepository,
			LocationService locationService, CompanyRepository companyRepository,
			PriceLevelRepository priceLevelRepository) {
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
		this.priceLevelRepository = priceLevelRepository;
	}

	@Transactional
	public void saveUpdateAccountProfiles(final List<OdooAccountProfile> list) {

		log.info("Saving Account Profiles...");
		long start = System.nanoTime();

		final User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();
		// All product must have a division/category, if not, set a default one
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AT_QUERY_109" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get first by compId";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountType defaultAccountType = accountTypeRepository.findFirstByCompanyIdOrderByIdAsc(companyId);
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
		List<String> apNames = list.stream().map(apDto -> apDto.getName().toUpperCase()).collect(Collectors.toList());
//		List<AccountProfile> accountProfiles = accountProfileRepository.findByCompanyIdAndNameIgnoreCaseIn(companyId,
//				apNames);
		 DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "AP_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 ="get all by compId";
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

		List<PriceLevel> tempPriceLevel = priceLevelRepository.findByCompanyId(companyId);
		int i = 0;

		for (OdooAccountProfile apDto : list) {

			AccountProfile accountProfile = new AccountProfile();
			// check exist by name, only one exist with a name
//			Optional<AccountProfile> optionalAP = accountProfiles.stream()
//					.filter(pc -> pc.getName().equalsIgnoreCase(apDto.getName())).findAny();
			Optional<AccountProfile> optionalAP = accountProfiles.stream()
					.filter(pc -> pc.getCustomerId() != null && !pc.getCustomerId().equals("")
							? pc.getCustomerId().equalsIgnoreCase(String.valueOf(apDto.getId()))
							: false)
					.findAny();

//			Optional<AccountProfile> optionalAP = null;
//			// check exist by name, only one exist with a name
//			if (String.valueOf(apDto.getId()) == null) {
//				optionalAP = accountProfiles.stream()
//						.filter(pc -> pc.getName().equalsIgnoreCase(apDto.getName() + "-" + apDto.getRef()))
//						.findFirst();
//			} else {
//				optionalAP = accountProfiles.stream()
//						.filter(pc -> pc.getCustomerId() != null
//								? pc.getCustomerId().equalsIgnoreCase(String.valueOf(apDto.getId()))
//								: false)
//						.findFirst();
//			}

			if (optionalAP.isPresent()) {
				accountProfile = optionalAP.get();
				System.out.println(
						accountProfile.getId() + "---------" + i++ + "=========" + accountProfile.getCustomerId());
				// if not update, skip this iteration. Not implemented now
				// if (!accountProfile.getThirdpartyUpdate()) { continue; }
			} else {
				accountProfile = new AccountProfile();
				accountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
//				accountProfile.setName(apDto.getName());
				accountProfile.setCustomerId(String.valueOf(apDto.getId()));
				accountProfile.setUser(user);
				accountProfile.setCompany(company);
				accountProfile.setAccountStatus(AccountStatus.Unverified);
				accountProfile.setDataSourceType(DataSourceType.TALLY);
				accountProfile.setImportStatus(true);
			}

			accountProfile.setName(apDto.getName() + "-" + apDto.getRef());

			// price level
			Optional<PriceLevel> optionalPriceLevel = tempPriceLevel.stream()
					.filter(pl -> String.valueOf(apDto.getPrice_list_id()).equals(pl.getAlias())).findAny();

			for (OdooAccountProfileCreditTerms creditTerm : apDto.getCredit_terms()) {
				accountProfile.setCreditLimit(creditTerm.getCredit_limit());
				accountProfile.setCreditDays(creditTerm.getPayment_term_days());
			}

			if (optionalPriceLevel.isPresent()) {
				accountProfile.setDefaultPriceLevel(optionalPriceLevel.get());
			}

//			accountProfile.setCustomerId(String.valueOf(apDto.getId()));

			if (apDto.getEmail() != null && !apDto.getEmail().equals("")) {
				if (isValidEmail(apDto.getEmail())) {
					accountProfile.setEmail1(apDto.getEmail());
				}
			} else {
				accountProfile.setEmail1("");
			}

			accountProfile.setAlias(apDto.getRef());

			if (apDto.getTrn() != null && !apDto.getTrn().equals("")) {
				accountProfile.setTinNo(apDto.getTrn());
			} else {
				accountProfile.setTinNo(null);
			}

			accountProfile.setDescription(apDto.getName());

			accountProfile.setActivated(true);

			if (apDto.getStreet() != null && !apDto.getStreet().equals("")) {
				if (apDto.getStreet2() != null && !apDto.getStreet2().equals("")) {
					accountProfile.setAddress(apDto.getStreet());
				} else {
					accountProfile.setAddress(apDto.getStreet() + "," + apDto.getStreet2());
				}
			} else {
				accountProfile.setAddress("No Address");
			}

			if (apDto.getCity() != null && !apDto.getCity().equals("")) {
				accountProfile.setCity(apDto.getCity());
			} else {
				accountProfile.setCity("No City");
			}

			if (apDto.getPhone() != null && !apDto.getPhone().equals("")) {
				accountProfile.setPhone1(apDto.getPhone());
			} else {
				accountProfile.setPhone1("");
			}

			// accountProfile.setDescription(String.valueOf(apDto.getId()));

			// accountProfile.setClosingBalance(Double.parseDouble(apDto.getCredit()));

			// account type

			if (accountProfile.getAccountType() == null) {
				accountProfile.setAccountType(defaultAccountType);
			}
			Optional<AccountProfile> opAccP = saveUpdateAccountProfiles.stream()
					.filter(so -> so.getName().equalsIgnoreCase(apDto.getName() + "-" + apDto.getRef())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}
			accountProfile.setDataSourceType(DataSourceType.TALLY);
			saveUpdateAccountProfiles.add(accountProfile);
		}
		accountProfileRepository.save(saveUpdateAccountProfiles);
		// bulkOperationRepositoryCustom.bulkSaveAccountProfile(saveUpdateAccountProfiles);

		// saveUpdateLocationAccountProfiles(locationAccountProfileDtos);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	public void saveUpdateLocations(final List<LocationDTO> locationDTOs) {
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
			saveUpdateLocations.add(location);
		}
		bulkOperationRepositoryCustom.bulkSaveLocations(saveUpdateLocations);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	@Async
	public void saveUpdateLocationAccountProfiles(final List<LocationAccountProfileDTO> locationAccountProfileDTOs) {
		log.info("Saving Location Account Profiles...");
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

		locationAccountProfileRepository.deleteByCompany(companyId);

		locationAccountProfileRepository.save(newLocationAccountProfiles);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

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

}
