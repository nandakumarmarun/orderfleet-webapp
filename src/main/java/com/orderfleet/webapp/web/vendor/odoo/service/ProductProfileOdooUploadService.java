package com.orderfleet.webapp.web.vendor.odoo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.EmployeeProfileLocation;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.LocationHierarchy;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PriceLevelList;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.TaxMaster;
import com.orderfleet.webapp.domain.TemporaryOpeningStock;
import com.orderfleet.webapp.domain.UnitOfMeasure;
import com.orderfleet.webapp.domain.UnitOfMeasureProduct;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.StockLocationType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.PriceLevelListRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.TaxMasterRepository;
import com.orderfleet.webapp.repository.TemporaryOpeningStockRepository;
import com.orderfleet.webapp.repository.UnitOfMeasureProductRepository;
import com.orderfleet.webapp.repository.UnitOfMeasureRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.PriceLevelListService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.UnitOfMeasureService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelListDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.TPLocationAccountProfilesDTO;
import com.orderfleet.webapp.web.rest.dto.TPUnitOfMeasureProductDTO;
import com.orderfleet.webapp.web.rest.dto.TPUserStockLocationDTO;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductGroupProductDTO;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooOpeningStock;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooPriceLevel;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooPriceLevelList;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooProductProfile;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooStockLocation;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooUnitOfMeasure;

/**
 * Service for save/update account profile related data from third party
 * softwares like tally.
 * <p>
 * Use the @Async annotation to process asynchronously.
 * </p>
 */
@Service
public class ProductProfileOdooUploadService {

	private final Logger log = LoggerFactory.getLogger(ProductProfileOdooUploadService.class);

	private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	private final DivisionRepository divisionRepository;

	private final ProductCategoryRepository productCategoryRepository;

	private final ProductGroupRepository productGroupRepository;

	private final ProductProfileRepository productProfileRepository;

	private final ProductGroupProductRepository productGroupProductRepository;

	private final CompanyRepository companyRepository;

	private final UnitOfMeasureRepository unitOfMeasureRepository;

	private final UnitOfMeasureProductRepository unitOfMeasureProductRepository;

	private final StockLocationRepository stockLocationRepository;

	private final StockLocationService stockLocationService;

	private final UserStockLocationRepository userStockLocationRepository;

	private final EmployeeProfileRepository employeeProfileRepository;

	private final PriceLevelRepository priceLevelRepository;

	private final PriceLevelListRepository priceLevelListRepository;

	private final TemporaryOpeningStockRepository temporaryOpeningStockRepository;

	private final TaxMasterRepository taxMasterRepository;

	private final EmployeeProfileLocationRepository employeeProfileLocationRepository;

	private final LocationRepository locationRepository;

	private final LocationAccountProfileRepository locationAccountProfileRepository;

	private final LocationAccountProfileService locationAccountProfileService;

	private final LocationService locationService;

	private final LocationHierarchyRepository locationHierarchyRepository;

	private final AccountProfileService accountProfileService;

