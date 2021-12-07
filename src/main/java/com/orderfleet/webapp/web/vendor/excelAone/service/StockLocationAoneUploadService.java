package com.orderfleet.webapp.web.vendor.excelAone.service;

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
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PriceLevelAccountProductGroup;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.domain.enums.StockLocationType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.PriceLevelAccountProductGroupRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
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
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelAccountProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;

/**
 * Service for save/update account profile related data from third party
 * softwares like tally.
 * <p>
 * Use the @Async annotation to process asynchronously.
 * </p>
 */
@Service
public class StockLocationAoneUploadService {

	private final Logger log = LoggerFactory.getLogger(StockLocationAoneUploadService.class);
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
	
	private final StockLocationRepository stockLocationRepository;

	public StockLocationAoneUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
			SyncOperationRepository syncOperationRepository, PriceLevelRepository priceLevelRepository,
			AccountProfileRepository accountProfileRepository, AccountTypeRepository accountTypeRepository,
			AccountProfileService accountProfileService, LocationRepository locationRepository,
			LocationHierarchyRepository locationHierarchyRepository,
			LocationAccountProfileRepository locationAccountProfileRepository,
			ReceivablePayableRepository receivablePayableRepository,
			LocationAccountProfileService locationAccountProfileService, UserRepository userRepository,
			ProductGroupRepository productGroupRepository,
			PriceLevelAccountProductGroupRepository priceLevelAccountProductGroupRepository,
			LocationService locationService,StockLocationRepository stockLocationRepository) {
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
		this.stockLocationRepository=stockLocationRepository;
	}

	@Transactional
	public void saveUpdateStockLocations(final List<StockLocationDTO> stockLocationDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<StockLocation> saveUpdateStockLocations = new HashSet<>();
		// find all locations
		List<StockLocation> stockLocations = stockLocationRepository.findAllByCompanyId(company.getId());
		for (StockLocationDTO locDto : stockLocationDTOs) {
			// check exist by name, only one exist with a name
			Optional<StockLocation> optionalLoc = stockLocations.stream()
					.filter(sl -> sl.getName().equals(locDto.getName())).findAny();
			StockLocation stockLocation;
			if (optionalLoc.isPresent()) {
				stockLocation = optionalLoc.get();
				// if not update, skip this iteration.
				// if (!location.getThirdpartyUpdate()) {continue;}
			} else {
				stockLocation = new StockLocation();
				stockLocation.setPid(StockLocationService.PID_PREFIX + RandomUtil.generatePid());
				stockLocation.setAlias(locDto.getAlias());
				stockLocation.setStockLocationType(StockLocationType.ACTUAL);
				stockLocation.setCompany(company);
			}
			stockLocation.setName(locDto.getName());
			stockLocation.setActivated(locDto.getActivated());
			saveUpdateStockLocations.add(stockLocation);
		}
		bulkOperationRepositoryCustom.bulkSaveStockLocations(saveUpdateStockLocations);
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
			Optional<Location> loc = locations.stream().filter(pl -> locAccDto.getLocationName().equalsIgnoreCase(pl.getAlias()))
					.findAny();
			// find accountprofile
			Optional<AccountProfile> acc = accountProfiles.stream()
					.filter(ap -> locAccDto.getAccountProfileName().equalsIgnoreCase(ap.getAlias()))
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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_123" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by compId and name ingore case";
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
			accountProfiles.stream().filter(a -> a.getName().equals(rpDto.getAccountName())).findAny().ifPresent(ap -> {
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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_123" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by compId and name ingore case";
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
					.filter(pc -> pc.getName().equals(apDto.getName())).findAny();
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
			String description1 ="Updating the account closing balance zero by compId";
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
	public void saveUpdatePriceLevelAccountProductGroups(
			List<PriceLevelAccountProductGroupDTO> priceLevelAccountProductGroupDTOs, SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		List<PriceLevelAccountProductGroup> savePriceLevelAccountProductGroup = new ArrayList<>();
		// find all exist account profiles
		List<String> apNames = priceLevelAccountProductGroupDTOs.stream().map(rp -> rp.getAccountName().toUpperCase())
				.collect(Collectors.toList());
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_123" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get by compId and name ingore case";
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
