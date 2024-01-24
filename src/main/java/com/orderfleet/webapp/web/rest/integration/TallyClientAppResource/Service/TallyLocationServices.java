package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service;
import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.TallyMasters;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.*;
import com.orderfleet.webapp.service.async.TPAccountProfileManagementService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TallyLocationServices {

    private final Logger log = LoggerFactory.getLogger(TPAccountProfileManagementService.class);

    private final Logger logger = LoggerFactory.getLogger("QueryFormatting");

    private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

    private final SyncOperationRepository syncOperationRepository;

    private final LocationRepository locationRepository;

    @Autowired
    private AlterIdMasterService alterIdMasterService;

    boolean flag= false;

    boolean flagac= false;

    public TallyLocationServices(
            BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
            SyncOperationRepository syncOperationRepository,
            LocationRepository locationRepository,
            AlterIdMasterService alterIdMasterService) {
        this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
        this.syncOperationRepository = syncOperationRepository;
        this.locationRepository = locationRepository;
        this.alterIdMasterService = alterIdMasterService;
    }

    @Transactional
    public void saveUpdateLocationsUpdationIdNew(
            List<LocationDTO> locationDTOs,
            final SyncOperation syncOperation,
            boolean fullUpdate) {
        long start = System.nanoTime();
        final Company company = syncOperation.getCompany();

        Set<Location> saveUpdateLocations = new HashSet<>();
        locationDTOs=locationDTOs
                .stream()
                .filter(data -> !data.getName()
                        .equals("Territory"))
                .collect(Collectors.toList());

        // find all locations
        List<Location> locations = locationRepository.findAllByCompanyId(company.getId());
        Set<Long> dectivatedloc = new HashSet<>();
        if(fullUpdate == true) {
            for(Location pp :locations) {
                if(!pp.getName().equals("Primary")
                        && !pp.getName().equals("Territory")
                        && pp.getActivated()
                        && pp.getLocationId() != null){
                    flag = false;
                    locationDTOs.forEach(data ->{
                        if(pp.getLocationId().equals(data.getLocationId())) {
                            flag = true;
                        }
                    });
                    if(!flag) {
                        dectivatedloc.add(pp.getId());
                    }
                }
            }
            if(!dectivatedloc.isEmpty()) {
                locationRepository.deactivatelocationId(dectivatedloc);
            }
        }
        for (LocationDTO locDto : locationDTOs) {
            Optional<Location> optionalLoc = null;

            optionalLoc = locations.stream()
                    .filter(p -> p.getLocationId() != null
                            && !p.getLocationId().equals("")
                            ? p.getLocationId().equals(locDto.getLocationId())
                            : false)
                    .findAny();
            Location location = new Location();
            if (optionalLoc.isPresent()) {
                location = optionalLoc.get();
            } else {
                Optional<Location> optionalLocName = locations.stream()
                        .filter(p -> p.getName() != null && !p.getName().equals("")
                                ? p.getName().equals(locDto.getName())
                                : false)
                        .findAny();

                if(!optionalLocName.isPresent()) {
                    location.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid());
                    location.setLocationId(locDto.getLocationId());
                    location.setDescription(locDto.getName());
                    location.setCompany(company);
                }else {
                    location = optionalLocName.get();
                    location.setLocationId(locDto.getLocationId());
                }
            }
            location.setActivated(locDto.getActivated());
            location.setDescription(locDto.getName());
            location.setName(locDto.getName());
            Optional<Location> opAccP = saveUpdateLocations.stream()
                    .filter(so -> so.getName().equalsIgnoreCase(locDto.getName())).findAny();
            if (opAccP.isPresent()) {
                continue;
            }
            saveUpdateLocations.add(location);
        }

        List<Location> UpdateLocations = saveUpdateLocations.stream()
                .filter(data -> data.getId() != null).collect(Collectors.toList());
        List<Location> saveLocations = saveUpdateLocations.stream()
                .filter(data ->  data.getId() == null).collect(Collectors.toList());

        locationRepository.flush();
        locationRepository.save(UpdateLocations);

        if(!saveLocations.isEmpty()) {
            locationRepository.flush();
            locationRepository.save(saveLocations);
        }
        Long Listcount = locationDTOs
                .parallelStream()
                .filter(locationDTO -> locationDTO.getAlterId() == null)
                .count();
        System.out.println("locationDTOsnullcount"+Listcount);
        if(Listcount == 0) {
            LocationDTO locationDTO = locationDTOs.stream()
                    .max(Comparator.comparingLong(LocationDTO::getAlterId)).get();

            AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
            alterIdMasterDTO.setAlterId(locationDTO.getAlterId());
            alterIdMasterDTO.setMasterName(TallyMasters.LOCATION.toString());
            alterIdMasterDTO.setCompanyId(company.getId());
            alterIdMasterService.save(alterIdMasterDTO);
        }

        long end = System.nanoTime();
        double elapsedTime = (end - start) / 1000000.0;
        // update sync table
        syncOperation.setCompleted(true);
        syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
        syncOperation.setLastSyncTime(elapsedTime);
        syncOperationRepository.save(syncOperation);
        log.info("Sync completed in {} ms", elapsedTime);
        dectivatedloc.forEach(data -> log.info("deactivated id " + dectivatedloc.size() + data));
    }
}
