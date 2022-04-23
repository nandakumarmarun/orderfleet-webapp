package com.orderfleet.webapp.service.async;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.domain.EcomProductProfileProduct;
import com.orderfleet.webapp.domain.GSTProductGroup;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PriceLevelList;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupEcomProduct;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.TaxMaster;
import com.orderfleet.webapp.domain.TemporaryOpeningStock;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.StockLocationType;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.EcomProductProfileProductRepository;
import com.orderfleet.webapp.repository.EcomProductProfileRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.PriceLevelListRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupEcomProductsRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.TaxMasterRepository;
import com.orderfleet.webapp.repository.TemporaryOpeningStockRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EcomProductProfileService;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.PriceLevelListService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.TaxMasterService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.ecom.mapper.EcomProductProfileMapper;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileProductDTO;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelListDTO;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupEcomProductDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;
import com.orderfleet.webapp.web.rest.dto.TaxMasterDTO;
import com.orderfleet.webapp.web.rest.integration.dto.GSTProductGroupDTO;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductGroupProductDTO;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductProfileCustomDTO;
import com.orderfleet.webapp.web.rest.mapper.TaxMasterMapper;

/**
 * Service for save/update product profile related data from third party
 * softwares like tally.
 * <p>
 * Use the @Async annotation to process asynchronously.
 * </p>
 */
@Service
public class TPProductProfileManagementService {

	private final Logger log = LoggerFactory.getLogger(TPProductProfileManagementService.class);

	private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	private final SyncOperationRepository syncOperationRepository;

	private final DivisionRepository divisionRepository;

	private final ProductCategoryRepository productCategoryRepository;

	private final ProductGroupRepository productGroupRepository;

	private final ProductProfileRepository productProfileRepository;

	private final ProductGroupProductRepository productGroupProductRepository;

	private final StockLocationRepository stockLocationRepository;

	private final OpeningStockRepository openingStockRepository;

	private final TemporaryOpeningStockRepository temporaryOpeningStockRepository;

	private final PriceLevelRepository priceLevelRepository;

	private final PriceLevelListRepository priceLevelListRepository;

	private final TaxMasterRepository taxMasterRepository;

	private final TaxMasterMapper taxMasterMapper;

	private final EcomProductProfileRepository ecomProductProfileRepository;

	private final EcomProductProfileMapper ecomProductProfileMapper;

	private final EcomProductProfileProductRepository ecomProductProfileProductRepository;

	private final ProductGroupEcomProductsRepository productGroupEcomProductsRepository;

	private final CompanyConfigurationRepository companyConfigurationRepository;
	
	boolean flag= false;

	@Autowired
	private StockLocationService stockLocationService;

	private Collection<ProductCategoryDTO> productGroups;

	public TPProductProfileManagementService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
			SyncOperationRepository syncOperationRepository, DivisionRepository divisionRepository,
			ProductCategoryRepository productCategoryRepository, ProductGroupRepository productGroupRepository,
			ProductProfileRepository productProfileRepository,
			ProductGroupProductRepository productGroupProductRepository,
			StockLocationRepository stockLocationRepository, OpeningStockRepository openingStockRepository,
			PriceLevelRepository priceLevelRepository, PriceLevelListRepository priceLevelListRepository,
			TaxMasterRepository taxMasterRepository, TaxMasterMapper taxMasterMapper,
			EcomProductProfileRepository ecomProductProfileRepository,
			EcomProductProfileMapper ecomProductProfileMapper,
			EcomProductProfileProductRepository ecomProductProfileProductRepository,
			ProductGroupEcomProductsRepository productGroupEcomProductsRepository,
			CompanyConfigurationRepository companyConfigurationRepository,
			TemporaryOpeningStockRepository temporaryOpeningStockRepository) {
		super();
		this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
		this.syncOperationRepository = syncOperationRepository;
		this.divisionRepository = divisionRepository;
		this.productCategoryRepository = productCategoryRepository;
		this.productGroupRepository = productGroupRepository;
		this.productProfileRepository = productProfileRepository;
		this.productGroupProductRepository = productGroupProductRepository;
		this.stockLocationRepository = stockLocationRepository;
		this.openingStockRepository = openingStockRepository;
		this.priceLevelRepository = priceLevelRepository;
		this.priceLevelListRepository = priceLevelListRepository;
		this.taxMasterRepository = taxMasterRepository;
		this.taxMasterMapper = taxMasterMapper;
		this.ecomProductProfileRepository = ecomProductProfileRepository;
		this.ecomProductProfileMapper = ecomProductProfileMapper;
		this.ecomProductProfileProductRepository = ecomProductProfileProductRepository;
		this.productGroupEcomProductsRepository = productGroupEcomProductsRepository;
		this.companyConfigurationRepository = companyConfigurationRepository;
		this.temporaryOpeningStockRepository = temporaryOpeningStockRepository;
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
	public void saveUpdateProductCategoriesId(final List<ProductCategoryDTO> productCategoryDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<ProductCategory> saveUpdateProductCategories = new HashSet<>();
		// find all product category
		List<ProductCategory> productCategories = productCategoryRepository.findByCompanyId(company.getId());
		for (ProductCategoryDTO pcDto : productCategoryDTOs) {
			Optional<ProductCategory> optionalPC = null;
			// check exist by name, only one exist with a name
			if (pcDto.getProductCategoryId() == null) {
				optionalPC = productCategories.stream().filter(pc -> pc.getName().equals(pcDto.getName())).findAny();
			} else {
				optionalPC = productCategories.stream()
						.filter(pc -> pc.getProductCategoryId() != null
								? pc.getProductCategoryId().equals(pcDto.getProductCategoryId())
								: false)
						.findAny();
			}
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
				productCategory.setProductCategoryId(pcDto.getProductCategoryId());
				productCategory.setDataSourceType(DataSourceType.TALLY);
				productCategory.setCompany(company);
			}
			productCategory.setName(pcDto.getName());
			productCategory.setAlias(pcDto.getAlias());
			productCategory.setDescription(pcDto.getDescription());
			productCategory.setActivated(pcDto.getActivated());
			Optional<ProductCategory> opAccP = saveUpdateProductCategories.stream()
					.filter(so -> so.getName().equalsIgnoreCase(pcDto.getName())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}
			saveUpdateProductCategories.add(productCategory);
		}
		bulkOperationRepositoryCustom.bulkSaveProductCategory(saveUpdateProductCategories);
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
	public void saveUpdateProductCategoriesUpdateId(final List<ProductCategoryDTO> productCategoryDTOs,
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
				productCategory.setProductCategoryId(pcDto.getProductCategoryId());
				productCategory.setDataSourceType(DataSourceType.TALLY);
				productCategory.setDescription(pcDto.getName());
				productCategory.setCompany(company);
			}
			productCategory.setName(pcDto.getName() + "~" +pcDto.getProductCategoryId());
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
	public void saveUpdateProductGroupsid(final List<ProductGroupDTO> productGroupDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<ProductGroup> saveUpdateProductGroups = new HashSet<>();

		// find all product group
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(company.getId());

		for (ProductGroupDTO pgDto : productGroupDTOs) {
			Optional<ProductGroup> optionalPG = null;
			if (pgDto.getProductGroupId() == null) {
				optionalPG = productGroups.stream().filter(p -> p.getName().equals(pgDto.getName())).findAny();
			} else {
				optionalPG = productGroups.stream()
						.filter(p -> p.getProductGroupId() != null
								? p.getProductGroupId().equals(pgDto.getProductGroupId())
								: false)
						.findAny();
			}
			// check exist by name, only one exist with a name

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
				productGroup.setProductGroupId(pgDto.getProductGroupId());
				productGroup.setDataSourceType(DataSourceType.TALLY);
				productGroup.setCompany(company);
			}
			productGroup.setAlias(pgDto.getAlias());
			productGroup.setName(pgDto.getName());
			productGroup.setDescription(pgDto.getDescription());
			productGroup.setActivated(pgDto.getActivated());
			Optional<ProductGroup> opAccP = saveUpdateProductGroups.stream()
					.filter(so -> so.getName().equalsIgnoreCase(pgDto.getName())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}
			saveUpdateProductGroups.add(productGroup);
		}
//		saveUpdateProductGroups.forEach(i -> System.out.println("save prg"+i));
		bulkOperationRepositoryCustom.bulkSaveProductGroup(saveUpdateProductGroups);
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
	public void saveUpdateProductGroupsUpdateId(final List<ProductGroupDTO> productGroupDTOs,
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
				productGroup.setProductGroupId(pgDto.getProductGroupId());
				productGroup.setDataSourceType(DataSourceType.TALLY);
				productGroup.setDescription(pgDto.getName());
				productGroup.setCompany(company);
			}
			productGroup.setAlias(pgDto.getAlias());
			productGroup.setName(pgDto.getName() + "~" +pgDto.getProductGroupId());
			productGroup.setDescription(pgDto.getName());
			productGroup.setActivated(pgDto.getActivated());
			Optional<ProductGroup> opAccP = saveUpdateProductGroups.stream()
					.filter(so -> so.getName().equalsIgnoreCase(pgDto.getName())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}
			saveUpdateProductGroups.add(productGroup);
		}
