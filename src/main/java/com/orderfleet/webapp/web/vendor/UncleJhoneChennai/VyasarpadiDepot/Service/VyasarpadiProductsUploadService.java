package com.orderfleet.webapp.web.vendor.UncleJhoneChennai.VyasarpadiDepot.Service;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.ProductUJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VyasarpadiProductsUploadService {

	private final Logger log = LoggerFactory.getLogger(VyasarpadiProductsUploadService.class);

	private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	private final DivisionRepository divisionRepository;

	private final ProductCategoryRepository productCategoryRepository;

	private final ProductGroupRepository productGroupRepository;

	private final ProductProfileRepository productProfileRepository;

	private final ProductGroupProductRepository productGroupProductRepository;

	private final CompanyRepository companyRepository;

	public VyasarpadiProductsUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
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

	public void saveUpdateProductProfiles(
			List<ProductUJ> productUJ) {
		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		String Thread = "Thread "+RandomUtil.generateThread()+" -- ["+company.getLegalName()+"] : ";
		log.info(Thread + "Enter : saveUpdateProductProfiles() ");

		Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();

		List<ProductGroupDTO> productGroupDtos = new ArrayList<>();

		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(company.getId());

		Division defaultDivision = divisionRepository.findFirstByCompanyId(company.getId());

		Optional<ProductCategory> defaultCategory =
				productCategoryRepository
						.findByCompanyIdAndNameIgnoreCase(
								company.getId(),
								"Not Applicable");

		log.debug(Thread + "Defalut Category : " + defaultCategory.get().getName());

		for (ProductUJ ppDto : productUJ) {
			ProductProfile productProfile;
			Optional<ProductProfile> optionalPP = productProfiles
					.stream()
					.filter(p -> p.getProductId().trim()
							.equalsIgnoreCase(ppDto.getCode().trim()))
					.findAny();

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
			productProfile.setName(ppDto.getName().trim() + "-" + ppDto.getCode().trim());
			productProfile.setPrice(BigDecimal.valueOf(ppDto.getWholesalePrice()));
			productProfile.setTaxRate(ppDto.getTaxRate());
			productProfile.setMrp(ppDto.getMrp());
			productProfile.setActivated(true);
			productProfile.setSku(ppDto.getSku());
			productProfile.setProductCode(ppDto.getAlphaCode());
			productProfile.setCompoundUnitQty(Double.valueOf(ppDto.getUnitMlLtr()));
			productProfile.setStockQty(Double.valueOf(ppDto.getStockQty()));
			productProfile.setUnitQty(Double.valueOf(ppDto.getUnitQty()));
			productProfile.setProductCategory(defaultCategory.get());

			if (ppDto.getHsnCode() != null && !ppDto.getHsnCode().equals("")) {
				productProfile.setHsnCode(ppDto.getHsnCode());
			}

			ProductGroupDTO productGroupDTO = new ProductGroupDTO();
			productGroupDTO.setName(ppDto.getGroup());
			productGroupDTO.setAlias(ppDto.getGroup());
			productGroupDtos.add(productGroupDTO);
			saveUpdateProductProfiles.add(productProfile);

		}

		bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);
		log.debug(Thread + "SaveProductProfile :"+saveUpdateProductProfiles.size());
		log.debug(Thread + "Default Category : " + defaultCategory.get().getName());

		List<ProductGroupDTO> productGroups =
				productGroupDtos
						.stream()
						.map(ProductGroupDTO :: getName)
						.distinct()
						.map(name -> productGroupDtos
								.stream()
								.filter(pg -> pg.getName()
										.equals(name))
								.findFirst()
								.get())
						.collect(Collectors.toList());

		log.debug(Thread + "Distinct size :"+productGroups.size());
		saveUpdateProductGroups(productGroups);
		saveSync(start);
	}

	@Transactional
	private void saveUpdateProductGroups(
			List<ProductGroupDTO> productGroupDtos) {
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		String Thread = "Thread "+RandomUtil.generateThread()+" -- ["+company.getLegalName()+"] : ";
		log.info(Thread + "Enter : saveUpdateProductGroups() ");
		long start = System.nanoTime();

		Set<ProductGroup> saveUpdateProductGroups = new HashSet<>();

		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(company.getId());

		log.debug(Thread + "Processing Data");
		for (ProductGroupDTO pgDto : productGroupDtos) {
			Optional<ProductGroup> optionalPG = productGroups
					.stream()
					.filter(p -> p.getName()
							.equals(pgDto.getName()))
					.findAny();
			ProductGroup productGroup;
			if (optionalPG.isPresent()) {
				productGroup = optionalPG.get();
			} else {
				productGroup = new ProductGroup();
				productGroup.setPid(ProductGroupService.PID_PREFIX + RandomUtil.generatePid());
				productGroup.setName(pgDto.getName());
				productGroup.setDataSourceType(DataSourceType.VENDOR);
				productGroup.setCompany(company);
			}
			productGroup.setDescription(pgDto.getName());
			productGroup.setAlias(pgDto.getAlias());
			productGroup.setActivated(true);
			saveUpdateProductGroups.add(productGroup);
		}
		log.debug(Thread + "Saving Product Groups: " + saveUpdateProductGroups);
		bulkOperationRepositoryCustom.bulkSaveProductGroup(saveUpdateProductGroups);
		saveSync(start);
	}

	public void saveProductGroupProduct(
			List<ProductUJ> productUJ) {
		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		String Thread = "Thread "+RandomUtil.generateThread()+" -- ["+company.getLegalName()+"] : ";
		log.info(Thread + "Enter : saveProductGroupProduct() ");

		List<ProductGroupProduct> newProductGroupProducts = new ArrayList<>();

		productGroupProductRepository.deleteByCompany(companyId);

		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);

		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyId();

		Optional<ProductGroup> defaultGroups = productGroupRepository
				.findByCompanyIdAndNameIgnoreCase(
						companyId,
						"General");

		log.debug(Thread + "Processing Data");
		for (ProductUJ product : productUJ) {
			ProductGroupProduct productGroupProduct = new ProductGroupProduct();

			Optional<ProductProfile> opPp = productProfiles
					.stream()
					.filter(pp -> product.getCode().trim()
							.equalsIgnoreCase(pp.getProductId().trim()))
					.findAny();

			Optional<ProductGroup> opPG = productGroups
					.stream()
					.filter(pg -> product.getGroup()
							.equalsIgnoreCase(pg.getName()))
					.findAny();

			if (opPp.isPresent() && opPG.isPresent()) {
				productGroupProduct.setProductGroup(opPG.get());
				productGroupProduct.setProduct(opPp.get());
				productGroupProduct.setCompany(company);
			} else {
				productGroupProduct.setProductGroup(defaultGroups.get());
				productGroupProduct.setProduct(opPp.get());
				productGroupProduct.setCompany(company);
			}
			newProductGroupProducts.add(productGroupProduct);
		}
		log.debug(Thread + "Saving New Product Groups Products");
		productGroupProductRepository.save(newProductGroupProducts);
		saveSync(start);

	}

	private void saveSync(long start) {
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		log.info("Sync completed in {} ms", elapsedTime);
	}


}
