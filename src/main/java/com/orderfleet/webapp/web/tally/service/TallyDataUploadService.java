package com.orderfleet.webapp.web.tally.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.GstLedger;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.PostDatedVoucher;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PriceLevelList;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.GstAccountType;
import com.orderfleet.webapp.domain.enums.StockLocationType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.GstLedgerRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.PostDatedVoucherRepository;
import com.orderfleet.webapp.repository.PriceLevelListRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.PostDatedVoucherService;
import com.orderfleet.webapp.service.PriceLevelListService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.tally.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.tally.dto.GstLedgerDTO;
import com.orderfleet.webapp.web.tally.dto.LocationDTO;
import com.orderfleet.webapp.web.tally.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.tally.dto.PostDatedVoucherDTO;
import com.orderfleet.webapp.web.tally.dto.PriceLevelListDTO;
import com.orderfleet.webapp.web.tally.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.tally.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.tally.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.tally.dto.ReceivablePayableDTO;
import com.orderfleet.webapp.web.tally.dto.StockLocationDTO;
import com.orderfleet.webapp.web.tally.dto.StockSummaryDTO;

@Service
@Transactional
public class TallyDataUploadService {

	private static final Logger log = LoggerFactory.getLogger(TallyDataUploadService.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private ProductCategoryRepository productCategoryRepository;

	@Inject
	private DivisionRepository divisionRepository;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private PriceLevelRepository priceLevelRepository;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private StockLocationService stockLocationService;

	@Inject
	private ReceivablePayableRepository receivablePayableRepository;

	@Inject
	private PriceLevelListRepository priceLevelListRepository;

	@Inject
	private PostDatedVoucherRepository postDatedVoucherRepository;

	@Inject
	private GstLedgerRepository gstLedgerRepository;

	public List<AccountProfile> saveOrUpdateAccountProfile(List<AccountProfileDTO> accountProfileDtos,
			Company company) {
		log.info("Account Profile list size : {}" + accountProfileDtos.size());
		List<AccountProfile> accountProfileList = new ArrayList<>();
		List<AccountProfile> newAccountProfiles = new ArrayList<>();
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AT_QUERY_109" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get first by compId";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountType accountType = accountTypeRepository.findFirstByCompanyIdOrderByIdAsc(company.getId());
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
		// PriceLevel defaultPriceLevel =
		// priceLevelRepository.findFirstByCompanyId(company.getId());
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "AP_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 = "get all by compId";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		List<AccountProfile> dbAccountProfiles = accountProfileRepository.findAllByCompanyId(company.getId());

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

		List<PriceLevel> dbPriceLevels = priceLevelRepository.findByCompanyId(company.getId());
		for (AccountProfileDTO apUpload : accountProfileDtos) {
			AccountProfile toSaveAccountProfile;

			Optional<AccountProfile> opAccountProfile = dbAccountProfiles.stream()
					.filter(ap -> ap.getName() != null
							? ap.getName().equals(apUpload.getName()) || ap.getAlias().equals(apUpload.getGuid())
							: false)
					.findAny();

			Optional<PriceLevel> opPriceLevel = dbPriceLevels.stream()
					.filter(pl -> pl.getName().equals(apUpload.getDefaultPriceLevelName())).findAny();

			if (opAccountProfile.isPresent()) {
				toSaveAccountProfile = opAccountProfile.get();
			} else {
				toSaveAccountProfile = new AccountProfile();
				toSaveAccountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
				toSaveAccountProfile.setAlias(apUpload.getGuid());
				toSaveAccountProfile.setCompany(company);
				// defaults
				toSaveAccountProfile.setCity("No City");
				toSaveAccountProfile.setDataSourceType(DataSourceType.VENDOR);
				toSaveAccountProfile.setAccountStatus(AccountStatus.Verified);
				toSaveAccountProfile.setAccountType(accountType);
			}
			toSaveAccountProfile.setName(apUpload.getName());
			toSaveAccountProfile
					.setAddress(apUpload.getAddress() == null || apUpload.getAddress() == "NULL" ? "No Address"
							: apUpload.getAddress());
			toSaveAccountProfile.setPhone1(
					apUpload.getPhone1() == null || apUpload.getPhone1() == "NULL" || apUpload.getPhone1().length() > 20
							? "9999999999"
							: apUpload.getPhone1());
			toSaveAccountProfile.setClosingBalance(apUpload.getClosingBalance());
			toSaveAccountProfile.setActivated(true);
			if (opPriceLevel.isPresent()) {
				toSaveAccountProfile.setDefaultPriceLevel(opPriceLevel.get());
			} /*
				 * else{ toSaveAccountProfile.setDefaultPriceLevel(defaultPriceLevel); }
				 */
			newAccountProfiles.add(toSaveAccountProfile);
			// check account profile exist anymore in db
			dbAccountProfiles.removeIf(a -> a.getName().equals(apUpload.getName()));
		}
		dbAccountProfiles.removeIf(a -> a.getName().equals(company.getLegalName()));
		for (AccountProfile aa : dbAccountProfiles) {
			aa.setActivated(false);
			newAccountProfiles.add(aa);
		}
		accountProfileList.addAll(accountProfileRepository.save(newAccountProfiles));
		return accountProfileList;
	}

	public void saveOrUpdateLocations(List<LocationDTO> locationDtos, Company company) {
		log.info("Location list size : {}" + locationDtos.size());
		Set<Location> saveUpdateLocations = new HashSet<>();
		List<Location> locations = locationRepository.findAllByCompanyId(company.getId());
		for (LocationDTO locDto : locationDtos) {
			Optional<Location> optionalLoc = locations.stream()
					.filter(p -> p.getName().equals(locDto.getName()) || p.getAlias().equals(locDto.getGuid()))
					.findAny();
			Location location;
			if (optionalLoc.isPresent()) {
				location = optionalLoc.get();
			} else {
				location = new Location();
				location.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid());
				location.setName(locDto.getName());
				location.setCompany(company);
				location.setAlias(locDto.getGuid());
			}
			location.setActivated(true);
			saveUpdateLocations.add(location);
			// check location exist anymore in db
			locations.removeIf(a -> a.getName().equals(locDto.getName()));
		}
		locations.removeIf(a -> a.getName().equals("Territory"));
		for (Location loc : locations) {
			loc.setActivated(false);
			saveUpdateLocations.add(loc);
		}
		bulkOperationRepositoryCustom.bulkSaveLocations(saveUpdateLocations);
	}

