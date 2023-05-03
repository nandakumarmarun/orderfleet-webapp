package com.orderfleet.webapp.web.vendor.focus.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductGroupProductDTO;
import com.orderfleet.webapp.web.vendor.focus.dto.ProductProfileFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.ProductProfileNewFocus;

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
	
	private final SyncOperationRepository syncOperationRepository;

	public ProductProfileFocusUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
			DivisionRepository divisionRepository, ProductCategoryRepository productCategoryRepository,
			ProductGroupRepository productGroupRepository, ProductProfileRepository productProfileRepository,
			ProductGroupProductRepository productGroupProductRepository, CompanyRepository companyRepository,SyncOperationRepository syncOperationRepository) {
		super();
		this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
		this.divisionRepository = divisionRepository;
		this.productCategoryRepository = productCategoryRepository;
		this.productGroupRepository = productGroupRepository;
		this.productProfileRepository = productProfileRepository;
		this.productGroupProductRepository = productGroupProductRepository;
		this.companyRepository = companyRepository;
		this.syncOperationRepository = syncOperationRepository;
	}

	final Long companyId = (long) 304975;
	public void saveUpdateProductProfiles(List<ProductProfileNewFocus> list) {
		
		log.info("Saving Product Profiles.........");
		
		long start = System.nanoTime();
//		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		
		Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();
		
		Set<String> ppNames = list.stream().map(p -> p.getItemName()).collect(Collectors.toSet());
		
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
				for (ProductProfileNewFocus ppDto :list) {
					// check exist by name, only one exist with a name
					Optional<ProductProfile> optionalPP = productProfiles.stream()
							.filter(p -> p.getName().equals(ppDto.getItemName())).findAny();
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
						productProfile.setName(ppDto.getItemName());
						productProfile.setDivision(defaultDivision);
						productProfile.setDataSourceType(DataSourceType.TALLY);
						productProfile.setWidth(Double.parseDouble(ppDto.itemwidth));
						productProfile.setRateConversion(Double.parseDouble(ppDto.getRateConversion()));
					}

	
					productProfile.setProductId(ppDto.getItemCode());
					productProfile.setPrice(BigDecimal.valueOf(ppDto.getSellingRate()));
					productProfile.setTaxRate(ppDto.getGstPer());
					productProfile.setMrp(0);
					productProfile.setActivated(true);
					productProfile.setWidth(Double.parseDouble(ppDto.itemwidth));
					productProfile.setRateConversion(Double.parseDouble(ppDto.getRateConversion()));
					
					if (ppDto.getHsnCode() != null && !ppDto.getHsnCode().equals("")) {
						productProfile.setHsnCode(ppDto.getHsnCode());
					}
					
					productProfile.setSku(ppDto.getBaseUnits());
					
//					if (ppDto.getSellingRate() != null && !ppDto.getSellingRate().equals("")) {
//						productProfile.setPrice(BigDecimal.valueOf(Double.valueOf(ppDto.getStandard_price())));
//					} else {
//						productProfile.setPrice(BigDecimal.valueOf(0));
//					}

					Optional<ProductProfile> opAccP = saveUpdateProductProfiles.stream()
							.filter(so -> so.getName().equalsIgnoreCase(ppDto.getItemName())).findAny();
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
                   productGroupProductDTO.setGroupName(ppDto.getItemType() != null ? ppDto.getItemType() :defaultpg.getName());
					
					productGroupProductDTO.setProductName(ppDto.getItemName());

					productGroupProductDTOs.add(productGroupProductDTO);

					ProductGroupDTO productGroupDTO = new ProductGroupDTO();
					productGroupDTO.setName(ppDto.getItemType());
					productGroupDTO.setAlias(ppDto.getItemType());
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
				Optional<SyncOperation> oPsyncOperation = syncOperationRepository.findOneByCompanyIdAndOperationType(companyId, SyncOperationType.PRODUCTPROFILE);
				SyncOperation syncOperation;
				if(oPsyncOperation.isPresent()){
					syncOperation = oPsyncOperation.get();
					syncOperation.setOperationType(SyncOperationType.PRODUCTPROFILE);
					syncOperation.setCompleted(true);
					syncOperation.setLastSyncStartedDate(LocalDateTime.now());
					syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
					syncOperation.setLastSyncTime(elapsedTime);
					syncOperation.setCompany(company);
				}else{
					syncOperation = new SyncOperation();
					syncOperation.setOperationType(SyncOperationType.PRODUCTPROFILE);
					syncOperation.setCompleted(true);
					syncOperation.setLastSyncStartedDate(LocalDateTime.now());
					syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
					syncOperation.setLastSyncTime(elapsedTime);
					syncOperation.setCompany(company);
				}
				System.out.println( "syncCompleted Date : "+syncOperation.getLastSyncCompletedDate());
				syncOperationRepository.save(syncOperation);
				log.info("Sync completed in {} ms", elapsedTime);
			}


	private void saveUpdateProductGroupProduct(List<TPProductGroupProductDTO> productGroupProductDTOs) {
		log.debug("Saving Product Group Products : ");
		long start = System.nanoTime();
//		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		log.debug("Login details : " +"["+ companyId + "," + company.getLegalName()+"]");
		log.debug("initializing session Registries");
		List<ProductGroupProduct> newProductGroupProducts = new ArrayList<>();
		log.debug("Clearing Existing Association");
		productGroupProductRepository.deleteByCompany(companyId);
		log.debug("Fetching product profiles");
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
		log.debug("Fetching product Groups");
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(company.getId());
		log.debug("Processing With New data");
		for (TPProductGroupProductDTO pgpDto : productGroupProductDTOs)
		{
			ProductGroupProduct productGroupProduct = new ProductGroupProduct();
			Optional<ProductGroup> opPg = productGroups.stream()
					.filter(pl -> pgpDto.getGroupName().equals(pl.getName())).findFirst();
			Optional<ProductProfile> opPp = productProfiles.stream()
					.filter(pp -> pgpDto.getProductName().equals(pp.getName())).findFirst();
			if (opPp.isPresent() && opPg.isPresent())
			{
				productGroupProduct.setProductGroup(opPg.get());
				productGroupProduct.setProduct(opPp.get());
				productGroupProduct.setCompany(company);
				newProductGroupProducts.add(productGroupProduct);
			}
		}
		log.debug("Saving new Product Group product Association");
		List<ProductGroupProduct> result = productGroupProductRepository.save(newProductGroupProducts);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		log.info("Sync completed in {} ms", elapsedTime);
	}


	private void saveUpdateProductGroups(List<ProductGroupDTO> productGroupDtos) {
		// TODO Auto-generated method stub
		log.info("Saving Product Groups.........");
		long start = System.nanoTime();
		Set<ProductGroup> saveUpdateProductGroups = new HashSet<>();

//		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
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