//		saveUpdateProductGroups.forEach(i -> System.out.println("save prg"+i));
		bulkOperationRepositoryCustom.bulkSaveProductGroup(saveUpdateProductGroups);
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
//		saveUpdateProductGroups.forEach(i -> System.out.println("save prg"+i));
		bulkOperationRepositoryCustom.bulkSaveProductGroup(saveUpdateProductGroups);
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
			productProfile.setCessTaxRate(ppDto.getCessTaxRate());
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
	public void saveUpdateProductProfilesId(final List<ProductProfileDTO> productProfileDTOs,
			final SyncOperation syncOperation) {
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
			Optional<ProductProfile> optionalPP = null;
			// check exist by name, only one exist with a name
			if (ppDto.getProductId() == null) {
				optionalPP = productProfiles.stream().filter(p -> p.getName().equals(ppDto.getName())).findAny();
			} else {
				optionalPP = productProfiles.stream()
						.filter(p -> p.getProductId() != null ? p.getProductId().equals(ppDto.getProductId()) : false)
						.findAny();
			}
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
				productProfile.setProductId(ppDto.getProductId());
				productProfile.setName(ppDto.getName());
				productProfile.setDivision(defaultDivision);
				productProfile.setDataSourceType(DataSourceType.TALLY);
			}
			productProfile.setName(ppDto.getName());
			productProfile.setAlias(ppDto.getAlias());
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
			Optional<ProductProfile> opAccP = saveUpdateProductProfiles.stream()
					.filter(so -> so.getName().equalsIgnoreCase(ppDto.getName())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}
			saveUpdateProductProfiles.add(productProfile);
		}
		saveUpdateProductProfiles
				.forEach(obj -> System.out.println("saved product pro======" + saveUpdateProductProfiles));
		bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);
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
	public void saveUpdateProductProfilesUpdateId(final List<ProductProfileDTO> productProfileDTOs,
			final SyncOperation syncOperation) {
		
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
		productCategorys.forEach(data -> {
			String[] name = data.getName().split("~");
			data.setName(name[0]);
		});

		List<ProductGroupProduct> productGroupProducts = new ArrayList<>();
		Set<Long> dectivatedpp = new HashSet<>();

		Optional<CompanyConfiguration> optProductGroupTax = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.PRODUCT_GROUP_TAX);

		if (optProductGroupTax.isPresent() && optProductGroupTax.get().getValue().equalsIgnoreCase("true")) {

			productGroupProducts = productGroupProductRepository.findByProductGroupProductActivatedAndCompanyId();

		}
		//find pp is not tally
		for(ProductProfile pp :productProfiles) {
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
			ProductProfile productProfile;
			if (optionalPP.isPresent()) {
				productProfile = optionalPP.get();
				productProfile.setName(ppDto.getName() + "~" + ppDto.getProductId());
				// if not update, skip this iteration.
				if (!productProfile.getThirdpartyUpdate()) {
					continue;
				}
			} else {
				productProfile = new ProductProfile();
				productProfile.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
				productProfile.setCompany(company);
				productProfile.setProductId(ppDto.getProductId());
				productProfile.setDivision(defaultDivision);
				productProfile.setProductDescription(ppDto.getName());
				productProfile.setDataSourceType(DataSourceType.TALLY);
			}
			productProfile.setName(ppDto.getName() + "~" + ppDto.getProductId());
			productProfile.setAlias(ppDto.getAlias());
			productProfile.setMrp(ppDto.getMrp());
			productProfile.setTaxRate(ppDto.getTaxRate());
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
		//update name
		List<ProductCategory> productCategorycopy = productCategoryRepository.findByCompanyId(company.getId());
		if(!productCategorycopy.isEmpty()) {
			productCategorycopy.forEach(data -> {
				if(!data.getName().contains("~")) {
				data.setName(data.getName() + "~" + data.getProductCategoryId());
				}
			});}
		productCategoryRepository.save(productCategorycopy);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		
		log.info("Sync completed in {} ms", elapsedTime);
		dectivatedpp.forEach(data -> log.info("deactivated id " + dectivatedpp.size() + data));
		
	}
	
	@Transactional
	public void saveUpdateProductProfilesUpdateIdNew(final List<ProductProfileDTO> productProfileDTOs,
			final SyncOperation syncOperation) {
		
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
		for(ProductProfile pp :productProfiles) {
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
		dectivatedpp.forEach(data -> log.info("deactivated id " + dectivatedpp.size() + data));
		
	}


	@Transactional
	public void saveUpdateProductProfiles(final TPProductProfileCustomDTO productProfileCustomDTO,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();
		if (productProfileCustomDTO.getProductProfileDTOs() != null
				&& productProfileCustomDTO.getProductProfileDTOs().size() > 0) {
			// find all exist product profiles
			Set<String> ppNames = productProfileCustomDTO.getProductProfileDTOs().stream().map(p -> p.getName())
					.collect(Collectors.toSet());
			List<ProductProfile> productProfiles = productProfileRepository.findByCompanyIdAndNameIn(company.getId(),
					ppNames);
			// All product must have a division/category, if not, set a default
			// one
			Division defaultDivision = divisionRepository.findFirstByCompanyId(company.getId());
			ProductCategory defaultCategory = productCategoryRepository
					.findByCompanyIdAndNameIgnoreCase(company.getId(), "Not Applicable").get();
			for (ProductProfileDTO ppDto : productProfileCustomDTO.getProductProfileDTOs()) {
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
					// check exist by attribute, if exist do not update that
					// attributes, so change dto's value
					// price
					if (productProfileCustomDTO.getAttributes().stream().anyMatch(a -> a.equals("price"))) {
						ppDto.setPrice(productProfile.getPrice());
					}
					// description
					if (productProfileCustomDTO.getAttributes().stream().anyMatch(a -> a.equals("description"))) {
						ppDto.setDescription(productProfile.getDescription());
					}
					// taxRate
					if (productProfileCustomDTO.getAttributes().stream().anyMatch(a -> a.equals("taxRate"))) {
						ppDto.setTaxRate(productProfile.getTaxRate());
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
				productProfile.setDescription(ppDto.getDescription());
				productProfile.setPrice(ppDto.getPrice());
				productProfile.setMrp(ppDto.getMrp());
				productProfile.setTaxRate(ppDto.getTaxRate());
				productProfile.setSku(ppDto.getSku());
				productProfile.setActivated(ppDto.getActivated());
				if (ppDto.getUnitQty() != null) {
					productProfile.setUnitQty(ppDto.getUnitQty());
				}
				// update category
				Optional<ProductCategory> optionalCategory = productCategoryRepository
						.findByCompanyIdAndNameIgnoreCase(company.getId(), ppDto.getProductCategoryName());
				if (optionalCategory.isPresent()) {
					productProfile.setProductCategory(optionalCategory.get());
				} else {
					productProfile.setProductCategory(defaultCategory);
				}
				saveUpdateProductProfiles.add(productProfile);
			}
			bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);
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
	public void saveUpdateProductGroupProduct(final List<TPProductGroupProductDTO> productGroupProductDTOs,
			final SyncOperation syncOperation) {
		
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = syncOperation.getCompany().getId();

		Set<ProductGroupProduct> saveUpdateProductGroupProducts = new HashSet<>();
		if (syncOperation.getReset()) {
			productGroupProductRepository.updateProductGroupProductInactivate(companyId);
		}
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(companyId);
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);

		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.ALIAS_TO_NAME);

		for (TPProductGroupProductDTO pgpDto : productGroupProductDTOs) {
			// check exist by names,
			Optional<ProductGroupProduct> optionalPGP = productGroupProductRepository
					.findByCompanyIdAndProductGroupNameIgnoreCaseAndProductNameIgnoreCase(companyId,
							pgpDto.getGroupName(), pgpDto.getProductName());
			// if not exist save
			if (!optionalPGP.isPresent()) {
				Optional<ProductGroup> optionalGroup = productGroups.stream()
						.filter(pl -> pgpDto.getGroupName().equals(pl.getName())).findAny();
				Optional<ProductProfile> optionalpp = productProfiles.stream()
						.filter(pl -> pgpDto.getProductName().equals(pl.getName())).findAny();

				if (optAliasToName.isPresent() && optAliasToName.get().getValue().equalsIgnoreCase("true")) {

					optionalpp = productProfiles.stream()
							.filter(pl -> pl.getAlias() != null && !pl.getAlias().equals("")
									? pgpDto.getProductName().equals(pl.getAlias())
									: pgpDto.getProductName().equals(pl.getName()))
							.findAny();

				}

				if (optionalpp.isPresent() && optionalGroup.isPresent()) {
					ProductGroupProduct pgp = new ProductGroupProduct();
					pgp.setProductGroup(optionalGroup.get());
					pgp.setProduct(optionalpp.get());
					pgp.setCompany(company);
					pgp.setActivated(true);
					saveUpdateProductGroupProducts.add(pgp);
				}
			} else {
				optionalPGP.get().setActivated(true);
				saveUpdateProductGroupProducts.add(optionalPGP.get());
			}
		}
		bulkOperationRepositoryCustom.bulkSaveProductGroupProductProfile(saveUpdateProductGroupProducts);
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
	public void saveUpdateProductGroupProductUpdateId(final List<TPProductGroupProductDTO> productGroupProductDTOs,
			final SyncOperation syncOperation) {
		
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = syncOperation.getCompany().getId();
		Set<ProductGroupProduct> saveUpdateProductGroupProducts = new HashSet<>();
		if (syncOperation.getReset()) {
			productGroupProductRepository.updateProductGroupProductInactivate(companyId);
		}
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(companyId);
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
		productGroups.forEach(data -> {
			String[] name = data.getName().split("~");
			data.setName(name[0]);
		});
		productProfiles.forEach(data -> {
			String[] name = data.getName().split("~");
			data.setName(name[0]);
		});
		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.ALIAS_TO_NAME);

		for (TPProductGroupProductDTO pgpDto : productGroupProductDTOs) {
			// check exist by names,
			Optional<ProductGroupProduct> optionalPGP = productGroupProductRepository
					.findByCompanyIdAndProductGroupNameIgnoreCaseAndProductNameIgnoreCase(companyId,
							pgpDto.getGroupName(), pgpDto.getProductName());
			// if not exist save
			if (!optionalPGP.isPresent()) {
				Optional<ProductGroup> optionalGroup = productGroups.stream()
						.filter(pl -> pgpDto.getGroupName().equals(pl.getName())).findAny();
				Optional<ProductProfile> optionalpp = productProfiles.stream()
						.filter(pl -> pgpDto.getProductName().equals(pl.getName())).findAny();

				if (optAliasToName.isPresent() && optAliasToName.get().getValue().equalsIgnoreCase("true")) {

					optionalpp = productProfiles.stream()
							.filter(pl -> pl.getAlias() != null && !pl.getAlias().equals("")
									? pgpDto.getProductName().equals(pl.getAlias())
									: pgpDto.getProductName().equals(pl.getName()))
							.findAny();

				}

				if (optionalpp.isPresent() && optionalGroup.isPresent()) {
					ProductGroupProduct pgp = new ProductGroupProduct();
					pgp.setProductGroup(optionalGroup.get());
					pgp.setProduct(optionalpp.get());
					pgp.setCompany(company);
					pgp.setActivated(true);
					saveUpdateProductGroupProducts.add(pgp);
				}
			} else {
				optionalPGP.get().setActivated(true);
				saveUpdateProductGroupProducts.add(optionalPGP.get());
			}
		}
		saveUpdateProductGroupProducts.forEach(data -> {
			if(!data.getProductGroup().getName().contains("~")) {
				data.getProductGroup().setName(data.getProductGroup().getName() + "~" + data.getProductGroup().getProductGroupId());
			}
		});
		bulkOperationRepositoryCustom.bulkSaveProductGroupProductProfile(saveUpdateProductGroupProducts);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		//update name
				List<ProductProfile> productProfilesCopy = productProfileRepository
						.findByCompanyIdAndActivatedTrue(companyId);
				if(!productProfilesCopy.isEmpty()) {
					productProfilesCopy.forEach(data -> {
						if(!data.getName().contains("~")) {
						data.setName(data.getName() + "~" + data.getProductId());
						}
					});}
				productProfileRepository.save(productProfilesCopy);
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}
	
	@Transactional
	@Async
	public void saveUpdateProductGroupProductUpdateIdNew(final List<TPProductGroupProductDTO> productGroupProductDTOs,
			final SyncOperation syncOperation) {
		
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = syncOperation.getCompany().getId();
		Set<ProductGroupProduct> saveUpdateProductGroupProducts = new HashSet<>();
		if (syncOperation.getReset()) {
			productGroupProductRepository.updateProductGroupProductInactivate(companyId);
		}
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(companyId);
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
//		productGroups.forEach(data -> {
//			String[] name = data.getName().split("~");
//			data.setName(name[0]);
//		});
//		productProfiles.forEach(data -> {
//			String[] name = data.getName().split("~");
//			data.setName(name[0]);
//		});
		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.ALIAS_TO_NAME);

		for (TPProductGroupProductDTO pgpDto : productGroupProductDTOs) {
			// check exist by names,
			Optional<ProductGroupProduct> optionalPGP = productGroupProductRepository
					.findByCompanyIdAndProductGroupNameIgnoreCaseAndProductNameIgnoreCase(companyId,
							pgpDto.getGroupName(), pgpDto.getProductName());
			// if not exist save
			if (!optionalPGP.isPresent()) {
				Optional<ProductGroup> optionalGroup = productGroups.stream()
						.filter(pl -> pgpDto.getGroupName().equals(pl.getName())).findAny();
				Optional<ProductProfile> optionalpp = productProfiles.stream()
						.filter(pl -> pgpDto.getProductName().equals(pl.getName())).findAny();

				if (optAliasToName.isPresent() && optAliasToName.get().getValue().equalsIgnoreCase("true")) {

					optionalpp = productProfiles.stream()
							.filter(pl -> pl.getAlias() != null && !pl.getAlias().equals("")
									? pgpDto.getProductName().equals(pl.getAlias())
									: pgpDto.getProductName().equals(pl.getName()))
							.findAny();

				}

				if (optionalpp.isPresent() && optionalGroup.isPresent()) {
					ProductGroupProduct pgp = new ProductGroupProduct();
					pgp.setProductGroup(optionalGroup.get());
					pgp.setProduct(optionalpp.get());
					pgp.setCompany(company);
					pgp.setActivated(true);
					saveUpdateProductGroupProducts.add(pgp);
				}
			} else {
				optionalPGP.get().setActivated(true);
				saveUpdateProductGroupProducts.add(optionalPGP.get());
			}
		}
//		saveUpdateProductGroupProducts.forEach(data -> {
//			if(!data.getProductGroup().getName().contains("~")) {
//				data.getProductGroup().setName(data.getProductGroup().getName() + "~" + data.getProductGroup().getProductGroupId());
//			}
//		});
		bulkOperationRepositoryCustom.bulkSaveProductGroupProductProfile(saveUpdateProductGroupProducts);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		//update name
//				List<ProductProfile> productProfilesCopy = productProfileRepository
//						.findByCompanyIdAndActivatedTrue(companyId);
//				if(!productProfilesCopy.isEmpty()) {
//					productProfilesCopy.forEach(data -> {
//						if(!data.getName().contains("~")) {
//						data.setName(data.getName() + "~" + data.getProductId());
//						}
//					});}
//				productProfileRepository.save(productProfilesCopy);
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}
	
	@Transactional
	@Async
	public void saveDeleteProductGroupProduct(final List<TPProductGroupProductDTO> productGroupProductDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = syncOperation.getCompany().getId();

		Set<ProductGroupProduct> saveUpdateProductGroupProducts = new HashSet<>();
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(companyId);
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.ALIAS_TO_NAME);

		for (TPProductGroupProductDTO pgpDto : productGroupProductDTOs) {
			Optional<ProductGroup> optionalGroup = productGroups.stream()
					.filter(pl -> pgpDto.getGroupName().equals(pl.getName())).findAny();
			Optional<ProductProfile> optionalpp = productProfiles.stream()
					.filter(pl -> pgpDto.getProductName().equals(pl.getName())).findAny();

			if (optAliasToName.isPresent() && optAliasToName.get().getValue().equalsIgnoreCase("true")) {
				optionalpp = productProfiles.stream()
						.filter(pl -> pl.getAlias() != null && !pl.getAlias().equals("")
								? pgpDto.getProductName().equals(pl.getAlias())
								: pgpDto.getProductName().equals(pl.getName()))
						.findAny();

			}

			if (optionalpp.isPresent() && optionalGroup.isPresent()) {
				ProductGroupProduct pgp = new ProductGroupProduct();
				pgp.setProductGroup(optionalGroup.get());
				pgp.setProduct(optionalpp.get());
				pgp.setCompany(company);
				pgp.setActivated(true);
				saveUpdateProductGroupProducts.add(pgp);
			}
		}
		bulkOperationRepositoryCustom.bulkSaveProductGroupProductProfile(saveUpdateProductGroupProducts);
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
	public void saveDeleteProductGroupProductUpdateId(final List<TPProductGroupProductDTO> productGroupProductDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = syncOperation.getCompany().getId();
		Set<ProductGroupProduct> saveUpdateProductGroupProducts = new HashSet<>();
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(companyId);
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
		productGroups.forEach(data -> {
			String[] name = data.getName().split("~");
			data.setName(name[0]);
		});
		productProfiles.forEach(data -> {
			String[] name = data.getName().split("~");
			data.setName(name[0]);
		});
		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.ALIAS_TO_NAME);

		for (TPProductGroupProductDTO pgpDto : productGroupProductDTOs) {
			Optional<ProductGroup> optionalGroup = productGroups.stream()
					.filter(pl -> pgpDto.getGroupName().equals(pl.getName())).findAny();
			Optional<ProductProfile> optionalpp = productProfiles.stream()
					.filter(pl -> pgpDto.getProductName().equals(pl.getName())).findAny();

			if (optAliasToName.isPresent() && optAliasToName.get().getValue().equalsIgnoreCase("true")) {
				optionalpp = productProfiles.stream()
						.filter(pl -> pl.getAlias() != null && !pl.getAlias().equals("")
								? pgpDto.getProductName().equals(pl.getAlias())
								: pgpDto.getProductName().equals(pl.getName()))
						.findAny();

			}

			if (optionalpp.isPresent() && optionalGroup.isPresent()) {
				ProductGroupProduct pgp = new ProductGroupProduct();
				pgp.setProductGroup(optionalGroup.get());
				pgp.setProduct(optionalpp.get());
				pgp.setCompany(company);
				pgp.setActivated(true);
				saveUpdateProductGroupProducts.add(pgp);
			}
		}
		
		saveUpdateProductGroupProducts.forEach(data -> {
			if(!data.getProductGroup().getName().contains("~")) {
				data.getProductGroup().setName(data.getProductGroup().getName() + "~" + data.getProductGroup().getProductGroupId());
			}
		});
		productGroupProductRepository.save(saveUpdateProductGroupProducts);
