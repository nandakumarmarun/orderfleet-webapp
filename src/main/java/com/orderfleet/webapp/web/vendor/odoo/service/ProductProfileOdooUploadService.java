package com.orderfleet.webapp.web.vendor.odoo.service;

import java.math.BigDecimal;
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
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.util.RandomUtil;
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

	public ProductProfileOdooUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
			DivisionRepository divisionRepository, ProductCategoryRepository productCategoryRepository,
			ProductGroupRepository productGroupRepository, ProductProfileRepository productProfileRepository,
			ProductGroupProductRepository productGroupProductRepository, CompanyRepository companyRepository) {
		super();
		this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
		this.divisionRepository = divisionRepository;
		this.productCategoryRepository = productCategoryRepository;
		this.productGroupRepository = productGroupRepository;
		this.productProfileRepository = productProfileRepository;
		this.productGroupProductRepository = productGroupProductRepository;
		this.companyRepository = companyRepository;
	}

	@Transactional
	public void saveUpdateProductProfiles(final List<ResultOdooProductProfile> resultProductProfiles) {

		log.info("Saving Product Profiles.........");

		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();
		// find all exist product profiles
		Set<String> ppNames = resultProductProfiles.stream().map(p -> p.getName()).collect(Collectors.toSet());
		List<ProductProfile> productProfiles = productProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);

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

		for (ResultOdooProductProfile ppDto : resultProductProfiles) {
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

			productProfile.setAlias(ppDto.getProductCode());
			productProfile.setPrice(BigDecimal.valueOf(ppDto.getPrice()));
			productProfile.setMrp(0);

			if (ppDto.getTaxRate() != null) {
				productProfile
						.setTaxRate(ppDto.getTaxRate().size() > 0 ? Double.valueOf(ppDto.getTaxRate().get(0)) : 5);
				productProfile.setActivated(true);
			}

			Optional<ProductProfile> opAccP = saveUpdateProductProfiles.stream()
					.filter(so -> so.getName().equalsIgnoreCase(ppDto.getName())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}

			if (ppDto.getSku() != null && ppDto.getSku().size() > 0) {
				int lastElementIndex = ppDto.getSku().size() - 1;

				String sku = ppDto.getSku().get(lastElementIndex);

				productProfile.setSku(sku);

				if (ppDto.getUnitQty() != null && ppDto.getUnitQty().size() > 0) {

					List<UnitQtyOdooProductProfile> unitQuantities = ppDto.getUnitQty();

					int lastElementIndexUq = unitQuantities.size() - 1;

					if (sku.equalsIgnoreCase("KGM")) {
						productProfile.setUnitQty(unitQuantities.get(lastElementIndexUq).getKGM());
					}

					if (sku.equalsIgnoreCase("PCS")) {
						productProfile.setUnitQty(unitQuantities.get(lastElementIndexUq).getPCS());
					}

					if (sku.equalsIgnoreCase("NOS")) {
						productProfile.setUnitQty(unitQuantities.get(lastElementIndexUq).getNOS());
					}

				}

			}

			productProfile.setSize(String.valueOf(ppDto.getSize()));
			productProfile.setHsnCode(ppDto.getHsnCode() != null ? ppDto.getHsnCode() : "");

			productProfile.setProductCategory(defaultCategory.get());

			saveUpdateProductProfiles.add(productProfile);

		}
		bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);
		saveUpdateProductGroupProduct(resultProductProfiles);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	@Async
	public void saveUpdateProductGroupProduct(List<ResultOdooProductProfile> resultProductProfiles) {
		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		Set<ProductGroupProduct> saveUpdateProductGroupProducts = new HashSet<>();

		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(companyId);
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
		for (ResultOdooProductProfile pgpDto : resultProductProfiles) {
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
		// update sync table
		log.info("Product Group Product Sync completed in {} ms", elapsedTime);
	}
}
