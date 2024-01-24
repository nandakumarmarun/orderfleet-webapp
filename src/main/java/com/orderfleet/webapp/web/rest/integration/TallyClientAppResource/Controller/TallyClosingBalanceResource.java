package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Controller;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.PostDatedVoucherRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.TallyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PostDatedVoucherAllocationService;
import com.orderfleet.webapp.service.PostDatedVoucherService;
import com.orderfleet.webapp.service.async.TPAccountProfileManagementService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.integration.MasterDataAccountProfileResourceUpdateIdNew;
import com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service.TallyClosingBalanceService;
import com.orderfleet.webapp.web.rest.tallypartner.DocumentUserWiseUpdateController;
import com.orderfleet.webapp.web.tally.service.TallyDataUploadService;
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

public class TallyClosingBalanceResource {

    private final Logger log = LoggerFactory.getLogger(MasterDataAccountProfileResourceUpdateIdNew.class);

    @Inject
    private SyncOperationRepository syncOperationRepository;

    @Inject
    private TallyClosingBalanceService tallyClosingBalanceService;


    @RequestMapping(value = "/account-profiles-closing-balanceUpdateIdNew.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> bulkSaveAccountProfileClosingBalances(
            @Valid @RequestBody List<AccountProfileDTO> accountProfileDTOs) {
        log.debug("REST request to save account profiles closing balance : {}", accountProfileDTOs.size());
        return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
                        SyncOperationType.ACCOUNT_PROFILE_CLOSING_BALANCE).map(so -> {
                    Optional<SyncOperation> opSyncAP = syncOperationRepository.findOneByCompanyIdAndOperationType(
                            SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.ACCOUNT_PROFILE);
                    if (!opSyncAP.get().getCompleted()) {
                        return new ResponseEntity<>("account-profile save processing try after some time.",
                                HttpStatus.BAD_REQUEST);
                    }
                    // update sync status
                    so.setCompleted(false);
                    so.setLastSyncStartedDate(LocalDateTime.now());
                    syncOperationRepository.save(so);
                    // save/update
                    tallyClosingBalanceService.saveUpdateAccountProfileClosingBalancesUpdateIdNew(accountProfileDTOs, so);
                    return new ResponseEntity<>("Uploaded", HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(
                        "account profile closing balance sync operation not registered for this company",
                        HttpStatus.BAD_REQUEST));
    }

}
