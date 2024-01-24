package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.TallyMasters;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.service.AlterIdMasterService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.TaxMasterService;
import com.orderfleet.webapp.service.async.TPAccountProfileManagementService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AlterIdMasterDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.TaxMasterDTO;
import com.orderfleet.webapp.web.rest.mapper.TaxMasterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TallyTaxMasterService {

    private final Logger log = LoggerFactory.getLogger(TPAccountProfileManagementService.class);

    private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

    private final SyncOperationRepository syncOperationRepository;

    private final CompanyConfigurationRepository companyConfigurationRepository;

    private final TaxMasterMapper taxMasterMapper;

    private final TaxMasterRepository taxMasterRepository;


    public TallyTaxMasterService(
            BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
            SyncOperationRepository syncOperationRepository,
            CompanyConfigurationRepository companyConfigurationRepository,
            TaxMasterMapper taxMasterMapper, TaxMasterRepository taxMasterRepository,
            AlterIdMasterService alterIdMasterService) {
        this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
        this.syncOperationRepository = syncOperationRepository;
        this.companyConfigurationRepository = companyConfigurationRepository;
        this.taxMasterMapper = taxMasterMapper;
        this.taxMasterRepository = taxMasterRepository;
        this.alterIdMasterService = alterIdMasterService;
    }

    @Autowired
    private AlterIdMasterService alterIdMasterService;

    boolean flag= false;

    @Transactional
    public void saveUpdateTaxMasters(
            List<TaxMasterDTO> taxMasterDTOs,
            SyncOperation syncOperation) {
        long start = System.nanoTime();
        final Company company = syncOperation.getCompany();
        Set<TaxMaster> saveUpdateTaxMasters = new HashSet<>();

        // find all taxMasters
        List<TaxMaster> taxMasters =
                taxMasterRepository
                        .findAllByCompanyId(company.getId());

        for (TaxMasterDTO locDto : taxMasterDTOs) {
            // check exist by name, only one exist with a name
            Optional<TaxMaster> optionalLoc =
                    taxMasters.stream()
                            .filter(p -> p.getVatName()
                                    .equals(locDto.getVatName()))
                            .findAny();

            TaxMaster taxMaster;
            if (optionalLoc.isPresent()) {
                taxMaster = optionalLoc.get();
                taxMaster.setDescription(locDto.getDescription());
                taxMaster.setVatClass(locDto.getVatClass());
                taxMaster.setVatPercentage(locDto.getVatPercentage());
                taxMaster.setDescription(locDto.getDescription());
            } else {
                taxMaster = new TaxMaster();
                taxMaster = taxMasterMapper.taxMasterDTOToTaxMaster(locDto);
                taxMaster.setPid(TaxMasterService.PID_PREFIX + RandomUtil.generatePid());
                taxMaster.setCompany(company);
            }
            saveUpdateTaxMasters.add(taxMaster);
        }

        bulkOperationRepositoryCustom.bulkSaveTaxMasters(saveUpdateTaxMasters);
        long end = System.nanoTime();
        double elapsedTime = (end - start) / 1000000.0;

        syncOperation.setCompleted(true);
        syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
        syncOperation.setLastSyncTime(elapsedTime);
        syncOperationRepository.save(syncOperation);
        log.info("Sync completed in {} ms", elapsedTime);
    }
}
