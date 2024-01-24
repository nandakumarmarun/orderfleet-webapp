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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TallyAccountServices {

    private final Logger log = LoggerFactory.getLogger(TPAccountProfileManagementService.class);

    private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

    private final SyncOperationRepository syncOperationRepository;


    private final DivisionRepository divisionRepository;

    private final PriceLevelRepository priceLevelRepository;

    private final AccountProfileRepository accountProfileRepository;

    private final AccountTypeRepository accountTypeRepository;

    private final UserRepository userRepository;

    private final LocationService locationService;

    @Autowired
    private AlterIdMasterService alterIdMasterService;

    boolean flag= false;

    boolean flagac= false;

    public TallyAccountServices(
            BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
            SyncOperationRepository syncOperationRepository,
            DivisionRepository divisionRepository,
            PriceLevelRepository priceLevelRepository,
            AccountProfileRepository accountProfileRepository,
            AccountTypeRepository accountTypeRepository,
            LocationRepository locationRepository,
            UserRepository userRepository,
            LocationService locationService,
            AlterIdMasterService alterIdMasterService) {
        super();
        this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
        this.syncOperationRepository = syncOperationRepository;
        this.divisionRepository = divisionRepository;
        this.priceLevelRepository = priceLevelRepository;
        this.accountProfileRepository = accountProfileRepository;
        this.accountTypeRepository = accountTypeRepository;
        this.userRepository = userRepository;
        this.locationService = locationService;
        this.alterIdMasterService = alterIdMasterService;
    }


    @Transactional
    public void saveUpdateAccountProfilesUpdateIdNew(
            final List<AccountProfileDTO> accountProfileDTOs,
            final SyncOperation syncOperation, boolean fullUpdate) {
        long start = System.nanoTime();
        final Company company = syncOperation.getCompany();
        log.info("Saving Account Profiles : " + accountProfileDTOs.size());

        Optional<User> opUser =
                userRepository
                        .findOneByLogin(
                                SecurityUtils.getCurrentUserLogin());

        User userObject = new User();
        if (opUser.isPresent()) {
            userObject = opUser.get();
        } else {
            userObject = userRepository.findOneByLogin("siteadmin").get();
        }

        final User user = userObject;
        final Long companyId = company.getId();
        Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();

        AccountType defaultAccountType =
                accountTypeRepository
                        .findFirstByCompanyIdOrderByIdAsc(company.getId());

        log.info("Default Account Type :" + defaultAccountType.getName());

        List<AccountProfile> accountProfiles =
                accountProfileRepository
                        .findAllByCompanyIdOrderbyid();

        log.info("Db accounts     : " + accountProfiles.size());
        log.info("Tally  accounts : " + accountProfileDTOs.size());

        List<PriceLevel>
                tempPriceLevel = priceLevelRepository.findByCompanyId(companyId);

        Set<Long> dectivatedac = new HashSet<>();

        if(fullUpdate == true) {
            if(!accountProfiles.isEmpty()
                    && accountProfiles.size() > 1
                    && !accountProfileDTOs.isEmpty()) {

                accountProfiles.remove(0);
                List<AccountProfile> tallyAccountProfile =
                        accountProfiles
                                .stream()
                                .filter(data -> data.getDataSourceType()
                                        .equals(DataSourceType.TALLY))
                                .collect(Collectors.toList());

                for(AccountProfile ac :tallyAccountProfile) {
                    flagac = false;
                    accountProfileDTOs.forEach(data ->{
                        if(ac.getCustomerId().equals(data.getCustomerId())) {
                            flagac = true;
                        }
                    });
                    if(!flagac) {
                        dectivatedac.add(ac.getId());
                    }
                }
            }
            if(!dectivatedac.isEmpty()) {
                accountProfileRepository.deactivateAcoundProfileUsingInId(dectivatedac);
            }
        }

        for (AccountProfileDTO apDto : accountProfileDTOs) {
            Optional<AccountProfile> optionalAP = null;

            optionalAP = accountProfiles.stream()
                    .filter(p -> p.getCustomerId() != null
                            && !p.getCustomerId().equals("")
                            ? p.getCustomerId().equals(apDto.getCustomerId())
                            : false)
                    .findAny();

            AccountProfile accountProfile = new AccountProfile();
            if (optionalAP.isPresent()) {
                accountProfile = optionalAP.get();
                accountProfile.setName(apDto.getName());
            } else {
                accountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
                accountProfile.setCustomerId(apDto.getCustomerId());
                accountProfile.setUser(user);
                accountProfile.setCompany(company);
                accountProfile.setAccountStatus(AccountStatus.Unverified);
                accountProfile.setDataSourceType(DataSourceType.TALLY);
                accountProfile.setDescription(apDto.getName());
                accountProfile.setImportStatus(true);
            }
            accountProfile.setName(apDto.getName());
            accountProfile.setTrimChar(apDto.getTrimChar());
            accountProfile.setTinNo(apDto.getTinNo());
            accountProfile.setDescription(apDto.getName());
            accountProfile.setAlias(apDto.getAlias());

            if (isValidPhone(apDto.getPhone1())) {
                accountProfile.setPhone1(apDto.getPhone1());
            } else {
                accountProfile.setPhone1("");
            }

            if (isValidPhone(apDto.getPhone2())) {
                accountProfile.setPhone2(apDto.getPhone2());
            } else {
                accountProfile.setPhone2("");
            }
            if (isValidEmail(apDto.getEmail1())) {
                accountProfile.setEmail1(apDto.getEmail1());
            }
            accountProfile.setPin(apDto.getPin());
            accountProfile.setActivated(apDto.getActivated());
            accountProfile.setAddress(apDto.getAddress());
            accountProfile.setCity(apDto.getCity());
            accountProfile.setContactPerson(apDto.getContactPerson());
            accountProfile.setStateName(apDto.getStateName());
            accountProfile.setCountryName(apDto.getCountryName());
            accountProfile.setGstRegistrationType(
                    apDto.getGstRegistrationType() == null
                            ? "Regular" : apDto.getGstRegistrationType());

            if (apDto.getDefaultPriceLevelName() != null
                    && !apDto.getDefaultPriceLevelName().equalsIgnoreCase("")) {

                Optional<PriceLevel> optionalPriceLevel = tempPriceLevel.stream()
                        .filter(pl -> apDto.getDefaultPriceLevelName().equals(pl.getName())).findAny();

                if (optionalPriceLevel.isPresent()) {
                    accountProfile.setDefaultPriceLevel(optionalPriceLevel.get());
                } else {
                    if (apDto.getDefaultPriceLevelName().length() > 0) {
                        PriceLevel priceLevel = new PriceLevel();
                        priceLevel.setPid(PriceLevelService.PID_PREFIX + RandomUtil.generatePid());
                        priceLevel.setName(apDto.getDefaultPriceLevelName());
                        priceLevel.setActivated(true);
                        priceLevel.setCompany(company);
                        priceLevel = priceLevelRepository.save(priceLevel);
                        tempPriceLevel.add(priceLevel);
                        accountProfile.setDefaultPriceLevel(priceLevel);
                    }
                }
            }else {
                accountProfile.setDefaultPriceLevel(null);
            }
            if (accountProfile.getAccountType() == null) {
                accountProfile.setAccountType(defaultAccountType);
            }

            accountProfile.setDataSourceType(DataSourceType.TALLY);
            Optional<AccountProfile> opAccP = saveUpdateAccountProfiles
                    .stream()
                    .filter(so -> so.getName()
                            .equalsIgnoreCase(apDto.getName()))
                    .findAny();
            if (opAccP.isPresent()) {
                continue;
            }
            saveUpdateAccountProfiles.add(accountProfile);
        }
        log.info("Saving accountProfileDTOs.Account Profiles : " + saveUpdateAccountProfiles.size());
        bulkOperationRepositoryCustom.bulkSaveAccountProfile(saveUpdateAccountProfiles);

        Long Listcount =
                accountProfileDTOs
                        .parallelStream()
                        .filter(accountProfileDTO -> accountProfileDTO.getAlterId() == null)
                        .count();

        log.debug("Null Count : " + Listcount);
        if(Listcount == 0) {
            AccountProfileDTO accountProfileDTO =
                    accountProfileDTOs
                            .stream()
                            .max(Comparator.comparingLong(AccountProfileDTO::getAlterId))
                            .get();

            AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
            alterIdMasterDTO.setAlterId(accountProfileDTO.getAlterId());
            alterIdMasterDTO.setMasterName(TallyMasters.ACCOUNT_PROFILE.toString());
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
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    private static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        if (phone.length() > 20) {
            return false;
        }
        return true;
    }

}
