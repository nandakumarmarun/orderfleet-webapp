package com.orderfleet.webapp.service.async;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.TallyMasters;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.EcomProductProfileProductRepository;
import com.orderfleet.webapp.repository.EcomProductProfileRepository;
import com.orderfleet.webapp.repository.GstLedgerRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.PriceLevelAccountProductGroupRepository;
import com.orderfleet.webapp.repository.PriceLevelListRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupEcomProductsRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.TaxMasterRepository;
import com.orderfleet.webapp.repository.TemporaryOpeningStockRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AlterIdMasterService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationHierarchyService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.ecom.mapper.EcomProductProfileMapper;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AlterIdMasterDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.TaxMasterMapper;


@Service
public class TPalterIdservicesManagementService {
	private final Logger log = LoggerFactory.getLogger(TPAccountProfileManagementService.class);
	
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	
	private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;
	
	private final ProductCategoryRepository productCategoryRepository;
	
	private final SyncOperationRepository syncOperationRepository;
	
	private final ProductGroupRepository productGroupRepository;
	
	private final DivisionRepository divisionRepository;

	private final ProductProfileRepository productProfileRepository;

	private final ProductGroupProductRepository productGroupProductRepository;

	private final PriceLevelRepository priceLevelRepository;

	private final AccountProfileRepository accountProfileRepository;

	private final AccountProfileService accountProfileService;

	private final AccountTypeRepository accountTypeRepository;

	private final LocationRepository locationRepository;

	private final LocationHierarchyRepository locationHierarchyRepository;

	private final LocationAccountProfileRepository locationAccountProfileRepository;

	private final LocationAccountProfileService locationAccountProfileService;

	private final LocationHierarchyService locationHierarchyService;

	private final UserRepository userRepository;
	
	private final LocationService locationService;

	private final CompanyConfigurationRepository companyConfigurationRepository;
	
	boolean flag= false;
	
	boolean flagac= false;
	
	@Autowired
	private AlterIdMasterService alterIdMasterService;

