package com.orderfleet.webapp.web.vendor.garuda.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PriceLevelList;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.StockLocationType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.PriceLevelListRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.PriceLevelListService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelListDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductGroupProductDTO;
import com.orderfleet.webapp.web.vendor.excel.service.ProductProfileUploadService;
import com.orderfleet.webapp.web.vendor.garuda.dto.PriceLevelGarudaDTO;
import com.orderfleet.webapp.web.vendor.garuda.dto.PriceLevelListGarudaDTO;
import com.orderfleet.webapp.web.vendor.garuda.dto.ProductProfileGarudaDTO;

@Service
public class ProductProfileGarudaUploadService {

	private final Logger log = LoggerFactory.getLogger(ProductProfileUploadService.class);

	private final CompanyRepository companyRepository;
	private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;
	private final SyncOperationRepository syncOperationRepository;
	private final DivisionRepository divisionRepository;
	private final ProductCategoryRepository productCategoryRepository;
	private final ProductGroupRepository productGroupRepository;
	private final ProductProfileRepository productProfileRepository;
	private final ProductGroupProductRepository productGroupProductRepository;
	private final StockLocationRepository stockLocationRepository;
	private final OpeningStockRepository openingStockRepository;
	private final PriceLevelRepository priceLevelRepository;
	private final PriceLevelListRepository priceLevelListRepository;
	private final StockLocationService stockLocationService;

