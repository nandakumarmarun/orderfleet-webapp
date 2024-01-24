package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.async.TPAccountProfileManagementService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TallyClosingBalanceService {


    private final Logger log = LoggerFactory.getLogger(TPAccountProfileManagementService.class);
    private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
    private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

    private final SyncOperationRepository syncOperationRepository;

    private final PriceLevelRepository priceLevelRepository;

    private final AccountProfileRepository accountProfileRepository;

    public TallyClosingBalanceService(
            BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
            SyncOperationRepository syncOperationRepository,
            PriceLevelRepository priceLevelRepository,
            AccountProfileRepository accountProfileRepository) {
        this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
        this.syncOperationRepository = syncOperationRepository;
        this.priceLevelRepository = priceLevelRepository;
        this.accountProfileRepository = accountProfileRepository;
    }

    @Transactional
    @Async
    public void saveUpdateAccountProfileClosingBalancesUpdateIdNew(
            final List<AccountProfileDTO> accountProfileDTOs,
            final SyncOperation syncOperation) {
        long start = System.nanoTime();
        Long companyId = syncOperation.getCompany().getId();
        Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();

        // find all exist account profiles
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
                                companyId);

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
        logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
                + description);

        for (AccountProfileDTO apDto : accountProfileDTOs) {

            // check exist by name, only one exist with a name
            Optional<AccountProfile> optionalAP =
                    accountProfiles
                            .stream()
                            .filter(pc -> pc.getName()
                                    .equalsIgnoreCase(apDto.getName()))
                            .findAny();

            AccountProfile accountProfile;
            if (optionalAP.isPresent()) {
                accountProfile = optionalAP.get();
                accountProfile.setClosingBalance(apDto.getClosingBalance());
                saveUpdateAccountProfiles.add(accountProfile);
            }
        }

        DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
        DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String id1 = "AP_QUERY_125" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
        String description1 = "Updating the account closing balance zero by compId";
        LocalDateTime startLCTime1 = LocalDateTime.now();
        String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
        String startDate1 = startLCTime1.format(DATE_FORMAT1);
        logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);

        accountProfileRepository
                .updateAccountProfileClosingBalanceZeroByCompanyId(
                        companyId);

        String flag1 = "Normal";
        LocalDateTime endLCTime1 = LocalDateTime.now();
        String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
        String endDate1 = startLCTime1.format(DATE_FORMAT1);
        Duration duration1 = Duration.between(startLCTime1, endLCTime1);
        long minutes1 = duration1.toMinutes();
        if (minutes1 <= 1 && minutes1 >= 0) {
            flag1 = "Fast";
        }
        if (minutes1 > 1 && minutes1 <= 2) {
            flag1 = "Normal";
        }
        if (minutes1 > 2 && minutes1 <= 10) {
            flag1 = "Slow";
        }
        if (minutes1 > 10) {
            flag1 = "Dead Slow";
        }
        logger.info(id1 + "," + endDate1 + "," + startTime1 + ","
                + endTime1 + "," + minutes1 + ",END," + flag1 + ","
                + description1);

        bulkOperationRepositoryCustom
                .bulkSaveAccountProfile(
                        saveUpdateAccountProfiles);

        long end = System.nanoTime();
        double elapsedTime = (end - start) / 1000000.0;
        syncOperation.setCompleted(true);
        syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
        syncOperation.setLastSyncTime(elapsedTime);
        syncOperationRepository.save(syncOperation);
        log.info("Sync completed in {} ms", elapsedTime);
    }
}