	public TPalterIdservicesManagementService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
			ProductCategoryRepository productCategoryRepository, SyncOperationRepository syncOperationRepository,
			ProductGroupRepository productGroupRepository, DivisionRepository divisionRepository,
			ProductProfileRepository productProfileRepository,
			ProductGroupProductRepository productGroupProductRepository, PriceLevelRepository priceLevelRepository,
			AccountProfileRepository accountProfileRepository, AccountProfileService accountProfileService,
			AccountTypeRepository accountTypeRepository, LocationRepository locationRepository,
			LocationHierarchyRepository locationHierarchyRepository,
			LocationAccountProfileRepository locationAccountProfileRepository,
			LocationAccountProfileService locationAccountProfileService,
			LocationHierarchyService locationHierarchyService, UserRepository userRepository,
			LocationService locationService, CompanyConfigurationRepository companyConfigurationRepository,
			AlterIdMasterService alterIdMasterService) {
		super();
		this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
		this.productCategoryRepository = productCategoryRepository;
		this.syncOperationRepository = syncOperationRepository;
		this.productGroupRepository = productGroupRepository;
		this.divisionRepository = divisionRepository;
		this.productProfileRepository = productProfileRepository;
		this.productGroupProductRepository = productGroupProductRepository;
		this.priceLevelRepository = priceLevelRepository;
		this.accountProfileRepository = accountProfileRepository;
		this.accountProfileService = accountProfileService;
		this.accountTypeRepository = accountTypeRepository;
		this.locationRepository = locationRepository;
		this.locationHierarchyRepository = locationHierarchyRepository;
		this.locationAccountProfileRepository = locationAccountProfileRepository;
		this.locationAccountProfileService = locationAccountProfileService;
		this.locationHierarchyService = locationHierarchyService;
		this.userRepository = userRepository;
		this.locationService = locationService;
		this.companyConfigurationRepository = companyConfigurationRepository;
		this.alterIdMasterService = alterIdMasterService;
	}

	@Transactional
	public void saveUpdateProductCategories(final List<ProductCategoryDTO> productCategoryDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<ProductCategory> saveUpdateProductCategories = new HashSet<>();
		// find all product category
		List<ProductCategory> productCategories = productCategoryRepository.findByCompanyId(company.getId());
		for (ProductCategoryDTO pcDto : productCategoryDTOs) {
			// check exist by name, only one exist with a name
			Optional<ProductCategory> optionalPC = productCategories.stream()
					.filter(pc -> pc.getName().equals(pcDto.getName())).findAny();
			ProductCategory productCategory;
			if (optionalPC.isPresent()) {
				productCategory = optionalPC.get();
				// if not update, skip this iteration.
				if (!productCategory.getThirdpartyUpdate()) {
					continue;
				}
			} else {
				productCategory = new ProductCategory();
				productCategory.setPid(ProductCategoryService.PID_PREFIX + RandomUtil.generatePid());
				productCategory.setName(pcDto.getName());
				productCategory.setDataSourceType(DataSourceType.TALLY);
				productCategory.setCompany(company);
			}
			productCategory.setProductCategoryId(pcDto.getProductCategoryId());
			productCategory.setAlias(pcDto.getAlias());
			productCategory.setDescription(pcDto.getDescription());
			productCategory.setActivated(pcDto.getActivated());
			saveUpdateProductCategories.add(productCategory);
		}
		bulkOperationRepositoryCustom.bulkSaveProductCategory(saveUpdateProductCategories);

		Long Listcount = productCategoryDTOs.parallelStream().filter(productCategoryDTO -> productCategoryDTO.getAlterId() == null).count();
	    System.out.println("productCategoryDTOsnullcount"+Listcount);
		
	    if(Listcount == 0) {
	         
			ProductCategoryDTO productCategoryDTO = productCategoryDTOs.stream()
					.max(Comparator.comparingLong(ProductCategoryDTO::getAlterId)).get();
			AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
			alterIdMasterDTO.setAlterId(productCategoryDTO.getAlterId());
			alterIdMasterDTO.setMasterName(TallyMasters.PRODUCT_CATEGORY.toString());
			alterIdMasterDTO.setCompanyId(company.getId());
			if(productCategoryDTO.getAlterId() != 0) {
				alterIdMasterService.save(alterIdMasterDTO);
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
	public void saveUpdateProductCategoriesUpdateIdNew(final List<ProductCategoryDTO> productCategoryDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<ProductCategory> saveUpdateProductCategories = new HashSet<>();
		// find all product category
		List<ProductCategory> productCategories = productCategoryRepository.findAllByCompanyId();
		for (ProductCategoryDTO pcDto : productCategoryDTOs) {
			Optional<ProductCategory> optionalPC = null;
			// check exist by name, only one exist with a name
			
				optionalPC = productCategories.stream()
						.filter(p -> p.getProductCategoryId() != null && !p.getProductCategoryId().equals("")
						? p.getProductCategoryId().equals(pcDto.getProductCategoryId())
						: false)
				.findAny();
				
				System.out.println("ispresent"+optionalPC.isPresent());
			ProductCategory productCategory = new ProductCategory();
			if (optionalPC.isPresent()) {
				productCategory = optionalPC.get();
				// if not update, skip this iteration.
				if (!productCategory.getThirdpartyUpdate()) {
					continue;
				}
			} else {
				Optional<ProductCategory> optionalPCName = productCategories.stream()
						.filter(p -> p.getName() != null && !p.getName().equals("")
						? p.getName().equals(pcDto.getName())
						: false)
				.findAny();
				if(!optionalPCName.isPresent()) {
					productCategory.setPid(ProductCategoryService.PID_PREFIX + RandomUtil.generatePid());
					productCategory.setProductCategoryId(pcDto.getProductCategoryId());
					productCategory.setDataSourceType(DataSourceType.TALLY);
					productCategory.setDescription(pcDto.getName());
					productCategory.setCompany(company);
				}else {
					productCategory = optionalPCName.get();
					productCategory.setProductCategoryId(pcDto.getProductCategoryId());
				}
			}
//			productCategory.setName(pcDto.getName() + "~" +pcDto.getProductCategoryId());
			productCategory.setName(pcDto.getName());
			productCategory.setAlias(pcDto.getAlias());
			productCategory.setDescription(pcDto.getName());
			productCategory.setActivated(pcDto.getActivated());
			Optional<ProductCategory> opAccP = saveUpdateProductCategories.stream()
					.filter(so -> so.getName().equalsIgnoreCase(pcDto.getName())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}
			saveUpdateProductCategories.add(productCategory);
		}
		bulkOperationRepositoryCustom.bulkSaveProductCategory(saveUpdateProductCategories);
		
		Long Listcount = productCategoryDTOs.parallelStream().filter(productCategoryDTO -> productCategoryDTO.getAlterId() == null).count();
	    System.out.println("productCategoryDTOsnullcount"+Listcount);
		
	    if(Listcount == 0) {
	         
			ProductCategoryDTO productCategoryDTO = productCategoryDTOs.stream()
					.max(Comparator.comparingLong(ProductCategoryDTO::getAlterId)).get();
			AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
			alterIdMasterDTO.setAlterId(productCategoryDTO.getAlterId());
			alterIdMasterDTO.setMasterName(TallyMasters.PRODUCT_CATEGORY.toString());
			alterIdMasterDTO.setCompanyId(company.getId());
			if(productCategoryDTO.getAlterId() != 0) {
				alterIdMasterService.save(alterIdMasterDTO);
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
	public void saveUpdateProductGroups(final List<ProductGroupDTO> productGroupDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();

		Set<ProductGroup> saveUpdateProductGroups = new HashSet<>();
		// find all product group
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(company.getId());
		for (ProductGroupDTO pgDto : productGroupDTOs) {
			// check exist by name, only one exist with a name
			Optional<ProductGroup> optionalPG = productGroups.stream().filter(p -> p.getName().equals(pgDto.getName()))
					.findAny();
			ProductGroup productGroup;
			if (optionalPG.isPresent()) {
				productGroup = optionalPG.get();
				// if not update, skip this iteration.
				if (!productGroup.getThirdpartyUpdate()) {
					continue;
				}
			} else {
				productGroup = new ProductGroup();
				productGroup.setPid(ProductGroupService.PID_PREFIX + RandomUtil.generatePid());
				productGroup.setName(pgDto.getName());
				productGroup.setDataSourceType(DataSourceType.TALLY);
				productGroup.setCompany(company);
			}
			productGroup.setAlias(pgDto.getAlias());
			productGroup.setProductGroupId(pgDto.getProductGroupId());
			productGroup.setDescription(pgDto.getDescription());
			productGroup.setActivated(pgDto.getActivated());
			saveUpdateProductGroups.add(productGroup);
		}
		bulkOperationRepositoryCustom.bulkSaveProductGroup(saveUpdateProductGroups);
        
		Long Listcount = productGroupDTOs.parallelStream().filter(productGroupDTO -> productGroupDTO.getAlterId() == null).count();
	    System.out.println("productGroupDTOsnullcount"+Listcount);
		if(Listcount == 0) {
			ProductGroupDTO productGroupDTO = productGroupDTOs.stream()
					.max(Comparator.comparingLong(ProductGroupDTO::getAlterId)).get();
			AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
			alterIdMasterDTO.setAlterId(productGroupDTO.getAlterId());
			alterIdMasterDTO.setMasterName(TallyMasters.PRODUCT_GROUP.toString());
			alterIdMasterDTO.setCompanyId(company.getId());
			alterIdMasterService.save(alterIdMasterDTO);
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
	public void saveUpdateProductGroupsUpdateIdNew(final List<ProductGroupDTO> productGroupDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<ProductGroup> saveUpdateProductGroups = new HashSet<>();

		// find all product group
		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyId();

		for (ProductGroupDTO pgDto : productGroupDTOs) {
			Optional<ProductGroup> optionalPG = null;
				optionalPG = productGroups.stream()
						.filter(p -> p.getProductGroupId() != null && !p.getProductGroupId().equals("")
						? p.getProductGroupId().equals(pgDto.getProductGroupId())
						: false)
				.findAny();
			
				
			// check exist by name, only one exist with a name

			ProductGroup productGroup =new ProductGroup();
			if (optionalPG.isPresent()) {
				productGroup = optionalPG.get();

				// if not update, skip this iteration.
				if (!productGroup.getThirdpartyUpdate()) {
					continue;
				}
			} else {
				Optional<ProductGroup> optionalPGname = productGroups.stream()
						.filter(p -> p.getName() != null && !p.getName().equals("")
						? p.getName().equals(pgDto.getName())
						: false)
				.findAny();
				if(!optionalPGname.isPresent()) {
					productGroup.setPid(ProductGroupService.PID_PREFIX + RandomUtil.generatePid());
					productGroup.setProductGroupId(pgDto.getProductGroupId());
					productGroup.setDataSourceType(DataSourceType.TALLY);
					productGroup.setDescription(pgDto.getName());
					productGroup.setCompany(company);
				}else {
					productGroup= optionalPGname.get();
					productGroup.setProductGroupId(pgDto.getProductGroupId());
				}				
			}
			productGroup.setAlias(pgDto.getAlias());
//			productGroup.setName(pgDto.getName() + "~" +pgDto.getProductGroupId());\
			productGroup.setName(pgDto.getName());
			productGroup.setDescription(pgDto.getName());
			productGroup.setActivated(pgDto.getActivated());
			Optional<ProductGroup> opAccP = saveUpdateProductGroups.stream()
					.filter(so -> so.getName().equalsIgnoreCase(pgDto.getName())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}
			saveUpdateProductGroups.add(productGroup);
		}

		bulkOperationRepositoryCustom.bulkSaveProductGroup(saveUpdateProductGroups);
		Long Listcount = productGroupDTOs.parallelStream().filter(productGroupDTO -> productGroupDTO.getAlterId() == null).count();
	    System.out.println("productGroupDTOsnullcount"+Listcount);
		if(Listcount == 0) {
			ProductGroupDTO productGroupDTO = productGroupDTOs.stream()
					.max(Comparator.comparingLong(ProductGroupDTO::getAlterId)).get();
			AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
			alterIdMasterDTO.setAlterId(productGroupDTO.getAlterId());
			alterIdMasterDTO.setMasterName(TallyMasters.PRODUCT_GROUP.toString());
			alterIdMasterDTO.setCompanyId(company.getId());
			alterIdMasterService.save(alterIdMasterDTO);
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
	public void saveUpdateProductProfiles(final List<ProductProfileDTO> productProfileDTOs,
			final SyncOperation syncOperation) {
		log.info("Saving Product Profiles:----" + productProfileDTOs.size());
		productProfileDTOs.forEach(data -> System.out
				.println("name =====" + data.getName() + "alter id======" + data.getAlterId() + "createdDate ===== "
						+ data.getCreatedDate() + "LastmodifiedDate====" + data.getLastModifiedDate()));
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		System.out.println("productProfileDTOsaompanyid" + company.getId());
		Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();

		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.ALIAS_TO_NAME);

		if (optAliasToName.isPresent() && optAliasToName.get().getValue().equalsIgnoreCase("true")) {
			for (ProductProfileDTO ppDto : productProfileDTOs) {
				String name = ppDto.getName();
				ppDto.setName(ppDto.getAlias() != null && !ppDto.getAlias().equals("") ? ppDto.getAlias() : name);
				ppDto.setAlias(name);
			}
		}

		// find all exist product profiles
		Set<String> ppNames = productProfileDTOs.stream().map(p -> p.getName()).collect(Collectors.toSet());
		List<ProductProfile> productProfiles = productProfileRepository.findByCompanyIdAndNameIn(company.getId(),
				ppNames);

		List<ProductCategory> productCategorys = productCategoryRepository.findByCompanyId(company.getId());

		List<ProductGroupProduct> productGroupProducts = new ArrayList<>();

		Optional<CompanyConfiguration> optProductGroupTax = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.PRODUCT_GROUP_TAX);

		if (optProductGroupTax.isPresent() && optProductGroupTax.get().getValue().equalsIgnoreCase("true")) {

			productGroupProducts = productGroupProductRepository.findByProductGroupProductActivatedAndCompanyId();

		}

		// All product must have a division/category, if not, set a default one
		Division defaultDivision = divisionRepository.findFirstByCompanyId(company.getId());
		Optional<ProductCategory> defaultCategory = productCategoryRepository
				.findByCompanyIdAndNameIgnoreCase(company.getId(), "Not Applicable");
		ProductCategory productCategory = new ProductCategory();
		if (!defaultCategory.isPresent()) {
			productCategory = new ProductCategory();
			productCategory.setPid(ProductCategoryService.PID_PREFIX + RandomUtil.generatePid());
			productCategory.setName("Not Applicable");
			productCategory.setDataSourceType(DataSourceType.TALLY);
			productCategory.setCompany(company);
			productCategory = productCategoryRepository.save(productCategory);
		} else {
			productCategory = defaultCategory.get();
		}

		Optional<CompanyConfiguration> optAddCompoundUnit = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.ADD_COMPOUND_UNIT);

		for (ProductProfileDTO ppDto : productProfileDTOs) {
			// check exist by name, only one exist with a name
			Optional<ProductProfile> optionalPP = productProfiles.stream()
					.filter(p -> p.getName().equals(ppDto.getName())).findAny();
			ProductProfile productProfile;
			if (optionalPP.isPresent()) {
				productProfile = optionalPP.get();
				// if not update, skip this iteration.
				if (!productProfile.getThirdpartyUpdate()) {
					continue;
				}
			} else {
				productProfile = new ProductProfile();
				productProfile.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
				productProfile.setCompany(company);
				productProfile.setDivision(defaultDivision);
				productProfile.setDataSourceType(DataSourceType.TALLY);
			}
			productProfile.setName(ppDto.getName());
			productProfile.setAlias(ppDto.getAlias());
			productProfile.setProductId(ppDto.getProductId());
			productProfile.setDescription(ppDto.getDescription());
			productProfile.setMrp(ppDto.getMrp());
			productProfile.setTaxRate(ppDto.getTaxRate());
			productProfile.setSku(ppDto.getSku());
			productProfile.setActivated(ppDto.getActivated());
			productProfile.setTrimChar(ppDto.getTrimChar());
			productProfile.setSize(ppDto.getSize());
			productProfile.setHsnCode(ppDto.getHsnCode());
			productProfile.setProductDescription(ppDto.getProductDescription());
			productProfile.setBarcode(ppDto.getBarcode());
			productProfile.setRemarks(ppDto.getRemarks());

			if (optProductGroupTax.isPresent() && optProductGroupTax.get().getValue().equalsIgnoreCase("true")) {

				if (productGroupProducts.size() > 0) {
					Optional<ProductGroupProduct> opPgp = productGroupProducts.stream()
							.filter(pgp -> pgp.getProduct().getName().equals(ppDto.getName())).findAny();

					if (opPgp.isPresent()) {
						double taxRate = opPgp.get().getProductGroup().getTaxRate();

						if (taxRate > 0 && ppDto.getTaxRate() == 0) {
							productProfile.setTaxRate(taxRate);
						}
					}
				}
			}

			if (optAddCompoundUnit.isPresent() && optAddCompoundUnit.get().getValue().equalsIgnoreCase("true")) {

				if (ppDto.getSku().contains("case of")) {
					String numberOnly = ppDto.getSku().replaceAll("[^0-9]", "");
					String value = numberOnly;
					double convertionValue = Double.parseDouble(value);
					if (convertionValue == 0) {
						convertionValue = 1;
					}
					productProfile.setCompoundUnitQty(convertionValue);

					Double cUnitQty = convertionValue;
					productProfile.setPrice(ppDto.getPrice().multiply(new BigDecimal(cUnitQty)));
					productProfile.setUnitQty(ppDto.getUnitQty() != null ? ppDto.getUnitQty() : 1d);
					productProfile.setCompoundUnitQty(cUnitQty);

				} else {
					if (ppDto.getUnitQty() != null) {
						productProfile.setUnitQty(ppDto.getUnitQty());
					} else {
						productProfile.setUnitQty(1d);
					}
					productProfile.setPrice(ppDto.getPrice());
				}

			} else {
				if (ppDto.getUnitQty() != null) {
					productProfile.setUnitQty(ppDto.getUnitQty());
				} else {
					productProfile.setUnitQty(1d);
				}
				productProfile.setPrice(ppDto.getPrice());
			}

			// update category
			Optional<ProductCategory> optionalCategory = productCategorys.stream()
					.filter(pl -> ppDto.getProductCategoryName().equals(pl.getName())).findAny();

			if (optionalCategory.isPresent()) {
				productProfile.setProductCategory(optionalCategory.get());
			} else {
				productProfile.setProductCategory(productCategory);
			}
			saveUpdateProductProfiles.add(productProfile);
		}
		bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);

		Long Listcount = productProfileDTOs.parallelStream().filter(productProfileDTO -> productProfileDTO.getAlterId() == null).count();
	    System.out.println("productProfileDTOsnullcount"+Listcount);
		if(Listcount == 0) {
			ProductProfileDTO productProflleDTO = productProfileDTOs.stream()
					.max(Comparator.comparingLong(ProductProfileDTO::getAlterId)).get();
			AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
			alterIdMasterDTO.setAlterId(productProflleDTO.getAlterId());
			alterIdMasterDTO.setMasterName(TallyMasters.PRODUCT_PROFILE.toString());
			alterIdMasterDTO.setCompanyId(company.getId());
			alterIdMasterService.save(alterIdMasterDTO);
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
	public void saveUpdateProductProfilesUpdateIdNew(final List<ProductProfileDTO> productProfileDTOs,
			final SyncOperation syncOperation,boolean fullUpdate) {
		
		log.info("Saving Product Profiles:----" + productProfileDTOs.size());
		long start = System.nanoTime();
		
		final Company company = syncOperation.getCompany();
		Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();
		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.ALIAS_TO_NAME);

		if (optAliasToName.isPresent() && optAliasToName.get().getValue().equalsIgnoreCase("true")) {
			for (ProductProfileDTO ppDto : productProfileDTOs) {
				String name = ppDto.getName();
				ppDto.setName(ppDto.getAlias() != null && !ppDto.getAlias().equals("") ? ppDto.getAlias() : name);
				ppDto.setAlias(name);
			}
		}

		// find all exist product profiles
		Set<String> ppNames = productProfileDTOs.stream().map(p -> p.getName()).collect(Collectors.toSet());
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId();

		
		List<ProductCategory> productCategorys = productCategoryRepository.findByCompanyId(company.getId());
//		productCategorys.forEach(data -> {
//			String[] name = data.getName().split("~");
//			data.setName(name[0]);
//		});

		List<ProductGroupProduct> productGroupProducts = new ArrayList<>();
		Set<Long> dectivatedpp = new HashSet<>();

		Optional<CompanyConfiguration> optProductGroupTax = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.PRODUCT_GROUP_TAX);

		if (optProductGroupTax.isPresent() && optProductGroupTax.get().getValue().equalsIgnoreCase("true")) {

			productGroupProducts = productGroupProductRepository.findByProductGroupProductActivatedAndCompanyId();

		}
		//find pp is not tally
		if(fullUpdate == true){
			 List<ProductProfile> tallyproductProfiles = productProfiles.stream().filter(data -> data.getDataSourceType().equals(DataSourceType.TALLY)).collect(Collectors.toList());
			for(ProductProfile pp :tallyproductProfiles) {
				 flag = false;
				productProfileDTOs.forEach(data ->{
					if(pp.getProductId().equals(data.getProductId())) {
						flag = true;
					}
				});
				if(!flag) {
					dectivatedpp.add(pp.getId());
				}
			}
				if(!dectivatedpp.isEmpty()) {
					productProfileRepository.deactivateProductProfileUsingInId(dectivatedpp);
				}
		}
		
		// All product must have a division/category, if not, set a default one
		Division defaultDivision = divisionRepository.findFirstByCompanyId(company.getId());
		Optional<ProductCategory> defaultCategory = productCategoryRepository
				.findByCompanyIdAndNameIgnoreCase(company.getId(), "Not Applicable");
		ProductCategory productCategory = new ProductCategory();
		if (!defaultCategory.isPresent()) {
			productCategory = new ProductCategory();
			productCategory.setPid(ProductCategoryService.PID_PREFIX + RandomUtil.generatePid());
			productCategory.setName("Not Applicable");
			productCategory.setDataSourceType(DataSourceType.TALLY);
			productCategory.setCompany(company);
			productCategory = productCategoryRepository.save(productCategory);
		} else {
			productCategory = defaultCategory.get();
		}

		Optional<CompanyConfiguration> optAddCompoundUnit = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.ADD_COMPOUND_UNIT);

		for (ProductProfileDTO ppDto : productProfileDTOs) {
			Optional<ProductProfile> optionalPP = null;
			// check exist by name, only one exist with a name
		
				optionalPP = productProfiles.stream()
						.filter(p -> p.getProductId() != null && !p.getProductId().equals("")
						? p.getProductId().equals(ppDto.getProductId())
								: false)
						.findAny();

			ProductProfile productProfile = new ProductProfile();
			if (optionalPP.isPresent()) {
				productProfile = optionalPP.get();
//				productProfile.setName(ppDto.getName() + "~" + ppDto.getProductId());
				productProfile.setName(ppDto.getName());
				// if not update, skip this iteration.
				if (!productProfile.getThirdpartyUpdate()) {
					continue;
				}
			} else {
				Optional<ProductProfile> optionalPPName = productProfiles.stream()
						.filter(p -> p.getName() != null && !p.getName().equals("")
						? p.getName().equals(ppDto.getName())
								: false)
						.findAny();
				if(!optionalPPName.isPresent()) {
					productProfile.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
					productProfile.setCompany(company);
					productProfile.setProductId(ppDto.getProductId());
					productProfile.setDivision(defaultDivision);
					productProfile.setProductDescription(ppDto.getName());
					productProfile.setDataSourceType(DataSourceType.TALLY);
				}else {
					productProfile = optionalPPName.get();
					productProfile.setProductId(ppDto.getProductId());
				}
				
			}
			productProfile.setName(ppDto.getName());
			productProfile.setAlias(ppDto.getAlias());
			productProfile.setMrp(ppDto.getMrp());
			productProfile.setTaxRate(ppDto.getTaxRate());
			productProfile.setCessTaxRate(ppDto.getCessTaxRate());
			productProfile.setSku(ppDto.getSku());
			productProfile.setActivated(ppDto.getActivated());
			productProfile.setTrimChar(ppDto.getTrimChar());
			productProfile.setSize(ppDto.getSize());
			productProfile.setHsnCode(ppDto.getHsnCode());
			productProfile.setProductDescription(ppDto.getName());
			productProfile.setBarcode(ppDto.getBarcode());
			productProfile.setRemarks(ppDto.getRemarks());

			if (optProductGroupTax.isPresent() && optProductGroupTax.get().getValue().equalsIgnoreCase("true")) {

				if (productGroupProducts.size() > 0) {
					Optional<ProductGroupProduct> opPgp = productGroupProducts.stream()
							.filter(pgp -> pgp.getProduct().getName().equals(ppDto.getName())).findAny();

					if (opPgp.isPresent()) {
						double taxRate = opPgp.get().getProductGroup().getTaxRate();

						if (taxRate > 0 && ppDto.getTaxRate() == 0) {
							productProfile.setTaxRate(taxRate);
						}
					}
				}
			}

			if (optAddCompoundUnit.isPresent() && optAddCompoundUnit.get().getValue().equalsIgnoreCase("true")) {

				if (ppDto.getSku().contains("case of")) {
					String numberOnly = ppDto.getSku().replaceAll("[^0-9]", "");
					String value = numberOnly;
					double convertionValue = Double.parseDouble(value);
					if (convertionValue == 0) {
						convertionValue = 1;
					}
					productProfile.setCompoundUnitQty(convertionValue);

					Double cUnitQty = convertionValue;
					productProfile.setPrice(ppDto.getPrice().multiply(new BigDecimal(cUnitQty)));
					productProfile.setUnitQty(ppDto.getUnitQty() != null ? ppDto.getUnitQty() : 1d);
					productProfile.setCompoundUnitQty(cUnitQty);

				} else {
					if (ppDto.getUnitQty() != null) {
						productProfile.setUnitQty(ppDto.getUnitQty());
					} else {
						productProfile.setUnitQty(1d);
					}
					productProfile.setPrice(ppDto.getPrice());
				}

			} else {
				if (ppDto.getUnitQty() != null) {
					productProfile.setUnitQty(ppDto.getUnitQty());
				} else {
					productProfile.setUnitQty(1d);
				}
				productProfile.setPrice(ppDto.getPrice());
			}

			// update category
			Optional<ProductCategory> optionalCategory = productCategorys.stream()
					.filter(pl -> ppDto.getProductCategoryName().equals(pl.getName())).findAny();

			if (optionalCategory.isPresent()) {
				productProfile.setProductCategory(optionalCategory.get());
			} else {
				productProfile.setProductCategory(productCategory);
			}
			Optional<ProductProfile> opAccP = saveUpdateProductProfiles.stream()
					.filter(so -> so.getName().equalsIgnoreCase(ppDto.getName())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}
			
			saveUpdateProductProfiles.add(productProfile);
			 
		}
		bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);

		Long Listcount = productProfileDTOs.parallelStream().filter(productProfileDTO -> productProfileDTO.getAlterId() == null).count();
	    System.out.println("productProfileDTOsnullcount"+Listcount);

		if(Listcount == 0) {
			ProductProfileDTO productProflleDTO = productProfileDTOs.stream()
					.max(Comparator.comparingLong(ProductProfileDTO::getAlterId)).get();
			AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
			alterIdMasterDTO.setAlterId(productProflleDTO.getAlterId());
			alterIdMasterDTO.setMasterName(TallyMasters.PRODUCT_PROFILE.toString());
			alterIdMasterDTO.setCompanyId(company.getId());
			alterIdMasterService.save(alterIdMasterDTO);
		}

		//update name
//		List<ProductCategory> productCategorycopy = productCategoryRepository.findByCompanyId(company.getId());
//		if(!productCategorycopy.isEmpty()) {
//			productCategorycopy.forEach(data -> {
//				if(!data.getName().contains("~")) {
//				data.setName(data.getName() + "~" + data.getProductCategoryId());
//				}
//			});}
//		productCategoryRepository.save(productCategorycopy);


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
			location.setLocationId(locDto.getLocationId());
			saveUpdateLocations.add(location);
		}
		bulkOperationRepositoryCustom.bulkSaveLocations(saveUpdateLocations);
		
		Long Listcount = locationDTOs.parallelStream().filter(locationDTO -> locationDTO.getAlterId() == null).count();
	    System.out.println("locationDTOsnullcount"+Listcount);
		if(Listcount == 0) {
			LocationDTO locationDTO = locationDTOs.stream()
					.max(Comparator.comparingLong(LocationDTO::getAlterId)).get();
			
			AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
			alterIdMasterDTO.setAlterId(locationDTO.getAlterId());
			alterIdMasterDTO.setMasterName(TallyMasters.LOCATION.toString());
			alterIdMasterDTO.setCompanyId(company.getId());
			alterIdMasterService.save(alterIdMasterDTO);
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
	public void saveUpdateLocationsUpdationIdNew(List<LocationDTO> locationDTOs, final SyncOperation syncOperation,boolean fullUpdate) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		
		Set<Location> saveUpdateLocations = new HashSet<>();
		locationDTOs=locationDTOs.stream().filter(data -> !data.getName().equals("Territory")).collect(Collectors.toList());
		// find all locations
		List<Location> locations = locationRepository.findAllByCompanyId(company.getId());
		Set<Long> dectivatedloc = new HashSet<>();
		if(fullUpdate == true) {
		for(Location pp :locations) {
			
				if(!pp.getName().equals("Primary") && !pp.getName().equals("Territory") && pp.getActivated() && pp.getLocationId() != null){
					  flag = false;
						 locationDTOs.forEach(data ->{
							if(pp.getLocationId().equals(data.getLocationId())) {
								flag = true;
							}
						});
						if(!flag) {
							dectivatedloc.add(pp.getId());
						}
				}
				
			}
				if(!dectivatedloc.isEmpty()) {
					locationRepository.deactivatelocationId(dectivatedloc);
				}
			}
		//locations.forEach(data -> System.out.println("====name==="+ data.getName()));
		for (LocationDTO locDto : locationDTOs) {
			Optional<Location> optionalLoc = null;
			// check exist by name, only one exist with a name

			optionalLoc = locations.stream()
					.filter(p -> p.getLocationId() != null && !p.getLocationId().equals("")
							? p.getLocationId().equals(locDto.getLocationId())
							: false)
					.findAny();
			Location location = new Location();
			if (optionalLoc.isPresent()) {
				location = optionalLoc.get();
				// if not update, skip this iteration.
				// if (!location.getThirdpartyUpdate()) {continue;}
			} else {
				// check exist by name,
				/* System.out.println("create neww"); */
				
				
				Optional<Location> optionalLocName = locations.stream()
						.filter(p -> p.getName() != null && !p.getName().equals("")
								? p.getName().equals(locDto.getName())
								: false)
						.findAny();
				
				if(!optionalLocName.isPresent()) {
//					System.out.println("create neww no name exist"+ locDto.getName() +"====gui===="+locDto.getLocationId());
					location.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid());
					location.setLocationId(locDto.getLocationId());
					location.setDescription(locDto.getName());
					location.setCompany(company);
				}else {
//					System.out.println("create replce guid exist"+ locDto.getName() +"====gui===="+locDto.getLocationId());
					location = optionalLocName.get();
					location.setLocationId(locDto.getLocationId());
				}
		
			}
			location.setActivated(locDto.getActivated());
			location.setDescription(locDto.getName());
//			location.setName(locDto.getName() + "~" + locDto.getLocationId());
			location.setName(locDto.getName());
			Optional<Location> opAccP = saveUpdateLocations.stream()
					.filter(so -> so.getName().equalsIgnoreCase(locDto.getName())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}
			saveUpdateLocations.add(location);
		}
		
		
		List<Location> UpdateLocations = saveUpdateLocations.stream().filter(data -> data.getId() != null).collect(Collectors.toList());
		List<Location> saveLocations = saveUpdateLocations.stream().filter(data ->  data.getId() == null).collect(Collectors.toList());
//		UpdateLocations.forEach(data -> System.out.println("id === "+data.getId() + "name===== "+data.getName()));
//		System.out.println("===========================================================================================");
//		saveLocations.forEach(data -> System.out.println("id === "+data.getId() + "name===== "+data.getName()));
		//updation Location
		locationRepository.flush();
//		System.out.println("updated===="+UpdateLocations.size());
		locationRepository.save(UpdateLocations);
		//Save Location
		
		if(!saveLocations.isEmpty()) {
			locationRepository.flush();
//			System.out.println("Save===="+saveLocations.size());
			locationRepository.save(saveLocations);
		}
		
		Long Listcount = locationDTOs.parallelStream().filter(locationDTO -> locationDTO.getAlterId() == null).count();
	    System.out.println("locationDTOsnullcount"+Listcount);
		if(Listcount == 0) {
			LocationDTO locationDTO = locationDTOs.stream()
					.max(Comparator.comparingLong(LocationDTO::getAlterId)).get();
			
			AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
			alterIdMasterDTO.setAlterId(locationDTO.getAlterId());
			alterIdMasterDTO.setMasterName(TallyMasters.LOCATION.toString());
			alterIdMasterDTO.setCompanyId(company.getId());
			alterIdMasterService.save(alterIdMasterDTO);
		}
		
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
		dectivatedloc.forEach(data -> log.info("deactivated id " + dectivatedloc.size() + data));
	}

	@Transactional
	public void saveUpdateAccountProfiles(final List<AccountProfileDTO> accountProfileDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		 boolean isOptimised = false ;
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
			}else {
				accountProfile.setDefaultPriceLevel(null);
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
		
		Long Listcount = accountProfileDTOs.parallelStream().filter(accountProfileDTO -> accountProfileDTO.getAlterId() == null).count();
	    System.out.println("accountProfileDTOnullcount"+Listcount);
		if(Listcount == 0) {
			AccountProfileDTO accountProfileDTO = accountProfileDTOs.stream()
					.max(Comparator.comparingLong(AccountProfileDTO::getAlterId)).get();
			
			AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
			alterIdMasterDTO.setAlterId(accountProfileDTO.getAlterId());
			alterIdMasterDTO.setMasterName(TallyMasters.ACCOUNT_PROFILE.toString());
			alterIdMasterDTO.setCompanyId(company.getId());
			alterIdMasterService.save(alterIdMasterDTO);
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
	public void saveUpdateAccountProfilesUpdateIdNew(final List<AccountProfileDTO> accountProfileDTOs,
			final SyncOperation syncOperation,boolean fullUpdate) {
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
		if(fullUpdate == true) {
			List<AccountType> accountTypes = accountTypeRepository.findAllByCompanyId(company.getId());
			if(!accountProfiles.isEmpty() && accountProfiles.size() > 1 && !accountProfileDTOs.isEmpty()) {
				accountProfiles.remove(0);
				List<AccountProfile> tallyAccountProfile = accountProfiles.stream().filter(data -> data.getDataSourceType().equals(DataSourceType.TALLY)).collect(Collectors.toList());
				for(AccountProfile ac :tallyAccountProfile) {
				
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
		}
		
		for (AccountProfileDTO apDto : accountProfileDTOs) {
			Optional<AccountProfile> optionalAP = null;
			// check exist by name, only one exist with a name

			optionalAP = accountProfiles.stream()
					.filter(p -> p.getCustomerId() != null && !p.getCustomerId().equals("")
							? p.getCustomerId().equals(apDto.getCustomerId())
							: false)
					.findAny();

			AccountProfile accountProfile = new AccountProfile();
			if (optionalAP.isPresent()) {
				accountProfile = optionalAP.get();
//				accountProfile.setName(apDto.getName() + "~" + apDto.getCustomerId());
				accountProfile.setName(apDto.getName());
				// if not update, skip this iteration. Not implemented now
				// if (!accountProfile.getThirdpartyUpdate()) { continue; }
			} else {
				
				Optional<AccountProfile> optionalAPName = accountProfiles.stream()
						.filter(p -> p.getName() != null && !p.getName().equals("")
								? p.getName().equals(apDto.getName())
								: false)
						.findAny();
				if(!optionalAPName.isPresent()) {
					accountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
					accountProfile.setCustomerId(apDto.getCustomerId());
					accountProfile.setUser(user);
					accountProfile.setCompany(company);
					accountProfile.setAccountStatus(AccountStatus.Unverified);
					accountProfile.setDataSourceType(DataSourceType.TALLY);
					accountProfile.setDescription(apDto.getName());
					accountProfile.setImportStatus(true);
				}else {
					accountProfile = optionalAPName.get();
					accountProfile.setCustomerId(apDto.getCustomerId());
				}
				
			}
//			accountProfile.setName(apDto.getName() + "~" + apDto.getCustomerId());
			accountProfile.setName(apDto.getName());
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
			}else {
				accountProfile.setDefaultPriceLevel(null);
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
		
		Long Listcount = accountProfileDTOs.parallelStream().filter(accountProfileDTO -> accountProfileDTO.getAlterId() == null).count();
	    System.out.println("accountProfileDTOnullcount"+Listcount);
		if(Listcount == 0) {
			AccountProfileDTO accountProfileDTO = accountProfileDTOs.stream()
					.max(Comparator.comparingLong(AccountProfileDTO::getAlterId)).get();
			
			AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
			alterIdMasterDTO.setAlterId(accountProfileDTO.getAlterId());
			alterIdMasterDTO.setMasterName(TallyMasters.ACCOUNT_PROFILE.toString());
			alterIdMasterDTO.setCompanyId(company.getId());
			alterIdMasterService.save(alterIdMasterDTO);
		}
		
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
//		dectivatedac.forEach(data -> log.info("deactivated id " + dectivatedac.size() +" +"+ data));
	}
	
	@Transactional
	@Async
	public void saveUpdateLocationAccountProfiles(final List<LocationAccountProfileDTO> locationAccountProfileDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		 boolean isOptimised = false ;
		final Company company = syncOperation.getCompany();
		final Long companyId = syncOperation.getCompany().getId();
		List<LocationAccountProfile> newLocationAccountProfiles = new ArrayList<>();
		List<LocationAccountProfile> locationAccountProfiles = locationAccountProfileService
				.findAllLocationAccountProfiles(companyId);
		
		
		locationAccountProfileDTOs.forEach(data -> System.out.println(
				"taxmasterdebuggingalterId" + "name =====" + data.getAccountProfileName() + "alter id======" + data.getAlterId()));
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
	
			LocationAccountProfileDTO locationAccountProfileDTO = locationAccountProfileDTOs.stream()
					.max(Comparator.comparingLong(LocationAccountProfileDTO::getAlterId)).get();
			
			AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
			alterIdMasterDTO.setAlterId(locationAccountProfileDTO.getAlterId());
			alterIdMasterDTO.setMasterName(TallyMasters.LOCATION_ACCOUNT_PROFILE.toString());
			alterIdMasterDTO.setCompanyId(company.getId());
			alterIdMasterService.save(alterIdMasterDTO);
		
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
	public void saveUpdateLocationAccountProfilesUpdateIdNew(final List<LocationAccountProfileDTO> locationAccountProfileDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		log.debug(String.valueOf(start)+"-"+syncOperation.getCompany().getId());
		final Company company = syncOperation.getCompany();
		final Long companyId = syncOperation.getCompany().getId();
		List<LocationAccountProfile> newLocationAccountProfiles = new ArrayList<>();
		List<LocationAccountProfile> locationAccountProfiles = locationAccountProfileService
				.findAllLocationAccountProfiles(companyId);
		log.debug(String.valueOf(start)+"-"+syncOperation.getCompany().getId() + " : locationAccountProfiles : " + locationAccountProfiles.size() );
		// delete all assigned location account profile from tally
		// locationAccountProfileRepository.deleteByCompanyIdAndDataSourceTypeAndThirdpartyUpdateTrue(company.getId(),DataSourceType.TALLY);
		List<AccountProfile> accountProfiles = accountProfileService.findAllAccountProfileByCompanyId(companyId);
		log.debug(String.valueOf(start)+"-"+syncOperation.getCompany().getId() + " : AccountProfiles : " + accountProfiles.size());
		List<Location> locations = locationService.findAllLocationByCompanyId(companyId);
		log.debug(String.valueOf(start)+"-"+syncOperation.getCompany().getId() + " : locations : " + locations.size());
		List<Long> locationAccountProfilesIds = new ArrayList<>();
//		locations.forEach(data -> {
//			String[] name = data.getName().split("~");
//			data.setName(name[0]);
//		});
//		accountProfiles.forEach(data -> {
//			String[] name = data.getName().split("~");
//			data.setName(name[0]);
//		});
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
			log.debug(String.valueOf(start)+"-"+syncOperation.getCompany().getId() + " : locationAccountProfilesIds : " + locationAccountProfilesIds.size());
			locationAccountProfileRepository.deleteByIdIn(companyId, locationAccountProfilesIds);
		}

		log.debug(String.valueOf(start)+"-"+syncOperation.getCompany().getId() + " : newLocationAccountProfiles : " + newLocationAccountProfiles.size());
		locationAccountProfileRepository.save(newLocationAccountProfiles);
		LocationAccountProfileDTO locationAccountProfileDTO = locationAccountProfileDTOs.stream()
				.max(Comparator.comparingLong(LocationAccountProfileDTO::getAlterId)).get();
		
		AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
		alterIdMasterDTO.setAlterId(locationAccountProfileDTO.getAlterId());
		alterIdMasterDTO.setMasterName(TallyMasters.LOCATION_ACCOUNT_PROFILE.toString());
		alterIdMasterDTO.setCompanyId(company.getId());
		alterIdMasterService.save(alterIdMasterDTO);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info(String.valueOf(start)+"-"+syncOperation.getCompany().getId() + " : Sync completed in {} ms", elapsedTime);
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
