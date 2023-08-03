package com.orderfleet.webapp.web.vendor.excelHcreation;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductGroupProductDTO;
import com.orderfleet.webapp.web.vendor.excel.service.ProductProfileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductProfileService {

    private final Logger log = LoggerFactory.getLogger(ProductProfileUploadService.class);
    @Inject
    private BulkOperationRepositoryCustom bulkOperationRepositoryCustom;
    @Inject
    private  SyncOperationRepository syncOperationRepository;
    @Inject
    private CompanyRepository companyRepository;
    @Inject
    private DivisionRepository divisionRepository;
    @Inject
    private  ProductCategoryRepository productCategoryRepository;
    @Inject
    private ProductGroupRepository productGroupRepository;
    @Inject
    private  ProductProfileRepository productProfileRepository;
    @Inject
    private ProductGroupProductRepository productGroupProductRepository;
    @Inject
    private  StockLocationRepository stockLocationRepository;
    @Inject
    private OpeningStockRepository openingStockRepository;

    @Autowired
    private StockLocationService stockLocationService;

    @Transactional
    public void saveUpdateProductProfiles(final List<ProductProfileDTO> productProfileDTOs,
                                          final SyncOperation syncOperation) {
        log.info("Saving product profiles of H creation");
        long start = System.nanoTime();
        final Company company = syncOperation.getCompany();
        Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();
        // find all exist product profiles
        Set<String> ppAlias = productProfileDTOs.stream().map(p -> p.getProductId()).collect(Collectors.toSet());
//		List<ProductProfile> productProfiles = productProfileRepository
//				.findByCompanyIdAndAliasIgnoreCaseIn(company.getId(), ppAlias);

        List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId();

        List<TPProductGroupProductDTO> productGroupProductDTOs = new ArrayList<>();

        List<ProductGroupDTO> productGroupDtos = new ArrayList<>();

        List<StockLocationDTO> stockLocationDTOs = new ArrayList<>();

        List<OpeningStockDTO> openingStockDtos = new ArrayList<>();

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
            productCategory.setDataSourceType(DataSourceType.VENDOR);
            productCategory.setCompany(company);
            productCategory = productCategoryRepository.save(productCategory);
        } else {
            productCategory = defaultCategory.get();
        }

        for (ProductProfileDTO ppDto : productProfileDTOs) {
            // check exist by name, only one exist with a name

            ProductProfile productProfile = new ProductProfile();

                Optional<ProductProfile> optionalPP = productProfiles.stream()
                        .filter(p -> p.getProductId().equalsIgnoreCase(ppDto.getProductId())).findAny();

                if (optionalPP.isPresent()) {
                    productProfile = optionalPP.get();

                } else {
                    productProfile = new ProductProfile();
                    productProfile.setPid(com.orderfleet.webapp.service.ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
                    productProfile.setCompany(company);
                    productProfile.setDivision(defaultDivision);
                    productProfile.setDataSourceType(DataSourceType.TALLY);

            }
            productProfile.setName(ppDto.getName());
            productProfile.setProductId(ppDto.getProductId());
            productProfile.setAlias(ppDto.getAlias());
            productProfile.setDescription(ppDto.getDescription());
            productProfile.setPrice(ppDto.getPrice());
            productProfile.setMrp(ppDto.getMrp());
            productProfile.setTaxRate(ppDto.getTaxRate());
            productProfile.setSku(ppDto.getSku());
            productProfile.setActivated(true);
            productProfile.setSize(ppDto.getSize());
            productProfile.setHsnCode(ppDto.getHsnCode());

            productProfile.setUnitQty(ppDto.getUnitQty() != null ? ppDto.getUnitQty() : 1.0);

            productProfile.setProductCategory(defaultCategory.get());


            TPProductGroupProductDTO productGroupProductDTO = new TPProductGroupProductDTO();

            if (ppDto.getProductCategoryName() != null && !ppDto.getProductCategoryName().equals("")) {
            } else {
                ppDto.setProductCategoryName("General");
            }

            productGroupProductDTO.setGroupName(ppDto.getProductCategoryName());
            productGroupProductDTO.setProductName(ppDto.getName());

            productGroupProductDTOs.add(productGroupProductDTO);

            ProductGroupDTO productGroupDTO = new ProductGroupDTO();
            productGroupDTO.setName(ppDto.getProductCategoryName());
            productGroupDTO.setAlias(ppDto.getProductCategoryName());

            productGroupDtos.add(productGroupDTO);
            // }
            saveUpdateProductProfiles.add(productProfile);

        }

        bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);
//        log.info("Saving product groups");
//        saveUpdateProductGroups(productGroupDtos);
        log.info("Saving product group product profiles");
        saveUpdateProductGroupProduct(productGroupProductDTOs);
        long end = System.nanoTime();
        double elapsedTime = (end - start) / 1000000.0;
        // update sync table
        syncOperation.setCompleted(true);
        syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
        syncOperation.setLastSyncTime(elapsedTime);
        syncOperationRepository.save(syncOperation);
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

//    public void saveUpdateProductGroups(final List<ProductGroupDTO> productGroupDTOs) {
//        log.info("Saving Product Groups.........");
//        long start = System.nanoTime();
//        Set<ProductGroup> saveUpdateProductGroups = new HashSet<>();
//
//        final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
//        Company company = companyRepository.findOne(companyId);
//        // find all product group
//        List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(company.getId());
//        for (ProductGroupDTO pgDto : productGroupDTOs) {
//            // check exist by name, only one exist with a name
//            Optional<ProductGroup> optionalPG = productGroups.stream().filter(p -> p.getName().equals(pgDto.getName()))
//                    .findAny();
//            ProductGroup productGroup;
//            if (optionalPG.isPresent()) {
//                productGroup = optionalPG.get();
//                // if not update, skip this iteration.
//                if (!productGroup.getThirdpartyUpdate()) {
//                    continue;
//                }
//            } else {
//                productGroup = new ProductGroup();
//                productGroup.setPid(ProductGroupService.PID_PREFIX + RandomUtil.generatePid());
//                productGroup.setName(pgDto.getName());
//                productGroup.setDataSourceType(DataSourceType.TALLY);
//                productGroup.setCompany(company);
//            }
//            productGroup.setAlias(pgDto.getAlias());
//            productGroup.setDescription(pgDto.getDescription());
//            productGroup.setActivated(true);
//
//            Optional<ProductGroup> opPgs = saveUpdateProductGroups.stream()
//                    .filter(so -> so.getName().equalsIgnoreCase(pgDto.getName())).findAny();
//            if (opPgs.isPresent()) {
//                continue;
//            }
//
//            saveUpdateProductGroups.add(productGroup);
//        }
//        bulkOperationRepositoryCustom.bulkSaveProductGroup(saveUpdateProductGroups);
//        long end = System.nanoTime();
//        double elapsedTime = (end - start) / 1000000.0;
//        // update sync table
//
//        log.info("Sync completed in {} ms", elapsedTime);
//    }

}
