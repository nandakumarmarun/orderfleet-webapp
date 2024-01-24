package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Controller;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service.TallyLocationAccountServices;
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
public class TallyLocationAccountResource {

    private final Logger log = LoggerFactory.getLogger(TallyLocationAccountResource.class);
    @Inject
    private SyncOperationRepository syncOperationRepository;

    @Inject
    private TallyLocationAccountServices tallyLocationAccountServices;


    /**
     * Controller method for bulk saving location account profiles with an alternate ID
     * and profile update ID.
     * This endpoint handles a POST request and produces a JSON response.
     */
    @RequestMapping(value = "/location-account-alterid-profileUpdateId.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> bulkSaveLocationAccountProfilesWithId(
            @Valid @RequestBody List<LocationAccountProfileDTO> locationAccountProfileDTOs) {
        log.debug("REST request to save location-accountprofiles : {}", locationAccountProfileDTOs.size());

        return syncOperationRepository.findOneByCompanyIdAndOperationType(
                SecurityUtils.getCurrentUsersCompanyId(),
                        SyncOperationType.LOCATION_ACCOUNT_PROFILE).map(so -> {
                    Optional<SyncOperation> opSyncLO = syncOperationRepository
                            .findOneByCompanyIdAndOperationType(
                            SecurityUtils.getCurrentUsersCompanyId(),
                                    SyncOperationType.LOCATION);

                    if (!opSyncLO.get().getCompleted()) {
                        return new ResponseEntity<>("location save processing try after some time.",
                                HttpStatus.BAD_REQUEST);
                    }

                    Optional<SyncOperation> opSyncAP = syncOperationRepository
                            .findOneByCompanyIdAndOperationType(
                            SecurityUtils.getCurrentUsersCompanyId(),
                                    SyncOperationType.ACCOUNT_PROFILE);

                    if (!opSyncAP.get().getCompleted()) {
                        return new ResponseEntity<>("account-profile save processing try after some time.",
                                HttpStatus.BAD_REQUEST);
                    }

                    so.setCompleted(false);
                    so.setLastSyncStartedDate(LocalDateTime.now());
                    syncOperationRepository.save(so);
                    // save/update
                    tallyLocationAccountServices
                            .saveUpdateLocationAccountProfilesUpdateIdNew(
                                    locationAccountProfileDTOs, so);

                    return new ResponseEntity<>("Uploaded", HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>("Location-Account-Profile sync operation not registered for this company",
                        HttpStatus.BAD_REQUEST));
    }


}