	public ProductProfileGarudaUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
			SyncOperationRepository syncOperationRepository, DivisionRepository divisionRepository,
			ProductCategoryRepository productCategoryRepository, ProductGroupRepository productGroupRepository,
			ProductProfileRepository productProfileRepository,
			ProductGroupProductRepository productGroupProductRepository,
			StockLocationRepository stockLocationRepository, OpeningStockRepository openingStockRepository,
			PriceLevelRepository priceLevelRepository, PriceLevelListRepository priceLevelListRepository,
			CompanyRepository companyRepository, StockLocationService stockLocationService) {
		super();
		this.companyRepository = companyRepository;
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
		this.stockLocationService = stockLocationService;
	}

	@Transactional
	public void saveUpdateProductProfiles(final List<ProductProfileGarudaDTO> productProfileDTOs) {
		long start = System.nanoTime();
		log.info("saveUpdateProductProfiles company id {}", SecurityUtils.getCurrentUsersCompanyId());
		final Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		final Long companyId = company.getId();
		List<OpeningStockDTO> openingStockDtos = new ArrayList<>();

		Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();

		List<TPProductGroupProductDTO> productGroupProductDTOs = new ArrayList<>();

		List<ProductGroupDTO> productGroupDtos = new ArrayList<>();

		List<StockLocationDTO> stockLocationDTOs = new ArrayList<>();

		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId();

		List<ProductCategory> productCategorys = productCategoryRepository.findByCompanyId(company.getId());

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

		// Price Level
		List<PriceLevelListGarudaDTO> wholeSalerList = new ArrayList<>();
		List<PriceLevelListGarudaDTO> retailerList = new ArrayList<>();

		List<PriceLevel> priceLevels = priceLevelRepository.findByCompanyId(company.getId());
		Set<PriceLevel> saveUpdatePriceLevels = new HashSet<>();

		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(companyId);

		for (ProductProfileGarudaDTO ppDto : productProfileDTOs) {
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
				productProfile.setName(ppDto.getName());
				productProfile.setDivision(defaultDivision);
				productProfile.setDataSourceType(DataSourceType.TALLY);
			}
			// productProfile.setAlias(ppDto.getAlias());
			// productProfile.setDescription(ppDto.getDescription());
			productProfile.setProductId(ppDto.getCode());
			productProfile.setPrice(new BigDecimal(0));
			productProfile.setMrp(ppDto.getMrp());
			productProfile.setTaxRate(ppDto.getTaxRate());
			productProfile.setSku(ppDto.getSku());
			productProfile.setActivated(true);
			if (ppDto.getUnitQty() != null) {
				productProfile.setUnitQty(ppDto.getUnitQty());
			}

			productProfile.setProductCategory(defaultCategory.get());

			StockLocationDTO stockLocationDTO = new StockLocationDTO();
			stockLocationDTO.setName(ppDto.getStockLocation());
			stockLocationDTO.setAlias(ppDto.getStockLocation());

			stockLocationDTOs.add(stockLocationDTO);

			// Price Level
			PriceLevelListGarudaDTO wholeSales = new PriceLevelListGarudaDTO();
			wholeSales.setProductProfileName(ppDto.getName());
			wholeSales.setPrice(ppDto.getWholesalePrice());
			wholeSalerList.add(wholeSales);

			PriceLevelListGarudaDTO retailer = new PriceLevelListGarudaDTO();
			retailer.setProductProfileName(ppDto.getName());
			retailer.setPrice(ppDto.getRetailPrice());
			retailerList.add(retailer);

			OpeningStockDTO openingStockDto = new OpeningStockDTO();
			openingStockDto.setProductProfileName(productProfile.getName());
			openingStockDto.setStockLocationName(ppDto.getStockLocation());
			openingStockDto.setQuantity(ppDto.getStockQty());
			openingStockDtos.add(openingStockDto);

			TPProductGroupProductDTO productGroupProductDTO = new TPProductGroupProductDTO();

			productGroupProductDTO.setGroupName(ppDto.getProductGroup());
			productGroupProductDTO.setProductName(ppDto.getName());

			productGroupProductDTOs.add(productGroupProductDTO);

			ProductGroupDTO productGroupDTO = new ProductGroupDTO();
			productGroupDTO.setName(ppDto.getProductGroup());
			productGroupDTO.setAlias(ppDto.getProductGroup());

			productGroupDtos.add(productGroupDTO);

			saveUpdateProductProfiles.add(productProfile);
		}

		bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);
		log.info("Saving product groups");
		saveUpdateProductGroups(productGroupDtos);
		log.info("Stock Location Size {}", stockLocationDTOs.size());
		List<StockLocationDTO> stkLocations = stockLocationDTOs.stream().filter(distinctByKey(cpt -> cpt.getName())).collect(Collectors.toList());
		log.info("Saving Stock Locations.... {}", stkLocations.size());
		saveUpdateStockLocations(stkLocations);
		log.info("Saving product group product profiles");
		saveUpdateProductGroupProduct(productGroupProductDTOs);
		log.info("Saving opening stock");
		saveUpdateOpeningStock(openingStockDtos);

		// Price List
		List<PriceLevelGarudaDTO> plListDto = new ArrayList<>();
		PriceLevelGarudaDTO wholeSaleDTO = new PriceLevelGarudaDTO();
		wholeSaleDTO.setName("Wholesaler");
		wholeSaleDTO.setPriceLevelListGarudaDTO(wholeSalerList);
		plListDto.add(wholeSaleDTO);

		PriceLevelGarudaDTO retailerDTO = new PriceLevelGarudaDTO();
		retailerDTO.setName("Retailer");
		retailerDTO.setPriceLevelListGarudaDTO(retailerList);
		plListDto.add(retailerDTO);

		log.info("PricelevelList Save", plListDto.size());

		if (plListDto.size() > 0) {

			List<PriceLevelListDTO> priceLevelListDTOs = new ArrayList<>();
			for (PriceLevelGarudaDTO plDto : plListDto) {

				Optional<PriceLevel> optionalPl = priceLevels.stream()
						.filter(pl -> pl.getName().equals(plDto.getName())).findAny();

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
				// priceLevel.setAlias(String.valueOf(plDto.getPricelist_id()));
				priceLevel.setActivated(true);

				Optional<PriceLevel> opPl = saveUpdatePriceLevels.stream()
						.filter(so -> so.getName().equalsIgnoreCase(plDto.getName())).findAny();
				if (opPl.isPresent()) {
					continue;
				}

				System.out.println("-------" + plDto.getPriceLevelListGarudaDTO().size());

				for (PriceLevelListGarudaDTO plList : plDto.getPriceLevelListGarudaDTO()) {

					if (plList.getProductProfileName() != null && !plList.getProductProfileName().equals("")) {

						Optional<ProductProfile> optionalPp = productProfiles.stream()
								.filter(pl -> pl.getName() != null ? pl.getName().equals(plList.getProductProfileName())
										: "abcd".equals(plList.getProductProfileName()))
								.findAny();
						if (optionalPp.isPresent()) {
							PriceLevelListDTO priceLevelListDTO = new PriceLevelListDTO(optionalPp.get().getName(),
									plDto.getName(), plList.getPrice(), 0.0, 0.0);
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
		}

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	@Async
	public void saveUpdateOpeningStock(List<OpeningStockDTO> openingStockDTOs) {
		long start = System.nanoTime();
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		Set<OpeningStock> saveOpeningStocks = new HashSet<>();
		// All opening-stock must have a stock-location, if not, set a default
		// one
		StockLocation defaultStockLocation = stockLocationRepository.findFirstByCompanyId(company.getId());
		log.info("defaulst stock location found :" + defaultStockLocation.getName());
		// find all exist product profiles
		Set<String> ppNames = openingStockDTOs.stream().map(os -> os.getProductProfileName())
				.collect(Collectors.toSet());
		log.info("product profile names found :" + ppNames.size());
		List<StockLocation> StockLocations = stockLocationService.findAllStockLocationByCompanyId(companyId);

		List<ProductProfile> productProfiles = productProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);
		log.info(" product profiles found :" + productProfiles.size());
		openingStockRepository.deleteByCompanyId(company.getId());
		log.info("Deleted opening stock");
		for (OpeningStockDTO osDto : openingStockDTOs) {
			// only save if account profile exist
			productProfiles.stream().filter(pp -> pp.getName().equals(osDto.getProductProfileName())).findAny()
					.ifPresent(pp -> {
						OpeningStock openingStock = new OpeningStock();
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
									.filter(pl -> osDto.getStockLocationName().equals(pl.getAlias())).findAny();
							if (optionalStockLocation.isPresent()) {
								openingStock.setStockLocation(optionalStockLocation.get());
							} else {
								openingStock.setStockLocation(defaultStockLocation);
							}
						}
						openingStock.setQuantity(osDto.getQuantity());
						saveOpeningStocks.add(openingStock);
					});
		}
		log.info("Saving opening stock");
		bulkOperationRepositoryCustom.bulkSaveOpeningStocks(saveOpeningStocks);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;

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
	public void saveUpdateStockLocations(final List<StockLocationDTO> list) {

		log.info("Saving Stock Locations.........");

		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		Set<StockLocation> saveUpdateStockLocations = new HashSet<>();

//		Set<String> stkName = list.stream().map(p -> String.valueOf(p.getName())).collect(Collectors.toSet());
//		List<StockLocation> stockLocations = stockLocationRepository
//				.findByCompanyIdAndAliasIgnoreCaseIn(company.getId(), stkName);

		List<StockLocation> stockLocations = stockLocationRepository.findAllByCompanyId();

		for (StockLocationDTO stkDto : list) {
			// check exist by name, only one exist with a name
			Optional<StockLocation> optionalStk = stockLocations.stream()
					.filter(p -> p.getName().equalsIgnoreCase(String.valueOf(stkDto.getName()))).findAny();
			StockLocation stockLocation;
			if (optionalStk.isPresent()) {
				stockLocation = optionalStk.get();
				// if not update, skip this iteration.
			} else {
				stockLocation = new StockLocation();
				stockLocation.setPid(StockLocationService.PID_PREFIX + RandomUtil.generatePid());
				stockLocation.setCompany(company);
				stockLocation.setName(String.valueOf(stkDto.getName()));
				stockLocation.setActivated(true);
				stockLocation.setStockLocationType((StockLocationType.ACTUAL));
			}

			stockLocation.setAlias(stkDto.getAlias());

			saveUpdateStockLocations.add(stockLocation);

		}

		stockLocationRepository.save(saveUpdateStockLocations);

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
	@Async
	public void saveUpdateProductGroupProduct(final List<ProductProfileGarudaDTO> productProfileDTOs, Long cId) {
		log.info("saveUpdateProductGroupProduct company id {}", cId);
		long start = System.nanoTime();
		final Company company = companyRepository.findOne(cId);
		final Long companyId = company.getId();
		Set<ProductGroupProduct> saveUpdateProductGroupProducts = new HashSet<>();
//		if (syncOperation.getReset()) {
//			productGroupProductRepository.updateProductGroupProductInactivate(companyId);
//		}
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(companyId);
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
		for (ProductProfileGarudaDTO pgpDto : productProfileDTOs) {
			// check exist by names,
			Optional<ProductGroupProduct> optionalPGP = productGroupProductRepository
					.findByCompanyIdAndProductGroupNameIgnoreCaseAndProductNameIgnoreCase(companyId, "General",
							pgpDto.getName());
			// if not exist save
			if (!optionalPGP.isPresent()) {
				Optional<ProductGroup> optionalGroup = productGroups.stream()
						.filter(pl -> "General".equals(pl.getName())).findAny();
				Optional<ProductProfile> optionalpp = productProfiles.stream()
						.filter(pl -> pgpDto.getName().equals(pl.getName())).findAny();
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
		log.info("Product Group Product Sync completed in {} ms", elapsedTime);
	}
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Map<Object,Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}


}
