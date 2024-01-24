package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.StockLocationType;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.async.TPProductProfileManagementService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TallyStockLocationService {

    private final StockLocationRepository stockLocationRepository;

    private final Logger log = LoggerFactory.getLogger(TPProductProfileManagementService.class);

    private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

    private final SyncOperationRepository syncOperationRepository;


    public TallyStockLocationService(
            StockLocationRepository stockLocationRepository,
            BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
            SyncOperationRepository syncOperationRepository) {
        this.stockLocationRepository = stockLocationRepository;
        this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
        this.syncOperationRepository = syncOperationRepository;
    }

    @Transactional
    public void saveUpdateStockLocations(final List<StockLocationDTO> stockLocationDTOs,
                                         final SyncOperation syncOperation) {
        long start = System.nanoTime();
        final Company company = syncOperation.getCompany();
        Set<StockLocation> saveUpdateStockLocations = new HashSet<>();

        List<StockLocation> stockLocations =
                stockLocationRepository
                        .findAllByCompanyId(company.getId());

        for (StockLocationDTO locDto : stockLocationDTOs) {
            Optional<StockLocation> optionalLoc = stockLocations
                    .stream()
                    .filter(sl -> sl.getName()
                            .equals(locDto.getName()))
                    .findAny();
            StockLocation stockLocation;
            if (optionalLoc.isPresent()) {
                stockLocation = optionalLoc.get();
            } else {
                stockLocation = new StockLocation();
                stockLocation.setPid(StockLocationService.PID_PREFIX + RandomUtil.generatePid());
                stockLocation.setName(locDto.getName());
                stockLocation.setStockLocationType(StockLocationType.ACTUAL);
                stockLocation.setCompany(company);
            }
            stockLocation.setActivated(locDto.getActivated());
            saveUpdateStockLocations.add(stockLocation);
        }

        bulkOperationRepositoryCustom
                .bulkSaveStockLocations(
                        saveUpdateStockLocations);

        long end = System.nanoTime();
        double elapsedTime = (end - start) / 1000000.0;

        syncOperation.setCompleted(true);
        syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
        syncOperation.setLastSyncTime(elapsedTime);
        syncOperationRepository.save(syncOperation);
        log.info("Sync completed in {} ms", elapsedTime);
    }
}
