package com.orderfleet.webapp.web.vendor.excelHcreation;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.vendor.excel.service.AccountProfileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AccountProfileServiceHcreation {

    private final Logger log = LoggerFactory.getLogger(AccountProfileUploadService.class);
    @Inject
    private BulkOperationRepositoryCustom bulkOperationRepositoryCustom;
    @Inject
    private SyncOperationRepository syncOperationRepository;
    @Inject
    private AccountProfileService accountProfileService;
    @Inject
    private CompanyRepository companyRepository;
    @Inject
    private LocationAccountProfileRepository locationAccountProfileRepository;
    @Inject
    private LocationAccountProfileService locationAccountProfileService;
    @Inject
    private AccountProfileRepository accountProfileRepository;
    @Inject
    private LocationService locationService;
   @Inject
    private AccountTypeRepository accountTypeRepository;

    @Inject
    private  UserRepository userRepository;



    public void saveUpdateAccountProfiles(List<AccountProfileDTO> accountProfileDTOs, SyncOperation so) {


            long start = System.nanoTime();
            final Company company = so.getCompany();
            final User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
            final Long companyId = company.getId();
            Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();
            // All product must have a division/category, if not, set a default one
        List<LocationAccountProfileDTO> locationAccountProfiles =  new ArrayList<>();
            AccountType defaultAccountType = accountTypeRepository.findFirstByCompanyIdOrderByIdAsc(company.getId());

            log.info("Default Account type 123456:" + defaultAccountType.getName());
            // find all exist account profiles
            List<String> apAlias = accountProfileDTOs.stream().map(apDto -> apDto.getCustomerId().toUpperCase())
                    .collect(Collectors.toList());

            List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyId();

            for (AccountProfileDTO apDto : accountProfileDTOs) {
                AccountProfile accountProfile;

                    Optional<AccountProfile> optionalAP = accountProfiles.stream()
                            .filter(pc -> pc.getCustomerId() != null && !pc.getCustomerId().equals("")
                                    ? pc.getCustomerId().equalsIgnoreCase(String.valueOf(apDto.getCustomerId()))
                                    : false)
                            .findAny();
                    if (optionalAP.isPresent()) {
                        accountProfile = optionalAP.get();
                        // if not update, skip this iteration. Not implemented now
                        // if (!accountProfile.getThirdpartyUpdate()) { continue; }
                    } else {
                        accountProfile = new AccountProfile();
                        accountProfile.setPid(com.orderfleet.webapp.service.AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
                        accountProfile.setCustomerId(apDto.getAlias());
                        accountProfile.setUser(user);
                        accountProfile.setCompany(company);
                        accountProfile.setAccountStatus(AccountStatus.Unverified);
                        accountProfile.setDataSourceType(DataSourceType.TALLY);
                        accountProfile.setImportStatus(true);
                    }
                    accountProfile.setAlias(apDto.getAlias());

                    accountProfile.setName(apDto.getName());
                    accountProfile.setTrimChar(apDto.getTrimChar());
                if (isValidPhone(apDto.getPhone1())) {
                    accountProfile.setPhone1(apDto.getPhone1());
                }
                if (isValidPhone(apDto.getPhone2())) {
                    accountProfile.setPhone2(apDto.getPhone2());
                }
                if (isValidEmail(apDto.getEmail1())) {
                    accountProfile.setEmail1(apDto.getEmail1());
                }
                if(apDto.getCustomerCode()!=null) {
                    accountProfile.setCustomerCode(apDto.getCustomerCode());}

                accountProfile.setTinNo(apDto.getTinNo());
                accountProfile.setPin(apDto.getPin());
                accountProfile.setDescription(apDto.getDescription());
                accountProfile.setActivated(true);
                accountProfile.setAddress(apDto.getAddress());
                accountProfile.setCity(apDto.getCity());


                // account type
                log.info("AccountProfileAccount type == null :" + accountProfile.getAccountType());
                if (accountProfile.getAccountType() == null) {
                    accountProfile.setAccountType(defaultAccountType);
                }
                List<AccountType> accountTypes = accountTypeRepository.findAllByCompanyId();

                if (apDto.getAccountTypeName() != null ) {
                    Optional<AccountType> optionalAccountType = accountTypes.stream()
                            .filter(atn -> apDto.getAccountTypeName().equals(atn.getName())).findAny();

                    if (optionalAccountType.isPresent()) {
                        accountProfile.setAccountType(optionalAccountType.get());
                    } else {
                        accountProfile.setAccountType(defaultAccountType);
                    }
                }
                LocationAccountProfileDTO locationAccountProfileDTO = new LocationAccountProfileDTO();
                locationAccountProfileDTO.setAccountProfileName(apDto.getName());
                locationAccountProfileDTO.setLocationName("Territory");
                locationAccountProfiles.add(locationAccountProfileDTO);
                Optional<AccountProfile> opAcp = saveUpdateAccountProfiles.stream()
                        .filter(acp -> acp.getName().equalsIgnoreCase(apDto.getName())).findAny();

                if (opAcp.isPresent()) {
                    continue;
                }
                saveUpdateAccountProfiles.add(accountProfile);
            }
            bulkOperationRepositoryCustom.bulkSaveAccountProfile(saveUpdateAccountProfiles);

        accountProfileService.autoTaskCreationForAccountProfiles(company);

            saveUpdateLocationAccountProfiles(locationAccountProfiles);
            long end = System.nanoTime();
            double elapsedTime = (end - start) / 1000000.0;
            // update sync table
            so.setCompleted(true);
            so.setLastSyncCompletedDate(LocalDateTime.now());
            so.setLastSyncTime(elapsedTime);
            syncOperationRepository.save(so);
            log.info("Sync completed in {} ms", elapsedTime);
        }
@Transactional
@Async
public void saveUpdateLocationAccountProfiles(List<LocationAccountProfileDTO> locationAccountProfile) {
System.out.println("Enter here to Save Location AccountProfiles");
        long start = System.nanoTime();
        final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
        Company company = companyRepository.findOne(companyId);
        List<LocationAccountProfile> newLocationAccountProfiles = new ArrayList<>();
        List<LocationAccountProfile> locationAccountProfiles = locationAccountProfileService
                .findAllLocationAccountProfiles(companyId);
        // delete all assigned location account profile from tally
        // locationAccountProfileRepository.deleteByCompanyIdAndDataSourceTypeAndThirdpartyUpdateTrue(company.getId(),DataSourceType.TALLY);
        List<AccountProfile> accountProfiles = accountProfileService.findAllAccountProfileByCompanyId(companyId);
        List<Location> locations = locationService.findAllLocationByCompanyId(companyId);
        List<Long> locationAccountProfilesIds = new ArrayList<>();

        for (LocationAccountProfileDTO locAccDto : locationAccountProfile) {
            LocationAccountProfile locationAccount= new LocationAccountProfile();

            Optional<Location> loc = locations.stream().filter(pl -> locAccDto.getLocationName().equals(pl.getAlias()))
                    .findFirst();
            // find accountprofile
            // System.out.println(loc.get()+"===Location");

            Optional<AccountProfile> acc = accountProfiles.stream()
                    .filter(ap -> locAccDto.getAccountProfileName().equals(ap.getName())).findFirst();
            if (acc.isPresent()) {
                List<Long> locationAccountProfileIds = locationAccountProfiles.stream()
                        .filter(lap -> acc.get().getPid().equals(lap.getAccountProfile().getPid()))
                        .map(lap -> lap.getId()).collect(Collectors.toList());
                if (locationAccountProfileIds.size() != 0) {
                    locationAccountProfilesIds.addAll(locationAccountProfileIds);
                }
                if (loc.isPresent()) {
                    locationAccount.setLocation(loc.get());
                } else if (acc.isPresent()) {
                    locationAccount.setLocation(locations.get(0));
                }
                locationAccount.setAccountProfile(acc.get());
                locationAccount.setCompany(company);
                newLocationAccountProfiles.add(locationAccount);
            }
        }
        if (locationAccountProfilesIds.size() != 0) {
            locationAccountProfileRepository.deleteByIdIn(companyId, locationAccountProfilesIds);
        }

        locationAccountProfileRepository.save(newLocationAccountProfiles);

        long end = System.nanoTime();
        double elapsedTime = (end - start) / 1000000.0;
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