	public void saveOrUpdateProductProfiles(List<ProductProfileDTO> productProfileDTOs, Company company) {
		log.info("ProductProfile list size : {}" + productProfileDTOs.size());
		final Division defaultDivision = divisionRepository.findFirstByCompanyId(company.getId());
		final ProductCategory defaultProductCategory = productCategoryRepository
				.findFirstByCompanyIdOrderById(company.getId());
		List<ProductProfile> dbProductProfiles = productProfileRepository.findAllByCompanyId(company.getId());
		List<ProductCategory> dbProductCategories = productCategoryRepository.findAllByCompanyId(company.getId());
		List<ProductGroup> existingProductGroups = productGroupRepository.findByCompanyId(company.getId());
		List<ProductProfile> newProductProfiles = new ArrayList<>();
		for (ProductProfileDTO product : productProfileDTOs) {

			ProductProfile toSaveProductProfile;
			// check product profile already exist
			Optional<ProductProfile> opProductProfile = dbProductProfiles.stream()
					.filter(p -> p.getName() != null
							? p.getName().equals(product.getName()) || p.getAlias().equals(product.getGuid())
							: false)
					.findAny();

			// check product category already exist
			Optional<ProductCategory> opProductCategory = dbProductCategories.stream()
					.filter(pc -> pc.getName().equals(product.getProductCategoryName())).findAny();

			List<Double> taxRates = existingProductGroups.stream()
					.filter(pg -> product.getDescription().equals(pg.getName())).map(pg -> pg.getTaxRate())
					.collect(Collectors.toList());

			double taxRate = taxRates.size() > 0 ? taxRate = taxRates.get(0) : 0;

			if (opProductProfile.isPresent()) {
				toSaveProductProfile = opProductProfile.get();
			} else {
				toSaveProductProfile = new ProductProfile();
				toSaveProductProfile.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
				toSaveProductProfile.setCompany(company);
				toSaveProductProfile.setAlias(product.getGuid());
				// defaults
				toSaveProductProfile.setDivision(defaultDivision);
			}
			if (opProductCategory.isPresent()) {
				toSaveProductProfile.setProductCategory(opProductCategory.get());
			} else {
				toSaveProductProfile.setProductCategory(defaultProductCategory);
			}
			toSaveProductProfile.setName(product.getName());
			toSaveProductProfile.setSku(product.getSku());

			toSaveProductProfile.setTaxRate(product.getTaxRate() == 0 ? taxRate : product.getTaxRate());
			toSaveProductProfile.setMrp(product.getMrp());
			toSaveProductProfile.setPrice(BigDecimal.valueOf(product.getPrice()));
			toSaveProductProfile.setActivated(true);

			Optional<ProductProfile> opPProfile = newProductProfiles.stream()
					.filter(pp -> pp.getName().equals(product.getName())).findAny();

			if (!opPProfile.isPresent()) {
				newProductProfiles.add(toSaveProductProfile);
			}
			// check product profile exist anymore in db
			dbProductProfiles.removeIf(p -> p.getName().equals(product.getName()));
		}
		for (ProductProfile pp : dbProductProfiles) {
			pp.setActivated(false);
			newProductProfiles.add(pp);
		}
		productProfileRepository.save(newProductProfiles);
	}

