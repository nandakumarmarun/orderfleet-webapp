package com.orderfleet.webapp.web.vendor.sap.service;

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
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductGroupProductDTO;
import com.orderfleet.webapp.web.vendor.sap.dto.ResponseBodySapProductProfile;

/**
 * Service for save/update account profile related data from third party
 * softwares like tally.
 * <p>
 * Use the @Async annotation to process asynchronously.
 * </p>
 */
@Service
public class ProductProfileSapUploadService {

	private final Logger log = LoggerFactory.getLogger(ProductProfileSapUploadService.class);

	private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	private final DivisionRepository divisionRepository;

	private final ProductCategoryRepository productCategoryRepository;

	private final ProductGroupRepository productGroupRepository;

	private final ProductProfileRepository productProfileRepository;

	private final ProductGroupProductRepository productGroupProductRepository;

	private final CompanyRepository companyRepository;

	public ProductProfileSapUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
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
	public void saveUpdateProductProfiles(final List<ResponseBodySapProductProfile> resultProductProfiles) {

		log.info("Saving Product Profiles.........");

		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();
		// find all exist product profiles
		Set<String> ppNames = resultProductProfiles.stream().map(p -> p.getStr2()).collect(Collectors.toSet());
		List<ProductProfile> productProfiles = productProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);

		List<ProductCategory> productCategorys = productCategoryRepository.findByCompanyId(company.getId());

		List<String> productGroupNames = new ArrayList<>();

		List<TPProductGroupProductDTO> productGroupProductDTOs = new ArrayList<>();

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

		for (ResponseBodySapProductProfile ppDto : resultProductProfiles) {
			// check exist by name, only one exist with a name
			Optional<ProductProfile> optionalPP = productProfiles.stream()
					.filter(p -> p.getName().equals(ppDto.getStr2())).findAny();
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
				productProfile.setName(ppDto.getStr2());
				productProfile.setDivision(defaultDivision);
				productProfile.setDataSourceType(DataSourceType.TALLY);
			}

			productProfile.setAlias(ppDto.getStr1());
			productProfile.setPrice(BigDecimal.valueOf(0));
			productProfile.setMrp(0);

			productProfile.setTaxRate(18);
			productProfile.setActivated(true);

			Optional<ProductProfile> opAccP = saveUpdateProductProfiles.stream()
					.filter(so -> so.getName().equalsIgnoreCase(ppDto.getStr2())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}

			productProfile.setSku(ppDto.getSalUnitMsr());
			if (ppDto.getWeightPerPC() != null && !ppDto.getWeightPerPC().isEmpty()) {
				productProfile.setUnitQty(Double.parseDouble(ppDto.getWeightPerPC()));
			}
			productProfile.setProductCategory(defaultCategory.get());

			TPProductGroupProductDTO productGroupProductDTO = new TPProductGroupProductDTO();

			productGroupProductDTO.setGroupName(ppDto.getItemGroupCode());
			productGroupProductDTO.setProductName(ppDto.getStr2());

			productGroupProductDTOs.add(productGroupProductDTO);

			productGroupNames.add(ppDto.getItemGroupCode());

			saveUpdateProductProfiles.add(productProfile);

		}
		bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);

		List<String> listWithoutDuplicatesPg = productGroupNames.stream().distinct().collect(Collectors.toList());

		saveUpdateProductGroups(listWithoutDuplicatesPg);
		saveUpdateProductGroupProduct(productGroupProductDTOs, listWithoutDuplicatesPg);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	@Async
	private void saveUpdateProductGroups(List<String> productGroupNames) {

		log.info("Saving Product Groups.........");

		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		Set<ProductGroup> saveUpdateProductGroups = new HashSet<>();
		// find all product group
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(company.getId());

		for (String pg : productGroupNames) {
			// check exist by name, only one exist with a name
			if (pg != null) {
				Optional<ProductGroup> optionalPG = productGroups.stream().filter(p -> p.getName().equals(pg))
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
					productGroup.setName(pg);
					productGroup.setDataSourceType(DataSourceType.TALLY);
					productGroup.setCompany(company);
				}
				productGroup.setActivated(true);
				saveUpdateProductGroups.add(productGroup);
			}
		}
		bulkOperationRepositoryCustom.bulkSaveProductGroup(saveUpdateProductGroups);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);

	}

	@Transactional
	@Async
	public void saveUpdateProductGroupProduct(List<TPProductGroupProductDTO> productGroupProductDTOs,
			List<String> pgNames) {

		log.info("Saving Product Group Products.........");

		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(companyId);

		List<Long> pgIds = new ArrayList<>();

		for (String pg : pgNames) {

			if (pg != null) {
				Optional<ProductGroup> optionalPG = productGroups.stream().filter(p -> p.getName().equals(pg))
						.findAny();

				if (optionalPG.isPresent()) {
					pgIds.add(optionalPG.get().getId());
				}
			}
		}

		deleteByCompanyAndProductGroup(companyId, pgIds);

		Set<ProductGroupProduct> saveUpdateProductGroupProducts = new HashSet<>();

		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
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

		log.info("Sync completed in {} ms", elapsedTime);

	}

	@Transactional
	public void deleteByCompanyAndProductGroup(long companyId, List<Long> pgIds) {
		productGroupProductRepository.deleteByCompanyAndProductGroup(companyId, pgIds);
	}
}
