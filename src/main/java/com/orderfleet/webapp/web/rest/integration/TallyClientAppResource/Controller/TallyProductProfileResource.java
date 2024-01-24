package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Controller;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service.TallyAccountServices;
import com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service.TallyProductProfileServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/tp/v3")
public class TallyProductProfileResource {

    private final Logger log = LoggerFactory.getLogger(TallyProductProfileResource.class);

    @Inject
    private SyncOperationRepository syncOperationRepository;

    @Inject
    private TallyProductProfileServices tallyProductProfileServices;


    /**
     * REST endpoint for bulk saving or updating
     * Product Profiles with an optional ID alteration.
     * This endpoint is used to synchronize
     * Product profile data with an external system.
     */
    @RequestMapping(value = "/product-profileUpdateIdNew-alterId.json/{fullUpdate}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> bulkSaveProductProfilesWithId(
            @Valid @RequestBody List<ProductProfileDTO> productProfileDTOs ,
            @PathVariable("fullUpdate") boolean fullUpdate ) {

        log.debug("REST request to save ProductProfiles : {}",
                productProfileDTOs.size()
                        + " : method "+ fullUpdate);

        return syncOperationRepository
                .findOneByCompanyIdAndOperationType(
                        SecurityUtils.getCurrentUsersCompanyId(),
                        SyncOperationType.PRODUCTPROFILE).map(so -> {

            Optional<SyncOperation> opSyncCG = syncOperationRepository
                    .findOneByCompanyIdAndOperationType(
                            SecurityUtils.getCurrentUsersCompanyId(),
                            SyncOperationType.PRODUCTCATEGORY);

            if (opSyncCG.isPresent() && !opSyncCG.get().getCompleted()) {
                return new ResponseEntity<>(
                        "product-category save processing try after some time.",
                        HttpStatus.BAD_REQUEST);
            }

            Optional<SyncOperation> opSyncPG =
                    syncOperationRepository
                            .findOneByCompanyIdAndOperationType(
                                    SecurityUtils.getCurrentUsersCompanyId(),
                                    SyncOperationType.PRODUCTGROUP);

            if (opSyncPG.isPresent() && !opSyncPG.get().getCompleted()) {
                return new ResponseEntity<>(
                        "product-group save processing try after some time.",
                        HttpStatus.BAD_REQUEST);
            }

            // update sync status
            so.setCompleted(false);
            so.setLastSyncStartedDate(LocalDateTime.now());
            syncOperationRepository.save(so);

            // save/update
            tallyProductProfileServices
                    .saveUpdateProductProfilesUpdateIdNew(
                            productProfileDTOs,so,fullUpdate);

            return new ResponseEntity<>("Uploaded", HttpStatus.OK);
        }).orElse(new ResponseEntity<>(
                "Product-profile sync operation not registered for this company",
                HttpStatus.BAD_REQUEST));
    }
}
