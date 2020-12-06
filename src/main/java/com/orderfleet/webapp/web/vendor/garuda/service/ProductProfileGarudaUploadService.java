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
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PriceLevelList;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.CompanyRepository;
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
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PriceLevelListService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.ecom.mapper.EcomProductProfileMapper;
import com.orderfleet.webapp.web.rest.dto.PriceLevelListDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.TaxMasterMapper;
import com.orderfleet.webapp.web.vendor.excel.service.ProductProfileUploadService;
import com.orderfleet.webapp.web.vendor.garuda.dto.PriceLevelGarudaDTO;
import com.orderfleet.webapp.web.vendor.garuda.dto.PriceLevelListGarudaDTO;
import com.orderfleet.webapp.web.vendor.garuda.dto.ProductProfileGarudaDTO;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooPriceLevel;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooPriceLevelList;

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
	private final TaxMasterRepository taxMasterRepository;
	private final TaxMasterMapper taxMasterMapper;
	private final EcomProductProfileRepository ecomProductProfileRepository;
	private final EcomProductProfileMapper ecomProductProfileMapper;
	private final EcomProductProfileProductRepository ecomProductProfileProductRepository;
	private final ProductGroupEcomProductsRepository productGroupEcomProductsRepository;

	public ProductProfileGarudaUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
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
			ProductGroupEcomProductsRepository productGroupEcomProductsRepository, CompanyRepository companyRepository) {
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
		this.taxMasterRepository = taxMasterRepository;
		this.taxMasterMapper = taxMasterMapper;
		this.ecomProductProfileRepository = ecomProductProfileRepository;
		this.ecomProductProfileMapper = ecomProductProfileMapper;
		this.ecomProductProfileProductRepository = ecomProductProfileProductRepository;
		this.productGroupEcomProductsRepository = productGroupEcomProductsRepository;
	}

	@Transactional
	public void saveUpdateProductProfiles(final List<ProductProfileGarudaDTO> productProfileDTOs) {
		long start = System.nanoTime();
		log.info("saveUpdateProductProfiles company id {}", SecurityUtils.getCurrentUsersCompanyId());
		final Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		final Long companyId = company.getId();
		Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();

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
		
		Set<ProductGroupProduct> saveUpdateProductGroupProducts = new HashSet<>();
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
			productProfile.setPrice(new BigDecimal(0));
			productProfile.setMrp(ppDto.getMrp());
			productProfile.setTaxRate(ppDto.getTaxRate());
			productProfile.setSku(ppDto.getSku());
			productProfile.setActivated(true);
			if (ppDto.getUnitQty() != null) {
				productProfile.setUnitQty(ppDto.getUnitQty());
			}

			productProfile.setProductCategory(defaultCategory.get());
			
			// Price Level 
			PriceLevelListGarudaDTO wholeSales = new PriceLevelListGarudaDTO();
			wholeSales.setProductProfileName(ppDto.getName());
			wholeSales.setPrice(ppDto.getWholesalePrice());
			wholeSalerList.add(wholeSales);
			
			PriceLevelListGarudaDTO retailer = new PriceLevelListGarudaDTO();
			retailer.setProductProfileName(ppDto.getName());
			retailer.setPrice(ppDto.getRetailPrice());
			retailerList.add(retailer);
			
			saveUpdateProductProfiles.add(productProfile);
			
			// product group
			
			// check exist by names,
			Optional<ProductGroupProduct> optionalPGP = productGroupProductRepository
					.findByCompanyIdAndProductGroupNameIgnoreCaseAndProductNameIgnoreCase(companyId, "General",
							ppDto.getName());
			// if not exist save
			if (!optionalPGP.isPresent()) {
				Optional<ProductGroup> optionalGroup = productGroups.stream()
						.filter(pl -> "General".equals(pl.getName())).findAny();
				Optional<ProductProfile> optionalpp = productProfiles.stream()
						.filter(pl -> ppDto.getName().equals(pl.getName())).findAny();
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
		
		bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);
		bulkOperationRepositoryCustom.bulkSaveProductGroupProductProfile(saveUpdateProductGroupProducts);
		
		
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
		final Company company =  companyRepository.findOne(cId);
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

}
