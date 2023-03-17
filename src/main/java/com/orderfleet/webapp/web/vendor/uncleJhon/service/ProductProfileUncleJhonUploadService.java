package com.orderfleet.webapp.web.vendor.uncleJhon.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductGroupProductDTO;
import com.orderfleet.webapp.web.vendor.focus.dto.ProductProfileNewFocus;
import com.orderfleet.webapp.web.vendor.focus.service.ProductProfileFocusUploadService;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.ProductUJ;
@Service
public class ProductProfileUncleJhonUploadService {

	private final Logger log = LoggerFactory.getLogger(ProductProfileUncleJhonUploadService.class);

	private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	private final DivisionRepository divisionRepository;

	private final ProductCategoryRepository productCategoryRepository;

	private final ProductGroupRepository productGroupRepository;

	private final ProductProfileRepository productProfileRepository;

	private final ProductGroupProductRepository productGroupProductRepository;

	private final CompanyRepository companyRepository;

	public ProductProfileUncleJhonUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
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

	public void saveProductGroupProduct(List<ProductUJ> productUJ) {
		log.info("Saving ProductGroup Product");
		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		List<ProductGroupProduct> newProductGroupProducts = new ArrayList<>();

		productGroupProductRepository.deleteByCompany(companyId);

		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);

		Optional<ProductGroup> defaultGroup = productGroupRepository.findByCompanyIdAndNameIgnoreCase(company.getId(),
				"General");

		for (ProductUJ product : productUJ) {
			ProductGroupProduct productGroupProduct = new ProductGroupProduct();
			Optional<ProductProfile> opPp = productProfiles.stream()
					.filter(pp -> product.getCode().trim().equalsIgnoreCase(pp.getProductId().trim())).findAny();

			if (opPp.isPresent()) {
				productGroupProduct.setProductGroup(defaultGroup.get());
				productGroupProduct.setProduct(opPp.get());
				productGroupProduct.setCompany(company);
				newProductGroupProducts.add(productGroupProduct);
			}
		}

	      productGroupProductRepository.save(newProductGroupProducts);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		log.info("Sync completed in {} ms", elapsedTime);

	}

	public void saveUpdateProductProfiles(List<ProductUJ> productUJ) {

		log.info("Saving Product Profiles.........");

		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();



		List<ProductProfile> productProfiles = productProfileRepository
				.findAllByCompanyId(company.getId());

		// All product must have a division/category, if not, set a default one
		Division defaultDivision = divisionRepository.findFirstByCompanyId(company.getId());

		Optional<ProductCategory> defaultCategory = productCategoryRepository
				.findByCompanyIdAndNameIgnoreCase(company.getId(), "Not Applicable");

		for (ProductUJ ppDto : productUJ) {
			// check exist by name, only one exist with a name
			Optional<ProductProfile> optionalPP = productProfiles.stream()
					.filter(p -> p.getProductId().trim().equalsIgnoreCase(ppDto.getCode().trim())).findAny();
			ProductProfile productProfile;
			if (optionalPP.isPresent()) {
				productProfile = optionalPP.get();

			} else {
				productProfile = new ProductProfile();
				productProfile.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
				productProfile.setCompany(company);
				productProfile.setDivision(defaultDivision);
				productProfile.setDataSourceType(DataSourceType.VENDOR);
				productProfile.setProductId(ppDto.getCode().trim());

			}

			productProfile.setName(ppDto.getName().trim()+"-"+ppDto.getCode().trim());
			productProfile.setPrice(BigDecimal.valueOf(ppDto.getWholesalePrice()));
			productProfile.setTaxRate(ppDto.getTaxRate());
			productProfile.setMrp(ppDto.getMrp());
			productProfile.setActivated(true);

			if (ppDto.getHsnCode() != null && !ppDto.getHsnCode().equals("")) {
				productProfile.setHsnCode(ppDto.getHsnCode());
			}

			productProfile.setSku(ppDto.getSku());
			productProfile.setProductCode(ppDto.getAlphaCode());
			productProfile.setCompoundUnitQty(Double.valueOf(ppDto.getUnitMlLtr()));
             productProfile.setStockQty(Double.valueOf(ppDto.getStockQty()));
			productProfile.setUnitQty(Double.valueOf(ppDto.getUnitQty()));
			productProfile.setProductCategory(defaultCategory.get());

			saveUpdateProductProfiles.add(productProfile);
		}
		log.info("Saving product profiles");
		bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);

	}

}
