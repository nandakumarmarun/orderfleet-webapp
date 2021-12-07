package com.orderfleet.webapp.service.impl.integration.async;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.LocationHierarchy;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.domain.enums.StockLocationType;
import com.orderfleet.webapp.domain.enums.TallyIntegrationStatusType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationHierarchyService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.PriceLevelListService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.TallyIntegrationStatusService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelListDTO;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;

/**
 * ThirdPartySaveService for save datas asynchronously from tally
 *
 * @author Sarath
 * @since Mar 3, 2017
 */
@Service
public class ThirdPartySaveService {

	private final Logger log = LoggerFactory.getLogger(ThirdPartySaveService.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private AccountTypeService accountTypeService;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private OpeningStockService openingStockService;

	@Inject
	private StockLocationService stockLocationService;

	@Inject
	private PriceLevelListService priceLevelListService;

	@Inject
	private PriceLevelService pricelevelservice;

	@Inject
	private ProductGroupProductRepository productGroupProductRepository;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private ReceivablePayableService receivablePayableService;

	@Inject
	private ReceivablePayableRepository receivablePayableRepository;

	@Inject
	private LocationService locationService;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private ProductCategoryService productCategoryService;

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private LocationHierarchyRepository locationHierarchyRepository;

	@Inject
	private LocationHierarchyService locationHierarchyService;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private DivisionRepository divisionRepository;

	@Inject
	private ProductCategoryRepository productCategoryRepository;

	@Inject
	private TallyIntegrationStatusService tallyIntegrationStatusService;

	// @Inject
	// private UserRepository userRepository;

	/**
	 * 
	 * Create or update a accountProfile.
	 * 
	 * @param accountProfileDTOs
	 * @param companyId
	 * @param userLogin
	 */

	@Async
	public void saveUpdateAccountProfiles(List<AccountProfileDTO> accountProfileDTOs, Long companyId,
			String userLogin) {
		log.debug("async start......");
		long start = System.currentTimeMillis();
		log.debug("REST request to save AccountProfile : {}", accountProfileDTOs.size());

		Company company = companyRepository.findOne(companyId);

		log.debug("REST request to save or update TallyIntegrationStatus...... company name  : "
				+ company.getLegalName());

		for (AccountProfileDTO accountProfileDTO : accountProfileDTOs) {

			if (accountProfileDTO.getName() != null) {

				if (accountProfileDTO.getDefaultPriceLevelName() != null
						&& !accountProfileDTO.getDefaultPriceLevelName().equalsIgnoreCase("")) {
					Optional<PriceLevelDTO> priceLevelDTO = pricelevelservice.findByCompanyIdName(companyId,
							accountProfileDTO.getDefaultPriceLevelName());
					if (priceLevelDTO.isPresent()) {
						accountProfileDTO.setDefaultPriceLevelPid(priceLevelDTO.get().getPid());
					} else {
						PriceLevelDTO priceLevelDTO2 = new PriceLevelDTO();
						priceLevelDTO2.setName(accountProfileDTO.getDefaultPriceLevelName());
						priceLevelDTO2.setActivated(true);
						priceLevelDTO2 = pricelevelservice.savePriceLevel(companyId, priceLevelDTO2);
						accountProfileDTO.setDefaultPriceLevelPid(priceLevelDTO2.getPid());
					}
				}
				accountProfileDTO.setAccountStatus(AccountStatus.Unverified);
				accountProfileDTO.setIsImportStatus(true);
				AccountTypeDTO accountTypeDTO = new AccountTypeDTO();
				if (accountProfileDTO.getAccountTypeName() != null
						&& !accountProfileDTO.getAccountTypeName().equalsIgnoreCase("")) {
					Optional<AccountTypeDTO> optionalAccountTypeDTO = accountTypeService
							.findByCompanyIdAndName(companyId, accountProfileDTO.getAccountTypeName());
					if (optionalAccountTypeDTO.isPresent()) {
						accountTypeDTO = optionalAccountTypeDTO.get();
					} else {
						accountTypeDTO.setName(accountProfileDTO.getAccountTypeName());
						accountTypeDTO.setActivated(true);
						accountTypeDTO = accountTypeService.saveAccountType(companyId, accountTypeDTO);
					}
				} else {
					AccountTypeDTO accountTypeDTOs = accountTypeService.findFirstByCompanyId(companyId);
					accountTypeDTO = accountTypeDTOs;
				}
				if (accountTypeDTO != null) {
					accountProfileDTO.setAccountTypePid(accountTypeDTO.getPid());
					Optional<AccountProfileDTO> accountProfile = accountProfileService.findByCompanyIdAndName(companyId,
							accountProfileDTO.getName());
					if (accountProfile.isPresent()) {
						accountProfileDTO.setPid(accountProfile.get().getPid());
						accountProfileDTO.setAccountTypePid(accountProfile.get().getAccountTypePid());
						accountProfileService.update(accountProfileDTO);
					} else {
						if (accountProfileDTO.getActivated()) {
							accountProfileService.saveAccountProfile(companyId, accountProfileDTO, userLogin);
						}
					}
				}
			}
		}
		log.debug("successfully saved account profile ........  company name  : " + company.getLegalName());
		tallyIntegrationStatusService.updateIntegratedStatus(companyId, TallyIntegrationStatusType.ACCOUNT_PROFILE,
				true);
		log.debug("successfully updated TallyIntegrationStatus......");
		log.info("Elapsed time in save: AccountProfiles : "
				+ TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start) + " minutes");
	}

