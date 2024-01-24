package com.orderfleet.webapp.web.rest.integration.TallyClientAppResource.Service;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.repository.PriceLevelListRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.service.PriceLevelListService;
import com.orderfleet.webapp.service.async.TPProductProfileManagementService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.PriceLevelListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TallyPriceLevelListService {

    private final Logger log = LoggerFactory.getLogger(TPProductProfileManagementService.class);
    private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;
    private final PriceLevelListRepository priceLevelListRepository;
    private final PriceLevelRepository priceLevelRepository;
    private final ProductProfileRepository productProfileRepository;
    @Inject
    private SyncOperationRepository syncOperationRepository;

    public TallyPriceLevelListService(
            BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
            PriceLevelListRepository priceLevelListRepository,
            PriceLevelRepository priceLevelRepository,
            ProductProfileRepository productProfileRepository) {
        this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
        this.priceLevelListRepository = priceLevelListRepository;
        this.priceLevelRepository = priceLevelRepository;
        this.productProfileRepository = productProfileRepository;
    }

    @Transactional
    @Async
    public void saveUpdatePriceLevelListUpdatedIdNew(
            final List<PriceLevelListDTO> priceLevelListDTOs,
            final SyncOperation syncOperation) {

        priceLevelListDTOs.forEach(dat->System.out.println(
                dat.getPriceLevelName() + " ======= "+ dat.getPriceLevelName() + " ==== "+ dat.discount));

        long start = System.nanoTime();
        final Company company = syncOperation.getCompany();
        final Long companyId = company.getId();
        Set<PriceLevelList> saveUpdatePriceLevelLists = new HashSet<>();
        // find all exist priceLevels

        Set<String> plNames =
                priceLevelListDTOs
                        .stream()
                        .map(pll -> pll.getPriceLevelName())
                        .collect(Collectors.toSet());

        List<PriceLevelList> priceLevelLists =
                priceLevelListRepository
                        .findByCompanyIdAndPriceLevelNameIgnoreCaseIn(
                                company.getId(), plNames);

        Map<String, ProductProfile> tempProductProfile = new HashMap<>();
        List<PriceLevel> tempPriceLevel = priceLevelRepository.findByCompanyId(companyId);
        List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);

        for (PriceLevelListDTO pllDto : priceLevelListDTOs) {
            // check exist by price-level name and product-profile name, only
            // one exist with a both same for a company
            Optional<PriceLevelList> optionalPll =
                    priceLevelLists
                            .stream()
                            .filter(pll -> pll.getPriceLevel().getName()
                                    .equals(pllDto.getPriceLevelName())
                                    && pll.getProductProfile().getName()
                                    .equals(pllDto.getProductProfileName()))
                    .findAny();

            PriceLevelList priceLevelList;
            if (optionalPll.isPresent()) {
                priceLevelList = optionalPll.get();
                // if not update, skip this iteration.
                // if(!priceLevel.getThirdpartyUpdate()) {continue;}
                priceLevelList.setDiscount(pllDto.getDiscount());
            } else {
                priceLevelList = new PriceLevelList();
                priceLevelList.setPid(PriceLevelListService.PID_PREFIX + RandomUtil.generatePid());
                priceLevelList.setCompany(company);
                priceLevelList.setDiscount(pllDto.getDiscount());
            }

            //set product profile and price level, if present
            Optional<ProductProfile> optionalPP;
            if (tempProductProfile.containsKey(pllDto.getProductProfileName())) {
                optionalPP = Optional.of(tempProductProfile.get(pllDto.getProductProfileName()));
            } else {
                optionalPP = productProfiles
                        .stream()
                        .filter(pl -> pllDto.getProductProfileName()
                                .equals(pl.getName()))
                        .findAny();
            }

            Optional<PriceLevel> optionalPL =
                    tempPriceLevel.stream()
                            .filter(pl -> pllDto.getPriceLevelName()
                                    .equals(pl.getName()))
                            .findAny();

            if (optionalPP.isPresent() && optionalPL.isPresent()) {
                priceLevelList.setProductProfile(optionalPP.get());
                tempProductProfile.put(pllDto.getProductProfileName(), optionalPP.get());
                priceLevelList.setPriceLevel(optionalPL.get());
                priceLevelList.setPrice(pllDto.getPrice());
                priceLevelList.setRangeFrom(pllDto.getRangeFrom());
                priceLevelList.setRangeTo(pllDto.getRangeTo());
                priceLevelList.setDiscount(pllDto.getDiscount());
                saveUpdatePriceLevelLists.add(priceLevelList);
            }
        }

        bulkOperationRepositoryCustom
                .bulkSaveUpdatePriceLevelLists(
                        saveUpdatePriceLevelLists);

        long end = System.nanoTime();
        double elapsedTime = (end - start) / 1000000.0;
        syncOperation.setCompleted(true);
        syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
        syncOperation.setLastSyncTime(elapsedTime);
        syncOperationRepository.save(syncOperation);
        log.info("Sync completed in {} ms", elapsedTime);
    }
}