	public void saveOrUpdateProductGroup(List<ProductGroupDTO> productGroupDtos, Company company) {
		log.info("Product Group list size : {}" + productGroupDtos.size());
		Set<ProductGroup> saveUpdateProductGroup = new HashSet<>();
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(company.getId());
		for (ProductGroupDTO productGroupDto : productGroupDtos) {

			Optional<ProductGroup> optionalProductGroup = productGroups.stream()
					.filter(p -> p.getName().equals(productGroupDto.getName())
							|| p.getAlias().equals(productGroupDto.getGuid()))
					.findAny();

			ProductGroup productGroup;
			if (optionalProductGroup.isPresent()) {
				productGroup = optionalProductGroup.get();
			} else {
				productGroup = new ProductGroup();
				productGroup.setPid(ProductGroupService.PID_PREFIX + RandomUtil.generatePid());
				productGroup.setCompany(company);
				productGroup.setAlias(productGroupDto.getGuid());
			}
			productGroup.setTaxRate(productGroupDto.getTaxRate());
			productGroup.setName(productGroupDto.getName());
			productGroup.setActivated(true);
			saveUpdateProductGroup.add(productGroup);
			// check product group exist anymore in db
			productGroups.removeIf(p -> p.getName().equals(productGroupDto.getName()));
		}
		productGroups.removeIf(p -> p.getName().equals("GENERAL"));
		for (ProductGroup pg : productGroups) {
			pg.setActivated(false);
			saveUpdateProductGroup.add(pg);
		}
		log.info("Product Group list size : {}" + saveUpdateProductGroup.size());
		bulkOperationRepositoryCustom.bulkSaveProductGroup(saveUpdateProductGroup);
	}

	public void saveOrUpdateProductCategory(List<ProductCategoryDTO> productCategoryDtos, Company company) {
		log.info("Product Category list size : {}" + productCategoryDtos.size());
		Set<ProductCategory> saveUpdateProductCategory = new HashSet<>();
		List<ProductCategory> productCategories = productCategoryRepository.findByCompanyId(company.getId());
		for (ProductCategoryDTO productCategoryDto : productCategoryDtos) {

			Optional<ProductCategory> optionalProductCategory = productCategories.stream()
					.filter(p -> p.getName().equals(productCategoryDto.getName())
							|| p.getAlias().equals(productCategoryDto.getGuid()))
					.findAny();

			ProductCategory productCategory;
			if (optionalProductCategory.isPresent()) {
				productCategory = optionalProductCategory.get();
			} else {
				productCategory = new ProductCategory();
				productCategory.setPid(ProductCategoryService.PID_PREFIX + RandomUtil.generatePid());
				productCategory.setName(productCategoryDto.getName());
				productCategory.setCompany(company);
				productCategory.setAlias(productCategoryDto.getGuid());
			}
			productCategory.setActivated(true);
			saveUpdateProductCategory.add(productCategory);
			// check product category exist anymore in db
			productCategories.removeIf(p -> p.getName().equals(productCategoryDto.getName()));
		}
		productCategories.removeIf(p -> p.getName().equals("GENERAL"));
		for (ProductCategory pc : productCategories) {
			pc.setActivated(false);
			saveUpdateProductCategory.add(pc);
		}
		bulkOperationRepositoryCustom.bulkSaveProductCategory(saveUpdateProductCategory);
	}

