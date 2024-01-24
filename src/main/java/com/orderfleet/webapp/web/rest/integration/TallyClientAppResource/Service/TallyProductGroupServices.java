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
import com.orderfleet.webapp.web.rest.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TallyProductGroupServices {
    private final Logger log = LoggerFactory.getLogger(TPAccountProfileManagementService.class);
    private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;
    private final ProductGroupRepository productGroupRepository;
    @Inject
    private SyncOperationRepository syncOperationRepository;
    @Autowired
    private AlterIdMasterService alterIdMasterService;
    public TallyProductGroupServices(
            BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
            ProductGroupRepository productGroupRepository,
            SyncOperationRepository syncOperationRepository,
            AlterIdMasterService alterIdMasterService) {
        this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
        this.productGroupRepository = productGroupRepository;
        this.syncOperationRepository = syncOperationRepository;
        this.alterIdMasterService = alterIdMasterService;
    }

    @Transactional
    public void
    saveUpdateProductGroupsUpdateIdNew(
            final List<ProductGroupDTO> productGroupDTOs,
            final SyncOperation syncOperation) {
        long start = System.nanoTime();
        final Company company = syncOperation.getCompany();
        Set<ProductGroup> saveUpdateProductGroups = new HashSet<>();

        // find all product group
        List<ProductGroup> productGroups =
                productGroupRepository.findAllByCompanyId();

        for (ProductGroupDTO pgDto : productGroupDTOs) {
            Optional<ProductGroup> optionalPG = null;
            optionalPG = productGroups.stream()
                    .filter(p -> p.getProductGroupId() != null
                            && !p.getProductGroupId().equals("")
                            ? p.getProductGroupId().equals(pgDto.getProductGroupId())
                            : false)
                    .findAny();

            ProductGroup productGroup =new ProductGroup();
            if (optionalPG.isPresent()) {
                productGroup = optionalPG.get();
                if (!productGroup.getThirdpartyUpdate()) {
                    continue;
                }
            } else {
                Optional<ProductGroup> optionalPGname =
                        productGroups
                                .stream()
                                .filter(p -> p.getName() != null
                                        && !p.getName().equals("")
                                        ? p.getName().equals(pgDto.getName())
                                        : false)
                                .findAny();
                if(!optionalPGname.isPresent()) {
                    productGroup.setPid(ProductGroupService.PID_PREFIX + RandomUtil.generatePid());
                    productGroup.setProductGroupId(pgDto.getProductGroupId());
                    productGroup.setDataSourceType(DataSourceType.TALLY);
                    productGroup.setDescription(pgDto.getName());
                    productGroup.setCompany(company);
                }else {
                    productGroup= optionalPGname.get();
                    productGroup.setProductGroupId(pgDto.getProductGroupId());
                }
            }
            productGroup.setAlias(pgDto.getAlias());
            productGroup.setName(pgDto.getName());
            productGroup.setDescription(pgDto.getName());
            productGroup.setActivated(pgDto.getActivated());
            Optional<ProductGroup> opAccP =
                    saveUpdateProductGroups
                            .stream()
                            .filter(so -> so.getName()
                                    .equalsIgnoreCase(pgDto.getName()))
                            .findAny();
            if (opAccP.isPresent()) {
                continue;
            }
            saveUpdateProductGroups.add(productGroup);
        }

        bulkOperationRepositoryCustom.bulkSaveProductGroup(saveUpdateProductGroups);
        Long Listcount = productGroupDTOs
                .parallelStream()
                .filter(productGroupDTO -> productGroupDTO.getAlterId() == null)
                .count();

        if(Listcount == 0) {
            ProductGroupDTO productGroupDTO = productGroupDTOs.stream()
                    .max(Comparator.comparingLong(ProductGroupDTO::getAlterId)).get();
            AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
            alterIdMasterDTO.setAlterId(productGroupDTO.getAlterId());
            alterIdMasterDTO.setMasterName(TallyMasters.PRODUCT_GROUP.toString());
            alterIdMasterDTO.setCompanyId(company.getId());
            alterIdMasterService.save(alterIdMasterDTO);
        }
        long end = System.nanoTime();
        double elapsedTime = (end - start) / 1000000.0;
        // update sync table
        syncOperation.setCompleted(true);
        syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
        syncOperation.setLastSyncTime(elapsedTime);
        syncOperationRepository.save(syncOperation);
        log.info("Sync completed in {} ms", elapsedTime);
    }

}
