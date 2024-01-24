package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Controller;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service.TallyLocationServices;
import com.orderfleet.webapp.web.rest.tallypartner.DocumentUserWiseUpdateController;
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

@RestController
@RequestMapping(value = "/api/tp/v3")
public class TallyLocationResource {

    private final Logger log = LoggerFactory.getLogger(TallyLocationResource.class);
    @Inject
    private SyncOperationRepository syncOperationRepository;

    @Inject
    private TallyLocationServices tallyMasterDataServices;

    @Inject
    private DocumentUserWiseUpdateController documentUserWiseUpdateController;



    /**
     * REST endpoint for bulk saving locations with optional full update.
     * This endpoint is used to synchronize location data with an external system.
     */
    @RequestMapping(value = "/locationsUpdateId-alterID.json/{fullUpdate}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> bulkSaveLocationsWithId(
            @Valid @RequestBody List<LocationDTO> locationDTOs,
            @PathVariable("fullUpdate") boolean fullUpdate ) {
        log.debug("Rest request to save locations : {}",
                locationDTOs.size(),fullUpdate);
        return syncOperationRepository
                .findOneByCompanyIdAndOperationType(
                        SecurityUtils.getCurrentUsersCompanyId(),
                SyncOperationType.LOCATION).map(so -> {
            so.setCompleted(false);
            so.setLastSyncStartedDate(LocalDateTime.now());
            syncOperationRepository.save(so);
            tallyMasterDataServices
                    .saveUpdateLocationsUpdationIdNew(
                            locationDTOs, so,fullUpdate);
            if (so.getUser()) {
                documentUserWiseUpdateController
                        .assignSaveUserLocations();
                so.setUser(false);
                so.setCompleted(true);
                syncOperationRepository.save(so);
            }
            return new ResponseEntity<>(
                    "Uploaded",
                    HttpStatus.OK);
        }).orElse(new ResponseEntity<>(
                "Locations sync operation not registered for this company",
                HttpStatus.BAD_REQUEST));
    }
}
