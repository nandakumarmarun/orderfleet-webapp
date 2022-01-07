package com.orderfleet.webapp.web.vendor.focus.service;

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
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductGroupProductDTO;
import com.orderfleet.webapp.web.vendor.focus.dto.ProductProfileFocus;

@Service
public class ProductProfileFocusUploadService {

	private final Logger log = LoggerFactory.getLogger(ProductProfileFocusUploadService.class);

	private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	private final DivisionRepository divisionRepository;

	private final ProductCategoryRepository productCategoryRepository;

	private final ProductGroupRepository productGroupRepository;

	private final ProductProfileRepository productProfileRepository;

	private final ProductGroupProductRepository productGroupProductRepository;

	private final CompanyRepository companyRepository;

	public ProductProfileFocusUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
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

	public void saveUpdateProductProfiles(List<ProductProfileFocus> list) {
		
		log.info("Saving Product Profiles.........");
		
		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		
		Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();
		
		Set<String> ppNames = list.stream().map(p -> p.getMaster_Name()).collect(Collectors.toSet());
		
		List<ProductProfile> productProfiles = productProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);
		List<ProductCategory> productCategorys = productCategoryRepository.findByCompanyId(company.getId());

		List<ProductGroupDTO> productGroupDtos = new ArrayList<>();

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
				for (ProductProfileFocus ppDto :list) {
					// check exist by name, only one exist with a name
					Optional<ProductProfile> optionalPP = productProfiles.stream()
							.filter(p -> p.getName().equals(ppDto.getMaster_Name())).findAny();
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
						productProfile.setName(ppDto.getMaster_Name());
						productProfile.setDivision(defaultDivision);
						productProfile.setDataSourceType(DataSourceType.TALLY);
					}

	
					productProfile.setProductId(ppDto.getMaster_Code());
					productProfile.setPrice(BigDecimal.valueOf(0));
					productProfile.setMrp(0);
					productProfile.setActivated(true);

					Optional<ProductProfile> opAccP = saveUpdateProductProfiles.stream()
							.filter(so -> so.getName().equalsIgnoreCase(ppDto.getMaster_Name())).findAny();
					if (opAccP.isPresent()) {
						continue;
					}
					productProfile.setUnitQty(1.0);
					productProfile.setProductCategory(defaultCategory.get());
					TPProductGroupProductDTO productGroupProductDTO = new TPProductGroupProductDTO();
                
//					ProductGroup defaultPg = productGroupRepository.findFirstByCompanyId(company.getId());
				
//					Optional<ProductGroup> defaultProductGroup =productGroupRepository. findByCompanyIdAndNameIgnoreCase(company.getId(),"General");
                   ProductGroup defaultpg =productGroupRepository.findFirstByCompanyIdOrderByIdAsc(companyId);
//					System.out.println("************"+defaultpg);
                   productGroupProductDTO.setGroupName(defaultpg.getName());
					
					productGroupProductDTO.setProductName(ppDto.getMaster_Name());

					productGroupProductDTOs.add(productGroupProductDTO);

					ProductGroupDTO productGroupDTO = new ProductGroupDTO();
					productGroupDTO.setName(defaultpg.getName());
			

					productGroupDtos.add(productGroupDTO);

					saveUpdateProductProfiles.add(productProfile);
				}
				log.info("Saving product profiles");
				bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);
				log.info("Saving product groups");
				saveUpdateProductGroups(productGroupDtos);
				log.info("Saving product group product profiles");
				saveUpdateProductGroupProduct(productGroupProductDTOs);
				
				long end = System.nanoTime();
				double elapsedTime = (end - start) / 1000000.0;
				// update sync table

				log.info("Sync completed in {} ms", elapsedTime);
			}

	private void saveUpdateProductGroupProduct(List<TPProductGroupProductDTO> productGroupProductDTOs) {
		// TODO Auto-generated method stub
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

	private void saveUpdateProductGroups(List<ProductGroupDTO> productGroupDtos) {
		// TODO Auto-generated method stub
		log.info("Saving Product Groups.........");
		long start = System.nanoTime();
		Set<ProductGroup> saveUpdateProductGroups = new HashSet<>();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		// find all product group
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(company.getId());
		for (ProductGroupDTO pgDto : productGroupDtos) {
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
				}


