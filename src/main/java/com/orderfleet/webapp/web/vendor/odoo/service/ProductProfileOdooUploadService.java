package com.orderfleet.webapp.web.vendor.odoo.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.UnitOfMeasure;
import com.orderfleet.webapp.domain.UnitOfMeasureProduct;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.StockLocationType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UnitOfMeasureProductRepository;
import com.orderfleet.webapp.repository.UnitOfMeasureRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.UnitOfMeasureService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.TPUnitOfMeasureProductDTO;
import com.orderfleet.webapp.web.rest.dto.TPUserStockLocationDTO;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductGroupProductDTO;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooProductProfile;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooStockLocation;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooUnitOfMeasure;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResultOdooProductProfile;
import com.orderfleet.webapp.web.vendor.odoo.dto.UnitQtyOdooProductProfile;

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

	public ProductProfileOdooUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
			DivisionRepository divisionRepository, ProductCategoryRepository productCategoryRepository,
			ProductGroupRepository productGroupRepository, ProductProfileRepository productProfileRepository,
			ProductGroupProductRepository productGroupProductRepository, CompanyRepository companyRepository,
			UnitOfMeasureRepository unitOfMeasureRepository,
			UnitOfMeasureProductRepository unitOfMeasureProductRepository,
			StockLocationRepository stockLocationRepository, StockLocationService stockLocationService,
			UserStockLocationRepository userStockLocationRepository,
			EmployeeProfileRepository employeeProfileRepository) {
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
	}

	@Transactional
	public void saveUpdateProductProfiles(final List<OdooProductProfile> list) {

		log.info("Saving Product Profiles.........");

		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();
		// find all exist product profiles
		Set<String> ppNames = list.stream().map(p -> p.getName()).collect(Collectors.toSet());
		List<ProductProfile> productProfiles = productProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);

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

		for (OdooProductProfile ppDto : list) {
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
				continue;
			}

			String uomId = String.valueOf(ppDto.getUom());

			Optional<UnitOfMeasure> opUom = unitOfMeasures.stream().filter(so -> so.getUomId().equalsIgnoreCase(uomId))
					.findAny();

			if (opUom.isPresent()) {
				productProfile.setSku(opUom.get().getName());

				TPUnitOfMeasureProductDTO unitOfMeasureProductDTO = new TPUnitOfMeasureProductDTO();

				unitOfMeasureProductDTO.setUnitOfMeasureName(opUom.get().getName());
				unitOfMeasureProductDTO.setProductName(ppDto.getName());

				unitOFMeasureProductDTOs.add(unitOfMeasureProductDTO);

			}

//			if (ppDto.getSku() != null && ppDto.getSku().size() > 0) {
//				int lastElement = ppDto.getSku().size() - 1;
//
//				Object[] objArray = ppDto.getSku().get(lastElement);
//
//				if (objArray != null && objArray.length > 0) {
//
//					int lastElementIndex = objArray.length - 1;
//
//					String sku = String.valueOf(objArray[lastElementIndex]);
//
//					productProfile.setSku(sku);
//
//					if (ppDto.getUnitQty() != null && ppDto.getUnitQty().size() > 0) {
//
//						List<UnitQtyOdooProductProfile> unitQuantities = ppDto.getUnitQty();
//
//						int lastElementIndexUq = unitQuantities.size() - 1;
//
//						if (sku.equalsIgnoreCase("KGM")) {
//							productProfile.setUnitQty(unitQuantities.get(lastElementIndexUq).getKGM());
//						}
//
//						if (sku.equalsIgnoreCase("PCS")) {
//							productProfile.setUnitQty(unitQuantities.get(lastElementIndexUq).getPCS());
//						}
//
//						if (sku.equalsIgnoreCase("NOS")) {
//							productProfile.setUnitQty(unitQuantities.get(lastElementIndexUq).getNOS());
//						}
//
//					}
//				}
//
//			}
			productProfile.setUnitQty(1.0);
			productProfile.setDescription(String.valueOf(ppDto.getId()));
			productProfile.setProductId(String.valueOf(ppDto.getId()));

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
		bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);
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

		Set<String> stkAlias = list.stream().map(p -> String.valueOf(p.getId())).collect(Collectors.toSet());
		List<StockLocation> stockLocations = stockLocationRepository
				.findByCompanyIdAndAliasIgnoreCaseIn(company.getId(), stkAlias);

		int i = 1;

		List<TPUserStockLocationDTO> tpUserStockLocationDTOs = new ArrayList<>();

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

			stockLocation.setName(name);

			TPUserStockLocationDTO tpUserStockLocationDTO = new TPUserStockLocationDTO();

			tpUserStockLocationDTO.setUserId(String.valueOf(stkDto.getSalesman_id()));
			tpUserStockLocationDTO.setStockLocationName(name);

			tpUserStockLocationDTOs.add(tpUserStockLocationDTO);

			saveUpdateStockLocations.add(stockLocation);

		}

		stockLocationRepository.save(saveUpdateStockLocations);
		log.info("Saving User Stock Locations.....");
		saveUserStockLoctions(tpUserStockLocationDTOs);

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
					.filter(pl -> pgpDto.getUnitOfMeasureName().equals(pl.getName())).findFirst();
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
		Set<String> uomNames = list.stream().map(p -> p.getName()).collect(Collectors.toSet());
		List<UnitOfMeasure> unitOfMeasures = unitOfMeasureRepository.findByCompanyIdAndNameIgnoreCaseIn(company.getId(),
				uomNames);

		for (OdooUnitOfMeasure uomDto : list) {
			// check exist by name, only one exist with a name
			Optional<UnitOfMeasure> optionalUom = unitOfMeasures.stream()
					.filter(p -> p.getName().equals(uomDto.getName())).findAny();
			UnitOfMeasure unitOfMeasure;
			if (optionalUom.isPresent()) {
				unitOfMeasure = optionalUom.get();

			} else {
				unitOfMeasure = new UnitOfMeasure();
				unitOfMeasure.setPid(UnitOfMeasureService.PID_PREFIX + RandomUtil.generatePid());
				unitOfMeasure.setCompany(company);
				unitOfMeasure.setName(uomDto.getName());

			}

			unitOfMeasure.setUomId(String.valueOf(uomDto.getId()));

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
}
