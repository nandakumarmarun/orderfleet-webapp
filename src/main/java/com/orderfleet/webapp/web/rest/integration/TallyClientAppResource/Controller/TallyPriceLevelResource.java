package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Controller;


import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service.TallyAccountServices;
import com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service.TallyPriceLevelService;
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
public class TallyPriceLevelResource {

    private final Logger log = LoggerFactory.getLogger(TallyAccountResource.class);

    @Inject
    private SyncOperationRepository syncOperationRepository;

    @Inject
    private TallyPriceLevelService tallyAccountServices;

    @Inject
    private DocumentUserWiseUpdateController documentUserWiseUpdateController;


    @RequestMapping(value = "/price-levels.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> bulkSavePriceLevels(@Valid @RequestBody List<PriceLevelDTO> priceLevelDTOs) {
        log.debug("REST request to save price level : {}", priceLevelDTOs.size());
        return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
                SyncOperationType.PRICE_LEVEL).map(so -> {
            // update sync status
            so.setCompleted(false);
            so.setLastSyncStartedDate(LocalDateTime.now());
            syncOperationRepository.save(so);
            // save/update
            tallyAccountServices.saveUpdatePriceLevels(priceLevelDTOs, so);

            if (so.getUser()) {
                documentUserWiseUpdateController.assignSaveUserPriceLevels();
                so.setUser(false);
                so.setCompleted(true);
                syncOperationRepository.save(so);
            }
            if (so.getDocument()) {
                documentUserWiseUpdateController.assighnDocumentPriceLevels();
            }

            return new ResponseEntity<>("Uploaded", HttpStatus.OK);
        }).orElse(new ResponseEntity<>("price-levels sync operation not registered for this company",
                HttpStatus.BAD_REQUEST));
    }

}
