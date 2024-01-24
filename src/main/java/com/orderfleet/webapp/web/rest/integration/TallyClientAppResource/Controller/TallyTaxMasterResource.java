package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Controller;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.TaxMasterDTO;
import com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service.TallyProductGroupServices;
import com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service.TallyTaxMasterService;
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
public class TallyTaxMasterResource {

    private final Logger log = LoggerFactory.getLogger(TallyProductGroupResouce.class);
    @Inject
    private SyncOperationRepository syncOperationRepository;

    @Inject
    private TallyTaxMasterService tallyTaxMasterService;

    @Inject
    private DocumentUserWiseUpdateController documentUserWiseUpdateController;

    /**
     * REST endpoint for bulk saving or updating Tax Masters.
     * This endpoint is used to synchronize tax master data with an external system.
     */
    @RequestMapping(value = "/tax-master.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> bulkSaveTaxMasters(
            @Valid @RequestBody List<TaxMasterDTO> taxMasterDTOs) {
        log.debug("REST request to save taxMasters : {}", taxMasterDTOs.size());
        return syncOperationRepository
                .findOneByCompanyIdAndOperationType(
                        SecurityUtils.getCurrentUsersCompanyId(),
                        SyncOperationType.TAX_MASTER)
                .map(so -> {
                    // update sync status
                    so.setCompleted(false);
                    so.setLastSyncStartedDate(LocalDateTime.now());
                    syncOperationRepository.save(so);
                    // save/update
                    tallyTaxMasterService.saveUpdateTaxMasters(taxMasterDTOs, so);
                    return new ResponseEntity<>(
                            "Uploaded",
                            HttpStatus.OK);
        }).orElse(new ResponseEntity<>(
                "TaxMaster sync operation not registered for this company",
                HttpStatus.BAD_REQUEST));
    }
}
