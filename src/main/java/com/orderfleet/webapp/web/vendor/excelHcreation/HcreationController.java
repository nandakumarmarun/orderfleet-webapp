package com.orderfleet.webapp.web.vendor.excelHcreation;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
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
@RequestMapping(value = "/api/excel/v1")
public class HcreationController {

    private final Logger log = LoggerFactory.getLogger(HcreationController.class);
     @Inject
     private  SyncOperationRepository syncOperationRepository;
     @Inject
     private CompanyRepository companyRepository;
     @Inject
     private ProductCategoryRepository productCategoryRepository;
     @Inject
     private ProductGroupRepository productGroupRepository;
     @Inject
     private ProductProfileRepository productProfileRepository;
     @Inject
     private ProductProfileService productProfileService;
     @Inject
     private AccountProfileServiceHcreation accountProfileService;



    @RequestMapping(value = "/hcreation-product-profiles.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> bulkSaveProductProfiles(
            @Valid @RequestBody List<ProductProfileDTO> productProfileDTOs) {

        log.debug("REST request to save ProductProfiles of Hcreation: {}", productProfileDTOs.size());
        return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
                SyncOperationType.PRODUCTPROFILE).map(so -> {
            // update sync status

            log.debug("----------------------------------------", productProfileDTOs.size());
            so.setCompleted(false);
            so.setLastSyncStartedDate(LocalDateTime.now());
            syncOperationRepository.save(so);
            // save/update
            productProfileService.saveUpdateProductProfiles(productProfileDTOs, so);
            return new ResponseEntity<>("Uploaded", HttpStatus.OK);
        }).orElse(new ResponseEntity<>("Product-Profile sync operation not registered for this company",
                HttpStatus.BAD_REQUEST));

    }

    @RequestMapping(value = "/hcreation-account-profiles.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> bulkSaveAccountProfiles(
            @Valid @RequestBody List<AccountProfileDTO> accountProfileDTOs) {
        log.debug("REST request to save AccountProfiles : {}", accountProfileDTOs.size());
        return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
                SyncOperationType.ACCOUNT_PROFILE).map(so -> {
            // update sync status
            so.setCompleted(false);
            so.setLastSyncStartedDate(LocalDateTime.now());
            syncOperationRepository.save(so);
            // save/update
            accountProfileService.saveUpdateAccountProfiles(accountProfileDTOs, so);
            return new ResponseEntity<>("Uploaded", HttpStatus.OK);
        }).orElse(new ResponseEntity<>("Account-Profile sync operation not registered for this company",
                HttpStatus.BAD_REQUEST));
    }

}
