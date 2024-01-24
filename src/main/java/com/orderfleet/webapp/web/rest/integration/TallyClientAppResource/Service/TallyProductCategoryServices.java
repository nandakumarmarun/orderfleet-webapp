package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.AccountStatus;
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
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TallyProductCategoryServices {

    private final Logger log = LoggerFactory.getLogger(TPAccountProfileManagementService.class);

    private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

    private final SyncOperationRepository syncOperationRepository;
    @Autowired
    private AlterIdMasterService alterIdMasterService;

    private final ProductCategoryRepository productCategoryRepository;

    public TallyProductCategoryServices(
            BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
            SyncOperationRepository syncOperationRepository,
            AlterIdMasterService alterIdMasterService,
            ProductCategoryRepository productCategoryRepository) {
        this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
        this.syncOperationRepository = syncOperationRepository;
        this.alterIdMasterService = alterIdMasterService;
        this.productCategoryRepository = productCategoryRepository;
    }

    @Transactional
    public void saveUpdateProductCategoriesUpdateIdNew(
            final List<ProductCategoryDTO> productCategoryDTOs,
            final SyncOperation syncOperation) {
        long start = System.nanoTime();
        final Company company = syncOperation.getCompany();
        Set<ProductCategory> saveUpdateProductCategories = new HashSet<>();
        List<ProductCategory> productCategories =
                productCategoryRepository.findAllByCompanyId();
        for (ProductCategoryDTO pcDto : productCategoryDTOs) {
            Optional<ProductCategory> optionalPC = null;

            optionalPC = productCategories.stream()
                    .filter(p -> p.getProductCategoryId() != null
                            && !p.getProductCategoryId().equals("")
                            ? p.getProductCategoryId().equals(pcDto.getProductCategoryId())
                            : false)
                    .findAny();

            log.debug("is present " + optionalPC.isPresent());
            ProductCategory productCategory = new ProductCategory();

            if (optionalPC.isPresent()) {
                productCategory = optionalPC.get();
                if (!productCategory.getThirdpartyUpdate()) {
                    continue;
                }
            } else {
                Optional<ProductCategory> optionalPCName = productCategories.stream()
                        .filter(p -> p.getName() != null && !p.getName().equals("")
                                ? p.getName().equals(pcDto.getName())
                                : false)
                        .findAny();
                if(!optionalPCName.isPresent()) {
                    productCategory.setPid(ProductCategoryService.PID_PREFIX + RandomUtil.generatePid());
                    productCategory.setProductCategoryId(pcDto.getProductCategoryId());
                    productCategory.setDataSourceType(DataSourceType.TALLY);
                    productCategory.setDescription(pcDto.getName());
                    productCategory.setCompany(company);
                }else {
                    productCategory = optionalPCName.get();
                    productCategory.setProductCategoryId(pcDto.getProductCategoryId());
                }
            }
            productCategory.setName(pcDto.getName());
            productCategory.setAlias(pcDto.getAlias());
            productCategory.setDescription(pcDto.getName());
            productCategory.setActivated(pcDto.getActivated());
            Optional<ProductCategory> opAccP =
                    saveUpdateProductCategories
                            .stream()
                            .filter(so -> so.getName()
                                    .equalsIgnoreCase(pcDto.getName()))
                            .findAny();

            if (opAccP.isPresent()) {
                continue;
            }
            saveUpdateProductCategories.add(productCategory);
        }

        bulkOperationRepositoryCustom
                .bulkSaveProductCategory(
                        saveUpdateProductCategories);

        Long Listcount = productCategoryDTOs.parallelStream()
                .filter(productCategoryDTO -> productCategoryDTO.getAlterId() == null)
                .count();

        log.debug(" Product Category DTOs Null Count : " + Listcount);

        if(Listcount == 0) {
            ProductCategoryDTO productCategoryDTO = productCategoryDTOs.stream()
                    .max(Comparator.comparingLong(ProductCategoryDTO::getAlterId)).get();
            AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
            alterIdMasterDTO.setAlterId(productCategoryDTO.getAlterId());
            alterIdMasterDTO.setMasterName(TallyMasters.PRODUCT_CATEGORY.toString());
            alterIdMasterDTO.setCompanyId(company.getId());
            if(productCategoryDTO.getAlterId() != 0) {
                alterIdMasterService.save(alterIdMasterDTO);
            }
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