//		bulkOperationRepositoryCustom.bulkSaveProductGroupProductProfile(saveUpdateProductGroupProducts);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		List<ProductProfile> productProfilesCopy = productProfileRepository
				.findByCompanyIdAndActivatedTrue(companyId);
		if(!productProfilesCopy.isEmpty()) {
			productProfilesCopy.forEach(data -> {
				if(!data.getName().contains("~")) {
				data.setName(data.getName() + "~" + data.getProductId());
				}
			});}
		productProfileRepository.save(productProfilesCopy);
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}
	
	@Transactional
	@Async
	public void saveDeleteProductGroupProductUpdateIdNew(final List<TPProductGroupProductDTO> productGroupProductDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = syncOperation.getCompany().getId();
		Set<ProductGroupProduct> saveUpdateProductGroupProducts = new HashSet<>();
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(companyId);
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
//		productGroups.forEach(data -> {
//			String[] name = data.getName().split("~");
//			data.setName(name[0]);
//		});
//		productProfiles.forEach(data -> {
//			String[] name = data.getName().split("~");
//			data.setName(name[0]);
//		});
		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.ALIAS_TO_NAME);

		for (TPProductGroupProductDTO pgpDto : productGroupProductDTOs) {
			Optional<ProductGroup> optionalGroup = productGroups.stream()
					.filter(pl -> pgpDto.getGroupName().equals(pl.getName())).findAny();
			Optional<ProductProfile> optionalpp = productProfiles.stream()
					.filter(pl -> pgpDto.getProductName().equals(pl.getName())).findAny();

			if (optAliasToName.isPresent() && optAliasToName.get().getValue().equalsIgnoreCase("true")) {
				optionalpp = productProfiles.stream()
						.filter(pl -> pl.getAlias() != null && !pl.getAlias().equals("")
								? pgpDto.getProductName().equals(pl.getAlias())
								: pgpDto.getProductName().equals(pl.getName()))
						.findAny();

			}

			if (optionalpp.isPresent() && optionalGroup.isPresent()) {
				ProductGroupProduct pgp = new ProductGroupProduct();
				pgp.setProductGroup(optionalGroup.get());
				pgp.setProduct(optionalpp.get());
				pgp.setCompany(company);
				pgp.setActivated(true);
				saveUpdateProductGroupProducts.add(pgp);
			}
		}
		