	/**
	 * Create or update a locationaccountProfile.
	 * 
	 * @param accountProfileDTOs
	 * @param companyId
	 */
	@Async
	@Transactional
	public void saveAndUpdateLocationAccountProfile(List<AccountProfileDTO> accountProfileDTOs, Long companyId) {
		log.debug("async start......");
		long start = System.currentTimeMillis();
		log.debug("REST request to save location accountProfile : {}", accountProfileDTOs.size());

		Company company = companyRepository.findOne(companyId);
		log.debug("REST request to save or update TallyIntegrationStatus...... company name  : "
				+ company.getLegalName());

		// mapping the products groupname
		Map<String, List<AccountProfileDTO>> accountProfileDTOMap = accountProfileDTOs.parallelStream()
				.collect(Collectors.groupingBy(AccountProfileDTO::getDescription));

		List<LocationAccountProfile> locationAccountProfiles = new ArrayList<>();
		List<LocationAccountProfile> allLocationAccountProfile = locationAccountProfileService
				.findAllByCompanyId(companyId);
		// useed for finding not assighned from tally.............
		for (LocationAccountProfile locationAccountProfile2 : allLocationAccountProfile) {
			if (!locationAccountProfile2.getAccountProfile().getCity().equals("No City")) {
				 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description ="get one by pid";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				Optional<AccountProfile> accountProfile = accountProfileRepository
						.findOneByPid(locationAccountProfile2.getAccountProfile().getPid());

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
				Location location = locationRepository.findOneByPid(locationAccountProfile2.getLocation().getPid())
						.get();
				LocationAccountProfile profile = new LocationAccountProfile();
				profile.setAccountProfile(accountProfile.get());
				profile.setLocation(location);
				profile.setCompany(company);
				// list of values not from tally
				locationAccountProfiles.add(profile);
			}
		}

		for (Map.Entry<String, List<AccountProfileDTO>> entry : accountProfileDTOMap.entrySet()) {
			log.info("location name  :  " + entry.getKey() + " :  count  :  " + entry.getValue().size());
			Optional<LocationDTO> optionalLocationDTO = locationService.findByCompanyIdAndName(companyId,
					entry.getKey());

			if (optionalLocationDTO.isPresent()) {
				List<AccountProfile> newAccountProfiles = new ArrayList<>();
				for (AccountProfileDTO accountProfileDTO : entry.getValue()) {
					 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
						DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						String id = "AP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
						String description ="get by compId and name Ignore case";
						LocalDateTime startLCTime = LocalDateTime.now();
						String startTime = startLCTime.format(DATE_TIME_FORMAT);
						String startDate = startLCTime.format(DATE_FORMAT);
						logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
					Optional<AccountProfile> accountProfile = accountProfileRepository
							.findByCompanyIdAndNameIgnoreCase(company.getId(), accountProfileDTO.getName());
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
					if (accountProfile.isPresent()) {
						newAccountProfiles.add(accountProfile.get());
					}
				}
				if (!newAccountProfiles.isEmpty()) {
					Location location = locationRepository.findOneByPid(optionalLocationDTO.get().getPid()).get();
					for (AccountProfile accountProfile : newAccountProfiles) {
						List<LocationAccountProfile> locationAccountProfiles3 = locationAccountProfileService
								.findAllByCompanyAndAccountProfilePid(companyId, accountProfile.getPid());
						locationAccountProfileRepository.delete(locationAccountProfiles3);
						LocationAccountProfile profile = new LocationAccountProfile();
						profile.setAccountProfile(accountProfile);
						profile.setLocation(location);
						profile.setCompany(company);
						locationAccountProfileRepository.save(profile);
					}
				}
			}
		}

		log.debug("successfully saved location account profile ........  company name  : " + company.getLegalName());
		tallyIntegrationStatusService.updateIntegratedStatus(companyId,
				TallyIntegrationStatusType.LOCATION_ACCOUNT_PROFILE, true);
		log.debug("successfully updated TallyIntegrationStatus......");
		log.info("Elapsed time in save: location account profile : "
				+ TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start) + " minutes");
	}

	/**
	 * Create or update a PriceLevelList.
	 * 
	 * @param accountProfileDTOs
	 * @param companyId
	 */

	@Async
	public void saveAndUpdatePriceList(List<PriceLevelListDTO> priceLevelListDTOs, Long companyId) {
		log.debug("async start......");
		long start = System.currentTimeMillis();
		log.debug("REST request to save priceLevelListDTOs : {}", priceLevelListDTOs.size());

		Company company = companyRepository.findOne(companyId);
		log.debug("REST request to save or update TallyIntegrationStatus...... company name  : "
				+ company.getLegalName());

		for (PriceLevelListDTO priceLevelListDTO : priceLevelListDTOs) {
			Optional<PriceLevelListDTO> priceLevelListDTO2 = priceLevelListService
					.findAllByCompanyIdPriceLevelNameAndProductProfileName(companyId,
							priceLevelListDTO.getPriceLevelName(), priceLevelListDTO.getProductProfileName());
			if (priceLevelListDTO2.isPresent()) {
				priceLevelListDTO2.get().setPrice(priceLevelListDTO.getPrice());
				priceLevelListService.update(priceLevelListDTO2.get());
			} else {
				Optional<ProductProfileDTO> productProfileDto = productProfileService.findByCompanyIdAndName(companyId,
						priceLevelListDTO.getProductProfileName());
				if (productProfileDto.isPresent()) {
					Optional<PriceLevelDTO> priceLevelDTO = pricelevelservice.findByCompanyIdAndName(companyId,
							priceLevelListDTO.getPriceLevelName());
					if (priceLevelDTO.isPresent()) {
						priceLevelListDTO.setPriceLevelPid(priceLevelDTO.get().getPid());
						priceLevelListDTO.setProductProfilePid(productProfileDto.get().getPid());
						priceLevelListService.savePriceLevelList(priceLevelListDTO, companyId);
					} else {
						PriceLevelDTO priceLevelDTO2 = new PriceLevelDTO();
						priceLevelDTO2.setName(priceLevelListDTO.getPriceLevelName());
						priceLevelDTO2.setActivated(true);
						priceLevelDTO2 = pricelevelservice.savePriceLevel(companyId, priceLevelDTO2);
						priceLevelListDTO.setPriceLevelPid(priceLevelDTO2.getPid());
						priceLevelListDTO.setProductProfilePid(productProfileDto.get().getPid());
						priceLevelListService.savePriceLevelList(priceLevelListDTO, companyId);
					}
				}
			}
		}

		log.debug("successfully saved pricelevel list ........  company name  : " + company.getLegalName());
		tallyIntegrationStatusService.updateIntegratedStatus(companyId, TallyIntegrationStatusType.PRICE_LEVEL_LIST,
				true);
		log.debug("successfully updated TallyIntegrationStatus......");
		log.info("Elapsed time in save: pricelevel list  : "
				+ TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start) + " minutes");
	}

	/**
	 * Create or update a productProfile.
	 * 
	 * @param companyId
	 * 
	 * @param accountProfileDTOs
	 * @param companyId
	 */
	@Async
	@Transactional
	public void saveAndUpdateProductProfileList(List<ProductProfileDTO> productProfileDTOs, Long companyId) {
		log.debug("async start......");
		long start = System.currentTimeMillis();
		log.debug("REST request to save productProfileDTOs : {}", productProfileDTOs.size());

		Company company = companyRepository.findOne(companyId);
		log.debug("REST request to save or update TallyIntegrationStatus...... company name  : "
				+ company.getLegalName());

		// All product must have a division, set a default one
		Division division = divisionRepository.findFirstByCompanyId(companyId);
		log.debug("Request to save productProfiles processing.....  " + productProfileDTOs.size());
		// productProfileDTOs.parallelStream().forEach(ppdto ->
		for (ProductProfileDTO ppdto : productProfileDTOs) {
			final ProductProfile productProfile;
			Optional<ProductProfile> optionalProduct = productProfileRepository
					.findByCompanyIdAndNameIgnoreCase(companyId, ppdto.getName());
			if (optionalProduct.isPresent()) {
				productProfile = optionalProduct.get();
				// not varna
				if (company.getLegalName().equalsIgnoreCase("Varna")) {
					ppdto.setPrice(productProfile.getPrice());
					ppdto.setDescription(productProfile.getDescription());
					// ppdto.setTaxRate(productProfile.getTaxRate());
				}
			} else {
				productProfile = new ProductProfile();
				productProfile.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
				productProfile.setSize(ppdto.getSize());
				productProfile.setCompany(company);
				productProfile.setColorImage(ppdto.getColorImage());
				productProfile.setColorImageContentType(ppdto.getColorImageContentType());
				productProfile.setTaxRate(ppdto.getTaxRate());
			}
			productProfile.setName(ppdto.getName());
			productProfile.setAlias(ppdto.getAlias());
			productProfile.setDescription(ppdto.getDescription());
			productProfile.setPrice(ppdto.getPrice());
			productProfile.setMrp(ppdto.getMrp());
			productProfile.setSku(ppdto.getSku());
			productProfile.setActivated(ppdto.getActivated());
			if (ppdto.getUnitQty() != null) {
				productProfile.setUnitQty(ppdto.getUnitQty());
			}
			if (productProfile.getDivision() == null) {
				productProfile.setDivision(division);
			}
			// set category
			Optional<ProductCategory> optionalCategory = productCategoryRepository
					.findByCompanyIdAndNameIgnoreCase(companyId, ppdto.getProductCategoryName());
			if (optionalCategory.isPresent()) {
				productProfile.setProductCategory(optionalCategory.get());
			} else {
				if (productProfile.getProductCategory() == null) {
					productProfile.setProductCategory(productCategoryRepository
							.findByCompanyIdAndNameIgnoreCase(companyId, "Not Applicable").get());
				}
			}
			// save/update
			log.debug("Request to save productProfile : {}", productProfile.getName());
			productProfileRepository.save(productProfile);
			log.debug("productProfile saved successfully: {}", productProfile.getName());
		}

		log.debug("successfully saved productProfile list ........  company name  : " + company.getLegalName());
		tallyIntegrationStatusService.updateIntegratedStatus(companyId, TallyIntegrationStatusType.PRODUCT_PROFILE,
				true);
		log.debug("successfully updated TallyIntegrationStatus......");
		log.info("Elapsed time in save: productProfile list  : "
				+ TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start) + " minutes");
		log.debug("Request to save productProfiles successfully finished....  " + productProfileDTOs.size());

	}

	/**
	 * Create or update a productGroupProductProfile.
	 * 
	 * @param companyId
	 * 
	 * @param accountProfileDTOs
	 * @param companyId
	 */

	@Async
	public void saveAndUpdateProductGroupProductProfileList(List<ProductProfileDTO> productProfileDTOs,
			Long companyId) {
		log.debug("async start......");
		long start = System.currentTimeMillis();
		log.debug("REST request to save productGroup ProductProfile : {}", productProfileDTOs.size());

		Company company = companyRepository.findOne(companyId);
		log.debug("REST request to save or update TallyIntegrationStatus...... company name  : "
				+ company.getLegalName());

		for (ProductProfileDTO productProfileDTO : productProfileDTOs) {
			Optional<ProductProfile> ppdto = productProfileRepository.findByCompanyIdAndNameIgnoreCase(companyId,
					productProfileDTO.getName());
			if (ppdto.isPresent()) {
				// set group, it is in description.
				productGroupRepository.findByCompanyIdAndNameIgnoreCase(companyId, ppdto.get().getDescription())
						.ifPresent(pg -> {
							ProductGroupProduct productGroupProduct = new ProductGroupProduct();
							productGroupProduct.setProduct(ppdto.get());
							productGroupProduct.setProductGroup(pg);
							productGroupProduct.setCompany(ppdto.get().getCompany());
							productGroupProductRepository.save(productGroupProduct);
						});
			}
		}

		log.debug("successfully saved productGroup ProductProfile list ........  company name  : "
				+ company.getLegalName());
		tallyIntegrationStatusService.updateIntegratedStatus(companyId,
				TallyIntegrationStatusType.PRODUCT_GROUP_PRODUCT_PROFILE, true);
		log.debug("successfully updated TallyIntegrationStatus......");
		log.info("Elapsed time in save: productGroup ProductProfile list  : "
				+ TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start) + " minutes");
		log.debug(
				"Request to save productGroup ProductProfile successfully finished....  " + productProfileDTOs.size());
	}

	/**
	 * Create or update a openingStock.
	 * 
	 * @param companyId
	 * 
	 * @param accountProfileDTOs
	 * @param companyId
	 */

	@Async
	public void saveOpeningStocks(List<OpeningStockDTO> openingStockDTOs, Long companyId) {
		log.debug("async start......");
		long start = System.currentTimeMillis();
		log.debug("REST request to save OpeningStocks : {}", openingStockDTOs.size());

		Company company = companyRepository.findOne(companyId);
		log.debug("REST request to save or update TallyIntegrationStatus...... company name  : "
				+ company.getLegalName());

		// if not already exist, save openingStocks
		StockLocationDTO stockLocation = stockLocationService.findFirstByCompanyId(companyId);

		openingStockRepository.deleteByCompanyId(companyId);

		for (OpeningStockDTO openingStockDTO : openingStockDTOs) {
			Optional<ProductProfileDTO> productProfile = productProfileService.findByCompanyIdAndName(companyId,
					openingStockDTO.getProductProfileName());
			if (productProfile.isPresent()) {
				openingStockDTO.setProductProfilePid(productProfile.get().getPid());
				if (openingStockDTO.getStockLocationName() != null
						&& !openingStockDTO.getStockLocationName().equalsIgnoreCase("")) {

					Optional<StockLocationDTO> stockLocationOptional = stockLocationService
							.findByCompanyIdAndName(companyId, openingStockDTO.getStockLocationName());
					if (stockLocationOptional.isPresent()) {
						openingStockDTO.setStockLocationPid(stockLocationOptional.get().getPid());
					} else {
						StockLocationDTO stockLocationDTO = new StockLocationDTO();
						stockLocationDTO.setName(openingStockDTO.getStockLocationName());
						stockLocationDTO.setStockLocationType(StockLocationType.LOGICAL);
						stockLocationDTO.setActivated(true);
						stockLocationDTO = stockLocationService.save(stockLocationDTO, company);
						openingStockDTO.setStockLocationPid(stockLocationDTO.getPid());
					}
				} else {
					openingStockDTO.setStockLocationPid(stockLocation.getPid());
				}
				openingStockDTO.setActivated(true);
				openingStockService.saveOpeningStock(openingStockDTO, companyId);
			}
		}

		log.debug("successfully saved OpeningStock list ........  company name  : " + company.getLegalName());
		tallyIntegrationStatusService.updateIntegratedStatus(companyId, TallyIntegrationStatusType.OPENING_STOCK, true);
		log.debug("successfully updated TallyIntegrationStatus......");
		log.info("Elapsed time in save: OpeningStock list  : "
				+ TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start) + " minutes");
		log.debug("Request to save OpeningStock successfully finished....  " + openingStockDTOs.size());
	}

	/**
	 * Create or update a receivablePayable.
	 * 
	 * @param companyId
	 * 
	 * @param receivablePayables
	 * @param companyId
	 */

	@Async
	public void saveReceivablePayables(List<ReceivablePayableDTO> receivablePayableDTOs, Long companyId) {
		log.debug("async start......");
		long start = System.currentTimeMillis();
		log.debug("REST request to save receivablePayableDTOs : {}", receivablePayableDTOs.size());

		Company company = companyRepository.findOne(companyId);
		log.debug("REST request to save or update TallyIntegrationStatus...... company name  : "
				+ company.getLegalName());

		List<ReceivablePayableDTO> receivablePayable = receivablePayableService.findAllByCompanyId(companyId);
		if (!receivablePayable.isEmpty()) {
			receivablePayableRepository.deleteByCompanyId(companyId);
		}
		for (ReceivablePayableDTO receivablePayableDTO : receivablePayableDTOs) {
			if (receivablePayableDTO.getAccountName() != null) {
				receivablePayableDTO.setReceivablePayableType(ReceivablePayableType.Receivable);
				receivablePayableService.saveReceivablePayable(receivablePayableDTO, companyId);
			}
		}

		log.debug("successfully saved receivablePayable list ........  company name  : " + company.getLegalName());
		tallyIntegrationStatusService.updateIntegratedStatus(companyId, TallyIntegrationStatusType.RECEIVABLE_PAYABLE,
				true);
		log.debug("successfully updated TallyIntegrationStatus......");
		log.info("Elapsed time in save: receivablePayable list  : "
				+ TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start) + " minutes");
		log.debug("Request to save receivablePayable successfully finished....  " + receivablePayableDTOs.size());

	}

	/**
	 * Create or update accountTypes.
	 * 
	 * @param companyId
	 * 
	 * @param receivablePayables
	 * @param companyId
	 */

	@Async
	public void saveUpdateAccountTypes(List<AccountTypeDTO> accountTypeDTOs, Long companyId) {
		log.debug("async start......");
		long start = System.currentTimeMillis();
		log.debug("REST request to save accountTypeDTOs : {}", accountTypeDTOs.size());

		Company company = companyRepository.findOne(companyId);
		log.debug("REST request to save or update TallyIntegrationStatus...... company name  : "
				+ company.getLegalName());

		// if not already exist, save account types
		for (AccountTypeDTO accountTypeDTO : accountTypeDTOs) {
			Optional<AccountTypeDTO> accountType = accountTypeService.findByCompanyIdAndName(companyId,
					accountTypeDTO.getName());
			if (accountType.isPresent()) {
				accountTypeDTO.setPid(accountType.get().getPid());
				accountTypeService.update(accountTypeDTO);
			} else {
				accountTypeDTO.setActivated(true);
				accountTypeService.saveAccountType(companyId, accountTypeDTO);
			}
		}

		log.debug("successfully saved accountType list ........  company name  : " + company.getLegalName());
		tallyIntegrationStatusService.updateIntegratedStatus(companyId, TallyIntegrationStatusType.ACCOUNT_TYPE, true);
		log.debug("successfully updated TallyIntegrationStatus......");
		log.info("Elapsed time in new: accountType : "
				+ TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start) + " minutes");
		log.debug("Request to save accountType successfully finished....  " + accountTypeDTOs.size());
	}

	/**
	 * Create or update ProductCategories.
	 * 
	 * @param ProductCategories
	 * @param companyId
	 */

	@Async
	public void saveUpdateProductCategories(List<ProductCategoryDTO> productCategoryDTOs, Long companyId) {
		log.debug("async start......");
		long start = System.currentTimeMillis();
		log.debug("REST request to save ProductCategories : {}", productCategoryDTOs.size());

		Company company = companyRepository.findOne(companyId);
		log.debug("REST request to save or update TallyIntegrationStatus...... company name  : "
				+ company.getLegalName());

		// if not already exist, save productCategories
		for (ProductCategoryDTO productCategoryDTO : productCategoryDTOs) {
			Optional<ProductCategoryDTO> productCategory = productCategoryService.findByNameAndCompanyId(companyId,
					productCategoryDTO.getName());
			if (productCategory.isPresent()) {
				productCategoryDTO.setPid(productCategory.get().getPid());
				productCategoryService.update(productCategoryDTO);
			} else {
				productCategoryDTO.setActivated(true);
				productCategoryService.saveProductCategory(companyId, productCategoryDTO);
			}
		}

		log.debug("successfully saved productCategory list ........  company name  : " + company.getLegalName());
		tallyIntegrationStatusService.updateIntegratedStatus(companyId, TallyIntegrationStatusType.PRODUCT_CATEGORIES,
				true);
		log.debug("successfully updated TallyIntegrationStatus......");
		log.info("Elapsed time in new: productCategory : "
				+ TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start) + " minutes");
		log.debug("Request to save productCategory successfully finished....  " + productCategoryDTOs.size());
	}

	/**
	 * Create or update ProductGroups.
	 * 
	 * @param ProductGroups
	 * @param companyId
	 */

	@Async
	public void saveUpdateProductGroups(List<ProductGroupDTO> productGroupDTOs, Long companyId) {
		log.debug("async start......");
		long start = System.currentTimeMillis();
		log.debug("REST request to save productGroupDTOs : {}", productGroupDTOs.size());

		Company company = companyRepository.findOne(companyId);
		log.debug("REST request to save or update TallyIntegrationStatus...... company name  : "
				+ company.getLegalName());

		// if not already exist, save productGroups
		for (ProductGroupDTO productGroupDTO : productGroupDTOs) {
			Optional<ProductGroupDTO> productGroup = productGroupService.findByCompanyIdAndName(companyId,
					productGroupDTO.getName());
			if (productGroup.isPresent()) {
				log.debug("Product Group Name Alredy Exist: " + productGroupDTO.getName());
				log.debug("Product Group Update " + productGroupDTO.getName());
				productGroupDTO.setPid(productGroup.get().getPid());
				productGroupService.update(productGroupDTO);
			} else {
				productGroupDTO.setActivated(true);
				productGroupService.saveProductGroup(companyId, productGroupDTO);
			}
		}

		log.debug("successfully saved productGroup list ........  company name  : " + company.getLegalName());
		tallyIntegrationStatusService.updateIntegratedStatus(companyId, TallyIntegrationStatusType.PRODUCT_GROUP, true);
		log.debug("successfully updated TallyIntegrationStatus......");
		log.info("Elapsed time in new: productGroup : "
				+ TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start) + " minutes");
		log.debug("Request to save productGroup successfully finished....  " + productGroupDTOs.size());
	}

	/**
	 * Create or update ProductGroups.
	 * 
	 * @param ProductGroups
	 * @param companyId
	 */

	@Async
	@Transactional
	public void saveUpdateLocations(List<LocationDTO> locationDTOs, Long companyId) {
		log.debug("async start......");
		long start = System.currentTimeMillis();
		log.debug("REST request to save locationDTOs : {}", locationDTOs.size());

		Company company = companyRepository.findOne(companyId);
		log.debug("REST request to save or update TallyIntegrationStatus...... company name  : "
				+ company.getLegalName());

		// if not already exist, save productCategories
		for (LocationDTO locationDTO : locationDTOs) {
			Optional<LocationDTO> location = locationService.findByCompanyIdAndName(companyId, locationDTO.getName());
			if (location.isPresent()) {
				locationDTO.setPid(location.get().getPid());
				locationService.update(locationDTO);
			} else {
				locationDTO.setActivated(true);
				locationDTO = locationService.saveLocation(companyId, locationDTO);
			}
		}
		log.debug("create create Location Heirarchies ");
		createLocationHeirarchies(companyId);

		log.debug("successfully saved location list ........  company name  : " + company.getLegalName());
		tallyIntegrationStatusService.updateIntegratedStatus(companyId, TallyIntegrationStatusType.LOCATION, true);
		log.debug("successfully updated TallyIntegrationStatus......");
		log.info("Elapsed time in new: location : "
				+ TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start) + " minutes");
		log.debug("Request to save location successfully finished....  " + locationDTOs.size());
	}

	private void createLocationHeirarchies(Long companyId) {
		// in-activate if hierarchy already exist
		Long version;
		// Only one version of a company hierarchy is active at a time
		Optional<LocationHierarchy> locationHierarchy = locationHierarchyRepository
				.findFirstByCompanyIdAndActivatedTrueOrderByIdDesc(companyId);
		if (locationHierarchy.isPresent()) {
			locationHierarchyRepository.updateLocationHierarchyInactivatedFor(ZonedDateTime.now(),
					locationHierarchy.get().getVersion(), companyId);
			version = locationHierarchy.get().getVersion() + 1;
		} else {
			version = 1L;
		}

		List<Location> locations = locationRepository.findByCompanyIdAndActivatedTrue(companyId);
		if (locations != null && locations.size() > 0) {
			// save root location
			locations.parallelStream().filter(loc -> loc.getName().equalsIgnoreCase("Territory")).findAny()
					.ifPresent(loc -> {
						locationHierarchyService.saveRootLocationWithCompanyId(loc.getId(), companyId);
					});
			for (Location location : locations) {
				Optional<Location> parent = locations.parallelStream()
						.filter(loc -> location.getDescription().equals(loc.getName())).findAny();
				if (parent.isPresent()) {
					locationHierarchyRepository.insertLocationHierarchyWithParent(version, location.getId(),
							parent.get().getId(), companyId);
				}
			}
		}
	}
}
