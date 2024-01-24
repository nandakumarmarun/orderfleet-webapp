package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Controller;


import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AlterIdMasterService;
import com.orderfleet.webapp.service.async.TPProductProfileManagementService;
import com.orderfleet.webapp.service.async.TPalterIdservicesManagementService;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service.TallyProductCategoryServices;
import com.orderfleet.webapp.web.rest.tallypartner.DocumentUserWiseUpdateController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TallyProductCategoryResource {

    private final Logger log = LoggerFactory.getLogger(TallyProductCategoryResource.class);

    @Inject
    private SyncOperationRepository syncOperationRepository;

    @Inject
    private TallyProductCategoryServices tallyProductCategoryServices;

    @Inject
    private DocumentUserWiseUpdateController documentUserWiseUpdateController;

    @Autowired
    private AlterIdMasterService alterIdMasterService;


    /**
     * REST endpoint for bulk saving or updating Product Categories with an optional ID alteration.
     * This endpoint is used to synchronize product category data with an external system.
     */
    @RequestMapping(value = "/product-categoriesalterIDUpdateIdNew.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> bulkSaveProductCategoriesWithId(
            @Valid @RequestBody List<ProductCategoryDTO> productCategoryDTOs) {
        log.debug("REST request to save ProductCategories : {}", productCategoryDTOs.size());
        return syncOperationRepository
                .findOneByCompanyIdAndOperationType(
                        SecurityUtils.getCurrentUsersCompanyId(),
                        SyncOperationType.PRODUCTCATEGORY)
                .map(so -> {
                    // update sync status
                    so.setCompleted(false);
                    so.setLastSyncStartedDate(LocalDateTime.now());
                    syncOperationRepository.save(so);
                    // save/update
                    tallyProductCategoryServices
                            .saveUpdateProductCategoriesUpdateIdNew(
                                    productCategoryDTOs, so);

                    if (so.getUser()) {
                        documentUserWiseUpdateController.assignSaveUserProductCategories();
                        so.setUser(false);
                        so.setCompleted(true);
                        syncOperationRepository.save(so);
                    }
                    if (so.getDocument()) {
                        documentUserWiseUpdateController.assighnDocumentProductCategory();
                    }
                    return new ResponseEntity<>(
                            "Uploaded",
                            HttpStatus.OK);
        }).orElse(new ResponseEntity<>(
                "Product-Category sync operation not registered for this company",
                HttpStatus.BAD_REQUEST));
    }



}
