package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.TallyMasters;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AlterIdMasterService;

import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.async.TPAccountProfileManagementService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AlterIdMasterDTO;
import com.orderfleet.webapp.web.rest.integration.dto.GSTProductGroupDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TallyGstGroupWiseServices {

    private final Logger log = LoggerFactory.getLogger(TPAccountProfileManagementService.class);
    private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;
    private final SyncOperationRepository syncOperationRepository;
    private final ProductGroupRepository productGroupRepository;
    private final ProductGroupProductRepository productGroupProductRepository;
    private final ProductProfileRepository productProfileRepository;
    @Autowired
    private AlterIdMasterService alterIdMasterService;

    public TallyGstGroupWiseServices(
            BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
            SyncOperationRepository syncOperationRepository,
            ProductGroupRepository productGroupRepository,
            ProductGroupProductRepository productGroupProductRepository,
            ProductProfileRepository productProfileRepository,
            AlterIdMasterService alterIdMasterService) {
        this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
        this.syncOperationRepository = syncOperationRepository;
        this.productGroupRepository = productGroupRepository;
        this.productGroupProductRepository = productGroupProductRepository;
        this.productProfileRepository = productProfileRepository;
        this.alterIdMasterService = alterIdMasterService;
    }

    @Transactional
    @Async
    public void saveUpdateGSTProductGroupUpdatedId(
            List<GSTProductGroupDTO> gstpgDTOs,
            SyncOperation syncOperation) {
        long start = System.nanoTime();
        final Company company = syncOperation.getCompany();
        Set<GSTProductGroup> saveUpdategstpg = new HashSet<>();
        List<ProductGroup> productGroup =
                productGroupRepository.findAllByCompanyId(true);
        productGroup.forEach(data -> {
            String[] name = data.getName().split("~");
            data.setName(name[0]);
        });

        for (GSTProductGroupDTO ppDto : gstpgDTOs) {;
            Optional<ProductGroup> pg =
                    productGroup
                            .stream()
                            .filter(pgdata -> pgdata.getName()
                                    .equals(ppDto.getProductGroupName()))
                            .findAny();

            if (pg.isPresent()) {
                GSTProductGroup gstpg = new GSTProductGroup();
                gstpg.setProductGroup(pg.get());
                gstpg.setHsnsacCode(ppDto.getHsnsacCode());
                gstpg.setApplyDate(ppDto.getApplyDate());
                gstpg.setCentralTax(ppDto.getCentralTax());
                gstpg.setStateTax(ppDto.getStateTax());
                gstpg.setIntegratedTax(ppDto.getIntegratedTax());
                gstpg.setAditionalCess(ppDto.getAditionalCess());
                gstpg.setTaxType(ppDto.getTaxType());
                gstpg.setCompany(company);
                saveUpdategstpg.add(gstpg);
            }
        }
        bulkOperationRepositoryCustom.bulkSaveGSTProductGroup(saveUpdategstpg);

        for (GSTProductGroup gstpg : saveUpdategstpg) {
            List<String> productGroups = new ArrayList<>();
            productGroups.add(gstpg.getProductGroup().getPid());
            List<ProductProfile> productProfiles =
                    productGroupProductRepository
                            .findProductsByProductGroupPids(productGroups);
            if (productProfiles.size() > 0) {
                double vat = 0;
                if (gstpg.getIntegratedTax() != null
                        && (!gstpg.getIntegratedTax().equalsIgnoreCase(""))) {
                    vat = Double.valueOf(gstpg.getIntegratedTax().replace("%", ""));
                }
                productProfileRepository.updateTaxRate(vat, productProfiles);
            }
        }
        long end = System.nanoTime();
        double elapsedTime = (end - start) / 1000000.0;

        List<ProductGroup> productGroupcopy =
                productGroupRepository
                        .findAllByCompanyId(true);
        if(!productGroupcopy.isEmpty()) {
            productGroupcopy.forEach(data -> {
                if(!data.getName().contains("~")) {
                    data.setName(data.getName() + "~" + data.getProductGroupId());
                }
            });}
        productGroupRepository.save(productGroupcopy);
        syncOperation.setCompleted(true);
        syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
        syncOperation.setLastSyncTime(elapsedTime);
        syncOperationRepository.save(syncOperation);
        log.info("Sync completed in {} ms", elapsedTime);
    }

}
