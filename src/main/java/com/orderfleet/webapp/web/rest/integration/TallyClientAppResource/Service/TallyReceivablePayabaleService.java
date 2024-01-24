package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.service.async.TPAccountProfileManagementService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TallyReceivablePayabaleService {

    private final Logger log = LoggerFactory.getLogger(TPAccountProfileManagementService.class);
    private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
    private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;
    private final SyncOperationRepository syncOperationRepository;

    private final ReceivablePayableRepository receivablePayableRepository;

    private final AccountProfileRepository accountProfileRepository;


    public TallyReceivablePayabaleService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom, SyncOperationRepository syncOperationRepository, ReceivablePayableRepository receivablePayableRepository, AccountProfileRepository accountProfileRepository) {
        this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
        this.syncOperationRepository = syncOperationRepository;
        this.receivablePayableRepository = receivablePayableRepository;
        this.accountProfileRepository = accountProfileRepository;
    }

    @Transactional
    @Async
    public void saveUpdateReceivablePayablesUpdateIdNew(
            final List<ReceivablePayableDTO> receivablePayableDTOs,
            final SyncOperation syncOperation) {

        long start = System.nanoTime();
        final Company company = syncOperation.getCompany();
        Set<ReceivablePayable> saveReceivablePayable = new HashSet<>();

        DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
        DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String id = "AP_QUERY_123" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
        String description = "get by compId and name ingore case";
        LocalDateTime startLCTime = LocalDateTime.now();
        String startTime = startLCTime.format(DATE_TIME_FORMAT);
        String startDate = startLCTime.format(DATE_FORMAT);
        logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

        List<AccountProfile> accountProfiles =
                accountProfileRepository
                        .findByCompanyIdAndActivatedTrue(
                                company.getId());

        QueryStatus(DATE_TIME_FORMAT, DATE_FORMAT, id, description, startLCTime, startTime);

        receivablePayableRepository
                .deleteByCompanyId(
                        company.getId());

        for (ReceivablePayableDTO rpDto : receivablePayableDTOs) {
            // only save if account profile exist
            accountProfiles
                    .stream()
                    .filter(a -> a.getName()
                            .equalsIgnoreCase(rpDto.getAccountName()))
                    .findAny()
                    .ifPresent(ap -> {
                        ReceivablePayable receivablePayable = new ReceivablePayable();
                        if (receivablePayable.getReceivablePayableType() == null) {
                            receivablePayable.setReceivablePayableType(ReceivablePayableType.Receivable);
                        }
                        receivablePayable.setReceivablePayableType(rpDto.getReceivablePayableType());
                        receivablePayable.setPid(ReceivablePayableService.PID_PREFIX + RandomUtil.generatePid());
                        receivablePayable.setAccountProfile(ap);
                        receivablePayable.setCompany(company);
                        receivablePayable.setBillOverDue(Long.valueOf(rpDto.getBillOverDue()));
                        receivablePayable.setReferenceDocumentAmount(rpDto.getReferenceDocumentAmount());
                        receivablePayable.setReferenceDocumentBalanceAmount(rpDto.getReferenceDocumentBalanceAmount());
                        receivablePayable.setReferenceDocumentDate(rpDto.getReferenceDocumentDate());
                        receivablePayable.setReferenceDocumentNumber(rpDto.getReferenceDocumentNumber());
                        receivablePayable.setReferenceDocumentType(rpDto.getReferenceDocumentType());
                        receivablePayable.setRemarks(rpDto.getRemarks());
                        saveReceivablePayable.add(receivablePayable);
                    });
        }

        bulkOperationRepositoryCustom
                .bulkSaveReceivablePayables(
                        saveReceivablePayable);

        long end = System.nanoTime();
        double elapsedTime = (end - start) / 1000000.0;
        syncOperation.setCompleted(true);
        syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
        syncOperation.setLastSyncTime(elapsedTime);
        syncOperationRepository.save(syncOperation);
        log.info("Sync completed in {} ms", elapsedTime);
    }

    private void QueryStatus(
            DateTimeFormatter DATE_TIME_FORMAT,
            DateTimeFormatter DATE_FORMAT,
            String id, String description,
            LocalDateTime startLCTime,
            String startTime) {

        String flag = "Normal";
        LocalDateTime endLCTime = LocalDateTime.now();
        String endTime = endLCTime.format(DATE_TIME_FORMAT);
        String endDate = startLCTime.format(DATE_FORMAT);
        Duration duration = Duration.between(startLCTime, endLCTime);
        long minutes = duration.toMinutes();
        if (minutes <= 1 && minutes >= 0) {
            flag = "Fast";
        }
        if (minutes > 1 && minutes <= 2) {
            flag = "Normal";
        }
        if (minutes > 2 && minutes <= 10) {
            flag = "Slow";
        }
        if (minutes > 10) {
            flag = "Dead Slow";
        }
        logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + "," + description);

        // delete all receivable payable
    }
}
