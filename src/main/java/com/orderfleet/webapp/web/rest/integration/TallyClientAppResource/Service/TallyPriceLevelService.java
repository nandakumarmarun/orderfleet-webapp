package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.TaxMaster;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.TaxMasterRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.service.AlterIdMasterService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.TaxMasterService;
import com.orderfleet.webapp.service.async.TPAccountProfileManagementService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.dto.TaxMasterDTO;
import com.orderfleet.webapp.web.rest.mapper.TaxMasterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TallyPriceLevelService {

    private final Logger log = LoggerFactory.getLogger(TPAccountProfileManagementService.class);
    private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;
    private final SyncOperationRepository syncOperationRepository;
    private final PriceLevelRepository priceLevelRepository;
    private final CompanyConfigurationRepository companyConfigurationRepository;
    @Autowired
    private AlterIdMasterService alterIdMasterService;

    public TallyPriceLevelService(
            BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
            SyncOperationRepository syncOperationRepository,
            PriceLevelRepository priceLevelRepository,
            CompanyConfigurationRepository companyConfigurationRepository,
            AlterIdMasterService alterIdMasterService) {
        this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
        this.syncOperationRepository = syncOperationRepository;
        this.priceLevelRepository = priceLevelRepository;
        this.companyConfigurationRepository = companyConfigurationRepository;
        this.alterIdMasterService = alterIdMasterService;
    }


    @Transactional
    public void saveUpdatePriceLevels(
            final List<PriceLevelDTO> priceLevelDTOs,
            final SyncOperation syncOperation) {
        long start = System.nanoTime();
        final Company company = syncOperation.getCompany();
        Set<PriceLevel> saveUpdatePriceLevels = new HashSet<>();

        List<PriceLevel> priceLevels =
                priceLevelRepository
                        .findByCompanyId(company.getId());
        for (PriceLevelDTO plDto : priceLevelDTOs) {
            Optional<PriceLevel> optionalPl =
                    priceLevels
                            .stream()
                            .filter(pl -> pl.getName()
                                    .equals(plDto.getName()))
                    .findAny();
            PriceLevel priceLevel;
            if (optionalPl.isPresent()) {
                priceLevel = optionalPl.get();
            } else {
                priceLevel = new PriceLevel();
                priceLevel.setPid(PriceLevelService.PID_PREFIX + RandomUtil.generatePid());
                priceLevel.setName(plDto.getName());
                priceLevel.setCompany(company);
            }
            priceLevel.setActivated(plDto.getActivated());
            saveUpdatePriceLevels.add(priceLevel);
        }

        bulkOperationRepositoryCustom
                .bulkSaveUpdatePriceLevels(
                        saveUpdatePriceLevels);

        long end = System.nanoTime();
        double elapsedTime = (end - start) / 1000000.0;

        syncOperation.setCompleted(true);
        syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
        syncOperation.setLastSyncTime(elapsedTime);
        syncOperationRepository.save(syncOperation);
        log.info("Sync completed in {} ms", elapsedTime);
    }
}
