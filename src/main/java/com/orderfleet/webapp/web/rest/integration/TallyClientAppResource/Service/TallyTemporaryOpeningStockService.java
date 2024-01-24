package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.TemporaryOpeningStockRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.async.TPProductProfileManagementService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TallyTemporaryOpeningStockService {

    private final Logger log = LoggerFactory.getLogger(TPProductProfileManagementService.class);

    private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

    private final SyncOperationRepository syncOperationRepository;

    private final ProductProfileRepository productProfileRepository;

    private final StockLocationRepository stockLocationRepository;

    private final TemporaryOpeningStockRepository temporaryOpeningStockRepository;

    @Autowired
    private StockLocationService stockLocationService;

    public TallyTemporaryOpeningStockService(
            BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
            SyncOperationRepository syncOperationRepository,
            ProductProfileRepository productProfileRepository,
            StockLocationRepository stockLocationRepository,
            TemporaryOpeningStockRepository temporaryOpeningStockRepository) {
        this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
        this.syncOperationRepository = syncOperationRepository;
        this.productProfileRepository = productProfileRepository;
        this.stockLocationRepository = stockLocationRepository;
        this.temporaryOpeningStockRepository = temporaryOpeningStockRepository;
    }

    @Transactional
    @Async
    public void saveUpdateTemporaryOpeningStock(
            final List<OpeningStockDTO> openingStockDTOs,
            final SyncOperation syncOperation) {
        long start = System.nanoTime();
        final Company company = syncOperation.getCompany();
        final Long companyId = company.getId();
        Set<TemporaryOpeningStock> saveOpeningStocks = new HashSet<>();

        // All opening-stock must have a stock-location, if not, set a default
        // one
        StockLocation defaultStockLocation =
                stockLocationRepository
                        .findFirstByCompanyId(company.getId());

        // find all exist product profiles
        Set<String> ppNames =
                openingStockDTOs
                        .stream()
                        .map(os -> os.getProductProfileName())
                        .collect(Collectors.toSet());

        List<StockLocation> StockLocations =
                stockLocationService.findAllStockLocationByCompanyId(companyId);

        List<ProductProfile> productProfiles = productProfileRepository
                .findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);
        // delete all opening stock
        temporaryOpeningStockRepository.deleteByCompanyId(company.getId());
        for (OpeningStockDTO osDto : openingStockDTOs) {
            // only save if account profile exist
            productProfiles
                    .stream()
                    .filter(pp -> pp.getName().equals(osDto.getProductProfileName()))
                    .findAny()
                    .ifPresent(pp -> {
                        TemporaryOpeningStock openingStock = new TemporaryOpeningStock();
                        openingStock.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid()); // set
                        openingStock.setOpeningStockDate(LocalDateTime.now());
                        openingStock.setCreatedDate(LocalDateTime.now());
                        openingStock.setCompany(company);
                        openingStock.setProductProfile(pp);

                        if (osDto.getStockLocationName() == null) {
                            openingStock.setStockLocation(defaultStockLocation);
                        } else {
                            Optional<StockLocation> optionalStockLocation =
                                    StockLocations
                                            .stream()
                                            .filter(pl -> osDto.getStockLocationName()
                                                    .equals(pl.getName()))
                                            .findAny();
                            if (optionalStockLocation.isPresent()) {
                                openingStock.setStockLocation(optionalStockLocation.get());
                            } else {
                                openingStock.setStockLocation(defaultStockLocation);
                            }
                        }
                        openingStock.setBatchNumber(osDto.getBatchNumber());
                        openingStock.setQuantity(osDto.getQuantity());
                        if(osDto.getQuantity() != 0) {
                            saveOpeningStocks.add(openingStock);
                        }
                    });
        }
        bulkOperationRepositoryCustom.bulkSaveTemporaryOpeningStocks(saveOpeningStocks);
        long end = System.nanoTime();
        double elapsedTime = (end - start) / 1000000.0;

        syncOperation.setCompleted(true);
        syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
        syncOperation.setLastSyncTime(elapsedTime);
        syncOperationRepository.save(syncOperation);
        log.info("Sync completed in {} ms", elapsedTime);
    }
}