	public void saveOrUpdateStockLocation(List<StockLocationDTO> stockLocationDtos, Company company) {
		log.info("Stock Location list size : {}" + stockLocationDtos.size());
		Set<StockLocation> saveUpdateStockLocation = new HashSet<>();
		List<StockLocation> stockLocations = stockLocationRepository.findAllByCompanyId(company.getId());
		for (StockLocationDTO stockLocationDTO : stockLocationDtos) {
			Optional<StockLocation> optionalStockLocation = stockLocations.stream()
					.filter(p -> p.getName().equals(stockLocationDTO.getName())
							|| p.getAlias().equals(stockLocationDTO.getGuid()))
					.findAny();
			StockLocation stockLocation;
			if (optionalStockLocation.isPresent()) {
				stockLocation = optionalStockLocation.get();
				stockLocation.setAlias(stockLocationDTO.getGuid());
			} else {
				stockLocation = new StockLocation();
				stockLocation.setPid(StockLocationService.PID_PREFIX + RandomUtil.generatePid());
				stockLocation.setName(stockLocationDTO.getName());
				stockLocation.setStockLocationType(StockLocationType.ACTUAL);
				stockLocation.setCompany(company);
				stockLocation.setAlias(stockLocationDTO.getGuid());
			}
			stockLocation.setActivated(true);
			saveUpdateStockLocation.add(stockLocation);
			// check stock location exist anymore in db
			stockLocations.removeIf(p -> p.getName().equals(stockLocationDTO.getName()));
		}
		stockLocations.removeIf(p -> p.getName().equals("Main Location"));
		for (StockLocation sl : stockLocations) {
			sl.setActivated(false);
			saveUpdateStockLocation.add(sl);
		}
		bulkOperationRepositoryCustom.bulkSaveStockLocations(saveUpdateStockLocation);
	}

	public void saveOrUpdateOpeningStock(List<OpeningStockDTO> openingStockDtos, Company company) {
		log.info("Opening Stock list size : {}" + openingStockDtos.size());
		Set<OpeningStock> saveUpdateOpeningStock = new HashSet<>();
		StockLocation defaultStockLocation = stockLocationRepository.findFirstByCompanyId(company.getId());
		Set<String> ppNames = openingStockDtos.stream().map(os -> os.getProductProfileName())
				.collect(Collectors.toSet());

		List<StockLocation> StockLocations = stockLocationService.findAllStockLocationByCompanyId(company.getId());
		List<ProductProfile> productProfiles = productProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);
		openingStockRepository.deleteByCompanyId(company.getId());

