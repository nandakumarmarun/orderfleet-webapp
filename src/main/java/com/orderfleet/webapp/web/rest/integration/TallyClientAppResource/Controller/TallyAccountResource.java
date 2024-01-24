package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Controller;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service.TallyAccountServices;
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
public class TallyAccountResource {

    private final Logger log = LoggerFactory.getLogger(TallyAccountResource.class);

    @Inject
    private SyncOperationRepository syncOperationRepository;

    @Inject
    private TallyAccountServices tallyAccountServices;

    /**
     * REST endpoint to bulk save/update Account Profiles with a specified ID.
     * This endpoint is designed for handling JSON data in the request body.
     * Endpoint: POST /account-profilesUpdateId-alterId.json/{fullUpdate}
     * Produces: application/json
     *
     * @param accountProfileDTOs   List of AccountProfileDTO objects to be saved/updated.
     * @param fullUpdate           A boolean flag indicating whether it's a full update or not.
     * @return ResponseEntity<String> indicating the success or failure of the operation.
     * @since
     *
     */
    @RequestMapping(value = "/account-profilesUpdateId-alterId.json/{fullUpdate}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> bulkSaveAccountProfilesWithId(
            @Valid @RequestBody List<AccountProfileDTO> accountProfileDTOs,
            @PathVariable boolean fullUpdate) {

        log.debug("REST Request to Save AccountProfiles : {} " , accountProfileDTOs.size());

        return syncOperationRepository
                .findOneByCompanyIdAndOperationType(
                        SecurityUtils.getCurrentUsersCompanyId(),
                SyncOperationType.ACCOUNT_PROFILE)
                .map(so -> {

            so.setCompleted(false);
            so.setLastSyncStartedDate(LocalDateTime.now());
            syncOperationRepository.save(so);

            tallyAccountServices
                    .saveUpdateAccountProfilesUpdateIdNew(
                            accountProfileDTOs,so,fullUpdate);

            return new ResponseEntity<>("Uploaded", HttpStatus.OK);
        }).orElse(new ResponseEntity<>("Account-Profile sync operation not registered for this company ",
                HttpStatus.BAD_REQUEST));
    }
}