//		saveUpdateProductGroupProducts.forEach(data -> {
//			if(!data.getProductGroup().getName().contains("~")) {
//				data.getProductGroup().setName(data.getProductGroup().getName() + "~" + data.getProductGroup().getProductGroupId());
//			}
//		});
		productGroupProductRepository.save(saveUpdateProductGroupProducts);
//		bulkOperationRepositoryCustom.bulkSaveProductGroupProductProfile(saveUpdateProductGroupProducts);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
//		List<ProductProfile> productProfilesCopy = productProfileRepository
//				.findByCompanyIdAndActivatedTrue(companyId);
//		if(!productProfilesCopy.isEmpty()) {
//			productProfilesCopy.forEach(data -> {
//				if(!data.getName().contains("~")) {
//				data.setName(data.getName() + "~" + data.getProductId());
//				}
//			});}
//		productProfileRepository.save(productProfilesCopy);
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
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
				stockLocation.setName(locDto.getName());
				stockLocation.setStockLocationType(StockLocationType.ACTUAL);
				stockLocation.setCompany(company);
			}
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
	@Async
	public void saveUpdateOpeningStock(final List<OpeningStockDTO> openingStockDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = company.getId();
		Set<OpeningStock> saveOpeningStocks = new HashSet<>();
		// All opening-stock must have a stock-location, if not, set a default
		// one
		StockLocation defaultStockLocation = stockLocationRepository.findFirstByCompanyId(company.getId());
		// find all exist product profiles
		Set<String> ppNames = openingStockDTOs.stream().map(os -> os.getProductProfileName())
				.collect(Collectors.toSet());

		List<StockLocation> StockLocations = stockLocationService.findAllStockLocationByCompanyId(companyId);

		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.ALIAS_TO_NAME);

//		List<ProductProfile> productProfiles = productProfileRepository
//				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);

		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
		// delete all opening stock
		openingStockRepository.deleteByCompanyId(company.getId());
		for (OpeningStockDTO osDto : openingStockDTOs) {

			Optional<ProductProfile> optionalpp = productProfiles.stream()
					.filter(pp -> pp.getName().equals(osDto.getProductProfileName())).findAny();
			if (optAliasToName.isPresent() && optAliasToName.get().getValue().equalsIgnoreCase("true")) {

				optionalpp = productProfiles.stream()
						.filter(pl -> pl.getAlias() != null && !pl.getAlias().equals("")
								? osDto.getProductProfileName().equals(pl.getAlias())
								: osDto.getProductProfileName().equals(pl.getName()))
						.findAny();

			}

			// only save if account profile exist
//			productProfiles.stream().filter(pp -> pp.getName().equals(osDto.getProductProfileName())).findAny()
//					.ifPresent(pp -> {

			if (optionalpp.isPresent()) {
				OpeningStock openingStock = new OpeningStock();
				openingStock.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid()); // set
				openingStock.setOpeningStockDate(LocalDateTime.now());
				openingStock.setCreatedDate(LocalDateTime.now());
				openingStock.setCompany(company);
				openingStock.setProductProfile(optionalpp.get());

				if (osDto.getStockLocationName() == null) {
					openingStock.setStockLocation(defaultStockLocation);
				} else {
					// stock location
					Optional<StockLocation> optionalStockLocation = StockLocations.stream()
							.filter(pl -> osDto.getStockLocationName().equals(pl.getName())).findAny();
					if (optionalStockLocation.isPresent()) {
						openingStock.setStockLocation(optionalStockLocation.get());
					} else {
						openingStock.setStockLocation(defaultStockLocation);
					}
				}
				openingStock.setBatchNumber(osDto.getBatchNumber());
				openingStock.setQuantity(osDto.getQuantity());
				saveOpeningStocks.add(openingStock);
			}
		}
		bulkOperationRepositoryCustom.bulkSaveOpeningStocks(saveOpeningStocks);
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
	public void saveUpdateOpeningStockUpadetId(final List<OpeningStockDTO> openingStockDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = company.getId();
		Set<OpeningStock> saveOpeningStocks = new HashSet<>();
		// All opening-stock must have a stock-location, if not, set a default
		// one
		StockLocation defaultStockLocation = stockLocationRepository.findFirstByCompanyId(company.getId());
		// find all exist product profiles
		Set<String> ppNames = openingStockDTOs.stream().map(os -> os.getProductProfileName())
				.collect(Collectors.toSet());

		List<StockLocation> StockLocations = stockLocationService.findAllStockLocationByCompanyId(companyId);

		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.ALIAS_TO_NAME);

//		List<ProductProfile> productProfiles = productProfileRepository
//				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);

		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
		productProfiles.forEach(data -> {
			String[] name = data.getName().split("~");
			data.setName(name[0]);
		});
		// delete all opening stock
		openingStockRepository.deleteByCompanyId(company.getId());
		for (OpeningStockDTO osDto : openingStockDTOs) {

			Optional<ProductProfile> optionalpp = productProfiles.stream()
					.filter(pp -> pp.getName().equals(osDto.getProductProfileName())).findAny();
			if (optAliasToName.isPresent() && optAliasToName.get().getValue().equalsIgnoreCase("true")) {

				optionalpp = productProfiles.stream()
						.filter(pl -> pl.getAlias() != null && !pl.getAlias().equals("")
								? osDto.getProductProfileName().equals(pl.getAlias())
								: osDto.getProductProfileName().equals(pl.getName()))
						.findAny();

			}

			// only save if account profile exist
//			productProfiles.stream().filter(pp -> pp.getName().equals(osDto.getProductProfileName())).findAny()
//					.ifPresent(pp -> {

			if (optionalpp.isPresent()) {
				OpeningStock openingStock = new OpeningStock();
				openingStock.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid()); // set
				openingStock.setOpeningStockDate(LocalDateTime.now());
				openingStock.setCreatedDate(LocalDateTime.now());
				openingStock.setCompany(company);
				openingStock.setProductProfile(optionalpp.get());

				if (osDto.getStockLocationName() == null) {
					openingStock.setStockLocation(defaultStockLocation);
				} else {
					// stock location
					Optional<StockLocation> optionalStockLocation = StockLocations.stream()
							.filter(pl -> osDto.getStockLocationName().equals(pl.getName())).findAny();
					if (optionalStockLocation.isPresent()) {
						openingStock.setStockLocation(optionalStockLocation.get());
					} else {
						openingStock.setStockLocation(defaultStockLocation);
					}
				}
				
				openingStock.setBatchNumber(osDto.getBatchNumber());
				openingStock.setQuantity(osDto.getQuantity());
				saveOpeningStocks.add(openingStock);
			}
		}
		
		bulkOperationRepositoryCustom.bulkSaveOpeningStocks(saveOpeningStocks);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		//update name
		List<ProductProfile> productProfilesCopy = productProfileRepository
				.findByCompanyIdAndActivatedTrue(companyId);
		if(!productProfilesCopy.isEmpty()) {
			productProfilesCopy.forEach(data -> {
				if(!data.getName().contains("~")) {
				data.setName(data.getName() + "~" + data.getProductId());
				}
			});}
		productProfileRepository.save(productProfilesCopy);
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}
	
	@Transactional
	@Async
	public void saveUpdateOpeningStockUpadetIdNew(final List<OpeningStockDTO> openingStockDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = company.getId();
		Set<OpeningStock> saveOpeningStocks = new HashSet<>();
		// All opening-stock must have a stock-location, if not, set a default
		// one
		StockLocation defaultStockLocation = stockLocationRepository.findFirstByCompanyId(company.getId());
		// find all exist product profiles
		Set<String> ppNames = openingStockDTOs.stream().map(os -> os.getProductProfileName())
				.collect(Collectors.toSet());

		List<StockLocation> StockLocations = stockLocationService.findAllStockLocationByCompanyId(companyId);

		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.ALIAS_TO_NAME);

