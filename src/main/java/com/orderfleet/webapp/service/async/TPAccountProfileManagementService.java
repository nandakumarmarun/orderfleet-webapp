package com.orderfleet.webapp.service.async;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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

	private final GstLedgerRepository gstLedgerRepository;

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
			LocationService locationService,GstLedgerRepository gstLedgerRepository) {
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
	}

	@Transactional
	public void saveUpdateAccountProfiles(final List<AccountProfileDTO> accountProfileDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		final Long companyId = company.getId();
		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();
		// All product must have a division/category, if not, set a default one
		AccountType defaultAccountType = accountTypeRepository.findFirstByCompanyId(company.getId());
		// find all exist account profiles
		List<String> apNames = accountProfileDTOs.stream().map(apDto -> apDto.getName().toUpperCase())
				.collect(Collectors.toList());
		List<AccountProfile> accountProfiles = accountProfileRepository.findByCompanyIdAndNameIgnoreCaseIn(companyId,
				apNames);
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
			accountProfile.setTrimChar(apDto.getTrimChar());
			accountProfile.setAlias(apDto.getAlias());
			if (isValidPhone(apDto.getPhone1())) {
				accountProfile.setPhone1(apDto.getPhone1());
			}
			if (isValidPhone(apDto.getPhone2())) {
				accountProfile.setPhone2(apDto.getPhone2());
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
			saveUpdateAccountProfiles.add(accountProfile);
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
			Optional<Location> loc = locations.stream().filter(pl -> locAccDto.getLocationName().equals(pl.getName()))
					.findAny();
			// find accountprofile
			Optional<AccountProfile> acc = accountProfiles.stream()
					.filter(ap -> locAccDto.getAccountProfileName().equals(ap.getName()))
					.findAny();

			if (loc.isPresent() && acc.isPresent()) {

				List<Long> locationAccountProfileIds = locationAccountProfiles
						.stream().filter(lap -> acc.get().getPid().equals(lap.getAccountProfile().getPid()))
						.map(lap -> lap.getId())
						.collect(Collectors.toList());
				if(locationAccountProfileIds.size() != 0){
					locationAccountProfilesIds.addAll(locationAccountProfileIds);
				}

				profile.setLocation(loc.get());
				profile.setAccountProfile(acc.get());
				profile.setCompany(company);
				newLocationAccountProfiles.add(profile); 
			}
		}
		if(locationAccountProfilesIds.size() != 0) {
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
		List<AccountProfile> accountProfiles = accountProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), apNames);
		// delete all receivable payable
		receivablePayableRepository.deleteByCompanyId(company.getId());
		for (ReceivablePayableDTO rpDto : receivablePayableDTOs) {
			// only save if account profile exist
			accountProfiles.stream().filter(a -> a.getName().equalsIgnoreCase(rpDto.getAccountName())).findAny().ifPresent(ap -> {
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
	public void saveUpdateAccountProfileClosingBalances(final List<AccountProfileDTO> accountProfileDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		Long companyId = syncOperation.getCompany().getId();
		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();
		// find all exist account profiles
		List<String> apNames = accountProfileDTOs.stream().map(a -> a.getName().toUpperCase())
				.collect(Collectors.toList());

		List<AccountProfile> accountProfiles = accountProfileRepository.findByCompanyIdAndNameIgnoreCaseIn(companyId,
				apNames);

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
		accountProfileRepository.updateAccountProfileClosingBalanceZeroByCompanyId(companyId);
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
		for(GstLedgerDTO gstLedgerDTO : gstLedgers){
			Optional<GstLedger> opGstLedger = existingGstLedgers.stream()
									.filter(eGst -> eGst.getName().equals(gstLedgerDTO.getName())).findAny();
				GstLedger gstLedger = null;
				if(opGstLedger.isPresent()) {
					gstLedger = opGstLedger.get();
					gstLedger.setTaxRate(gstLedgerDTO.getTaxRate());
				}else {
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