		for (OpeningStockDTO openingStockDTO : openingStockDtos) {
			Optional<ProductProfile> optionalProductProfile = productProfiles.stream()
					.filter(pp -> pp.getName().equals(openingStockDTO.getProductProfileName())).findAny();
			if (openingStockDTO.getStockSummary() == null) {
				continue;
			}
			for (StockSummaryDTO stockSummaryDTO : openingStockDTO.getStockSummary()) {

				if (optionalProductProfile.isPresent()) {
					OpeningStock openingStock = new OpeningStock();
					openingStock.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid());
					openingStock.setOpeningStockDate(LocalDateTime.now());
					openingStock.setCreatedDate(LocalDateTime.now());
					openingStock.setCompany(company);
					openingStock.setProductProfile(optionalProductProfile.get());

					if (stockSummaryDTO.getStockLocationName().equals("NULL")) {
						openingStock.setStockLocation(defaultStockLocation);
					} else {
						// stock location
						Optional<StockLocation> optionalStockLocation = StockLocations.stream()
								.filter(pl -> stockSummaryDTO.getStockLocationName().equals(pl.getName())).findAny();
						if (optionalStockLocation.isPresent()) {
							openingStock.setStockLocation(optionalStockLocation.get());
						} else {
							openingStock.setStockLocation(defaultStockLocation);
						}
					}
					openingStock.setQuantity(stockSummaryDTO.getQuantity());
					saveUpdateOpeningStock.add(openingStock);
				}
			}
		}
		bulkOperationRepositoryCustom.bulkSaveOpeningStocks(saveUpdateOpeningStock);
	}

	public void saveOrReceivablePayable(List<ReceivablePayableDTO> receivablePayableDtos, Company company) {
		Set<ReceivablePayable> saveReceivablePayable = new HashSet<>();
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_106" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by compId";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfiles = accountProfileRepository.findByCompanyId(company.getId());
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

		receivablePayableRepository.deleteByCompanyId(company.getId());
		for (ReceivablePayableDTO receivablePayableDTO : receivablePayableDtos) {
			Optional<AccountProfile> optionalAccountProfile = accountProfiles.stream()
					.filter(ap -> ap.getName().equals(receivablePayableDTO.getAccountName())).findAny();
			if (optionalAccountProfile.isPresent()) {
				ReceivablePayable receivablePayable = new ReceivablePayable();
				receivablePayable.setPid(ReceivablePayableService.PID_PREFIX + RandomUtil.generatePid());
				receivablePayable.setAccountProfile(optionalAccountProfile.get());
				receivablePayable.setCompany(company);
				receivablePayable.setReceivablePayableType(receivablePayableDTO.getReceivablePayableType());
				receivablePayable.setReferenceDocumentAmount(receivablePayableDTO.getReferenceDocumentAmount());
				receivablePayable
						.setReferenceDocumentBalanceAmount(receivablePayableDTO.getReferenceDocumentBalanceAmount());
				receivablePayable.setReferenceDocumentNumber(receivablePayableDTO.getReferenceDocumentNumber());
				receivablePayable
						.setReferenceDocumentDate(convertDate(receivablePayableDTO.getReferenceDocumentDate()));
				receivablePayable.setBillOverDue(receivablePayableDTO.getBillOverDue());
				saveReceivablePayable.add(receivablePayable);
			}
		}
		bulkOperationRepositoryCustom.bulkSaveReceivablePayables(saveReceivablePayable);
	}

	@Transactional
	public void saveOrPriceLevel(List<PriceLevelListDTO> priceLevelListDtos, Company company) {
		Set<PriceLevel> saveUpdatePriceLevels = new HashSet<>();
		List<PriceLevel> priceLevels = priceLevelRepository.findByCompanyId(company.getId());
		String removeChar = "\t";
		Set<String> plNames = priceLevelListDtos.stream()
				.filter(pl -> !pl.getPriceLevel().equals("NULL") && !pl.getPriceLevel().endsWith(removeChar))
				.map(pl -> pl.getPriceLevel()).collect(Collectors.toSet());

		for (String priceLevelName : plNames) {
			Optional<PriceLevel> optionalPl = priceLevels.stream().filter(pl -> pl.getName().equals(priceLevelName))
					.findAny();
			PriceLevel priceLevel;
			if (optionalPl.isPresent()) {
				priceLevel = optionalPl.get();
			} else {
				priceLevel = new PriceLevel();
				priceLevel.setPid(PriceLevelService.PID_PREFIX + RandomUtil.generatePid());
				priceLevel.setName(priceLevelName);
				priceLevel.setCompany(company);
			}
			priceLevel.setActivated(true);
			saveUpdatePriceLevels.add(priceLevel);
			// deactivating pricelevels when not match with tally and serverdb
			Optional<PriceLevel> toDeactivate = priceLevels.stream()
					.filter(pl -> !pl.getName().equals(priceLevelName) && !pl.getName().equals("General")).findAny();
			if (toDeactivate.isPresent()) {
				priceLevelRepository.updateActivatedByIdAndCompanyId(false, toDeactivate.get().getId(),
						company.getId());
			}
		}
		bulkOperationRepositoryCustom.bulkSaveUpdatePriceLevels(saveUpdatePriceLevels);
	}

	@Transactional
	public void saveOrPriceLevelList(List<PriceLevelListDTO> priceLevelListDtos, Company company) {
		Set<PriceLevelList> saveUpdatePriceLevelLists = new HashSet<>();
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(company.getId());
		List<PriceLevel> priceLevels = priceLevelRepository.findByCompanyId(company.getId());
		priceLevelListRepository.deleteByCompanyId(company.getId());
		String removeChar = "\t";
		for (PriceLevelListDTO priceLevelListDTO : priceLevelListDtos) {
			Optional<ProductProfile> optionalPp = productProfiles.stream()
					.filter(pp -> pp.getName().equals(priceLevelListDTO.getName())).findAny();
			Optional<PriceLevel> optionalPl = priceLevels.stream()
					.filter(pl -> pl.getName().equals(priceLevelListDTO.getPriceLevel())).findAny();

			if (optionalPp.isPresent() && optionalPl.isPresent()
					&& !priceLevelListDTO.getPriceLevel().endsWith(removeChar)) {
				PriceLevelList priceLevelList = new PriceLevelList();
				priceLevelList.setPid(PriceLevelListService.PID_PREFIX + RandomUtil.generatePid());
				priceLevelList.setCompany(company);
				priceLevelList.setProductProfile(optionalPp.get());
				priceLevelList.setPriceLevel(optionalPl.get());
				priceLevelList.setPrice(priceLevelListDTO.getRate());
				saveUpdatePriceLevelLists.add(priceLevelList);
			}
		}
		bulkOperationRepositoryCustom.bulkSaveUpdatePriceLevelLists(saveUpdatePriceLevelLists);
	}

	@Transactional
	public void saveOrUpdatePostDatedVouchers(List<PostDatedVoucherDTO> postDatedVouchers, Company company) {
		List<PostDatedVoucher> savePostDatedVouchers = new ArrayList<>();
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by compId";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyId(company.getId());
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
		for (PostDatedVoucherDTO postDatedVoucherDTO : postDatedVouchers) {
			Optional<AccountProfile> opAccountProfile = accountProfiles.stream()
					.filter(ap -> ap.getName().equals(postDatedVoucherDTO.getPartyLedgerName())).findAny();

			if (opAccountProfile.isPresent()) {
				PostDatedVoucher postDatedVoucher = new PostDatedVoucher();
				postDatedVoucher.setPid(PostDatedVoucherService.PID_PREFIX + RandomUtil.generatePid());
				postDatedVoucher.setCompany(company);
				postDatedVoucher.setAccountProfile(opAccountProfile.get());
				postDatedVoucher.setReceivableBillNumber(postDatedVoucherDTO.getBillName());
				postDatedVoucher.setReferenceDocumentNumber(postDatedVoucherDTO.getVoucherNumber());
				postDatedVoucher.setReferenceDocumentDate(convertDate(postDatedVoucherDTO.getDate()));
				postDatedVoucher.setReferenceDocumentAmount(postDatedVoucherDTO.getBillAmount());
				savePostDatedVouchers.add(postDatedVoucher);
			}
		}
		postDatedVoucherRepository.deleteByCompanyId(company.getId());
		postDatedVoucherRepository.save(savePostDatedVouchers);
	}

	@Transactional
	public void saveOrUpdateGstLedgers(List<GstLedgerDTO> gstLedgers, Company company) {

		List<GstLedger> existingGstLedgers = gstLedgerRepository.findAllByCompanyId(company.getId());
		List<GstLedger> saveGstLedgers = new ArrayList<>();

		for (GstLedgerDTO gstLedgerDTO : gstLedgers) {
			if (gstLedgerDTO.getAccountType().equals("Duties & Taxes") && gstLedgerDTO.getTaxType().equals("GST")) {
				GstLedger gstLedger;

				Optional<GstLedger> opGstLedger = existingGstLedgers.stream()
						.filter(gl -> gl.getName().equals(gstLedgerDTO.getName())
								|| gl.getGuid().equals(gstLedgerDTO.getGuid()))
						.findAny();

				if (opGstLedger.isPresent()) {
					gstLedger = opGstLedger.get();
				} else {
					gstLedger = new GstLedger();
					gstLedger.setCompany(company);
					gstLedger.setTaxType(gstLedgerDTO.getTaxType());
					gstLedger.setAccountType(GstAccountType.DUTIES_AND_TAXES);
					gstLedger.setGuid(gstLedgerDTO.getGuid());
					gstLedger.setActivated(false);
				}
				gstLedger.setName(gstLedgerDTO.getName());
				gstLedger.setTaxRate(gstLedgerDTO.getTaxRate());
				saveGstLedgers.add(gstLedger);
			}
		}
		gstLedgerRepository.save(saveGstLedgers);
	}

	private LocalDate convertDate(String date) {
		if (date.length() < 11) {
			date = "0" + date;
		}
		String[] splitDates = date.split(" ");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
		LocalDate dateTime = LocalDate.parse(splitDates[0], formatter);
		return dateTime;
	}

}