//		List<ProductProfile> productProfiles = productProfileRepository
//				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);

		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
//		productProfiles.forEach(data -> {
//			String[] name = data.getName().split("~");
//			data.setName(name[0]);
//		});
		// delete all opening stock
		openingStockRepository.deleteByCompanyId(company.getId());
		for (OpeningStockDTO osDto : openingStockDTOs) {

			Optional<ProductProfile> optionalpp = productProfiles.stream()
					.filter(pp -> pp.getName().equals(osDto.getProductProfileName())).findAny();
			if (optAliasToName.isPresent() && optAliasToName.get().getValue().equalsIgnoreCase("true")) {

				optionalpp = productProfiles.stream()
						.filter(pl -> pl.getAlias() != null && !pl.getAlias().equals("")
								? osDto.getProductProfileName().equals(pl.getAlias())
								: osDto.getProductProfileName().equals(pl.getName()))
						.findAny();

			}

			// only save if account profile exist
//			productProfiles.stream().filter(pp -> pp.getName().equals(osDto.getProductProfileName())).findAny()
//					.ifPresent(pp -> {

			if (optionalpp.isPresent()) {
				OpeningStock openingStock = new OpeningStock();
				openingStock.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid()); // set
				openingStock.setOpeningStockDate(LocalDateTime.now());
				openingStock.setCreatedDate(LocalDateTime.now());
				openingStock.setCompany(company);
				openingStock.setProductProfile(optionalpp.get());

				if (osDto.getStockLocationName() == null) {
					openingStock.setStockLocation(defaultStockLocation);
				} else {
					// stock location
					Optional<StockLocation> optionalStockLocation = StockLocations.stream()
							.filter(pl -> osDto.getStockLocationName().equals(pl.getName())).findAny();
					if (optionalStockLocation.isPresent()) {
						openingStock.setStockLocation(optionalStockLocation.get());
					} else {
						openingStock.setStockLocation(defaultStockLocation);
					}
				}
				
				openingStock.setBatchNumber(osDto.getBatchNumber());
				openingStock.setQuantity(osDto.getQuantity());
				saveOpeningStocks.add(openingStock);
			}
		}
		
		bulkOperationRepositoryCustom.bulkSaveOpeningStocks(saveOpeningStocks);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		//update name
//		List<ProductProfile> productProfilesCopy = productProfileRepository
//				.findByCompanyIdAndActivatedTrue(companyId);
//		if(!productProfilesCopy.isEmpty()) {
//			productProfilesCopy.forEach(data -> {
//				if(!data.getName().contains("~")) {
//				data.setName(data.getName() + "~" + data.getProductId());
//				}
//			});}
//		productProfileRepository.save(productProfilesCopy);
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}
	
	@Transactional
	@Async
	public void saveUpdateTemporaryOpeningStockUpdatedId(final List<OpeningStockDTO> openingStockDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = company.getId();
		Set<TemporaryOpeningStock> saveOpeningStocks = new HashSet<>();
		// All opening-stock must have a stock-location, if not, set a default
		// one
		StockLocation defaultStockLocation = stockLocationRepository.findFirstByCompanyId(company.getId());
		// find all exist product profiles
		Set<String> ppNames = openingStockDTOs.stream().map(os -> os.getProductProfileName())
				.collect(Collectors.toSet());

		List<StockLocation> StockLocations = stockLocationService.findAllStockLocationByCompanyId(companyId);

		List<ProductProfile> productProfiles = productProfileRepository
				.findAllByCompanyIdActivatedTrue();
		productProfiles.forEach(data -> {
			String[] name = data.getName().split("~");
			data.setName(name[0]);
		});
		// delete all opening stock
		temporaryOpeningStockRepository.deleteByCompanyId(company.getId());
		for (OpeningStockDTO osDto : openingStockDTOs) {
			// only save if account profile exist
			productProfiles.stream().filter(pp -> pp.getName().equals(osDto.getProductProfileName())).findAny()
					.ifPresent(pp -> {
						TemporaryOpeningStock openingStock = new TemporaryOpeningStock();
						openingStock.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid()); // set
						openingStock.setOpeningStockDate(LocalDateTime.now());
						openingStock.setCreatedDate(LocalDateTime.now());
						openingStock.setCompany(company);
						openingStock.setProductProfile(pp);

						if (osDto.getStockLocationName() == null) {
							openingStock.setStockLocation(defaultStockLocation);
						} else {
							// stock location
							Optional<StockLocation> optionalStockLocation = StockLocations.stream()
									.filter(pl -> osDto.getStockLocationName().equals(pl.getName())).findAny();
							if (optionalStockLocation.isPresent()) {
								openingStock.setStockLocation(optionalStockLocation.get());
							} else {
								openingStock.setStockLocation(defaultStockLocation);
							}
						}
						openingStock.setBatchNumber(osDto.getBatchNumber());
						openingStock.setQuantity(osDto.getQuantity());
						saveOpeningStocks.add(openingStock);
					});
		}
		bulkOperationRepositoryCustom.bulkSaveTemporaryOpeningStocks(saveOpeningStocks);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		//update name
				List<ProductProfile> productProfilesCopy = productProfileRepository
						.findByCompanyIdAndActivatedTrue(companyId);
				if(!productProfilesCopy.isEmpty()) {
					productProfilesCopy.forEach(data -> {
						if(!data.getName().contains("~")) {
						data.setName(data.getName() + "~" + data.getProductId());
						}
					});}
				productProfileRepository.save(productProfilesCopy);
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}
	
	@Transactional
	@Async
	public void saveUpdateTemporaryOpeningStockUpdatedIdNew(final List<OpeningStockDTO> openingStockDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = company.getId();
		Set<TemporaryOpeningStock> saveOpeningStocks = new HashSet<>();
		// All opening-stock must have a stock-location, if not, set a default
		// one
		StockLocation defaultStockLocation = stockLocationRepository.findFirstByCompanyId(company.getId());
		// find all exist product profiles
		Set<String> ppNames = openingStockDTOs.stream().map(os -> os.getProductProfileName())
				.collect(Collectors.toSet());

		List<StockLocation> StockLocations = stockLocationService.findAllStockLocationByCompanyId(companyId);

		List<ProductProfile> productProfiles = productProfileRepository
				.findAllByCompanyIdActivatedTrue();
//		productProfiles.forEach(data -> {
//			String[] name = data.getName().split("~");
//			data.setName(name[0]);
//		});
		// delete all opening stock
		temporaryOpeningStockRepository.deleteByCompanyId(company.getId());
		for (OpeningStockDTO osDto : openingStockDTOs) {
			// only save if account profile exist
			productProfiles.stream().filter(pp -> pp.getName().equals(osDto.getProductProfileName())).findAny()
					.ifPresent(pp -> {
						TemporaryOpeningStock openingStock = new TemporaryOpeningStock();
						openingStock.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid()); // set
						openingStock.setOpeningStockDate(LocalDateTime.now());
						openingStock.setCreatedDate(LocalDateTime.now());
						openingStock.setCompany(company);
						openingStock.setProductProfile(pp);

						if (osDto.getStockLocationName() == null) {
							openingStock.setStockLocation(defaultStockLocation);
						} else {
							// stock location
							Optional<StockLocation> optionalStockLocation = StockLocations.stream()
									.filter(pl -> osDto.getStockLocationName().equals(pl.getName())).findAny();
							if (optionalStockLocation.isPresent()) {
								openingStock.setStockLocation(optionalStockLocation.get());
							} else {
								openingStock.setStockLocation(defaultStockLocation);
							}
						}
						openingStock.setBatchNumber(osDto.getBatchNumber());
						openingStock.setQuantity(osDto.getQuantity());
						saveOpeningStocks.add(openingStock);
					});
		}
		bulkOperationRepositoryCustom.bulkSaveTemporaryOpeningStocks(saveOpeningStocks);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		//update name