	public ProductProfileOdooUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
			DivisionRepository divisionRepository, ProductCategoryRepository productCategoryRepository,
			ProductGroupRepository productGroupRepository, ProductProfileRepository productProfileRepository,
			ProductGroupProductRepository productGroupProductRepository, CompanyRepository companyRepository,
			UnitOfMeasureRepository unitOfMeasureRepository,
			UnitOfMeasureProductRepository unitOfMeasureProductRepository,
			StockLocationRepository stockLocationRepository, StockLocationService stockLocationService,
			UserStockLocationRepository userStockLocationRepository,
			EmployeeProfileRepository employeeProfileRepository, PriceLevelRepository priceLevelRepository,
			PriceLevelListRepository priceLevelListRepository,
			TemporaryOpeningStockRepository temporaryOpeningStockRepository, TaxMasterRepository taxMasterRepository,
			EmployeeProfileLocationRepository employeeProfileLocationRepository, LocationRepository locationRepository,
			LocationAccountProfileRepository locationAccountProfileRepository,
			LocationAccountProfileService locationAccountProfileService, LocationService locationService,
			LocationHierarchyRepository locationHierarchyRepository, AccountProfileService accountProfileService) {
		super();
		this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
		this.divisionRepository = divisionRepository;
		this.productCategoryRepository = productCategoryRepository;
		this.productGroupRepository = productGroupRepository;
		this.productProfileRepository = productProfileRepository;
		this.productGroupProductRepository = productGroupProductRepository;
		this.companyRepository = companyRepository;
		this.unitOfMeasureRepository = unitOfMeasureRepository;
		this.unitOfMeasureProductRepository = unitOfMeasureProductRepository;
		this.stockLocationRepository = stockLocationRepository;
		this.stockLocationService = stockLocationService;
		this.userStockLocationRepository = userStockLocationRepository;
		this.employeeProfileRepository = employeeProfileRepository;
		this.priceLevelRepository = priceLevelRepository;
		this.priceLevelListRepository = priceLevelListRepository;
		this.temporaryOpeningStockRepository = temporaryOpeningStockRepository;
		this.taxMasterRepository = taxMasterRepository;
		this.employeeProfileLocationRepository = employeeProfileLocationRepository;
		this.locationRepository = locationRepository;
		this.locationAccountProfileRepository = locationAccountProfileRepository;
		this.locationAccountProfileService = locationAccountProfileService;
		this.locationService = locationService;
		this.locationHierarchyRepository = locationHierarchyRepository;
		this.accountProfileService = accountProfileService;
	}

	@Transactional
	public void saveUpdateProductProfiles(final List<OdooProductProfile> list) {

		log.info("Saving Product Profiles.........");

		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		List<ProductProfile> saveUpdateProductProfiles = new ArrayList<>();
		// find all exist product profiles
		Set<String> ppNames = list.stream().map(p -> p.getName()).collect(Collectors.toSet());
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId();
//				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);

		List<UnitOfMeasure> unitOfMeasures = unitOfMeasureRepository.findAllByCompanyId(true);

		List<ProductCategory> productCategorys = productCategoryRepository.findByCompanyId(company.getId());

		List<ProductGroupDTO> productGroupDtos = new ArrayList<>();

		List<TPProductGroupProductDTO> productGroupProductDTOs = new ArrayList<>();

		List<TPUnitOfMeasureProductDTO> unitOFMeasureProductDTOs = new ArrayList<>();

		// All product must have a division/category, if not, set a default one
		Division defaultDivision = divisionRepository.findFirstByCompanyId(company.getId());
		String cat = productCategorys.get(0).getName();
		Optional<ProductCategory> defaultCategory = productCategoryRepository
				.findByCompanyIdAndNameIgnoreCase(company.getId(), cat);
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

		// tax masters
		List<TaxMaster> alltaxMasters = taxMasterRepository.findAllByCompanyId(companyId);
		int i = 1;
		for (OdooProductProfile ppDto : list) {
			// check exist by name, only one exist with a name
			Optional<ProductProfile> optionalPP = productProfiles.stream()
					.filter(p -> (p.getName().equals(ppDto.getName()))
							&& (p.getProductId() != null ? p.getProductId().equals(String.valueOf(ppDto.getId()))
									: "abcd".equals(String.valueOf(ppDto.getId()))))
					.findAny();
			ProductProfile productProfile;
			if (optionalPP.isPresent()) {
				productProfile = optionalPP.get();
				// if not update, skip this iteration.
				if (!productProfile.getThirdpartyUpdate()) {
					continue;
				}
			} else {
//				productProfile = new ProductProfile();
//				productProfile.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
//				productProfile.setCompany(company);
//				productProfile.setName(ppDto.getName());
//				productProfile.setProductId(String.valueOf(ppDto.getId()));
//				productProfile.setDivision(defaultDivision);
//				productProfile.setDataSourceType(DataSourceType.TALLY);
				continue;
			}
			productProfile.setActivated(true);
			// tax
			List<TaxMaster> taxMasters = new ArrayList<>();
			Object taxIds = ppDto.getTax_ids();
			if (taxIds instanceof List<?>) {
				List<Integer> taxIdList = (List<Integer>) (Object) taxIds;

				for (int taxId : taxIdList) {

					alltaxMasters.stream()
							.filter(a -> a.getTaxId() != null ? a.getTaxId().equalsIgnoreCase("" + taxId) : false)
							.findAny().ifPresent(ap -> {
								if (ap != null) {

									taxMasters.add(ap);
								}
							});

				}
				productProfile.setTaxMastersList(taxMasters);
			}

			double taxRate = 0;

			for (TaxMaster taxmaster : taxMasters) {
				taxRate += taxmaster.getVatPercentage();
			}

			productProfile.setTaxRate(taxRate);

			productProfile.setAlias(ppDto.getDefault_code());
			if (ppDto.getStandard_price() != null && !ppDto.getStandard_price().equals("")) {
				productProfile.setPrice(BigDecimal.valueOf(Double.valueOf(ppDto.getStandard_price())));
			} else {
				productProfile.setPrice(BigDecimal.valueOf(0));
			}
			productProfile.setMrp(0);

//			if (ppDto.getTaxRate() != null) {
//				productProfile
//						.setTaxRate(ppDto.getTaxRate().size() > 0 ? Double.valueOf(ppDto.getTaxRate().get(0)) : 5);
//				productProfile.setActivated(true);
//			}

			Optional<ProductProfile> opAccP = saveUpdateProductProfiles.stream()
					.filter(so -> so.getName().equalsIgnoreCase(ppDto.getName())).findAny();
			if (opAccP.isPresent()) {
				System.out.println(ppDto.getName());
				i++;
				continue;
			}

			String uomId = String.valueOf(ppDto.getUom());

			Optional<UnitOfMeasure> opUom = unitOfMeasures.stream().filter(so -> so.getUomId().equalsIgnoreCase(uomId))
					.findAny();

			if (opUom.isPresent()) {
				productProfile.setSku(opUom.get().getName());

				TPUnitOfMeasureProductDTO unitOfMeasureProductDTO = new TPUnitOfMeasureProductDTO();

				unitOfMeasureProductDTO.setUnitOfMeasureId(opUom.get().getUomId());
				unitOfMeasureProductDTO.setProductName(ppDto.getName());

				unitOFMeasureProductDTOs.add(unitOfMeasureProductDTO);

			}

			productProfile.setUnitQty(1.0);
			productProfile.setDescription(String.valueOf(ppDto.getId()));

			productProfile.setProductCategory(defaultCategory.get());

			TPProductGroupProductDTO productGroupProductDTO = new TPProductGroupProductDTO();

			productGroupProductDTO.setGroupName(ppDto.getGroup());
			productGroupProductDTO.setProductName(ppDto.getName());

			productGroupProductDTOs.add(productGroupProductDTO);

			ProductGroupDTO productGroupDTO = new ProductGroupDTO();
			productGroupDTO.setName(ppDto.getGroup());
			productGroupDTO.setAlias(ppDto.getGroup());

			productGroupDtos.add(productGroupDTO);

			saveUpdateProductProfiles.add(productProfile);
		}

		log.info("Saving product profiles");
		List<ProductProfile> pprofiles = productProfileRepository.save(saveUpdateProductProfiles);
		log.info("Saved--" + pprofiles.size() + "---product profiles");
		// bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);
		log.info("Saving product groups");
		saveUpdateProductGroups(productGroupDtos);
		log.info("Saving product group product profiles");
		saveUpdateProductGroupProduct(productGroupProductDTOs);
		log.info("Saving unit of measures product profiles");
		saveUpdateUnitOfMeasureProduct(unitOFMeasureProductDTOs);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	public void saveUpdateStockLocations(final List<OdooStockLocation> list) {

		log.info("Saving Stock Locations.........");

		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		Set<StockLocation> saveUpdateStockLocations = new HashSet<>();

		// Set<String> stkAlias = list.stream().map(p ->
		// String.valueOf(p.getId())).collect(Collectors.toSet());
		List<StockLocation> stockLocations = stockLocationRepository.findAllByCompanyId();
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId();

		int i = 1;

		List<TPUserStockLocationDTO> tpUserStockLocationDTOs = new ArrayList<>();
		List<TPLocationAccountProfilesDTO> tpLocationAccountProfieDTOs = new ArrayList<>();

		List<OpeningStockDTO> openingStockDtos = new ArrayList<>();

		List<LocationDTO> locationDtos = new ArrayList<>();

		log.info("Stock Locations........." + stockLocations.size());

		for (OdooStockLocation stkDto : list) {
			// check exist by name, only one exist with a name
			Optional<StockLocation> optionalStk = stockLocations.stream()
					.filter(p -> p.getAlias().equals(String.valueOf(stkDto.getId()))).findAny();
			StockLocation stockLocation;
			if (optionalStk.isPresent()) {
				stockLocation = optionalStk.get();
				// if not update, skip this iteration.
			} else {
				stockLocation = new StockLocation();
				stockLocation.setPid(StockLocationService.PID_PREFIX + RandomUtil.generatePid());
				stockLocation.setCompany(company);
				stockLocation.setAlias(String.valueOf(stkDto.getId()));
				stockLocation.setActivated(true);
				stockLocation.setStockLocationType((StockLocationType.ACTUAL));
			}

			String name = stkDto.getName();

			Optional<StockLocation> opAccP = saveUpdateStockLocations.stream()
					.filter(so -> so.getName().equalsIgnoreCase(stkDto.getName())).findAny();
			if (opAccP.isPresent()) {
				name = stkDto.getName() + "~" + i;
				i++;
			}

			System.out.println("Assigning Stocklocation as Locations............");
			stockLocation.setName(name);

			LocationDTO locationDTO = new LocationDTO();

			locationDTO.setAlias(name);
			locationDTO.setName(name);

			locationDtos.add(locationDTO);

			if (stkDto.getCustomer_ids().size() > 0) {

				System.out.println("Assigning location account profiles............");
				TPLocationAccountProfilesDTO tpLocationAccountProfieDTO = new TPLocationAccountProfilesDTO();

				tpLocationAccountProfieDTO.setLocationName(name);
				tpLocationAccountProfieDTO.setAccountProfileIds(stkDto.getCustomer_ids());

				tpLocationAccountProfieDTOs.add(tpLocationAccountProfieDTO);
			}

			System.out.println("Assigning User Stocklocation............");

			TPUserStockLocationDTO tpUserStockLocationDTO = new TPUserStockLocationDTO();

			tpUserStockLocationDTO.setUserId(String.valueOf(stkDto.getSalesman_id()));
			tpUserStockLocationDTO.setStockLocationName(name);

			tpUserStockLocationDTOs.add(tpUserStockLocationDTO);

			for (OdooOpeningStock osList : stkDto.getProduct_qty()) {

				Optional<ProductProfile> optionalPp = productProfiles.stream()
						.filter(pl -> pl.getProductId() != null
								? pl.getProductId().equals(String.valueOf(osList.getId()))
								: "abcd".equals(String.valueOf(osList.getId())))
						.findAny();
				if (optionalPp.isPresent()) {
					OpeningStockDTO openingStockDto = new OpeningStockDTO();
					openingStockDto.setProductProfileName(optionalPp.get().getName());
					openingStockDto.setStockLocationName(name);
					openingStockDto.setQuantity(osList.getQty());
					openingStockDtos.add(openingStockDto);

				}

			}

			saveUpdateStockLocations.add(stockLocation);

		}

		if (locationDtos.size() > 0) {
			saveUpdateLocations(locationDtos);
			saveUpdateLocationHierarchy(locationDtos);
		}

		if (tpLocationAccountProfieDTOs.size() > 0) {
			savingStockLocationAccountProfiles(tpLocationAccountProfieDTOs);
		}

		stockLocationRepository.save(saveUpdateStockLocations);
		log.info("Saving User Stock Locations.....");
		saveUserStockLoctions(tpUserStockLocationDTOs);
		saveUserLocations(tpUserStockLocationDTOs);

		if (openingStockDtos.size() > 0) {
			saveUpdateTemporaryOpeningStock(openingStockDtos);
		}

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	@Async
	public void saveUpdateTemporaryOpeningStock(List<OpeningStockDTO> openingStockDTOs) {
		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
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
						openingStock.setBatchNumber("123");
						openingStock.setQuantity(osDto.getQuantity());
						saveOpeningStocks.add(openingStock);
					});
		}
		bulkOperationRepositoryCustom.bulkSaveTemporaryOpeningStocks(saveOpeningStocks);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	public void saveUpdateProductGroups(final List<ProductGroupDTO> productGroupDTOs) {
		log.info("Saving Product Groups.........");
		long start = System.nanoTime();
		Set<ProductGroup> saveUpdateProductGroups = new HashSet<>();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
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
			productGroup.setDescription(pgDto.getDescription());
			productGroup.setActivated(true);

			Optional<ProductGroup> opPgs = saveUpdateProductGroups.stream()
					.filter(so -> so.getName().equalsIgnoreCase(pgDto.getName())).findAny();
			if (opPgs.isPresent()) {
				continue;
			}

			saveUpdateProductGroups.add(productGroup);
		}
		bulkOperationRepositoryCustom.bulkSaveProductGroup(saveUpdateProductGroups);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	@Async
	public void saveUpdateProductGroupProduct(List<TPProductGroupProductDTO> productGroupProductDTOs) {

		log.info("Saving Product Group Products.........");
		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		List<ProductGroupProduct> newProductGroupProducts = new ArrayList<>();
		List<ProductGroupProduct> productGroupProducts = productGroupProductRepository
				.findAllByCompanyPid(company.getPid());

		// delete all assigned location account profile from tally
		// locationAccountProfileRepository.deleteByCompanyIdAndDataSourceTypeAndThirdpartyUpdateTrue(company.getId(),DataSourceType.TALLY);
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(company.getId());
		List<Long> productGroupProductsIds = new ArrayList<>();

		for (TPProductGroupProductDTO pgpDto : productGroupProductDTOs) {
			ProductGroupProduct productGroupProduct = new ProductGroupProduct();
			// find location

			Optional<ProductGroup> opPg = productGroups.stream()
					.filter(pl -> pgpDto.getGroupName().equals(pl.getName())).findFirst();
			// find accountprofile
			// System.out.println(loc.get()+"===Location");

			Optional<ProductProfile> opPp = productProfiles.stream()
					.filter(pp -> pgpDto.getProductName().equals(pp.getName())).findFirst();
			if (opPp.isPresent()) {
				List<Long> productGroupProductIds = productGroupProducts.stream()
						.filter(pgp -> opPp.get().getPid().equals(pgp.getProduct().getPid())).map(pgp -> pgp.getId())
						.collect(Collectors.toList());
				if (productGroupProductIds.size() != 0) {
					productGroupProductsIds.addAll(productGroupProductIds);
				}
				if (opPg.isPresent()) {
					productGroupProduct.setProductGroup(opPg.get());
				} else if (opPp.isPresent()) {
					productGroupProduct.setProductGroup(productGroups.get(0));
				}
				productGroupProduct.setProduct(opPp.get());
				productGroupProduct.setCompany(company);
				newProductGroupProducts.add(productGroupProduct);
			}
		}
		if (productGroupProductsIds.size() != 0) {
			productGroupProductRepository.deleteByIdIn(companyId, productGroupProductsIds);
		}

		productGroupProductRepository.save(newProductGroupProducts);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);

	}

	@Transactional
	@Async
	public void saveUpdateUnitOfMeasureProduct(List<TPUnitOfMeasureProductDTO> unitOfMeasureProductDTOs) {

		log.info("Saving Unit Of Measure Products.........");
		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		List<UnitOfMeasureProduct> newUnitOfMeasureProducts = new ArrayList<>();
		List<UnitOfMeasureProduct> unitOfMeasureProducts = unitOfMeasureProductRepository
				.findAllByCompanyPid(company.getPid());

		// delete all assigned location account profile from tally
		// locationAccountProfileRepository.deleteByCompanyIdAndDataSourceTypeAndThirdpartyUpdateTrue(company.getId(),DataSourceType.TALLY);
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
		List<UnitOfMeasure> unitOfMeasures = unitOfMeasureRepository.findByCompanyId(company.getId());
		List<Long> unitOfMeasureProductsIds = new ArrayList<>();

		for (TPUnitOfMeasureProductDTO pgpDto : unitOfMeasureProductDTOs) {
			UnitOfMeasureProduct unitOfMeasureProduct = new UnitOfMeasureProduct();
			// find location

			Optional<UnitOfMeasure> opPg = unitOfMeasures.stream()
					.filter(pl -> pgpDto.getUnitOfMeasureId().equals(pl.getUomId())).findAny();
			// find accountprofile
			// System.out.println(loc.get()+"===Location");

			Optional<ProductProfile> opPp = productProfiles.stream()
					.filter(pp -> pgpDto.getProductName().equals(pp.getName())).findFirst();
			if (opPp.isPresent()) {
				List<Long> uomIds = unitOfMeasureProducts.stream()
						.filter(pgp -> opPp.get().getPid().equals(pgp.getProduct().getPid())).map(pgp -> pgp.getId())
						.collect(Collectors.toList());
				if (uomIds.size() != 0) {
					unitOfMeasureProductsIds.addAll(uomIds);
				}
				if (opPg.isPresent()) {
					unitOfMeasureProduct.setUnitOfMeasure(opPg.get());
				} else if (opPp.isPresent()) {
					unitOfMeasureProduct.setUnitOfMeasure(unitOfMeasures.get(0));
				}
				unitOfMeasureProduct.setProduct(opPp.get());
				unitOfMeasureProduct.setCompany(company);
				newUnitOfMeasureProducts.add(unitOfMeasureProduct);
			}
		}
		if (unitOfMeasureProductsIds.size() != 0) {
			unitOfMeasureProductRepository.deleteByIdIn(companyId, unitOfMeasureProductsIds);
		}

		unitOfMeasureProductRepository.save(newUnitOfMeasureProducts);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);

	}

	@Transactional
	public void saveUpdateUnitOfMeasure(List<OdooUnitOfMeasure> list) {
		log.info("Saving Unit Of Measures.........");

		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		Set<UnitOfMeasure> saveUpdateUnitOfMeasure = new HashSet<>();
		// find all exist product profiles

		List<UnitOfMeasure> unitOfMeasures = unitOfMeasureRepository.findByCompanyId(companyId);

		for (OdooUnitOfMeasure uomDto : list) {
			// check exist by name, only one exist with a name
			Optional<UnitOfMeasure> optionalUom = unitOfMeasures.stream()
					.filter(p -> p.getUomId().equals(String.valueOf(uomDto.getId()))).findAny();
			UnitOfMeasure unitOfMeasure;
			if (optionalUom.isPresent()) {
				unitOfMeasure = optionalUom.get();

			} else {
				unitOfMeasure = new UnitOfMeasure();
				unitOfMeasure.setPid(UnitOfMeasureService.PID_PREFIX + RandomUtil.generatePid());
				unitOfMeasure.setCompany(company);
				unitOfMeasure.setUomId(String.valueOf(uomDto.getId()));

			}
			unitOfMeasure.setName(uomDto.getName());

			Optional<UnitOfMeasure> opUom = saveUpdateUnitOfMeasure.stream()
					.filter(so -> so.getName().equalsIgnoreCase(uomDto.getName())).findAny();
			if (opUom.isPresent()) {
				continue;
			}

			saveUpdateUnitOfMeasure.add(unitOfMeasure);

		}

		log.info("Saving Unit Of Measures....");
		unitOfMeasureRepository.save(saveUpdateUnitOfMeasure);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);

	}

	private void saveUserStockLoctions(List<TPUserStockLocationDTO> tpUserStockLocationDTOs) {
		log.info("Saving User Stock Locations.........");
		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		List<UserStockLocation> newUserStockLocations = new ArrayList<>();
		List<UserStockLocation> userStockLocations = userStockLocationRepository.findAllByCompanyPid(company.getPid());

		// delete all assigned location account profile from tally
		// locationAccountProfileRepository.deleteByCompanyIdAndDataSourceTypeAndThirdpartyUpdateTrue(company.getId(),DataSourceType.TALLY);
		List<StockLocation> stockLocations = stockLocationRepository.findAllByCompanyId(companyId);
		List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findAllByCompanyId(company.getId());
		List<Long> userStockLocationIds = new ArrayList<>();

		List<String> userPids = new ArrayList<>();
		for (TPUserStockLocationDTO uslDto : tpUserStockLocationDTOs) {
			UserStockLocation userStockLocation = new UserStockLocation();
			// find location

			Optional<StockLocation> opStk = stockLocations.stream()
					.filter(pl -> uslDto.getStockLocationName().equals(pl.getName())).findFirst();

			Optional<EmployeeProfile> opEmp = employeeProfiles.stream()
					.filter(pp -> uslDto.getUserId().equals(pp.getAlias())).findFirst();
			if (opStk.isPresent()) {
				List<Long> userstkLocationIds = userStockLocations.stream()
						.filter(pgp -> opStk.get().getPid().equals(pgp.getStockLocation().getPid()))
						.map(pgp -> pgp.getId()).collect(Collectors.toList());
				if (userstkLocationIds.size() != 0) {
					userStockLocationIds.addAll(userstkLocationIds);
				}

				if (opEmp.isPresent()) {
					User user = opEmp.get().getUser();
					if (user == null) {
						continue;
					}
					userPids.add(user.getPid());
					userStockLocation.setUser(user);
				} else {
					continue;
				}

				userStockLocation.setStockLocation(opStk.get());
				userStockLocation.setCompany(company);
				newUserStockLocations.add(userStockLocation);
			}
		}

		userStockLocationRepository.deleteByUserPidIn(userPids);

		userStockLocationRepository.save(newUserStockLocations);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);

	}

	private void saveUserLocations(List<TPUserStockLocationDTO> tpUserStockLocationDTOs) {
		log.info("Saving User Locations.........");
		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		List<EmployeeProfileLocation> newUserLocations = new ArrayList<>();
		List<EmployeeProfileLocation> userLocations = employeeProfileLocationRepository
				.findByLocationCompanyPid(company.getPid());

		// delete all assigned location account profile from tally
		// locationAccountProfileRepository.deleteByCompanyIdAndDataSourceTypeAndThirdpartyUpdateTrue(company.getId(),DataSourceType.TALLY);
		List<Location> locations = locationRepository.findAllByCompanyId(companyId);
		List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findAllByCompanyId(company.getId());
		List<Long> userStockLocationIds = new ArrayList<>();

		List<String> userPids = new ArrayList<>();
		for (TPUserStockLocationDTO uslDto : tpUserStockLocationDTOs) {
			EmployeeProfileLocation userLocation = new EmployeeProfileLocation();
			// find location

			Optional<Location> opStk = locations.stream()
					.filter(pl -> uslDto.getStockLocationName().equals(pl.getName())).findFirst();

			Optional<EmployeeProfile> opEmp = employeeProfiles.stream()
					.filter(pp -> uslDto.getUserId().equals(pp.getAlias())).findFirst();
			if (opStk.isPresent()) {
				List<Long> userstkLocationIds = userLocations.stream()
						.filter(pgp -> opStk.get().getPid().equals(pgp.getLocation().getPid())).map(pgp -> pgp.getId())
						.collect(Collectors.toList());
				if (userstkLocationIds.size() != 0) {
					userStockLocationIds.addAll(userstkLocationIds);
				}

				if (opEmp.isPresent()) {
					EmployeeProfile user = opEmp.get();
					if (user == null) {
						continue;
					}
					userPids.add(user.getPid());
					userLocation.setEmployeeProfile(user);
				} else {
					continue;
				}

				userLocation.setLocation(opStk.get());
				newUserLocations.add(userLocation);
			}
		}

		Optional<Location> opLoc = locations.stream().filter(pp -> pp.getName().equals("Territory")).findFirst();

		for (String employeePid : userPids) {
			Optional<EmployeeProfile> opEmp = employeeProfiles.stream().filter(pp -> pp.getPid().equals(employeePid))
					.findFirst();
			if (opEmp.isPresent() && opLoc.isPresent()) {
				EmployeeProfileLocation userLocation = new EmployeeProfileLocation();
				userLocation.setEmployeeProfile(opEmp.get());
				userLocation.setLocation(opLoc.get());
				newUserLocations.add(userLocation);
			}
		}
		employeeProfileLocationRepository.deleteByEmployeeProfilePidIn(userPids);

		employeeProfileLocationRepository.save(newUserLocations);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);

	}

	@Transactional
	public void saveUpdatePriceList(List<OdooPriceLevel> list) {

		log.info("Saving Price Level.........");

		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		Set<PriceLevel> saveUpdatePriceLevels = new HashSet<>();
		// find all priceLevels
		List<PriceLevel> priceLevels = priceLevelRepository.findByCompanyId(company.getId());

		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);

		List<PriceLevelListDTO> priceLevelListDTOs = new ArrayList<>();
		for (OdooPriceLevel plDto : list) {

			Optional<PriceLevel> optionalPl = priceLevels.stream()
					.filter(pl -> pl.getName().equals(plDto.getPricelist_name())).findAny();

			PriceLevel priceLevel;
			if (optionalPl.isPresent()) {
				priceLevel = optionalPl.get();
				// if not update, skip this iteration.
				// if (!priceLevel.getThirdpartyUpdate()) {continue;}
			} else {
				priceLevel = new PriceLevel();
				priceLevel.setPid(PriceLevelService.PID_PREFIX + RandomUtil.generatePid());
				priceLevel.setName(plDto.getPricelist_name());
				priceLevel.setCompany(company);
			}
			priceLevel.setAlias(String.valueOf(plDto.getPricelist_id()));
			priceLevel.setActivated(true);

			Optional<PriceLevel> opPl = saveUpdatePriceLevels.stream()
					.filter(so -> so.getName().equalsIgnoreCase(plDto.getPricelist_name())).findAny();
			if (opPl.isPresent()) {
				continue;
			}

			System.out.println("-------" + plDto.getProduct_list().size());

			for (OdooPriceLevelList plList : plDto.getProduct_list()) {

				if (!plList.getProduct_id().equals("false")) {

					Optional<ProductProfile> optionalPp = productProfiles.stream()
							.filter(pl -> pl.getProductId() != null ? pl.getProductId().equals(plList.getProduct_id())
									: "abcd".equals(plList.getProduct_id()))
							.findAny();
					if (optionalPp.isPresent()) {
						PriceLevelListDTO priceLevelListDTO = new PriceLevelListDTO(optionalPp.get().getName(),
								plDto.getPricelist_name(), plList.getPrice(), 0.0, 0.0);
						priceLevelListDTOs.add(priceLevelListDTO);

					}
				}

			}

			saveUpdatePriceLevels.add(priceLevel);
		}

		bulkOperationRepositoryCustom.bulkSaveUpdatePriceLevels(saveUpdatePriceLevels);

		if (priceLevelListDTOs.size() > 0) {
			saveUpdatePriceLevelList(priceLevelListDTOs);
		}

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);

	}

	@Transactional
	public void saveUpdatePriceLevelList(final List<PriceLevelListDTO> priceLevelListDTOs) {

		log.info("Saving Price Level Lists.........");

		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

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

		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	public void saveUpdateLocations(final List<LocationDTO> locationDTOs) {

		log.info("Saving Locations........." + locationDTOs.size());
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
			Location location = new Location();
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
		log.info(saveUpdateLocations.size() + "---------------------");
		// bulkOperationRepositoryCustom.bulkSaveLocations(saveUpdateLocations);
		List<Location> updated = locationRepository.save(saveUpdateLocations);

		log.info("Saved " + updated.size() + "  Locations---------------------");

		locationRepository.flush();
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	@Async
	public void saveUpdateLocationAccountProfiles(final List<LocationAccountProfileDTO> locationAccountProfileDTOs) {

		log.info("Saving Location Account Profiles........." + locationAccountProfileDTOs.size());
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
			// System.out.println(loc.get() + "===Location");

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

	@Transactional
	private void savingStockLocationAccountProfiles(List<TPLocationAccountProfilesDTO> tpLocationAccountProfieDTOs) {

		log.info("Saving Stock Location Account Profiles.........");
		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();

		List<AccountProfile> accountProfiles = accountProfileService.findAllAccountProfileByCompanyId(companyId);

		List<LocationAccountProfileDTO> locationAccountProfileDTOs = new ArrayList<>();

		Optional<AccountProfile> companyAccountProOptional = accountProfiles.stream()
				.filter(acp -> acp.getAccountType() != null ? acp.getAccountType().getName().equals("Company")
						: "abc123".equals("Company"))
				.findAny();
		if (companyAccountProOptional.isPresent()) {

			LocationAccountProfileDTO locationAccountProfileDto = new LocationAccountProfileDTO();

			locationAccountProfileDto.setAccountProfileName(companyAccountProOptional.get().getName());
			locationAccountProfileDto.setLocationName("Territory");

			locationAccountProfileDTOs.add(locationAccountProfileDto);
		}

		for (TPLocationAccountProfilesDTO tpLocationAccountProfilesDTO : tpLocationAccountProfieDTOs) {

			for (long accountProfileId : tpLocationAccountProfilesDTO.getAccountProfileIds()) {

				Optional<AccountProfile> accountProOptional = accountProfiles.stream()
						.filter(acp -> acp.getCustomerId() != null
								? acp.getCustomerId().equals(String.valueOf(accountProfileId))
								: "abc123".equals(String.valueOf(accountProfileId)))
						.findAny();

				if (accountProOptional.isPresent()) {

					LocationAccountProfileDTO locationAccountProfileDto = new LocationAccountProfileDTO();

					locationAccountProfileDto.setAccountProfileName(accountProOptional.get().getName());
					locationAccountProfileDto.setLocationName(tpLocationAccountProfilesDTO.getLocationName());

					locationAccountProfileDTOs.add(locationAccountProfileDto);
				}
			}
		}

		saveUpdateLocationAccountProfiles(locationAccountProfileDTOs);

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
