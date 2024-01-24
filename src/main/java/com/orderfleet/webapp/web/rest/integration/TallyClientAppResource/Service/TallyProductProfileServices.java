package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.TallyMasters;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.*;
import com.orderfleet.webapp.service.async.TPAccountProfileManagementService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AlterIdMasterDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TallyProductProfileServices {

    private final Logger log = LoggerFactory.getLogger(TPAccountProfileManagementService.class);

    private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

    private final SyncOperationRepository syncOperationRepository;

    private final ProductProfileRepository productProfileRepository;

    private final ProductCategoryRepository productCategoryRepository;

    private final CompanyConfigurationRepository companyConfigurationRepository;

    private final ProductGroupProductRepository productGroupProductRepository;

    private final DivisionRepository divisionRepository;



    @Autowired
    private AlterIdMasterService alterIdMasterService;

    boolean flag= false;


    public TallyProductProfileServices(
            BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
            SyncOperationRepository syncOperationRepository,
            ProductProfileRepository productProfileRepository,
            ProductCategoryRepository productCategoryRepository,
            CompanyConfigurationRepository companyConfigurationRepository,
            ProductGroupProductRepository productGroupProductRepository,
            DivisionRepository divisionRepository,
            AlterIdMasterService alterIdMasterService) {
        this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
        this.syncOperationRepository = syncOperationRepository;
        this.productProfileRepository = productProfileRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.companyConfigurationRepository = companyConfigurationRepository;
        this.productGroupProductRepository = productGroupProductRepository;
        this.divisionRepository = divisionRepository;
        this.alterIdMasterService = alterIdMasterService;
    }

    @Transactional
    public void saveUpdateProductProfilesUpdateIdNew(
            final List<ProductProfileDTO> productProfileDTOs,
            final SyncOperation syncOperation,
            boolean fullUpdate) {

        log.info("Saving Product Profiles : " + productProfileDTOs.size());
        long start = System.nanoTime();

        final Company company = syncOperation.getCompany();
        Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();

        Optional<CompanyConfiguration> optAliasToName =
                companyConfigurationRepository
                        .findByCompanyPidAndName(
                                company.getPid(),
                                CompanyConfig.ALIAS_TO_NAME);

        if (optAliasToName.isPresent()
                && optAliasToName.get().getValue().equalsIgnoreCase("true")) {

            for (ProductProfileDTO ppDto : productProfileDTOs) {
                String name = ppDto.getName();
                ppDto.setName(ppDto.getAlias() != null
                        && !ppDto.getAlias().equals("")
                        ? ppDto.getAlias() : name);
                ppDto.setAlias(name);
            }
        }

        List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId();
        List<ProductCategory> productCategorys = productCategoryRepository.findByCompanyId(company.getId());

        List<ProductGroupProduct> productGroupProducts = new ArrayList<>();
        Set<Long> dectivatedpp = new HashSet<>();

        Optional<CompanyConfiguration> optProductGroupTax =
                companyConfigurationRepository
                        .findByCompanyPidAndName(
                                company.getPid(),
                                CompanyConfig.PRODUCT_GROUP_TAX);

        if (optProductGroupTax.isPresent()
                && optProductGroupTax.get().getValue().equalsIgnoreCase("true")) {

            productGroupProducts =
                    productGroupProductRepository
                            .findByProductGroupProductActivatedAndCompanyId();
        }

        if(fullUpdate == true){
            List<ProductProfile> tallyproductProfiles =
                    productProfiles
                            .stream()
                            .filter(data -> data.getDataSourceType()
                                    .equals(DataSourceType.TALLY))
                            .collect(Collectors.toList());

            for(ProductProfile pp :tallyproductProfiles) {
                flag = false;
                productProfileDTOs.forEach(data ->{
                    if(pp.getProductId().equals(data.getProductId())) {
                        flag = true;
                    }
                });
                if(!flag) {
                    dectivatedpp.add(pp.getId());
                }
            }
            if(!dectivatedpp.isEmpty()) {
                productProfileRepository.deactivateProductProfileUsingInId(dectivatedpp);
            }
        }

        Division defaultDivision = divisionRepository.findFirstByCompanyId(company.getId());

        Optional<ProductCategory> defaultCategory =
                productCategoryRepository
                        .findByCompanyIdAndNameIgnoreCase(
                                company.getId(), "Not Applicable");

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

        Optional<CompanyConfiguration> optAddCompoundUnit =
                companyConfigurationRepository
                        .findByCompanyPidAndName(company.getPid(),
                                CompanyConfig.ADD_COMPOUND_UNIT);

        for (ProductProfileDTO ppDto : productProfileDTOs) {
            Optional<ProductProfile> optionalPP = null;
            optionalPP = productProfiles.stream()
                    .filter(p -> p.getProductId() != null
                            && !p.getProductId().equals("")
                            ? p.getProductId().equals(ppDto.getProductId())
                            : false)
                    .findAny();

            ProductProfile productProfile = new ProductProfile();
            if (optionalPP.isPresent()) {
                productProfile = optionalPP.get();
                productProfile.setName(ppDto.getName());
                if (!productProfile.getThirdpartyUpdate()) {
                    continue;
                }
            } else {
                productProfile.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
                productProfile.setCompany(company);
                productProfile.setProductId(ppDto.getProductId());
                productProfile.setDivision(defaultDivision);
                productProfile.setProductDescription(ppDto.getName());
                productProfile.setDataSourceType(DataSourceType.TALLY);
            }
            productProfile.setName(ppDto.getName());
            productProfile.setAlias(ppDto.getAlias());
            productProfile.setMrp(ppDto.getMrp());
            productProfile.setTaxRate(ppDto.getTaxRate());
            productProfile.setCessTaxRate(ppDto.getCessTaxRate());
            productProfile.setSku(ppDto.getSku());
            productProfile.setActivated(ppDto.getActivated());
            productProfile.setTrimChar(ppDto.getTrimChar());
            productProfile.setSize(ppDto.getSize());
            productProfile.setHsnCode(ppDto.getHsnCode());
            productProfile.setProductDescription(ppDto.getName());
            productProfile.setBarcode(ppDto.getBarcode());
            productProfile.setRemarks(ppDto.getRemarks());


            if (optProductGroupTax.isPresent()
                    && optProductGroupTax.get().getValue().equalsIgnoreCase("true")) {
                if (productGroupProducts.size() > 0) {
                    Optional<ProductGroupProduct> opPgp = productGroupProducts
                            .stream()
                            .filter(pgp -> pgp.getProduct().getName()
                                    .equals(ppDto.getName()))
                            .findAny();

                    if (opPgp.isPresent()) {
                        double taxRate = opPgp.get().getProductGroup().getTaxRate();
                        if (taxRate > 0 && ppDto.getTaxRate() == 0) {
                            productProfile.setTaxRate(taxRate);
                        }
                    }
                }
            }

            if (optAddCompoundUnit.isPresent()
                    && optAddCompoundUnit.get().getValue().equalsIgnoreCase("true")) {
                if (ppDto.getSku().contains("case of")) {
                    String numberOnly = ppDto.getSku().replaceAll("[^0-9]", "");
                    String value = numberOnly;
                    double convertionValue = Double.parseDouble(value);
                    if (convertionValue == 0) {
                        convertionValue = 1;
                    }
                    productProfile.setCompoundUnitQty(convertionValue);
                    Double cUnitQty = convertionValue;
                    productProfile.setPrice(ppDto.getPrice().multiply(new BigDecimal(cUnitQty)));
                    productProfile.setUnitQty(ppDto.getUnitQty() != null ? ppDto.getUnitQty() : 1d);
                    productProfile.setCompoundUnitQty(cUnitQty);
                } else {
                    if (ppDto.getUnitQty() != null) {
                        productProfile.setUnitQty(ppDto.getUnitQty());
                    } else {
                        productProfile.setUnitQty(1d);
                    }
                    productProfile.setPrice(ppDto.getPrice());
                }
            } else {
                if (ppDto.getUnitQty() != null) {
                    productProfile.setUnitQty(ppDto.getUnitQty());
                } else {
                    productProfile.setUnitQty(1d);
                }
                productProfile.setPrice(ppDto.getPrice());
            }

            Optional<ProductCategory> optionalCategory =
                    productCategorys.stream()
                            .filter(pl -> ppDto.getProductCategoryName()
                                    .equals(pl.getName()))
                            .findAny();

            if (optionalCategory.isPresent()) {
                productProfile.setProductCategory(optionalCategory.get());
            } else {
                productProfile.setProductCategory(productCategory);
            }
            Optional<ProductProfile> opAccP = saveUpdateProductProfiles.stream()
                    .filter(so -> so.getName().equalsIgnoreCase(ppDto.getName())).findAny();
            if (opAccP.isPresent()) {
                continue;
            }
            saveUpdateProductProfiles.add(productProfile);
        }

        bulkOperationRepositoryCustom
                .bulkSaveProductProfile(saveUpdateProductProfiles);

        Long Listcount = productProfileDTOs
                .parallelStream()
                .filter(productProfileDTO -> productProfileDTO.getAlterId() == null)
                .count();

        if(Listcount == 0) {
            ProductProfileDTO productProflleDTO = productProfileDTOs.stream()
                    .max(Comparator.comparingLong(ProductProfileDTO::getAlterId))
                    .get();
            AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
            alterIdMasterDTO.setAlterId(productProflleDTO.getAlterId());
            alterIdMasterDTO.setMasterName(TallyMasters.PRODUCT_PROFILE.toString());
            alterIdMasterDTO.setCompanyId(company.getId());
            alterIdMasterService.save(alterIdMasterDTO);
        }

        long end = System.nanoTime();
        double elapsedTime = (end - start) / 1000000.0;

        syncOperation.setCompleted(true);
        syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
        syncOperation.setLastSyncTime(elapsedTime);
        syncOperationRepository.save(syncOperation);
        log.info("Sync completed in {} ms", elapsedTime);
    }


}