//				List<ProductProfile> productProfilesCopy = productProfileRepository
//						.findByCompanyIdAndActivatedTrue(companyId);
//				if(!productProfilesCopy.isEmpty()) {
//					productProfilesCopy.forEach(data -> {
//						if(!data.getName().contains("~")) {
//						data.setName(data.getName() + "~" + data.getProductId());
//						}
//					});}
//				productProfileRepository.save(productProfilesCopy);
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}
	
	@Transactional
	@Async
	public void saveUpdateTemporaryOpeningStock(final List<OpeningStockDTO> openingStockDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = company.getId();
		Set<TemporaryOpeningStock> saveOpeningStocks = new HashSet<>();
		// All opening-stock must have a stock-location, if not, set a default
		// one
		StockLocation defaultStockLocation = stockLocationRepository.findFirstByCompanyId(company.getId());
		// find all exist product profiles
		Set<String> ppNames = openingStockDTOs.stream().map(os -> os.getProductProfileName())
				.collect(Collectors.toSet());

		List<StockLocation> StockLocations = stockLocationService.findAllStockLocationByCompanyId(companyId);

		List<ProductProfile> productProfiles = productProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);
		// delete all opening stock
		temporaryOpeningStockRepository.deleteByCompanyId(company.getId());
		for (OpeningStockDTO osDto : openingStockDTOs) {
			// only save if account profile exist
			productProfiles.stream().filter(pp -> pp.getName().equals(osDto.getProductProfileName())).findAny()
					.ifPresent(pp -> {
						TemporaryOpeningStock openingStock = new TemporaryOpeningStock();
						openingStock.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid()); // set
						openingStock.setOpeningStockDate(LocalDateTime.now());
						openingStock.setCreatedDate(LocalDateTime.now());
						openingStock.setCompany(company);
						openingStock.setProductProfile(pp);

						if (osDto.getStockLocationName() == null) {
							openingStock.setStockLocation(defaultStockLocation);
						} else {
							// stock location
							Optional<StockLocation> optionalStockLocation = StockLocations.stream()
									.filter(pl -> osDto.getStockLocationName().equals(pl.getName())).findAny();
							if (optionalStockLocation.isPresent()) {
								openingStock.setStockLocation(optionalStockLocation.get());
							} else {
								openingStock.setStockLocation(defaultStockLocation);
							}
						}
						openingStock.setBatchNumber(osDto.getBatchNumber());
						openingStock.setQuantity(osDto.getQuantity());
						if(osDto.getQuantity() != 0) {
							saveOpeningStocks.add(openingStock);
						}
					});
		}
		bulkOperationRepositoryCustom.bulkSaveTemporaryOpeningStocks(saveOpeningStocks);
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
	public void saveUpdatePriceLevels(final List<PriceLevelDTO> priceLevelDTOs, final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<PriceLevel> saveUpdatePriceLevels = new HashSet<>();
		// find all priceLevels
		List<PriceLevel> priceLevels = priceLevelRepository.findByCompanyId(company.getId());
		for (PriceLevelDTO plDto : priceLevelDTOs) {
			// check exist by name, only one exist with a name
			Optional<PriceLevel> optionalPl = priceLevels.stream().filter(pl -> pl.getName().equals(plDto.getName()))
					.findAny();
			PriceLevel priceLevel;
			if (optionalPl.isPresent()) {
				priceLevel = optionalPl.get();
				// if not update, skip this iteration.
				// if (!priceLevel.getThirdpartyUpdate()) {continue;}
			} else {
				priceLevel = new PriceLevel();
				priceLevel.setPid(PriceLevelService.PID_PREFIX + RandomUtil.generatePid());
				priceLevel.setName(plDto.getName());
				priceLevel.setCompany(company);
			}
			priceLevel.setActivated(plDto.getActivated());
			saveUpdatePriceLevels.add(priceLevel);
		}
		bulkOperationRepositoryCustom.bulkSaveUpdatePriceLevels(saveUpdatePriceLevels);
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
	public void saveUpdatePriceLevelList(final List<PriceLevelListDTO> priceLevelListDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = company.getId();
		Set<PriceLevelList> saveUpdatePriceLevelLists = new HashSet<>();
		// find all exist priceLevels
		Set<String> plNames = priceLevelListDTOs.stream().map(pll -> pll.getPriceLevelName())
				.collect(Collectors.toSet());

		Set<String> ppNames = priceLevelListDTOs.stream().map(pll -> pll.getProductProfileName())
				.collect(Collectors.toSet());

		List<PriceLevelList> priceLevelLists = priceLevelListRepository
				.findByCompanyIdAndPriceLevelNameIgnoreCaseIn(company.getId(), plNames);
		// temporary variables for storing db data for product profile and price
		// level
		Map<String, ProductProfile> tempProductProfile = new HashMap<>();
		List<PriceLevel> tempPriceLevel = priceLevelRepository.findByCompanyId(companyId);

		List<ProductProfile> productProfiles = productProfileRepository.findByCompanyIdAndNameIn(companyId, ppNames);

		for (PriceLevelListDTO pllDto : priceLevelListDTOs) {
			// check exist by price-level name and product-profile name, only
			// one exist with a both same for a company
			Optional<PriceLevelList> optionalPll = priceLevelLists.stream()
					.filter(pll -> pll.getPriceLevel().getName().equals(pllDto.getPriceLevelName())
							&& pll.getProductProfile().getName().equals(pllDto.getProductProfileName()))
					.findAny();
			PriceLevelList priceLevelList;
			if (optionalPll.isPresent()) {
				priceLevelList = optionalPll.get();
				// if not update, skip this iteration.
				// if (!priceLevel.getThirdpartyUpdate()) {continue;}
			} else {
				priceLevelList = new PriceLevelList();
				priceLevelList.setPid(PriceLevelListService.PID_PREFIX + RandomUtil.generatePid());
				priceLevelList.setCompany(company);
			}
			// set product profile and price level, if present
			Optional<ProductProfile> optionalPP;
			if (tempProductProfile.containsKey(pllDto.getProductProfileName())) {
				optionalPP = Optional.of(tempProductProfile.get(pllDto.getProductProfileName()));
			} else {
				optionalPP = productProfiles.stream().filter(pl -> pllDto.getProductProfileName().equals(pl.getName()))
						.findAny();
			}
			Optional<PriceLevel> optionalPL = tempPriceLevel.stream()
					.filter(pl -> pllDto.getPriceLevelName().equals(pl.getName())).findAny();
			if (optionalPP.isPresent() && optionalPL.isPresent()) {
				priceLevelList.setProductProfile(optionalPP.get());
				// put it in map for later access
				tempProductProfile.put(pllDto.getProductProfileName(), optionalPP.get());
				priceLevelList.setPriceLevel(optionalPL.get());
				priceLevelList.setPrice(pllDto.getPrice());
				priceLevelList.setRangeFrom(pllDto.getRangeFrom());
				priceLevelList.setRangeTo(pllDto.getRangeTo());
				saveUpdatePriceLevelLists.add(priceLevelList);
			}
		}
		bulkOperationRepositoryCustom.bulkSaveUpdatePriceLevelLists(saveUpdatePriceLevelLists);
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
	public void saveUpdatePriceLevelListUpdatedId(final List<PriceLevelListDTO> priceLevelListDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = company.getId();
		Set<PriceLevelList> saveUpdatePriceLevelLists = new HashSet<>();
		// find all exist priceLevels
		Set<String> plNames = priceLevelListDTOs.stream().map(pll -> pll.getPriceLevelName())
				.collect(Collectors.toSet());

//		Set<String> ppNames = priceLevelListDTOs.stream().map(pll -> pll.getProductProfileName())
//				.collect(Collectors.toSet());

		List<PriceLevelList> priceLevelLists = priceLevelListRepository
				.findByCompanyIdAndPriceLevelNameIgnoreCaseIn(company.getId(), plNames);
		// temporary variables for storing db data for product profile and price
		// level
		Map<String, ProductProfile> tempProductProfile = new HashMap<>();
		List<PriceLevel> tempPriceLevel = priceLevelRepository.findByCompanyId(companyId);

		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
		productProfiles.forEach(data -> {
			String[] name = data.getName().split("~");
			data.setName(name[0]);
		});

		for (PriceLevelListDTO pllDto : priceLevelListDTOs) {
			// check exist by price-level name and product-profile name, only
			// one exist with a both same for a company
			Optional<PriceLevelList> optionalPll = priceLevelLists.stream()
					.filter(pll -> pll.getPriceLevel().getName().equals(pllDto.getPriceLevelName())
							&& pll.getProductProfile().getName().equals(pllDto.getProductProfileName()))
					.findAny();
			PriceLevelList priceLevelList;
			if (optionalPll.isPresent()) {
				priceLevelList = optionalPll.get();
				// if not update, skip this iteration.
				// if (!priceLevel.getThirdpartyUpdate()) {continue;}
			} else {
				priceLevelList = new PriceLevelList();
				priceLevelList.setPid(PriceLevelListService.PID_PREFIX + RandomUtil.generatePid());
				priceLevelList.setCompany(company);
			}
			// set product profile and price level, if present
			Optional<ProductProfile> optionalPP;
			if (tempProductProfile.containsKey(pllDto.getProductProfileName())) {
				optionalPP = Optional.of(tempProductProfile.get(pllDto.getProductProfileName()));
			} else {
				optionalPP = productProfiles.stream().filter(pl -> pllDto.getProductProfileName().equals(pl.getName()))
						.findAny();
			}
			Optional<PriceLevel> optionalPL = tempPriceLevel.stream()
					.filter(pl -> pllDto.getPriceLevelName().equals(pl.getName())).findAny();
			if (optionalPP.isPresent() && optionalPL.isPresent()) {
				priceLevelList.setProductProfile(optionalPP.get());
				// put it in map for later access
				tempProductProfile.put(pllDto.getProductProfileName(), optionalPP.get());
				priceLevelList.setPriceLevel(optionalPL.get());
				priceLevelList.setPrice(pllDto.getPrice());
				priceLevelList.setRangeFrom(pllDto.getRangeFrom());
				priceLevelList.setRangeTo(pllDto.getRangeTo());
				saveUpdatePriceLevelLists.add(priceLevelList);
			}
		}
		bulkOperationRepositoryCustom.bulkSaveUpdatePriceLevelLists(saveUpdatePriceLevelLists);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		//setName
		List<ProductProfile> productProfilesCopy = productProfileRepository
				.findByCompanyIdAndActivatedTrue(companyId);
		if(!productProfilesCopy.isEmpty()) {
			productProfilesCopy.forEach(data -> {
				if(!data.getName().contains("~")) {
				data.setName(data.getName() + "~" + data.getProductId());
				}
			});}
		productProfileRepository.save(productProfilesCopy);
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}
	
	@Transactional
	@Async
	public void saveUpdatePriceLevelListUpdatedIdNew(final List<PriceLevelListDTO> priceLevelListDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		final Long companyId = company.getId();
		Set<PriceLevelList> saveUpdatePriceLevelLists = new HashSet<>();
		// find all exist priceLevels
		Set<String> plNames = priceLevelListDTOs.stream().map(pll -> pll.getPriceLevelName())
				.collect(Collectors.toSet());

//		Set<String> ppNames = priceLevelListDTOs.stream().map(pll -> pll.getProductProfileName())
//				.collect(Collectors.toSet());

		List<PriceLevelList> priceLevelLists = priceLevelListRepository
				.findByCompanyIdAndPriceLevelNameIgnoreCaseIn(company.getId(), plNames);
		// temporary variables for storing db data for product profile and price
		// level
		Map<String, ProductProfile> tempProductProfile = new HashMap<>();
		List<PriceLevel> tempPriceLevel = priceLevelRepository.findByCompanyId(companyId);

		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
//		productProfiles.forEach(data -> {
//			String[] name = data.getName().split("~");
//			data.setName(name[0]);
//		});

		for (PriceLevelListDTO pllDto : priceLevelListDTOs) {
			// check exist by price-level name and product-profile name, only
			// one exist with a both same for a company
			Optional<PriceLevelList> optionalPll = priceLevelLists.stream()
					.filter(pll -> pll.getPriceLevel().getName().equals(pllDto.getPriceLevelName())
							&& pll.getProductProfile().getName().equals(pllDto.getProductProfileName()))
					.findAny();
			PriceLevelList priceLevelList;
			if (optionalPll.isPresent()) {
				priceLevelList = optionalPll.get();
				// if not update, skip this iteration.
				// if (!priceLevel.getThirdpartyUpdate()) {continue;}
			} else {
				priceLevelList = new PriceLevelList();
				priceLevelList.setPid(PriceLevelListService.PID_PREFIX + RandomUtil.generatePid());
				priceLevelList.setCompany(company);
			}
			// set product profile and price level, if present
			Optional<ProductProfile> optionalPP;
			if (tempProductProfile.containsKey(pllDto.getProductProfileName())) {
				optionalPP = Optional.of(tempProductProfile.get(pllDto.getProductProfileName()));
			} else {
				optionalPP = productProfiles.stream().filter(pl -> pllDto.getProductProfileName().equals(pl.getName()))
						.findAny();
			}
			Optional<PriceLevel> optionalPL = tempPriceLevel.stream()
					.filter(pl -> pllDto.getPriceLevelName().equals(pl.getName())).findAny();
			if (optionalPP.isPresent() && optionalPL.isPresent()) {
				priceLevelList.setProductProfile(optionalPP.get());
				// put it in map for later access
				tempProductProfile.put(pllDto.getProductProfileName(), optionalPP.get());
				priceLevelList.setPriceLevel(optionalPL.get());
				priceLevelList.setPrice(pllDto.getPrice());
				priceLevelList.setRangeFrom(pllDto.getRangeFrom());
				priceLevelList.setRangeTo(pllDto.getRangeTo());
				saveUpdatePriceLevelLists.add(priceLevelList);
			}
		}
		bulkOperationRepositoryCustom.bulkSaveUpdatePriceLevelLists(saveUpdatePriceLevelLists);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		//setName
//		List<ProductProfile> productProfilesCopy = productProfileRepository
//				.findByCompanyIdAndActivatedTrue(companyId);
//		if(!productProfilesCopy.isEmpty()) {
//			productProfilesCopy.forEach(data -> {
//				if(!data.getName().contains("~")) {
//				data.setName(data.getName() + "~" + data.getProductId());
//				}
//			});}
//		productProfileRepository.save(productProfilesCopy);
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}
	
	@Transactional
	@Async
	public void saveUpdateProductWiseDefaultLedger(List<ProductProfileDTO> ppDTOs, SyncOperation syncOperation) {
		log.info("Requst to save Update Product Wise DefaultLedger------"+ppDTOs.size());
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();

		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(company.getId());
		
		for (ProductProfileDTO ppDto : ppDTOs) {
			// check exist by name, only one exist with a name
			
			Optional<ProductProfile> optionalProductProfile = productProfiles.stream()
					.filter(pp -> pp.getProductId() != null && !pp.getProductId().equals("")
					? pp.getProductId().equals(ppDto.getProductId())
					: false)
			.findAny();
			ProductProfile productProfile;
			if (optionalProductProfile.isPresent()) {
				
				productProfile = optionalProductProfile.get();
//				pp.setTaxRate(ppDto.getTaxRate());
				productProfile.setDefaultLedger(ppDto.getDefaultLedger());
				saveUpdateProductProfiles.add(productProfile);
			}
		}
		
		bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);
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
	public void saveUpdateGSTProductGroup(List<GSTProductGroupDTO> gstpgDTOs, SyncOperation syncOperation) {

		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<GSTProductGroup> saveUpdategstpg = new HashSet<>();
		for (GSTProductGroupDTO ppDto : gstpgDTOs) {
			// check exist by name, only one exist with a name
			Optional<ProductGroup> pg = productGroupRepository.findByCompanyIdAndNameIgnoreCase(company.getId(),
					ppDto.getProductGroupName());
			if (pg.isPresent()) {
				GSTProductGroup gstpg = new GSTProductGroup();

				gstpg.setProductGroup(pg.get());
				gstpg.setHsnsacCode(ppDto.getHsnsacCode());
				gstpg.setApplyDate(ppDto.getApplyDate());
				gstpg.setCentralTax(ppDto.getCentralTax());
				gstpg.setStateTax(ppDto.getStateTax());
				gstpg.setIntegratedTax(ppDto.getIntegratedTax());
				gstpg.setAditionalCess(ppDto.getAditionalCess());
				gstpg.setTaxType(ppDto.getTaxType());
				gstpg.setCompany(company);
				saveUpdategstpg.add(gstpg);
			}
		}
		bulkOperationRepositoryCustom.bulkSaveGSTProductGroup(saveUpdategstpg);

		// updateing tax rate to productprofile
		for (GSTProductGroup gstpg : saveUpdategstpg) {
			List<String> productGroups = new ArrayList<>();
			productGroups.add(gstpg.getProductGroup().getPid());
			List<ProductProfile> productProfiles = productGroupProductRepository
					.findProductsByProductGroupPids(productGroups);
			if (productProfiles.size() > 0) {
				double vat = 0;
				if (gstpg.getIntegratedTax() != null && (!gstpg.getIntegratedTax().equalsIgnoreCase(""))) {
					vat = Double.valueOf(gstpg.getIntegratedTax().replace("%", ""));
				}
				productProfileRepository.updateTaxRate(vat, productProfiles);
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
	public void saveUpdateGSTProductGroupUpdatedId(List<GSTProductGroupDTO> gstpgDTOs, SyncOperation syncOperation) {

		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<GSTProductGroup> saveUpdategstpg = new HashSet<>();
		List<ProductGroup> productGroup = productGroupRepository.findAllByCompanyId(true);
		productGroup.forEach(data -> {
			String[] name = data.getName().split("~");
			data.setName(name[0]);
		});
		for (GSTProductGroupDTO ppDto : gstpgDTOs) {
			// check exist by name, only one exist with a name
//			Optional<ProductGroup> pg = productGroupRepository.findByCompanyIdAndNameIgnoreCase(company.getId(),
//					ppDto.getProductGroupName());
			Optional<ProductGroup> pg = productGroup.stream().filter(pgdata -> pgdata.getName().equals(ppDto.getProductGroupName())).findAny();
			if (pg.isPresent()) {
				GSTProductGroup gstpg = new GSTProductGroup();

				gstpg.setProductGroup(pg.get());
				gstpg.setHsnsacCode(ppDto.getHsnsacCode());
				gstpg.setApplyDate(ppDto.getApplyDate());
				gstpg.setCentralTax(ppDto.getCentralTax());
				gstpg.setStateTax(ppDto.getStateTax());
				gstpg.setIntegratedTax(ppDto.getIntegratedTax());
				gstpg.setAditionalCess(ppDto.getAditionalCess());
				gstpg.setTaxType(ppDto.getTaxType());
				gstpg.setCompany(company);
				saveUpdategstpg.add(gstpg);
			}
		}
		bulkOperationRepositoryCustom.bulkSaveGSTProductGroup(saveUpdategstpg);

		// updateing tax rate to productprofile
		for (GSTProductGroup gstpg : saveUpdategstpg) {
			List<String> productGroups = new ArrayList<>();
			productGroups.add(gstpg.getProductGroup().getPid());
			List<ProductProfile> productProfiles = productGroupProductRepository
					.findProductsByProductGroupPids(productGroups);
			if (productProfiles.size() > 0) {
				double vat = 0;
				if (gstpg.getIntegratedTax() != null && (!gstpg.getIntegratedTax().equalsIgnoreCase(""))) {
					vat = Double.valueOf(gstpg.getIntegratedTax().replace("%", ""));
				}
				productProfileRepository.updateTaxRate(vat, productProfiles);
			}
		}
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		//update name
		List<ProductGroup> productGroupcopy = productGroupRepository.findAllByCompanyId(true);
		if(!productGroupcopy.isEmpty()) {
			productGroupcopy.forEach(data -> {
				if(!data.getName().contains("~")) {
				data.setName(data.getName() + "~" + data.getProductGroupId());
				}
			});}
		productGroupRepository.save(productGroupcopy);
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);

	}
	
	@Transactional
	@Async
	public void saveUpdateGSTProductGroupUpdatedIdNew(List<GSTProductGroupDTO> gstpgDTOs, SyncOperation syncOperation) {

		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<GSTProductGroup> saveUpdategstpg = new HashSet<>();
		List<ProductGroup> productGroup = productGroupRepository.findAllByCompanyId(true);
//		productGroup.forEach(data -> {
//			String[] name = data.getName().split("~");
//			data.setName(name[0]);
//		});
		for (GSTProductGroupDTO ppDto : gstpgDTOs) {
			// check exist by name, only one exist with a name
//			Optional<ProductGroup> pg = productGroupRepository.findByCompanyIdAndNameIgnoreCase(company.getId(),
//					ppDto.getProductGroupName());
			Optional<ProductGroup> pg = productGroup.stream().filter(pgdata -> pgdata.getName().equals(ppDto.getProductGroupName())).findAny();
			if (pg.isPresent()) {
				GSTProductGroup gstpg = new GSTProductGroup();

				gstpg.setProductGroup(pg.get());
				gstpg.setHsnsacCode(ppDto.getHsnsacCode());
				gstpg.setApplyDate(ppDto.getApplyDate());
				gstpg.setCentralTax(ppDto.getCentralTax());
				gstpg.setStateTax(ppDto.getStateTax());
				gstpg.setIntegratedTax(ppDto.getIntegratedTax());
				gstpg.setAditionalCess(ppDto.getAditionalCess());
				gstpg.setTaxType(ppDto.getTaxType());
				gstpg.setCompany(company);
				saveUpdategstpg.add(gstpg);
			}
		}
		bulkOperationRepositoryCustom.bulkSaveGSTProductGroup(saveUpdategstpg);

		// updateing tax rate to productprofile
		for (GSTProductGroup gstpg : saveUpdategstpg) {
			List<String> productGroups = new ArrayList<>();
			productGroups.add(gstpg.getProductGroup().getPid());
			List<ProductProfile> productProfiles = productGroupProductRepository
					.findProductsByProductGroupPids(productGroups);
			if (productProfiles.size() > 0) {
				double vat = 0;
				if (gstpg.getIntegratedTax() != null && (!gstpg.getIntegratedTax().equalsIgnoreCase(""))) {
					vat = Double.valueOf(gstpg.getIntegratedTax().replace("%", ""));
				}
				productProfileRepository.updateTaxRate(vat, productProfiles);
			}
		}
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		//update name
//		List<ProductGroup> productGroupcopy = productGroupRepository.findAllByCompanyId(true);
//		if(!productGroupcopy.isEmpty()) {
//			productGroupcopy.forEach(data -> {
//				if(!data.getName().contains("~")) {
//				data.setName(data.getName() + "~" + data.getProductGroupId());
//				}
//			});}
//		productGroupRepository.save(productGroupcopy);
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);

	}
	
	@Transactional
	public void saveUpdateTaxMasters(List<TaxMasterDTO> taxMasterDTOs, SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<TaxMaster> saveUpdateTaxMasters = new HashSet<>();
		// find all taxMasters
		List<TaxMaster> taxMasters = taxMasterRepository.findAllByCompanyId(company.getId());
		for (TaxMasterDTO locDto : taxMasterDTOs) {
			// check exist by name, only one exist with a name
			Optional<TaxMaster> optionalLoc = taxMasters.stream()
					.filter(p -> p.getVatName().equals(locDto.getVatName())).findAny();
			TaxMaster taxMaster;
			if (optionalLoc.isPresent()) {
				taxMaster = optionalLoc.get();
				taxMaster.setDescription(locDto.getDescription());
				taxMaster.setVatClass(locDto.getVatClass());
				taxMaster.setVatPercentage(locDto.getVatPercentage());
				taxMaster.setDescription(locDto.getDescription());

				// if not update, skip this iteration.
				// if (!taxMaster.getThirdpartyUpdate()) {continue;}
			} else {
				taxMaster = new TaxMaster();
				taxMaster = taxMasterMapper.taxMasterDTOToTaxMaster(locDto);
				taxMaster.setPid(TaxMasterService.PID_PREFIX + RandomUtil.generatePid());
				taxMaster.setCompany(company);
			}
			saveUpdateTaxMasters.add(taxMaster);
		}
		bulkOperationRepositoryCustom.bulkSaveTaxMasters(saveUpdateTaxMasters);
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
	public void saveUpdateProductProfileTaxMasters(List<ProductProfileDTO> ppDTOs, SyncOperation syncOperation) {

		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<String> ppNames = ppDTOs.stream().map(p -> p.getName()).collect(Collectors.toSet());
		List<ProductProfile> productProfiles = productProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);

		for (ProductProfileDTO ppDto : ppDTOs) {
			// check exist by name, only one exist with a name
			Optional<ProductProfile> optionalPP = productProfiles.stream()
					.filter(p -> p.getName().equals(ppDto.getName())).findAny();
			ProductProfile productProfile;
			if (optionalPP.isPresent()) {
				productProfile = optionalPP.get();
				List<TaxMaster> taxMasters = new ArrayList<>();
				for (TaxMasterDTO taxMasterDTO : ppDto.getProductProfileTaxMasterDTOs()) {
					TaxMaster taxMaster = taxMasterRepository.findAllByCompanyIdAndVatClassAndVatPercentage(
							company.getId(), taxMasterDTO.getVatClass(), taxMasterDTO.getVatPercentage());
					if (taxMaster != null) {
						taxMasters.add(taxMaster);
					}
				}
				productProfile.setTaxMastersList(taxMasters);
				productProfileRepository.save(productProfile);
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
	public void saveUpdateEcomProductProfiles(List<EcomProductProfileDTO> ppDTOs, SyncOperation syncOperation) {

		Set<EcomProductProfile> saveUpdateEcomProductProfiles = new HashSet<>();

		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<String> ppNames = ppDTOs.stream().map(p -> p.getName()).collect(Collectors.toSet());
		List<EcomProductProfile> productProfiles = ecomProductProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);

		for (EcomProductProfileDTO ppDto : ppDTOs) {
			// check exist by name, only one exist with a name
			Optional<EcomProductProfile> optionalPP = productProfiles.stream()
					.filter(p -> p.getName().equals(ppDto.getName())).findAny();
			EcomProductProfile ecomProductProfile;
			if (optionalPP.isPresent()) {
				ecomProductProfile = optionalPP.get();

			} else {
				ecomProductProfile = ecomProductProfileMapper.ecomProductProfileDTOToEcomProductProfile(ppDto);
				ecomProductProfile.setPid(EcomProductProfileService.PID_PREFIX + RandomUtil.generatePid());
				ecomProductProfile.setCompany(company);

			}
			saveUpdateEcomProductProfiles.add(ecomProductProfile);
		}

		bulkOperationRepositoryCustom.bulkSaveEcomProductProfiles(saveUpdateEcomProductProfiles);

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
	public void saveUpdateEcomProductProfileProducts(List<EcomProductProfileProductDTO> ppDTOs,
			SyncOperation syncOperation) {

		List<EcomProductProfileProduct> saveUpdateEcomProductProfileProducts = new ArrayList<>();

		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<String> ppNames = ppDTOs.stream().map(p -> p.getProductProfileDTO().getName()).collect(Collectors.toSet());
		Set<String> ecomppNames = ppDTOs.stream().map(p -> p.getEcomProductGroupDTO().getName())
				.collect(Collectors.toSet());

		List<EcomProductProfile> ecomProductProfiles = ecomProductProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ecomppNames);

		List<ProductProfile> productProfiles = productProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);

		for (EcomProductProfileProductDTO ppDto : ppDTOs) {
			// check exist by name, only one exist with a name
			Optional<ProductProfile> optionalPP = productProfiles.stream()
					.filter(p -> p.getName().equals(ppDto.getProductProfileDTO().getName())).findAny();
			Optional<EcomProductProfile> optionalEcomPP = ecomProductProfiles.stream()
					.filter(p -> p.getName().equals(ppDto.getEcomProductGroupDTO().getName())).findAny();

			if (optionalPP.isPresent() && optionalEcomPP.isPresent()) {
				// ecomProductProfileProductRepository.sa
				ProductProfile product = optionalPP.get();
				EcomProductProfile ecomProductProfile = optionalEcomPP.get();

				EcomProductProfileProduct ecomProductProfileProduct = new EcomProductProfileProduct(product,
						ecomProductProfile, company);

				saveUpdateEcomProductProfileProducts.add(ecomProductProfileProduct);
				ecomProductProfileProductRepository.deleteByEcomProductProfilePid(ecomProductProfile.getPid());
			}
		}

		bulkOperationRepositoryCustom.bulkSaveEcomProductProfileProducts(saveUpdateEcomProductProfileProducts);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}

	public void saveUpdateProductGroupEcomProductProfiles(List<ProductGroupEcomProductDTO> ppDTOs,
			SyncOperation syncOperation) {
		List<ProductGroupEcomProduct> saveUpdateProductGroupEcomProducts = new ArrayList<>();

		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(company.getId());

		for (ProductGroupEcomProductDTO productGroupEcomProductDTO : ppDTOs) {

			Set<String> ecomppNames = productGroupEcomProductDTO.getEcomProductProfileDTOs().stream()
					.map(p -> p.getName()).collect(Collectors.toSet());

			List<EcomProductProfile> ecomProductProfiles = ecomProductProfileRepository
					.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ecomppNames);

			Optional<ProductGroup> optionalPG = productGroups.stream()
					.filter(p -> p.getName().equals(productGroupEcomProductDTO.getProductGroupDTO().getName()))
					.findAny();

			if (optionalPG.isPresent() && ecomProductProfiles.size() > 0) {
				for (EcomProductProfile ecomProductProfile : ecomProductProfiles) {
					ProductGroupEcomProduct productGroupEcomProduct = new ProductGroupEcomProduct(ecomProductProfile,
							optionalPG.get(), company);
					saveUpdateProductGroupEcomProducts.add(productGroupEcomProduct);
				}
				productGroupEcomProductsRepository.deleteByProductGroupPid(optionalPG.get().getPid());
			}
		}

		bulkOperationRepositoryCustom.bulkSaveProductGroupEcomProducts(saveUpdateProductGroupEcomProducts);

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
	public void deleteProductProductGroups(long companyId) {
		productGroupProductRepository.deleteByCompany(companyId);
	}
}
