package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.TallyMasters;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.service.*;
import com.orderfleet.webapp.service.async.TPAccountProfileManagementService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AlterIdMasterDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TallyLocationAccountServices {

    private final Logger log = LoggerFactory.getLogger(TPAccountProfileManagementService.class);

    private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

    private final SyncOperationRepository syncOperationRepository;

    private final LocationAccountProfileRepository locationAccountProfileRepository;

    private final LocationService locationService;

    private final AccountProfileService accountProfileService;

    private final LocationAccountProfileService locationAccountProfileService;

    @Autowired
    private AlterIdMasterService alterIdMasterService;

    public TallyLocationAccountServices(
            BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
            SyncOperationRepository syncOperationRepository,
            LocationAccountProfileRepository locationAccountProfileRepository,
            LocationService locationService, AccountProfileService accountProfileService,
            LocationAccountProfileService locationAccountProfileService,
            AlterIdMasterService alterIdMasterService) {
        this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
        this.syncOperationRepository = syncOperationRepository;
        this.locationAccountProfileRepository = locationAccountProfileRepository;
        this.locationService = locationService;
        this.accountProfileService = accountProfileService;
        this.locationAccountProfileService = locationAccountProfileService;
        this.alterIdMasterService = alterIdMasterService;
    }

    @Transactional
    public void saveUpdateLocationAccountProfilesUpdateIdNew(
            final List<LocationAccountProfileDTO> locationAccountProfileDTOs,
            final SyncOperation syncOperation) {

        long start = System.nanoTime();
        log.debug(String.valueOf(start)+ "-" +syncOperation.getCompany().getId());
        final Company company = syncOperation.getCompany();
        final Long companyId = syncOperation.getCompany().getId();
        List<LocationAccountProfile> newLocationAccountProfiles = new ArrayList<>();
        List<LocationAccountProfile> locationAccountProfiles =
                locationAccountProfileService
                        .findAllLocationAccountProfiles(companyId);

        log.debug(String.valueOf(start) + "-" + syncOperation.getCompany().getId()
                + " : locationAccountProfiles : " + locationAccountProfiles.size() );

        List<AccountProfile> accountProfiles =
                accountProfileService.findAllAccountProfileByCompanyId(companyId);
        log.debug(String.valueOf(start)+"-"+syncOperation.getCompany().getId()
                + " : AccountProfiles : " + accountProfiles.size());
        List<Location> locations = locationService.findAllLocationByCompanyId(companyId);
        log.debug(String.valueOf(start)+"-"+syncOperation.getCompany().getId()
                + " : locations : " + locations.size());
        List<Long> locationAccountProfilesIds = new ArrayList<>();

        for (LocationAccountProfileDTO locAccDto : locationAccountProfileDTOs) {
            LocationAccountProfile profile = new LocationAccountProfile();

            Optional<Location> loc = locations
                    .stream()
                    .filter(pl -> locAccDto.getLocationName()
                            .equals(pl.getName()))
                    .findAny();

            Optional<AccountProfile> acc =
                    accountProfiles
                            .stream()
                            .filter(ap -> locAccDto.getAccountProfileName()
                                    .equals(ap.getName()))
                            .findAny();

            if (loc.isPresent() && acc.isPresent()) {

                List<Long> locationAccountProfileIds = locationAccountProfiles
                        .stream()
                        .filter(lap -> acc.get().getPid()
                                .equals(lap.getAccountProfile().getPid()))
                        .map(lap -> lap.getId())
                        .collect(Collectors.toList());

                if (locationAccountProfileIds.size() != 0) {
                    locationAccountProfilesIds.addAll(locationAccountProfileIds);
                }

                profile.setLocation(loc.get());
                profile.setAccountProfile(acc.get());
                profile.setCompany(company);
                newLocationAccountProfiles.add(profile);
            }
        }
        if (locationAccountProfilesIds.size() != 0) {
            log.debug(String.valueOf(start)+"-"+syncOperation.getCompany().getId()
                    + " : locationAccountProfilesIds : " + locationAccountProfilesIds.size());
            locationAccountProfileRepository.deleteByIdIn(companyId, locationAccountProfilesIds);
        }

        log.debug(String.valueOf(start)+"-"+syncOperation.getCompany().getId()
                + " : newLocationAccountProfiles : " + newLocationAccountProfiles.size());
        locationAccountProfileRepository.save(newLocationAccountProfiles);
        LocationAccountProfileDTO locationAccountProfileDTO = locationAccountProfileDTOs.stream()
                .max(Comparator.comparingLong(LocationAccountProfileDTO::getAlterId)).get();

        AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
        alterIdMasterDTO.setAlterId(locationAccountProfileDTO.getAlterId());
        alterIdMasterDTO.setMasterName(TallyMasters.LOCATION_ACCOUNT_PROFILE.toString());
        alterIdMasterDTO.setCompanyId(company.getId());
        alterIdMasterService.save(alterIdMasterDTO);
        long end = System.nanoTime();
        double elapsedTime = (end - start) / 1000000.0;
        // update sync table
        syncOperation.setCompleted(true);
        syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
        syncOperation.setLastSyncTime(elapsedTime);
        syncOperationRepository.save(syncOperation);
        log.info(String.valueOf(start) + "-" +syncOperation.getCompany().getId() + " : Sync completed in {} ms", elapsedTime);
    }


}
