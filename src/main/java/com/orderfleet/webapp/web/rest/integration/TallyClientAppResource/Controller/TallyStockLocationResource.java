package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Controller;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.async.TPProductProfileManagementService;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;
import com.orderfleet.webapp.web.rest.integration.MasterDataProductResource;
import com.orderfleet.webapp.web.rest.tallypartner.DocumentUserWiseUpdateController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping(value = "/api/tp/v3")
public class TallyStockLocationResource {

    private final Logger log = LoggerFactory.getLogger(TallyStockLocationResource.class);

    @Inject
    private SyncOperationRepository syncOperationRepository;

    @Inject
    private TPProductProfileManagementService tpProductProfileManagementService;

    @Inject
    private DocumentUserWiseUpdateController documentUserWiseUpdateController;

    @Inject
    private CompanyConfigurationRepository companyConfigurationRepository;


    @RequestMapping(value = "/stock-locations.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> bulkSavestockLocations(
            @Valid @RequestBody List<StockLocationDTO> stockLocationDTOs) {
        log.debug("REST request to save Stock Locations : {}", stockLocationDTOs.size());
        return syncOperationRepository
                .findOneByCompanyIdAndOperationType(
                        SecurityUtils.getCurrentUsersCompanyId(),
                SyncOperationType.STOCK_LOCATION).map(so -> {
            // update sync status
            so.setCompleted(false);
            so.setLastSyncStartedDate(LocalDateTime.now());
            syncOperationRepository.save(so);
            // save/update
            tpProductProfileManagementService.saveUpdateStockLocations(stockLocationDTOs, so);

            if (so.getUser()) {
                documentUserWiseUpdateController.assignSaveUserStockLocations();
                so.setUser(false);
                so.setCompleted(true);
                syncOperationRepository.save(so);
            }
            if (so.getDocument()) {
                documentUserWiseUpdateController.assighnStockLocationDestination();
                documentUserWiseUpdateController.assighnStockLocationSource();
            }
            return new ResponseEntity<>("Uploaded", HttpStatus.OK);
        }).orElse(new ResponseEntity<>("Stock-location sync operation not registered for this company",
                HttpStatus.BAD_REQUEST));
    }
}
