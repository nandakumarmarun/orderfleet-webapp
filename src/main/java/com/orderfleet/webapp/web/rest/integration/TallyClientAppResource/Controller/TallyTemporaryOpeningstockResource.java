package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Controller;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.async.TPProductProfileManagementService;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
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

import javax.inject.Inject;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TallyTemporaryOpeningstockResource {
    private final Logger log = LoggerFactory.getLogger(MasterDataProductResource.class);
    private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
    @Inject
    private SyncOperationRepository syncOperationRepository;

    @Inject
    private TPProductProfileManagementService tpProductProfileManagementService;

    @Inject
    private DocumentUserWiseUpdateController documentUserWiseUpdateController;

    @Inject
    private CompanyConfigurationRepository companyConfigurationRepository;

    @RequestMapping(value = "/temporary-opening-stock.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> bulkSaveTemporaryOpeningStock(@Valid @RequestBody List<OpeningStockDTO> openingStockDTOs) {
        log.debug("REST request to save opening stock : {}", openingStockDTOs.size());
        return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
                SyncOperationType.TEMPORARY_OPENING_STOCK).map(so -> {

            Optional<SyncOperation> opSyncSL = syncOperationRepository.findOneByCompanyIdAndOperationType(
                    SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.STOCK_LOCATION);

            if (!opSyncSL.get().getCompleted()) {
                return new ResponseEntity<>("stock-location save processing try after some time.",
                        HttpStatus.BAD_REQUEST);
            }

            Optional<SyncOperation> opSyncPP = syncOperationRepository.findOneByCompanyIdAndOperationType(
                    SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.PRODUCTPROFILE);

            if (!opSyncPP.get().getCompleted()) {
                return new ResponseEntity<>("product-profile save processing try after some time.",
                        HttpStatus.BAD_REQUEST);
            }
            // update sync status
            so.setCompleted(false);
            so.setLastSyncStartedDate(LocalDateTime.now());
            syncOperationRepository.save(so);
            // save/update
            tpProductProfileManagementService.saveUpdateTemporaryOpeningStock(openingStockDTOs, so);
            return new ResponseEntity<>("Uploaded", HttpStatus.OK);
        }).orElse(new ResponseEntity<>("opening-stock sync operation not registered for this company",
                HttpStatus.BAD_REQUEST));
    }
}
