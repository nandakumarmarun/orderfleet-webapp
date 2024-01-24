package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Controller;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.TaxMasterDTO;
import com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service.TallyGstGroupWiseServices;
import com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service.TallyTaxMasterService;
import com.orderfleet.webapp.web.rest.integration.dto.GSTProductGroupDTO;
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
public class TallyGstGroupWiseResource {

    private final Logger log = LoggerFactory.getLogger(TallyProductGroupResouce.class);
    @Inject
    private SyncOperationRepository syncOperationRepository;

    @Inject
    private TallyGstGroupWiseServices tallyGstGroupWiseServices;

    @Inject
    private DocumentUserWiseUpdateController documentUserWiseUpdateController;


    @RequestMapping(value = "/gst-product-groupUpdatedId.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> bulkSaveGSTProductGroupWise(@Valid @RequestBody List<GSTProductGroupDTO> gstDTOs) {
        log.debug("REST request to save gst_product-group : {}", gstDTOs.size());
        return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
                SyncOperationType.GST_PRODUCT_GROUP).map(so -> {

            // update sync status
            so.setCompleted(false);
            so.setLastSyncStartedDate(LocalDateTime.now());
            syncOperationRepository.save(so);
            // save/update
            tallyGstGroupWiseServices.saveUpdateGSTProductGroupUpdatedId(gstDTOs, so);
            return new ResponseEntity<>("Uploaded", HttpStatus.OK);
        }).orElse(new ResponseEntity<>("gst_product_group sync operation not registered for this company",
                HttpStatus.BAD_REQUEST));
    }
}
